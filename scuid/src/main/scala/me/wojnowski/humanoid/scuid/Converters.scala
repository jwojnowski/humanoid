/*
 * Copyright (c) 2023 Jakub Wojnowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.wojnowski.humanoid.scuid

import cats.implicits.toBifunctorOps
import me.wojnowski.humanoid.IdConverter
import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Custom
import me.wojnowski.scuid.Cuid2Long

trait Converters {

  implicit def cuid2Converter: IdConverter[Cuid2] = new IdConverter[Cuid2] {
    override def renderString(id: Cuid2): String = id.render

    override def fromString(rawString: String): Either[String, Cuid2] =
      Cuid2.validate(rawString).leftMap(_.prettyMessage)
  }

  implicit val cuid2LongConverter: IdConverter[Cuid2Long] = new IdConverter[Cuid2Long] {
    override def renderString(id: Cuid2Long): String = id.render

    override def fromString(rawString: String): Either[String, Cuid2Long] =
      Cuid2Long.validate(rawString).leftMap(_.prettyMessage)
  }

  implicit def cuid2CustomConverter[L <: Int: ValueOf]: IdConverter[Cuid2Custom[L]] = new IdConverter[Cuid2Custom[L]] {
    override def renderString(id: Cuid2Custom[L]): String = id.render

    override def fromString(rawString: String): Either[String, Cuid2Custom[L]] =
      Cuid2Custom.validate[L](rawString).leftMap(_.prettyMessage)

  }

}
