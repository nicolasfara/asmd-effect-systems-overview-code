package io.github.nicolasfara.user

import java.util.UUID
import scala.collection.mutable

trait UserRepository:
  def get(id: UUID): Option[User]
  def save(user: User): Unit
  def changeEmail(id: UUID, newEmail: String): Unit

object UserRepository:
  def get(id: UUID)(using repo: UserRepository): Option[User] = repo.get(id)
  def save(user: User)(using repo: UserRepository): Unit = repo.save(user)
  def changeEmail(id: UUID, newEmail: String)(using repo: UserRepository): Unit = repo.changeEmail(id, newEmail)

object UserRepositoryInMemory:
  def handle[A](state: mutable.Map[UUID, User])(program: UserRepository ?=> A): A =
    given UserRepository with
      def get(id: UUID): Option[User] = state.get(id)
      def save(user: User): Unit = state.update(user.id, user)
      def changeEmail(id: UUID, newEmail: String): Unit =
        state.get(id).foreach(user => state.update(id, user.copy(email = newEmail))) 
    program
