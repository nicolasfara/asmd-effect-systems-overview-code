package io.github.nicolasfara.user.stackonly

import cats.data.EitherT
import cats.syntax.all.*

import java.util.UUID

object UserLogic:
  def signup(name: Username, email: Email): AppStack[Unit] =
    for
      userId <- UUID.randomUUID().pure[AppStack]
      user <- User(userId, name, email).pure[AppStack]
      _ <- UserRepository.save(user)
      // _ <- EitherT.leftT[Base, Unit]("Email already exists")
      _ <- EmailService.sendEmail(email, "Welcome!", s"Hello $name, welcome to our service!")
    yield ()
