package io.github.nicolasfara.intro.parser

final case class Parser[A](parse: String => Option[(A, String)])

object Parser:
  import io.github.nicolasfara.intro.Monad
  import Monad.*

  given Monad[Parser] with
    def pure[A](a: A): Parser[A] = Parser(input => Some((a, input)))
    def flatMap[A, B](fa: Parser[A])(f: A => Parser[B]): Parser[B] = Parser: input =>
      fa.parse(input) match
        case Some((a, rest)) => f(a).parse(rest)
        case None => None

  extension [A](parser: Parser[A])
    def orElse(other: Parser[A]): Parser[A] = Parser: input =>
      parser.parse(input) match
        case Some(result) => Some(result)
        case None => other.parse(input)

  def unit[A](a: A): Parser[A] = Parser(input => Some((a, input)))

  def char(c: Char): Parser[Unit] = Parser:
    case input if input.nonEmpty && input.head == c => Some(((), input.tail))
    case _ => None

  def eof(): Parser[Unit] = Parser:
    case "" => Some(((), ""))
    case _ => None

  def complete[A](parser: Parser[A]): Parser[A] = for
    result <- parser
    _ <- eof()
  yield result

  def aab(): Parser[Unit] = for
    _ <- Parser.char('a')
    _ <- Parser.char('a')
    _ <- Parser.char('b')
  yield ()

  def aAndb(): Parser[Int] = (for
    _ <- Parser.char('a')
    i <- aAndb()
    _ <- Parser.char('b')
  yield i + 1).orElse(Parser.unit(0))

@main def runParser(): Unit =
  val input = "aaaaaaaaaabbbbbbbbbbbbbb"
  val result = Parser.aAndb().parse(input)
  result match
    case Some((value, remaining)) => println(s"Parsed successfully! Value: $value, Remaining input: '$remaining'")
    case None => println("Failed to parse.")
