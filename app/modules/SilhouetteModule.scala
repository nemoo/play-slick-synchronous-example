package modules

import com.google.inject.{AbstractModule, Provides}
import play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import play.silhouette.api.crypto._
import play.silhouette.api.services.{AuthenticatorService, IdentityService}
import play.silhouette.api.util._
import play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import play.silhouette.impl.authenticators.{CookieAuthenticator, _}
import play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import play.silhouette.password.BCryptPasswordHasher
import play.silhouette.persistence.daos.{DelegableAuthInfoDAO, InMemoryAuthInfoDAO}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.mvc.{Cookie, CookieHeaderEncoding}
import util.auth.{AuthEnv, User, UserService}
import util.errorhandler.SecurityErrorHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.Try

/**
  * Guice bindings for silhouette cookie authentication that is used
  * in the composer GUI for humans
  */
class SilhouetteModule extends AbstractModule with ScalaModule{

  override def configure(): Unit = {
    bind[Silhouette[AuthEnv]].to[SilhouetteProvider[AuthEnv]]
    bind[SecuredErrorHandler].to[SecurityErrorHandler]
    bind[UnsecuredErrorHandler].to[SecurityErrorHandler]
    bind[IdentityService[User]].to[UserService]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())

    bind[DelegableAuthInfoDAO[PasswordInfo]].toInstance(new InMemoryAuthInfoDAO[PasswordInfo])
  }

  @Provides
  def provideEnvironment(
                          authenticatorService: AuthenticatorService[CookieAuthenticator],
                          eventBus: EventBus,
                          userService: UserService
                        ): Environment[AuthEnv] = {
    Environment[AuthEnv](userService, authenticatorService, Seq.empty, eventBus)
  }

  /**
    * Provides the authenticator service.
    *
    * @param signer The signer implementation.
    * @param crypter The crypter implementation.
    * @param cookieHeaderEncoding Logic for encoding and decoding `Cookie` and `Set-Cookie` headers.
    * @param fingerprintGenerator The fingerprint generator implementation.
    * @param idGenerator The ID generator implementation.
    * @param configuration The Play configuration.
    * @param clock The clock instance.
    * @return The authenticator service.
    */
  @Provides
  def provideAuthenticatorService(
                                   signer: JcaSigner,
                                   crypter: JcaCrypter,
                                   cookieHeaderEncoding: CookieHeaderEncoding,
                                   fingerprintGenerator: FingerprintGenerator,
                                   idGenerator: IDGenerator,
                                   configuration: Configuration,
                                   clock: Clock): AuthenticatorService[CookieAuthenticator] = {

    val silhouetteConfig = configuration.underlying.getConfig("silhouette.authenticator")

    val cookieAuthSettings = CookieAuthenticatorSettings(
      cookieName = "id",
      cookiePath = "/",
      cookieDomain = None,
      secureCookie = silhouetteConfig.getBoolean("secureCookie"),
      httpOnlyCookie = silhouetteConfig.getBoolean("httpOnlyCookie"),
      sameSite = if (silhouetteConfig.hasPath("sameSite")) Cookie.SameSite.parse(silhouetteConfig.getString("sameSite")) else Some(Cookie.SameSite.Lax),
      useFingerprinting = true,
      cookieMaxAge = if (silhouetteConfig.hasPath("cookieMaxAge")) Some(Duration(silhouetteConfig.getString("cookieMaxAge")).asInstanceOf[FiniteDuration]) else None,
      authenticatorIdleTimeout = if (silhouetteConfig.hasPath("authenticatorIdleTimeout")) Some(Duration(silhouetteConfig.getString("authenticatorIdleTimeout")).asInstanceOf[FiniteDuration]) else None,
      authenticatorExpiry = Duration(silhouetteConfig.getString("authenticatorExpiry")).asInstanceOf[FiniteDuration]
    )

    val authenticatorEncoder: CrypterAuthenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(
      settings = cookieAuthSettings,
      repository = None,
      signer = signer,
      cookieHeaderEncoding = cookieHeaderEncoding,
      authenticatorEncoder = authenticatorEncoder,
      fingerprintGenerator = fingerprintGenerator,
      idGenerator = idGenerator,
      clock = clock)
  }


  /**
    * Provides the cookie signer for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The cookie signer for the authenticator.
    */
  @Provides
  def provideAuthenticatorCookieSigner(configuration: Configuration): JcaSigner = {
    val config: JcaSignerSettings = JcaSignerSettings(configuration.underlying.getString("silhouette.authenticator.signer.key"))
    new JcaSigner(config)
  }

  /**
    * Provides the crypter for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The crypter for the authenticator.
    */
  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): JcaCrypter = {
    val config: JcaCrypterSettings = JcaCrypterSettings(configuration.underlying.getString("silhouette.authenticator.crypter.key"))
    new JcaCrypter(config)
  }
}
