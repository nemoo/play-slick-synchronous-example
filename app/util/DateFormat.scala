package util

import java.time.format.{DateTimeFormatter, FormatStyle}

object DateFormat {
//  val datePattern: String = "dd.MM.yyyy HH:mm:ss"
  val dateTimePattern: String = "dd MMM yyyy HH:mm:ss"
  val humanReadable: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)
}
