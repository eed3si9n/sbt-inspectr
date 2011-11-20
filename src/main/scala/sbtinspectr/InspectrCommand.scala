package sbtinspectr

object InspectrCommand {
  import sbt._
  import Keys._
  import complete.Parser
  import complete.DefaultParsers._
  import CommandSupport.logger
  
  val inspectrCommand = "inspectr"
  val inspectrBrief = (inspectrCommand + " <key>", "Prints the value for 'key', and all of its dependencies' values.")
  val inspectrDetailed = inspectrCommand + """ <key>
	For a plain setting, the value bound to the key argument is displayed
	using its toString method.
	Otherwise, the type of task ("Task" or "Input task") is displayed.

	The process is repeated recursively for all of the settings that this
	setting depends on, and displayed as a tree.
"""
  
  def inspectr = Command(inspectrCommand, inspectrBrief, inspectrDetailed)(inspectrParser) { case (s, (sk)) =>
    // see http://harrah.github.com/xsbt/latest/sxr/Main.scala.html
    implicit val show = Project.showContextKey(s)
    val tree = Inspectr(Project.structure(s), Project.session(s).current.build, sk, 0).toAscii 
    logger(s).info(tree)
    s
  }
  
  lazy val inspectrParser = (s: State) => BuiltinCommands.spacedKeyParser(s)
}
