name := "play-content"

organization := "play-infra"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "play-infra" %% "play-wished" % "0.1",
  "org.pegdown" % "pegdown" % "1.4.2",
  "net.coobird" % "thumbnailator" % "0.4.7",
  "org.jsoup" % "jsoup" % "1.7.3",
  "commons-validator" % "commons-validator" % "1.4.0"
)

play.Project.playScalaSettings

resolvers += "quonb" at "http://repo.quonb.org/"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

publishTo := Some(Resolver.file("file", new File("/mvn-repo")))

testOptions in Test += Tests.Argument("junitxml")

