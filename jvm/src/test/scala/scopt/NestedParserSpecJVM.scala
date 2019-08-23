package scopttest

import minitest.SimpleTestSuite

import scala.collection.{ Seq => CSeq }

object NestedParserSpecJVM extends SimpleTestSuite {
  test("nested parser should parse second level command") {
    nestedParser(
      Config(cmd = "clients_add", clientName = "myClient"),
      "clients",
      "add",
      "myClient"
    )
  }

  test("nested parser should parse second level command") {
    nestedParser(
      Config(cmd = "clients_remove", id = "clientId"),
      "clients",
      "remove",
      "clientId"
    )
  }

  val nestedParser1 = new scopt.OptionParser[Config]("scopt") {
    head("scopt", "4.x")
    cmd("clients")
      .action((_, c) => c.copy(cmd = "clients"))
      .text("Manage clients")
      .children(
        cmd("add")
          .action((_, c) => c.copy(cmd = s"${c.cmd}_add"))
          .text("Add client")
          .children(
            arg[String]("<client-name>")
              .action((x, c) => c.copy(clientName = x))
              .text("Client name")
          ),
        cmd("remove")
          .action((_, c) => c.copy(cmd = s"${c.cmd}_remove"))
          .text("Remove client")
          .children(
            arg[String]("<id>")
              .action((x, c) => c.copy(id = x))
              .text("Client id")
          )
      )
    help("help")
  }
  def nestedParser(expectedConfig: Config, args: String*): Unit = {
    val result = nestedParser1.parse(CSeq(args: _*), Config())
    assert(result.get == expectedConfig, s"${result.get} did not equal $expectedConfig")
  }

  case class Config(cmd: String = "none", clientName: String = "none", id: String = "none")
}
