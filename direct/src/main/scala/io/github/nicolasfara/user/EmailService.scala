package io.github.nicolasfara.user

trait EmailService:
  def sendEmail(to: String, subject: String, body: String): Unit

object EmailService:
  def sendEmail(to: String, subject: String, body: String)(using emailService: EmailService): Unit =
    emailService.sendEmail(to, subject, body)

object EmailServiceHandler:
  def handle[A](program: EmailService ?=> A): A =
    given EmailService with
      def sendEmail(to: String, subject: String, body: String): Unit =
        println(s"Sending email to $to with subject '$subject' and body '$body'")
    program