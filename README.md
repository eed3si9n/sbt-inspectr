sbt-inspectr is an sbt plugin for calling inspect recursively.

## how to setup
Add the following to your `~/.sbt/plugins/build.sbt`:

    addSbtPlugin("com.eed3si9n" % "sbt-inspectr" % "0.0.2")

Then, for some reason with sbt 0.11.1, you have to do the following from sbt in your project:

    > reload plugins
    > clean
    > reload return

## how to use
The above automatically adds `inspectr` command.
It displays the value of the key, and then displays all of its dependencies and their values in a tree.

```
helloworld> inspectr package
[info] compile:package = Task[java.io.File]
[info]   +-compile:package-bin = Task[java.io.File]
[info]     +-compile:package-configuration(for package-bin) = Task[sbt.Packag..
[info]     | +-compile:mappings(for package-bin) = Task[scala.collection.Seq[..
[info]     | | +-compile:products = Task[scala.collection.Seq[java.io.File]]
[info]     | | 
[info]     | +-compile:artifact-path(for package-bin) = target/scala-2.9.1/he..
[info]     | | +-*/*:artifact-name = <function3>
[info]     | | +-*:cross-target = target/scala-2.9.1
[info]     | | +-*/*:scala-version = 2.9.1
[info]     | | +-compile:artifact(for package-bin) = Artifact(helloworld,jar,..
[info]     | | | +-*/*:artifact-classifier = None
[info]     | | | +-compile:configuration = compile
[info]     | | | 
[info]     | | +-*:project-id = production:helloworld:0.1-SNAPSHOT
[info]     | | 
[info]     | +-compile:package-options(for package-bin) = Task[scala.collecti..
[info]     |   +-*:name = helloworld
[info]     |   +-compile:main-class = Task[scala.Option[java.lang.String]]
[info]     |   +-*:organization-name = production
[info]     |   +-*:organization = production
[info]     |   +-*/*:homepage = None
[info]     |   +-*/*:package-options = Task[scala.collection.Seq[sbt.PackageO..
[info]     |   +-*/*:version = 0.1-SNAPSHOT
[info]     |   
[info]     +-compile:cache-directory(for package-bin) = target/scala-2.9.1/ca..
[info]     +-compile:streams(for package-bin) = Task[sbt.std.TaskStreams[sbt...
[info]       +-*/*:streams-manager = Task[sbt.std.Streams[sbt.Init$ScopedKey[..
[info]
```

## License
MIT License.
