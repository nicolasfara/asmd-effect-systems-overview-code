package io.github.nicolasfara.user

// import io.github.nicolasfara.CanFail

object UserLogic:
  type Email = String
  type Usename = String

  def signup(name: Usename, email: Email)(using 
    UserRepository, EmailService, //CanFail[String]
  ): Unit =
    val userId = java.util.UUID.randomUUID()
    val user = User(userId, name, email)
    UserRepository.save(user)
    // fail("Email already exists") // Simulate an error for demonstration
    EmailService.sendEmail(email, "Welcome!", s"Hello $name, welcome to our service!")

