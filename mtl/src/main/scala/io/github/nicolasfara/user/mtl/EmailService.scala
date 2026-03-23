package io.github.nicolasfara.user.mtl

import cats.effect.IO
import cats.effect.LiftIO

trait EmailService[F[_]]:
  def sendEmail(to: String, subject: String, body: String): F[Unit]

object EmailService:
  def apply[F[_]](using emailService: EmailService[F]): EmailService[F] = emailService

  given [F[_]: LiftIO]: EmailService[F] with
    def sendEmail(to: String, subject: String, body: String): F[Unit] =
      LiftIO[F].liftIO(
        IO.println(s"Sending email to $to with subject '$subject' and body '$body'")
      )
