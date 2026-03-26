package io.github.nicolasfara

import scala.util.boundary.{ Label, break }
import scala.util.boundary

type CanMiss[A] = Label[None.type]

object optional:
  def apply[A](program: Label[None.type] ?=> A): Option[A] =
    boundary(Some(program))

  def none[A](using Label[None.type]): Nothing = break(None)

  extension [A](opt: Option[A])
    def ?(using Label[None.type]): A = opt match
      case Some(value) => value
      case None => break(None)
    
@main def optionalExample(): Unit =
  val result = optional:
    val x = 10
    if x > 5 then optional.none
    else x * 2
  println(result) // Output: None

@main def questionMarkOptionalExample() =
  import optional.*

  def directStyleApi(x: Int)(using CanMiss[String]): Int =
    if x > 5 then none
    else x * 2

  val result = optional:
    val r = directStyleApi(2)
    println(s"Result from direct style API: $r")
  println(result) // Output: Some(4)
