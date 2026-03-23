package io.github.nicolasfara

import cats.{ Monad, MonadError }
import cats.syntax.all.*
import cats.effect.IO
import cats.data.EitherT
import cats.data.StateT
import cats.effect.IOApp

trait UserStore[F[_]]:
  def getUser(id: String): F[Option[User]]
  def saveUser(user: User): F[Unit]

object UserStore:
  def apply[F[_]](using store: UserStore[F]): UserStore[F] = store

  type TestUserStore[A] = StateT[[V] =>> Either[String, V], Map[String, User], A]
  given testStore: UserStore[TestUserStore] with
    def getUser(id: String): TestUserStore[Option[User]] = StateT.inspect: state =>
      state.get(id)
    
    def saveUser(user: User): TestUserStore[Unit] = StateT.modify: state =>
      state.updated(user.id, user)

  type ProductionUserStore[A] = StateT[[V] =>> EitherT[IO, String, V], Map[String, User], A]
  given productionStore: UserStore[ProductionUserStore] with
    def getUser(id: String): ProductionUserStore[Option[User]] =
      StateT.inspectF:
        state => EitherT.fromEither[IO](Right(state.get(id)))

    def saveUser(user: User): ProductionUserStore[Unit] =
      StateT.modify:
        state => state.updated(user.id, user)

object UserLogic:
  def renameUser[F[_]](id: String, newName: String)(using store: UserStore[F], raiseError: MonadError[F, String]): F[Unit] =
    for
      maybeUser <- store.getUser(id)
      user <- maybeUser match
        case Some(user) => Monad[F].pure(user)
        case None => MonadError[F, String].raiseError(s"User with id $id not found")
      updatedUser = user.copy(name = newName)
      _ <- store.saveUser(updatedUser)
    yield ()

  def appTestEnvironment(state: Map[String, User]): Unit =
    import UserStore.testStore
    renameUser("1", "Bob").run(state) match
      case Right(newState) => println("User renamed successfully! New state: " + newState)
      case Left(error) => println(s"Error: $error")

  def appProductionEnvironment(state: Map[String, User]): IO[Unit] =
    import UserStore.productionStore

    renameUser("1", "Bob").run(state).value.flatMap:
      case Right(newState) =>
        IO.println("User renamed successfully! New state: " + newState)
      case Left(error) =>
        IO.println(s"Error: $error")

@main def runUserLogic(): Unit =
  import UserLogic.*

  appTestEnvironment(Map("1" -> User("1", "Alice")))

object RunUserIO extends IOApp.Simple:
  def run: IO[Unit] =
    import io.github.nicolasfara.UserLogic.appProductionEnvironment
    val initialState = Map("1" -> User("1", "Alice"))
    appProductionEnvironment(initialState)
