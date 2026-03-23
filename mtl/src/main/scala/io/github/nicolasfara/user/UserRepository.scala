package io.github.nicolasfara.user

import java.util.UUID
import cats.Monad
import cats.mtl.Stateful
import cats.syntax.all.*

trait UserRepository[F[_]]:
  def get(id: UUID): F[Option[User]]
  def save(user: User): F[Unit]
  def changeUserEmail(id: UUID, newEmail: String): F[Unit]

object UserRepository:
  type UserDb = Map[UUID, User]

  given inMemoryRepository[F[_]: Monad](using state: Stateful[F, UserDb]): UserRepository[F] with
    def get(id: UUID): F[Option[User]] =
      state.get.map(_.get(id))

    def save(user: User): F[Unit] =
      state.modify(_.updated(user.id, user))

    def changeUserEmail(id: UUID, newEmail: String): F[Unit] =
      state.modify:
        state =>
          state.get(id) match
            case Some(user) => state.updated(id, user.copy(email = newEmail))
            case None => state // No change if user not found
