package io.github.nicolasfara.user

import io.github.nicolasfara.either
import scala.collection.mutable

@main def runUserApp(): Unit =
  import UserLogic.signup

  val res = UserRepositoryInMemory.handle(mutable.Map.empty):
    EmailServiceHandler.handle:
      either:
        signup("Alice", "alice@example.com")

  println(res) // Output: Left(Email already exists)
