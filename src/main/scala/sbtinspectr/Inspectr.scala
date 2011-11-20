package sbtinspectr

import sbt._
import Keys._
import sbt.Project.{ScopedKey, flattenLocals, compiled}
import sbt.Load.BuildStructure

// see http://harrah.github.com/xsbt/latest/sxr/Project.scala.html
object Inspectr {
  def apply(structure: BuildStructure, build: URI, scoped: ScopedKey[_],
      generation: Int)(implicit display: Show[ScopedKey[_]]): Inspectr = {
    val key = scoped.key
    val scope = scoped.scope
    
    lazy val clazz = key.manifest.erasure
    lazy val firstType = key.manifest.typeArguments.head
    val (typeName: String, value: Option[_]) =
      structure.data.get(scope, key) match {
        case None => ("", None)
        case Some(v) =>
          if(clazz == classOf[Task[_]]) ("Task[" + firstType.toString + "]", None)
          else if(clazz == classOf[InputTask[_]]) ("InputTask[" + firstType.toString + "]", None)
          else (key.manifest.toString, Some(v))
      }
    
    val description = key.description getOrElse{""}
    val definedIn = structure.data.definingScope(scope, key) match {
      case Some(sc) => display(ScopedKey(sc, key))
      case None => ""
    }
    val cMap = flattenLocals(compiled(structure.settings, false)(structure.delegates, structure.scopeLocal, display))
    // val related = cMap.keys.filter(k => k.key == key && k.scope != scope)
    val depends = cMap.get(scoped) match { case Some(c) => c.dependencies.toSet; case None => Set.empty }
    // val reverse = reverseDependencies(cMap, scoped)
    
    Inspectr(display(scoped), definedIn, typeName, value, description, build,
      depends map { apply(structure, build, _, generation + 1) })
  }
}

case class Inspectr(name: String,
  definedIn: String,
  typeName: String,
  value: Option[Any],
  description: String,
  build: URI,
  depends: Set[Inspectr]) {
  // [info] foo
  // [info]   +-bar
  // [info]   | +-baz
  // [info]   |
  // [info]   +-quux
  def toAscii: String = {
    toAsciiLines(0).mkString("\n")
  }
  
  private def toAsciiLines(level: Int): Vector[String] = {
    def valueString: String =
      value map {
        case f: File =>
          try {
            val base = new File(build)
            val rel: String = ((f :: Nil) x relativeTo(base)).head._2
            if (rel.length < f.toString.length) rel
            else f.toString
          } catch {
            case _ => f.toString
          }
        case x => x.toString
      } getOrElse {typeName}
    
    def toLine(level: Int): String = {
      val s = ("  " * level) + 
              (if (level == 0) "" else "+-") + definedIn +
              " = " + valueString
      if (s.length > 72) s.slice(0, 70) + ".."
      else s
    }
    
    def insertBar(s: String, at: Int): String =
      s.slice(0, at) +
      (s(at).toString match {
        case " " => "|"
        case x => x
      }) +
      s.slice(at + 1, s.length)    
    
    val dependLines = Vector(depends.toSeq: _*) map {_.toAsciiLines(level + 1)}
    val withBar = dependLines.zipWithIndex flatMap {
      case (lines, pos) if pos < (depends.size - 1) => lines map {insertBar(_, 2 * (level + 1))}
      case (lines, pos) =>
        if (lines.last.trim != "") lines ++ Vector("  " * (level + 1))
        else lines
    }
    toLine(level) +: withBar
  }
}
