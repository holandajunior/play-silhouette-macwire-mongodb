package controllers

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.{SignUpData, User}
import silhouette.environments.DefaultEnv
import models.JsonFormatUser._
import play.api.libs.json.Json
import _root_.services.api.UserService

import scala.concurrent.Future

class SignUpController(silhouette: Silhouette[DefaultEnv], userService: UserService, passwordHasher: PasswordHasher, authInfoRepository: AuthInfoRepository, authenticatorService: AuthenticatorService[JWTAuthenticator], eventBus: EventBus) extends BaseController {

  def signup = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body.validate[SignUpData].fold(
      errors => {
        Future.successful( BadRequest )
      },
      signUpData => {
        val loginInfo = new LoginInfo(CredentialsProvider.ID, signUpData.email)

        userService.retrieve(loginInfo).flatMap {
          case Some(user) => {
            Future.successful(BadRequest("User already exists"))
          }
          case None => {

            val user = new User(
              loginInfo = Some(loginInfo),
              email = Some(signUpData.email),
              firstName = Some(signUpData.firstName),
              lastName = Some(signUpData.lastName)
            )

            val passwordInfo = passwordHasher.hash(signUpData.password)

            for {
              // Save user and his infos
              user <- userService.save(user)
              authInfo <- authInfoRepository.add(loginInfo, passwordInfo)

              // Generate an JWT authenticator, returning a token to login
              authenticator <- authenticatorService.create(loginInfo)
              token <- authenticatorService.init(authenticator)

            } yield {
              // Send events
              eventBus.publish(SignUpEvent(user, request))
              eventBus.publish(LoginEvent(user, request))

              Ok(Json.obj("token" -> token))

            }

          }
        }

      }
    )

  }

}
