package util.errorhandler

import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.Future



class SecurityErrorHandler @Inject() extends  SecuredErrorHandler with UnsecuredErrorHandler {

  val logger = Logger(this.getClass())
  
  // 401 - Unauthorized
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = Future.successful {
    logger.debug("notAuthenticated")
    Redirect(controllers.routes.Auth.signin).withSession(request.session + ("ENTRY_URI" -> request.uri))
  }

  // 403 - Forbidden
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
//    Forbidden(views.html.errors.accessDenied())
    logger.debug("notAuthorized")
    Ok("You are not authorized to view this page")
  }

}