package modules

import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasher, PasswordHasherRegistry}
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, SocialProviderRegistry}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import controllers.{DashboardController, LoginController, SignUpController}
import daos.api.UserDao
import daos.mongo.{PasswordInfoDaoMongoImpl, UserDaoMongoImpl}
import play.api.i18n.{DefaultLangs, DefaultMessagesApi, Langs, MessagesApi}
import play.api.{BuiltInComponents, Configuration}
import play.modules.reactivemongo.{DefaultReactiveMongoApi, ReactiveMongoApi}
import services.UserServiceImpl
import services.api.UserService
import silhouette.environments.DefaultEnv

import scala.concurrent.duration.{Duration, FiniteDuration}

trait BuiltInComponentModule extends BuiltInComponents {
  import play.api.libs.concurrent.Execution.Implicits._
  import com.softwaremill.macwire.wire

  def configuration: Configuration
  lazy val langs = wire[DefaultLangs]
  lazy val messagesApi = wire[DefaultMessagesApi]

  // ReactiveMongo dependencies
  lazy val reactiveMongo: ReactiveMongoApi = wire[DefaultReactiveMongoApi]

  // DAO dependencies
  lazy val userDao: UserDao = wire[UserDaoMongoImpl]

  // Services dependencies
  lazy val userService: UserService = wire[UserServiceImpl]

  // Silhouette dependencies
  lazy val clock = wire[Clock]
  lazy val authenticatorDecoder = wire[Base64AuthenticatorEncoder]
  lazy val idGenerator = new SecureRandomIDGenerator()
  lazy val eventBus = wire[EventBus]

  lazy val authenticatorService: AuthenticatorService[JWTAuthenticator] = {
    val duration = configuration.underlying.getString("silhouette.jwt.authenticator.authenticatorExpiry")
    val expiration = Duration.apply(duration).asInstanceOf[FiniteDuration]
    val config = new JWTAuthenticatorSettings(fieldName = configuration.underlying.getString("silhouette.jwt.authenticator.headerName"),
                                              issuerClaim = configuration.underlying.getString("silhouette.jwt.authenticator.issuerClaim"),
                                              authenticatorExpiry = expiration,
                                              sharedSecret = configuration.underlying.getString("silhouette.jwt.authenticator.sharedSecret"))
    new JWTAuthenticatorService(config, None, authenticatorDecoder, idGenerator, clock)
  }

  private lazy val env: Environment[DefaultEnv] = Environment[DefaultEnv](
    userService, authenticatorService, List(), eventBus
  )

  lazy val securedErrorHandler: SecuredErrorHandler = wire[DefaultSecuredErrorHandler]
  lazy val unSecuredErrorHandler: UnsecuredErrorHandler = wire[DefaultUnsecuredErrorHandler]

  lazy val securedRequestHandler : SecuredRequestHandler = wire[DefaultSecuredRequestHandler]
  lazy val unsecuredRequestHandler : UnsecuredRequestHandler = wire[DefaultUnsecuredRequestHandler]
  lazy val userAwareRequestHandler : UserAwareRequestHandler = wire[DefaultUserAwareRequestHandler]

  lazy val securedAction: SecuredAction = wire[DefaultSecuredAction]
  lazy val unsecuredAction: UnsecuredAction = wire[DefaultUnsecuredAction]
  lazy val userAwareAction: UserAwareAction = wire[DefaultUserAwareAction]

  lazy val passwordDao = new PasswordInfoDaoMongoImpl(reactiveMongo, actorSystem)
  lazy val authInfoRepository = new DelegableAuthInfoRepository(passwordDao.delegableDao)
  lazy val bCryptPasswordHasher: PasswordHasher = new BCryptPasswordHasher
  lazy val passwordHasherRegistry: PasswordHasherRegistry = new PasswordHasherRegistry(bCryptPasswordHasher)

  lazy val credentialsProvider = new CredentialsProvider(authInfoRepository, passwordHasherRegistry)

  lazy val socialProviderRegistry = new SocialProviderRegistry(List())

  lazy val silhouetteDefaultEnv : Silhouette[DefaultEnv] = wire[SilhouetteProvider[DefaultEnv]]



  // Controllers dependencies
  lazy val signUpController = wire[SignUpController]
  lazy val loginController = wire[LoginController]
  lazy val dashboardController = wire[DashboardController]


}
