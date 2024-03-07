package util.auth

import play.silhouette.api.Env
import play.silhouette.impl.authenticators.CookieAuthenticator

trait AuthEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}
