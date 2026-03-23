package io.github.nicolasfara.user.stackonly

import cats.effect.IO
import cats.effect.IOApp

object UserApp extends IOApp.Simple:
  val initialUsers: UserDb = Map.empty

  def run: IO[Unit] =
    UserLogic
      .signup("Alice", "alice@bar.com")
      .value
      .run(initialUsers)
      .flatMap:
        case (newState, Right(_)) =>
          IO.println("User signed up successfully! New state: " + newState)
        case (_, Left(error)) =>
          IO.println(s"Error: $error")
