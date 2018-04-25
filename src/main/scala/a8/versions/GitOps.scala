package a8.versions

import m3.predef._
import m3.Exec

object GitOps {



  def branchName(projectDir: m3.fs.Directory) = {
    val gitLogStdout =
      Exec(Utilities.resolvedGitExec, "log", "-n", "1", "--pretty=%d", "HEAD")
        .inDirectory(projectDir)
        .execCaptureOutput()
        .stdout

    parseGitBranchName(gitLogStdout)
  }

  def parseGitBranchName(gitLogStdout: String): String = {
    val trimmedGitLogStdout = gitLogStdout.trim
    try {
      val bn =
        (trimmedGitLogStdout
          .replace(")", "")
          .replace("(", "")
          .splitList(",")
          .map(_.trim)
          .filter(b => b != "HEAD" && b != "origin/HEAD")
          .map { s =>
            s.splitList("->") match {
              case _ :: b :: Nil => b
              case l => l.head
            }
          }
          .headOption
          .getOrElse {
            println(s"unable to parse branch name from '${trimmedGitLogStdout}'")
            "unknown"
          }) match {
            case b if b.startsWith("origin/") => b.substring("origin/".length)
            case b => b
          }
      scrubBranchName(bn)
    } catch {
      case e: Exception =>
        println(s"unable to parse branch name from '${trimmedGitLogStdout}'")
        "unknown"
    }
  }

  def scrubBranchName(unscrubbedName: String): String = {
    unscrubbedName
      .filter(ch => ch.isLetterOrDigit)
      .toLowerCase
  }

}
