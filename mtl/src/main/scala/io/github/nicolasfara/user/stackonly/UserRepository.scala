package io.github.nicolasfara.user.stackonly

import cats.data.EitherT
import cats.data.StateT

import java.util.UUID

object UserRepository:
  def get(id: UUID): AppStack[Option[User]] =
    EitherT.liftF(StateT.inspect(_.get(id)))

  def save(user: User): AppStack[Unit] =
    EitherT.liftF(StateT.modify(_.updated(user.id, user)))

  def changeUserEmail(id: UUID, newEmail: Email): AppStack[Unit] =
    EitherT.liftF(
      StateT.modify:
        state =>
          state.get(id) match
            case Some(user) => state.updated(id, user.copy(email = newEmail))
            case None => state
    )
