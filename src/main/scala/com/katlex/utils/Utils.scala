package com.katlex
package utils

import java.util.Date

object Unapply {

  import scala.{BigDecimal => BD}

  object BigDecimal {
    def unapply(s:String):Option[BD] = {
      try {
        Some(BD(s))
      }
      catch {
        case _: Throwable => None
      }
    }
  }

  object Date {
    def unapply(s:String) = {
      try {
        Some(new Date(BD(s).toLong * 1000))
      }
      catch {
        case _: Throwable => None
      }
    }
  }
}

