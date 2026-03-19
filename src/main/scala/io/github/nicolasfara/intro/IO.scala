package io.github.nicolasfara.intro

final case class IO[A](unsafeRun: () => A)

object IO:
  given Monad[IO] with
    def pure[A](a: A): IO[A] = IO(() => a)
    def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
      IO(() => f(fa.unsafeRun()).unsafeRun())

  def putLine(line: String): IO[Unit] = IO(() => println(line))
  def readLine: IO[String] = IO(() => scala.io.StdIn.readLine())

@main def runIO(): Unit =
  import Monad.*

  val program: IO[Unit] = for
    _ <- IO.putLine("What is your name?")
    name <- IO.readLine
    _ <- IO.putLine(s"Hello, $name!")
  yield ()

  // println(program)

  program.unsafeRun()
