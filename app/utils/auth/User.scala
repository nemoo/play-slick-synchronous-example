package utils.auth

import com.mohiva.play.silhouette.api.Identity

case class User(
                 login: String,
                 permission: Permission
               ) extends Identity
