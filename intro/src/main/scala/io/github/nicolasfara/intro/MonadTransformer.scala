package io.github.nicolasfara.intro

trait MonadTransformer[T[_[_], _]]:
  def lift[M[_]: Monad, A](fa: M[A]): T[M, A]

object MonadTransformer:
  extension [M[_]: Monad, A](fa: M[A])
    def lift[T[_[_], _]](using mt: MonadTransformer[T]): T[M, A] = mt.lift(fa)

