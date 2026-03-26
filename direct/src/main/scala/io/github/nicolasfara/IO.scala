package io.github.nicolasfara

import java.nio.file.Path

trait IO:
  /** Writes the given content to the output. */
  def write(content: String): Unit
  /** Reads input and applies the given function to it. */
  def read[T](f: Iterator[String] => T): T

object IO:
  def write(content: String)(using io: IO): Unit = io.write(content)
  def read[T](f: Iterator[String] => T)(using io: IO): T = io.read(f)

  // ---- Handlers

  def console[A](program: IO ?=> A): A =
    given IO with
      def write(content: String): Unit = println(content)
      def read[T](f: Iterator[String] => T): T = f(scala.io.StdIn.readLine().linesIterator)
    program

  def file[A](path: String)(program: IO ?=> A): A =
    given IO with
      def write(content: String): Unit =
        val pw = new java.io.PrintWriter(path)
        try pw.write(content) finally pw.close()
      def read[T](f: Iterator[String] => T): T =
        val source = scala.io.Source.fromFile(path)
        try f(source.getLines()) finally source.close()
    program


@main def consoleIO(): Unit =
  IO.console:
    IO.write("What's your name?")
    val name = IO.read(_.mkString)
    IO.write(s"Hello, $name!")

@main def fileIO(): Unit =
  IO.file("output.txt"):
    IO.write("This is a test.")
    val content = IO.read(_.mkString("\n"))
    IO.write(s"Read from file: $content")

@main def higherOrderIO(): Unit =
  val path = Path.of("output.txt")
  val result = IO.file(path.toString):
    IO.write("Testing higher-order IO.")
    IO.read(lines => lines)
  result.foreach(line => println(s"Read line: $line"))
