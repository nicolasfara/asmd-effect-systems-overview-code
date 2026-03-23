package io.github.nicolasfara.intro.parser

object ParserStacked:
  import Monad.*
  import Identity.given
  import MonadTransformer.*
  import OptionT.given
  import StateT.given

  type State[S, A] = StateT[Identity, S, A]
  type Parser[A] = OptionT[[V] =>> State[String, V], A]

  extension [A](parser: Parser[A])
    def parse(input: String): (Option[A], String) = parser.value.runStateT(input)

  // Parser combinators
  def fail[A]: Parser[A] = OptionT.fail
  def get: Parser[String] = StateT.get.lift
  def set(value: String): Parser[Unit] = StateT.set(value).lift

  def char(c: Char): Parser[Unit] = for
    input <- get
    _ <- if input.nonEmpty && input.head == c then set(input.tail) else fail
  yield ()

@main def runParserStacked(): Unit =
  import Monad.*
  import Identity.given
  import OptionT.given
  import StateT.given
  import ParserStacked.*

  val input = "abc"
  val parser: Parser[Unit] = for
    _ <- char('a')
    _ <- char('b')
    _ <- char('c')
  yield ()

  val (result, remaining) = parser.parse(input)
  result match
    case Some(_) => println(s"Parsed successfully! Remaining input: '$remaining'")
    case None => println("Failed to parse.")
