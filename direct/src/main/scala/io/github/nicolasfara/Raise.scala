package io.github.nicolasfara

import scala.util.boundary
import scala.util.boundary.break

trait Raise[-E]:
  def raise(e: -> E): Nothing

object Raise:
  def apply[E, A](block: => A): Raise[E] ?=> A = block

  val rethrow: Raise[Throwable] = new Raise[Throwable]:
    def raise(e: -> Throwable): Nothing = throw e

  def raise[E](error: E)(using r: Raise[E]^): Nothing = r.raise(error)

  def fold[E, A, B](program: Raise[E]^ ?=> A)(onError: E -> B)(onSuccess: A -> B): B = boundary[B]: l ?=>
    given (Raise[E]^{l}) = new Raise[E]:
      def raise(e: -> E): Nothing = break(onError(e))
    onSuccess(program)

@main def testRaise(): Unit =
  given Raise[Throwable] = Raise.rethrow

  val result = Raise:
    Raise.raise(new RuntimeException("Something went wrong"))
    "This will never be reached"

  println(result) // This will never be printed

@main def testRaiseFold(): Unit =

  val result = Raise.fold {
    Raise.raise("Something went wrong")
    42 // This will never be reached
  } { error =>
    s"Error: $error"
  } { success =>
    s"Success: $success"
  }

  println(result) // Output: Error: Something went wrong