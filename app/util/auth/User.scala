package util.auth

import play.silhouette.api.Identity

case class User(
                 login: String,
                 permission: Permission
               ) extends Identity
