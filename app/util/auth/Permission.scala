package util.auth

sealed trait Permission
case object Administrator extends Permission
case object NormalUser extends Permission
