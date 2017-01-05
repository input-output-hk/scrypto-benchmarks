name := "scrypto-benchmarks"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
    "com.h2database" % "h2-mvstore" % "1.4.193",
    "org.scorexfoundation" %% "scrypto" % "1.2.0-SNAPSHOT")