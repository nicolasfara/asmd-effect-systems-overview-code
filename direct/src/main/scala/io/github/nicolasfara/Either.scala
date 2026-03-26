package io.github.nicolasfara

import scala.util.boundary.{Label, break}
import scala.util.boundary

type CanFail[A] = Label[Left[A, Nothing]]

object either:
  def apply[A, B](body: Label[Left[A, Nothing]] ?=> B): Either[A, B] =
    boundary(Right(body))

  def fail[A](failure: A)(using Label[Left[A, Nothing]]): Nothing = break(Left(failure))

  extension [A, B](either: Either[A, B])
    def ?(using Label[Left[A, Nothing]]): B = either match
      case Right(value) => value
      case Left(failure) => break(Left(failure))

@main def eitherExample(): Unit =
  val result = either:
    val x = 10
    if x > 5 then either.fail("x is too large")
    else x * 2
  println(result) // Output: Left(x is too large)

@main def questionMarkExample(): Unit =
  import either.*

  // def legacyApi(x: Int): Either[String, Int] =
  //   if x > 5 then Left("x is too large")
  //   else Right(x * 2)

  def directStyleApi(x: Int)(using CanFail[String]): Int =
    if x > 5 then fail("x is too large")
    else x * 2

  val result = either:
    // val r = legacyApi(10).?
    val r = directStyleApi(2)
    println(s"Result from legacy API: $r")

  println(result) // Output: Left(x is too large)
