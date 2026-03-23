package io.github.nicolasfara

import cats.{ Monad, MonadError }
import cats.syntax.all.*
import cats.effect.IO
import cats.data.EitherT
import cats.data.StateT
import cats.effect.IOApp
import cats.data.State

trait UserStore[F[_]]:
  def getUser(id: String): F[Option[User]]
  def saveUser(user: User): F[Unit]

object UserStore:
  def apply[F[_]](using store: UserStore[F]): UserStore[F] = store

  type TestUserStore[A] = EitherT[[V] =>> State[Map[String, User], V], String, A]
  given testStore: UserStore[TestUserStore] with
    def getUser(id: String): TestUserStore[Option[User]] =
      EitherT.liftF(State.inspect(_.get(id)))
    
    def saveUser(user: User): TestUserStore[Unit] =
      EitherT.liftF(State.modify(_.updated(user.id, user)))

  type ProductionUserStore[A] = StateT[[V] =>> EitherT[IO, String, V], Map[String, User], A]
  given productionStore: UserStore[ProductionUserStore] with
    def getUser(id: String): ProductionUserStore[Option[User]] =
      StateT.inspectF:
        state => EitherT.fromEither[IO](Right(state.get(id)))

    def saveUser(user: User): ProductionUserStore[Unit] =
      StateT.modify:
        state => state.updated(user.id, user)

object UserLogic:
  def renameUser[F[_]](id: String, newName: String)(using store: UserStore[F], error: MonadError[F, String]): F[Unit] =
    for
      maybeUser <- store.getUser(id)
      user <- maybeUser match
        case Some(user) => Monad[F].pure(user)
        case None => error.raiseError(s"User with id $id not found")
      updatedUser = user.copy(name = newName)
      _ <- store.saveUser(updatedUser)
    yield ()

  def appTestEnvironment(state: Map[String, User]): Unit =
    import UserStore.testStore
    val (newState, result) = renameUser("1", "Bob").value.run(state).value
    result match
      case Right(_) =>
        println("User renamed successfully! New state: " + newState)
      case Left(error) =>
        println(s"Error: $error. State remains unchanged: " + state)

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
