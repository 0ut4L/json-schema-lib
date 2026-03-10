lazy val V = new {
  val tpolecat = "0.5.1"
  val updates = "0.6.3"
  val `scala-fix` = "0.14.0"
  val `scala-fmt` = "2.5.4"
}

addSbtPlugin("org.typelevel" % "sbt-tpolecat" % V.tpolecat)
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % V.updates)
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % V.`scala-fix`)
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % V.`scala-fmt`)
