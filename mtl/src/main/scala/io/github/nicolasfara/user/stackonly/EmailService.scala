package io.github.nicolasfara.user.stackonly

import cats.data.EitherT
import cats.data.StateT
import cats.effect.IO

object EmailService:
  def sendEmail(to: Email, subject: String, body: String): AppStack[Unit] =
    EitherT.liftF(
      StateT.liftF(IO.println(s"Sending email to $to with subject '$subject' and body '$body'"))
    )
