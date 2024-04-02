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

package me.wojnowski.humanoid.circe.relaxed

import io.circe.Decoder
import io.circe.KeyDecoder
import me.wojnowski.humanoid.HumanId
import me.wojnowski.humanoid.IdConverter

trait RelaxedDecoders {
  implicit def humanIdDecoder[P <: String, Id](implicit valueOfPrefix: ValueOf[P], idable: IdConverter[Id]): Decoder[HumanId[P, Id]] =
    Decoder.decodeString.emap(HumanId.parsePrefixOptional[P, Id])

  implicit def humanIdKeyDecoder[P <: String, Id](implicit valueOfPrefix: ValueOf[P], idable: IdConverter[Id]): KeyDecoder[HumanId[P, Id]] =
    KeyDecoder.instance(HumanId.parsePrefixOptional[P, Id](_).toOption)
}
