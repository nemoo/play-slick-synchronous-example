package utils.auth

import play.api.Logger

import javax.inject.Inject
import scala.concurrent.Future


trait AuthenticatorService {
  def authenticate(user: String, password: String): Future[Unit]
}

@javax.inject.Singleton
class AuthenticatorServiceImpl @Inject() extends AuthenticatorService{

  val logger = Logger(this.getClass())

  override def authenticate(user: String, password: String): Future[Unit] ={

    logger.debug(s"authenticated $user")
    Future.successful(())
  }

}