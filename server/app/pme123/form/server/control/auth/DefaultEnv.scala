package pme123.form.server.control.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import pme123.form.server.entity.AuthUser

trait DefaultEnv extends Env {
  type I = AuthUser
  type A = CookieAuthenticator
}