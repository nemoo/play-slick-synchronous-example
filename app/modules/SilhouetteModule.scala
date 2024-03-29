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
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import com.typesafe.config.Config
import play.api.mvc.{Cookie, CookieHeaderEncoding}
import util.auth.{AuthEnv, User, UserService}
import util.errorhandler.SecurityErrorHandler

import scala.concurrent.ExecutionContext.Implicits.global



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

    implicit val sameSiteReader: ValueReader[Option[Option[Cookie.SameSite]]] =
      (config: Config, path: String) =>
        if (config.hasPathOrNull(path))
          if (config.getIsNull(path)) Some(None)
          else Some(Cookie.SameSite.parse(config.getString(path)))
        else None

    val config: CookieAuthenticatorSettings = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    val authenticatorEncoder: CrypterAuthenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(config, None, signer, cookieHeaderEncoding, authenticatorEncoder, fingerprintGenerator, idGenerator, clock)
  }


  /**
    * Provides the cookie signer for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The cookie signer for the authenticator.
    */
  @Provides
  def provideAuthenticatorCookieSigner(configuration: Configuration): JcaSigner = {
    val config: JcaSignerSettings = configuration.underlying.as[JcaSignerSettings]("silhouette.authenticator.signer")
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
    val config: JcaCrypterSettings = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }
}
