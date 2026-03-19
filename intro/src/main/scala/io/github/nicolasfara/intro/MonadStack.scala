package io.github.nicolasfara.intro

import Monad.*
import MonadTransformer.*

def failAndIO: OptionT[IO, Unit] = for
  _ <- IO.putLine("This will fail").lift[OptionT]
  _ <- OptionT.fail[IO, Unit]
  _ <- IO.putLine("This will never be printed").lift[OptionT]
yield ()

@main def runMonadStack(): Unit =
  failAndIO.runOptionT.unsafeRun() match
    case Some(_) => println("Unexpected success")
    case None => println("Expected failure")
