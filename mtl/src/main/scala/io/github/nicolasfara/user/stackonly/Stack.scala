package io.github.nicolasfara.user.stackonly

import cats.data.EitherT
import cats.data.StateT
import cats.effect.IO

import java.util.UUID

type Email = String
type Username = String
type UserDb = Map[UUID, User]
type Base[A] = StateT[IO, UserDb, A]
type AppStack[A] = EitherT[Base, String, A]
