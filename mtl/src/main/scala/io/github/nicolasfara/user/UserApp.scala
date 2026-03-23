package io.github.nicolasfara.user

import cats.Monad
import java.util.UUID
import cats.syntax.all.*
import cats.data.*
import cats.effect.IO
import cats.effect.IOApp
import cats.mtl.Raise

object UserLogic:
  type Email = String
  type Usename = String
  // type BlackList = Set[Email]

  def signup[F[_]: Monad](name: Usename, email: Email)(using
      repo: UserRepository[F],
      emailService: EmailService[F],
      raiseError: Raise[F, String],
  ): F[Unit] =
    for
      userId <- UUID.randomUUID().pure[F]
      user <- User(userId, name, email).pure[F]
      _ <- repo.save(user)
      _ <- raiseError.raise("Email already exists") // Simulate an error for demonstration
      _ <- emailService.sendEmail(email, "Welcome!", s"Hello $name, welcome to our service!")
    yield ()

object UserApp extends IOApp.Simple:
  // import EmailService.consoleEmailService
  // import UserRepository.inMemoryRepository
  import UserLogic.signup
  // import cats.mtl.Handle.handleForApplicativeError

  // type Base[A] = EitherT[IO, String, A]
  // type Eff[A] = StateT[Base, Map[UUID, User], A]

  // val initialUsers: Map[UUID, User] = Map.empty

  // def run: IO[Unit] = signup[Eff]("Alice", "alice@bar.com")
  //   .run(initialUsers)
  //   .value
  //   .flatMap:
  //     case Right((newState, _)) =>
  //       IO.println("User signed up successfully! New state: " + newState)
  //     case Left(error) =>
  //       IO.println(s"Error: $error")

  type Eff[A] = EitherT[[V] =>> StateT[IO, Map[UUID, User], V], String, A]

  val initialUsers: Map[UUID, User] = Map.empty

  def run: IO[Unit] = signup[Eff]("Alice", "alice@bar.com")
    .value
    .run(initialUsers)
    .flatMap:
      case (newState, Right(_)) =>
        IO.println("User signed up successfully! New state: " + newState)
      case (_, Left(error)) =>
        IO.println(s"Error: $error")

  
