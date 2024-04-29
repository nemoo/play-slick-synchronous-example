package controllers

import play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import play.silhouette.api.util.Credentials
import play.silhouette.api.{LoginEvent, LoginInfo, LogoutEvent, Silhouette}
import play.silhouette.impl.exceptions.IdentityNotFoundException
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import util.auth.{AuthEnv, AuthenticatorServiceImpl, UserService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


class Auth @Inject() (
                       val controllerComponents: ControllerComponents,
                       authenticator: AuthenticatorServiceImpl,
                       userService: UserService,
                       config: util.Config,
                       val silhouette: Silhouette[AuthEnv])(
  val ex: ExecutionContext)
extends BaseController {

  val logger: Logger = Logger(this.getClass)

  def signin: Action[AnyContent] = silhouette.UserAwareAction { implicit request: UserAwareRequest[AuthEnv, AnyContent] =>
    if (request.identity.isDefined)
      Redirect(routes.Application.listProjects)
    else
      Ok(views.html.signin(config = config))
  }

  def credentialsUnapply(credentials: Credentials): Option[(String, String)] = Some((credentials.identifier, credentials.password))

  val signInForm: Form[Credentials] = Form(mapping(
    "login" -> nonEmptyText,
    "password" -> nonEmptyText
  )(Credentials.apply)(credentialsUnapply))

  def authenticate: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
//    Redirect(controllers.routes.Application.listProjects())
    signInForm.bindFromRequest().fold(
      //      formWithErrors => Future.successful(Redirect(controllers.routes.Auth.signin().toString)),
      formWithErrors => Future.successful(Ok("Error")),
      formData => {
        val Credentials(identifier, password) = formData
        val entryUri = request.session.get("ENTRY_URI")
        val targetUri: String = entryUri.getOrElse(routes.Application.listProjects.toString)
        logger.info(s"targetUri: $targetUri")
        authenticator.authenticate(identifier, password).flatMap { _ =>
          val loginInfo = LoginInfo(providerID = "????", providerKey = identifier)
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              logger.info(s"found user $user")
              for {
                authenticator <- silhouette.env.authenticatorService.create(loginInfo)
                cookie <- silhouette.env.authenticatorService.init(authenticator)
                result <- silhouette.env.authenticatorService.embed(cookie, Redirect(targetUri).withSession(request.session - "ENTRY_URI"))
              } yield {
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                result
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case _ => Redirect(routes.Auth.signin)
        }
      }
    )
  }

  def signout: Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    val result = Redirect(routes.Auth.signin)
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
