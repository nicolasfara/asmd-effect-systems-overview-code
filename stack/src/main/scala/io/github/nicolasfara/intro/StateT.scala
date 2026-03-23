package io.github.nicolasfara.intro

final case class StateT[M[_], S, A](value: S => M[(A, S)])

object StateT:
  import Monad.*

  given [M[_]: Monad as m, S]: Monad[[A] =>> StateT[M, S, A]] with
    def pure[A](a: A): StateT[M, S, A] = StateT(s => m.pure((a, s)))

    def flatMap[A, B](fa: StateT[M, S, A])(f: A => StateT[M, S, B]): StateT[M, S, B] =
      StateT: s =>
        fa.value(s).flatMap: (a, nextState) =>
          f(a).value(nextState)

  given [S]: MonadTransformer[[M[_], A] =>> StateT[M, S, A]] with
    def lift[M[_]: Monad, A](fa: M[A]): StateT[M, S, A] =
      StateT(s => fa.map(a => (a, s)))

  extension [M[_]: Monad, S, A](stateT: StateT[M, S, A])
    def eval(initialState: S): M[A] =
      stateT.value(initialState).map((a, _) => a)

    def exec(initialState: S): M[S] =
      stateT.value(initialState).map((_, s) => s)

  def get[M[_]: Monad as m, S]: StateT[M, S, S] =
    StateT(s => m.pure((s, s)))

  def set[M[_]: Monad as m, S](newState: S): StateT[M, S, Unit] =
    StateT(_ => m.pure(((), newState)))

  def modify[M[_]: Monad, S](f: S => S): StateT[M, S, Unit] =
    for
      state <- get[M, S]
      _ <- set[M, S](f(state))
    yield ()

  def inspect[M[_]: Monad, S, A](f: S => A): StateT[M, S, A] =
    get[M, S].map(f)

