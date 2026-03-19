package io.github.nicolasfara

import cats.MonadError
import cats.syntax.all.*
import cats.data.*
import cats.mtl.Stateful

object Example:
  def decrementStateBoilerplate: EitherT[[V] =>> StateT[List, Int, V], Exception, String] = for {
    currentState <- EitherT.liftF(StateT.get[List, Int])
    result <- if (currentState < 0) then
      EitherT.leftT[[V] =>> StateT[List, Int, V], String](new Exception("State cannot be decremented below zero"))
    else
      EitherT.liftF(StateT.set[List, Int](currentState - 1)).as("State decremented successfully!")
  } yield result

  def decrementState[F[_]](using Stateful[F, Int], MonadError[F, Exception]): F[String] =
    for
      currentState <- Stateful.get
      result <- if (currentState > 0) then
        Stateful.set(currentState - 1) *> "State decremented successfully!".pure
      else
        MonadError[F, Exception].raiseError(new Exception("State cannot be decremented below zero"))
    yield result
