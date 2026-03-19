package io.github.nicolasfara.intro

type Identity[A] = A

object Identity:

  given Monad[Identity] with
    def pure[A](a: A): Identity[A] = a
    def flatMap[A, B](fa: Identity[A])(f: A => Identity[B]): Identity[B] = f(fa)
