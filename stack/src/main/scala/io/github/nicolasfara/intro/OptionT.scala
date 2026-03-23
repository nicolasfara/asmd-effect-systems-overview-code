package io.github.nicolasfara.intro

final case class OptionT[M[_], A](value: M[Option[A]])

object OptionT:
  import Monad.*

  given [M[_]: Monad as m]: Monad[[A] =>> OptionT[M, A]] with
    def pure[A](a: A): OptionT[M, A] = OptionT(m.pure(Some(a)))

    def flatMap[A, B](fa: OptionT[M, A])(f: A => OptionT[M, B]): OptionT[M, B] =
      OptionT(
        fa.value.flatMap:
          case Some(a) => f(a).value
          case None => m.pure(None)
      )

  given MonadTransformer[OptionT] with
    def lift[M[_]: Monad, A](fa: M[A]): OptionT[M, A] = OptionT(fa.map(Some(_)))

  def fail[M[_]: Monad as m, A]: OptionT[M, A] = OptionT(m.pure(None))
