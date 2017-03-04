package controllers

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.{EventBus, LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import silhouette.Roles.{AdminRole, UserRole}
import silhouette.WithRole
import silhouette.environments.DefaultEnv
import models.JsonFormatUser._
import models.LoginData
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.json.Json
import services.api.UserService

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, FiniteDuration}

class LoginController (silhouette: Silhouette[DefaultEnv], eventBus: EventBus, clock: Clock, configuration: Configuration, credentialsProvider: CredentialsProvider, userService: UserService, authenticatorService: AuthenticatorService[JWTAuthenticator]) extends BaseController {

  def login = silhouette.UnsecuredAction.async(parse.json) { implicit request =>

    request.body.validate[LoginData].fold(
      errors => {
        Future.successful(BadRequest)
      },
      loginData => {


        credentialsProvider.authenticate(Credentials(loginData.email, loginData.password)).flatMap {
          loginInfo =>

            userService.retrieve(loginInfo).flatMap {
              case Some(user) => authenticatorService.create(loginInfo).map {

                case authenticator if loginData.rememberMe => {
                  val c = configuration.underlying
                  val expirationTime : DateTime = clock.now.plus(c.getLong("silhouette.jwt.authenticator.rememberMe.authenticatorExpiry"))
                  val idleTime : FiniteDuration = Duration.apply(c.getString("silhouette.jwt.authenticator.rememberMe.authenticatorIdleTimeout")).asInstanceOf[FiniteDuration]
                  authenticator.copy(
                    expirationDateTime = expirationTime,
                    idleTimeout = Some(idleTime)
                  )


                }
                case authenticator => authenticator

              }.flatMap {

                authenticator => {
                  eventBus.publish(LoginEvent(user, request))
                  authenticatorService.init(authenticator).map {
                    token =>
                      Ok(Json.obj("token" -> token))
                  }
                }
              }

              case None => {
                Future.failed(new IdentityNotFoundException("User could not be found"))
              }

            }
        }.recover {
          case e: ProviderException =>
            Unauthorized
        }
      }
    )

  }

  def logout = silhouette.SecuredAction( WithRole(AdminRole) || WithRole(UserRole) ).async { implicit request =>
    eventBus.publish(LogoutEvent(request.identity, request))
    authenticatorService.discard(request.authenticator, Ok)
  }

}
