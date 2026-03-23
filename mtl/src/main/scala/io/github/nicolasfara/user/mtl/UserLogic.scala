package io.github.nicolasfara.user.mtl

import cats.Monad
import java.util.UUID
import cats.syntax.all.*

object UserLogic:
  type Email = String
  type Usename = String
  // type BlackList = Set[Email]

  def signup[F[_]: Monad](name: Usename, email: Email)(using
      repo: UserRepository[F],
      emailService: EmailService[F],
      // raiseError: Raise[F, String],
  ): F[Unit] =
    for
      userId <- UUID.randomUUID().pure[F]
      user <- User(userId, name, email).pure[F]
      _ <- repo.save(user)
      // _ <- raiseError.raise("Email already exists") // Simulate an error for demonstration
      _ <- emailService.sendEmail(email, "Welcome!", s"Hello $name, welcome to our service!")
    yield ()
