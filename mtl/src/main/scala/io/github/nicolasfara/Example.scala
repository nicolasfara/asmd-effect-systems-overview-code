package io.github.nicolasfara

import cats.MonadError
import cats.syntax.all.*
import cats.data.*
import cats.mtl.Stateful
import cats.Id

object Example:
  def decrementStateBoilerplate: EitherT[[V] =>> StateT[Id, Int, V], Exception, String] = for
    currentState <- EitherT.liftF(StateT.get[Id, Int])
    result <- if (currentState < 0) then
      EitherT.leftT[[V] =>> StateT[Id, Int, V], String](new Exception("State cannot be decremented below zero"))
    else
      EitherT.liftF(StateT.set[Id, Int](currentState - 1)).as("State decremented successfully!")
  yield result

  def decrementState[F[_]](using Stateful[F, Int], MonadError[F, Exception]): F[String] =
    for
      currentState <- Stateful.get
      result <- if (currentState > 0) then
        Stateful.set(currentState - 1) *> "State decremented successfully!".pure
      else
        MonadError[F, Exception].raiseError(new Exception("State cannot be decremented below zero"))
    yield result
