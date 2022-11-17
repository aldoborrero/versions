package io.accur8.neodeploy


import a8.shared.json.JsonTypedCodec
import a8.shared.json.ast.{JsArr, JsStr}
import zio.{Chunk, ExitCode, UIO, ZIO}
import a8.shared.SharedImports._
import PredefAssist._
import a8.shared.FileSystem.Directory
import a8.shared.app.{LoggerF, LoggingF}
import io.accur8.neodeploy.Command.CommandException
import zio.process.CommandError
import zio.process.CommandError.NonZeroErrorCode

object Command {

  implicit val jsonCodec =
    JsonTypedCodec.JsArr.dimap[Command](
      arr => Command(arr.values.collect{ case JsStr(s) => s }),
      cmd => JsArr(cmd.args.map(JsStr.apply).toList)
    )

  def apply(args: String*): Command =
    new Command(args)

  case class Result(
    exitCode: ExitCode,
    outputLines: Chunk[String],
  )


  case class CommandException(cause: CommandError, command: Command) extends Exception

}
case class Command(args: Iterable[String], workingDirectory: Option[Directory] = None) extends LoggingF {

  def workingDirectory(wd: Directory): Command =
    copy(workingDirectory = wd.some)

  def appendArgs(args: String*): Command =
    Command(args = this.args ++ args)

  def appendArgsSeq(args: Seq[String]): Command =
    Command(args = this.args ++ args)

  def asZioCommand: zio.process.Command =
    zio.process.Command(args.head, args.tail.toSeq :_*)

  def execDropOutput: ZIO[Any, CommandException, Unit] =
    exec()
      .as(())

  def execCaptureOutput: ZIO[Any, CommandException, Command.Result] =
    exec()

  def execLogOutput(implicit loggerF: LoggerF): ZIO[Any, CommandException, Command.Result] =
    exec(logLinesEffect = { lines => loggerF.debug(lines.mkString("\n   ", "\n   ", "\n    ")) })

  def exec(
    failOnNonZeroExitCode: Boolean = true,
    logLinesEffect: Chunk[String]=>UIO[Unit] = _ => zunit
  ): ZIO[Any, CommandException, Command.Result] = {
    val wd = workingDirectory.map(_.asNioPath.toFile).getOrElse(new java.io.File(".")).getAbsoluteFile
    loggerF.debug(s"running in ${wd} -- ${args.mkString(" ")}") *>
    asZioCommand
      .workingDirectory(workingDirectory.map(_.asNioPath.toFile).getOrElse(new java.io.File(".")))
      .redirectErrorStream(true)
      .run
      .flatMap { process =>
        process
          .stdout
          .linesStream
          .mapChunksZIO { lines =>
            logLinesEffect(lines)
              .as(lines)
          }
          .runCollect
          .flatMap { lines =>
            process
              .exitCode
              .map(_ -> lines)
          }
      }
      .either
      .flatMap {
        case Left(ce) =>
          ZIO.fail(CommandException(ce, this))
        case Right((exitCode, lines)) =>
          if (failOnNonZeroExitCode && exitCode.code > 0) {
            loggerF.debug(s"command failed with exit code ${exitCode.code} -- ${args.mkString(" ")}") *>
              ZIO.fail(CommandException(NonZeroErrorCode(exitCode), this))
          } else
            zsucceed(Command.Result(exitCode, lines))
      }

  }


}
