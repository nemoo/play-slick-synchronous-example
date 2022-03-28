package util.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

trait AuthEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}
