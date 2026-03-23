package io.github.nicolasfara.user

import cats.Monad
import cats.data.EitherT
import cats.data.StateT
import cats.effect.IO

trait EmailService[F[_]]:
  def sendEmail(to: String, subject: String, body: String): F[Unit]

object EmailService:
  given consoleEmailService: EmailService[IO] with
    def sendEmail(to: String, subject: String, body: String): IO[Unit] =
      IO.println(s"Sending email to $to with subject '$subject' and body '$body'")

  given [F[_]: Monad, E](using emailService: EmailService[F]): EmailService[[A] =>> EitherT[F, E, A]] with
    def sendEmail(to: String, subject: String, body: String): EitherT[F, E, Unit] =
      EitherT.liftF(emailService.sendEmail(to, subject, body))

  given [F[_]: Monad, S](using emailService: EmailService[F]): EmailService[[A] =>> StateT[F, S, A]] with
    def sendEmail(to: String, subject: String, body: String): StateT[F, S, Unit] =
      StateT.liftF(emailService.sendEmail(to, subject, body))
