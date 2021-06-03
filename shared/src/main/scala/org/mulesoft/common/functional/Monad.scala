package org.mulesoft.common.functional
import scala.language.higherKinds

trait Monad[F[_]] {
  def pure[A](value: A): F[A]
  def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
  def map[A, B](value: F[A])(func: A => B): F[B] = flatMap(value)(a => pure(func(a)))
}

object MonadInstances {
  type Identity[A] = A

  implicit val optionMonad: Monad[Option] = new Monad[Option] {
    override def pure[A](value: A): Option[A]                                     = Some(value)
    override def flatMap[A, B](value: Option[A])(func: A => Option[B]): Option[B] = value.flatMap(func)
  }

  implicit val identityMonad: Monad[Identity] = new Monad[Identity] {
    override def pure[A](value: A): Identity[A]                                         = value
    override def flatMap[A, B](value: Identity[A])(func: A => Identity[B]): Identity[B] = func(value)
  }

  implicit val seqMonad: Monad[Seq] = new Monad[Seq] {
    override def pure[A](value: A): Seq[A] = Seq(value)
    override def flatMap[A, B](value: Seq[A])(func: A => Seq[B]): Seq[B] = value.flatMap(func)
  }
}
