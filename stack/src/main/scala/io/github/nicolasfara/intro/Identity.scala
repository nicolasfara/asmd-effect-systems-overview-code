package io.github.nicolasfara.intro

type Identity[A] = A

object Identity:
  /** TODO: Implement the Monad instance for Identity */
  given Monad[Identity] with
    def pure[A](a: A): Identity[A] = ???
    def flatMap[A, B](fa: Identity[A])(f: A => Identity[B]): Identity[B] = ???
