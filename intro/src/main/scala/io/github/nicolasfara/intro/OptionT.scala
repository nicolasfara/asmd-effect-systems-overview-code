package io.github.nicolasfara.intro

final case class OptionT[M[_], A](runOptionT: M[Option[A]])

object OptionT:
  import Monad.*

  given [M[_]: Monad]: Monad[[A] =>> OptionT[M, A]] with
    def pure[A](a: A): OptionT[M, A] = OptionT(summon[Monad[M]].pure(Some(a)))

    def flatMap[A, B](fa: OptionT[M, A])(f: A => OptionT[M, B]): OptionT[M, B] =
      val m = summon[Monad[M]]
      OptionT(
        fa.runOptionT.flatMap:
          case Some(a) => f(a).runOptionT
          case None => m.pure(None)
      )

  given MonadTransformer[OptionT] with
    def lift[M[_]: Monad, A](fa: M[A]): OptionT[M, A] = OptionT(fa.map(Some(_)))

  def fail[M[_]: Monad as m, A]: OptionT[M, A] = OptionT(m.pure(None))
