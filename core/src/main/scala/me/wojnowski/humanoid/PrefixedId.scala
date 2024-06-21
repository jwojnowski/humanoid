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

package me.wojnowski.humanoid

import cats.Functor
import cats.implicits.toFunctorOps

final case class PrefixedId[P <: String, Id](id: Id)(implicit valueOfPrefix: ValueOf[P], idable: IdConverter[Id]) {
  def renderWithPrefix: String = s"${valueOfPrefix.value}_$renderNoPrefix"

  def renderNoPrefix: String = IdConverter[Id].renderString(id)

  override def toString: String = renderWithPrefix
}

case object PrefixedId {
  def parseRequirePrefix[P <: String]: ParseRequirePrefixPartiallyApplied[P] = new ParseRequirePrefixPartiallyApplied[P]

  def fromId[P <: String]: FromIdPartiallyApplied[P] = new FromIdPartiallyApplied[P]

  final private[PrefixedId] class FromIdPartiallyApplied[P <: String](private val dummy: Boolean = true) extends AnyVal {
    def apply[Id](id: Id)(implicit valueOfPrefix: ValueOf[P], idable: IdConverter[Id]): PrefixedId[P, Id] = PrefixedId[P, Id](id)
  }

  def parsePrefixOptional[P <: String, Id](
    rawString: String
  )(implicit
    valueOfPrefix: ValueOf[P],
    idable: IdConverter[Id]
  ): Either[String, PrefixedId[P, Id]] =
    idable.fromString(rawString.stripPrefix(valueOfPrefix.value + "_")).map(id => PrefixedId[P, Id](id))

  final private[PrefixedId] class ParseRequirePrefixPartiallyApplied[P <: String](private val dummy: Boolean = true) extends AnyVal {

    def apply[Id](rawString: String)(implicit idable: IdConverter[Id], valueOfPrefix: ValueOf[P]): Either[String, PrefixedId[P, Id]] =
      rawString.split('_') match {
        case Array(prefix, rawId) if prefix == valueOfPrefix.value =>
          IdConverter[Id].fromString(rawId).map(id => PrefixedId[P, Id](id))
        case Array(prefix, _)                                      =>
          Left(s"Unexpected prefix [$prefix], expected [${valueOfPrefix.value}]")
        case _                                                     =>
          Left(s"Expected prefix [${valueOfPrefix.value}] with exactly one underscore")
      }

  }

}

class PrefixedIdOps[P <: String: ValueOf, Id: IdConverter] {
  def parseRequirePrefix(rawString: String): Either[String, PrefixedId[P, Id]] = PrefixedId.parseRequirePrefix[P][Id](rawString)

  def parsePrefixOptional(rawString: String): Either[String, PrefixedId[P, Id]] = PrefixedId.parsePrefixOptional[P, Id](rawString)

  def fromId(id: Id): PrefixedId[P, Id] = PrefixedId.fromId[P](id)

  def random[F[_]: Functor](implicit idGenerator: IdGenerator[F, Id]): F[PrefixedId[P, Id]] =
    idGenerator.generate.map(id => PrefixedId[P, Id](id))
}

object PrefixedIdOps {
  def apply[P <: String: ValueOf, Id: IdConverter]: PrefixedIdOps[P, Id] =
    new PrefixedIdOps[P, Id]
}
