package io.github.nicolasfara.user.mtl

import java.util.UUID
import cats.data.*
import cats.effect.IO
import cats.effect.IOApp
import io.github.nicolasfara.user.mtl.EmailService
import io.github.nicolasfara.user.mtl.User
import io.github.nicolasfara.user.mtl.UserRepository


object UserApp extends IOApp.Simple:
  import UserLogic.signup

  type Base[A] = EitherT[IO, String, A]
  type Eff[A] = StateT[Base, Map[UUID, User], A]

  val initialUsers: Map[UUID, User] = Map.empty

  def run: IO[Unit] = signup[Eff]("Alice", "alice@bar.com")
    .run(initialUsers)
    .value
    .flatMap:
      case Right((newState, _)) =>
        IO.println("User signed up successfully! New state: " + newState)
      case Left(error) =>
        IO.println(s"Error: $error")

  // type Eff[A] = EitherT[[V] =>> StateT[IO, Map[UUID, User], V], String, A]

  // val initialUsers: Map[UUID, User] = Map.empty

  // def run: IO[Unit] = signup[Eff]("Alice", "alice@bar.com")
  //   .value
  //   .run(initialUsers)
  //   .flatMap:
  //     case (newState, Right(_)) =>
  //       IO.println("User signed up successfully! New state: " + newState)
  //     case (_, Left(error)) =>
  //       IO.println(s"Error: $error")

  
