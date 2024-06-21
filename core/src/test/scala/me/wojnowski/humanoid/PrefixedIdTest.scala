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

import me.wojnowski.humanoid.PrefixedIdTest.FakeId
import me.wojnowski.humanoid.PrefixedIdTest.fakeIdErrorMessage
import me.wojnowski.humanoid.PrefixedIdTest.fakeIdGen
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop

class PrefixedIdTest extends ScalaCheckSuite {

  type PrefixedFakeId = PrefixedId["fake", FakeId]
  val PrefixedFakeId = PrefixedIdOps["fake", FakeId]

  property("Direct creation doesn't alter the original ID") {
    Prop.forAll(fakeIdGen) { rawId =>
      val prefixedId = PrefixedFakeId.fromId(rawId)

      assertEquals(prefixedId.id, rawId)
    }
  }

  property("PrefixedId.renderWithPrefix returns the original ID with the prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val prefixedId = PrefixedFakeId.fromId(rawId)

      assertEquals(prefixedId.renderWithPrefix, s"fake_${rawId.value}")
    }
  }

  property("PrefixedId.renderNoPrefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val prefixedId = PrefixedFakeId.fromId(rawId)

      assertEquals(prefixedId.renderNoPrefix, rawId.value)
    }
  }

  property("PrefixedId.parseRequirePrefix succeeds with correct prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithCorrectPrefix = s"fake_${rawId.value}"
      val prefixedId             = PrefixedFakeId.fromId(rawId)
      val result                 = PrefixedFakeId.parseRequirePrefix(rawIdWithCorrectPrefix)

      assertEquals(result, Right(prefixedId))
    }
  }

  property("PrefixedId.parseRequirePrefix fails with wrong prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongPrefix = s"pfx_${rawId.value}"
      val result               = PrefixedFakeId.parseRequirePrefix(rawIdWithWrongPrefix)

      assertEquals(result, Left(s"Unexpected prefix [pfx], expected [fake]"))
    }
  }

  property("PrefixedId.parseRequirePrefix fails without any prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithoutPrefix = rawId.value
      val result             = PrefixedFakeId.parseRequirePrefix(rawIdWithoutPrefix)

      assertEquals(result, Left("Expected prefix [fake] with exactly one underscore"))
    }
  }

  property("PrefixedId.parseRequirePrefix fails when ID itself is invalid") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongId = s"fake_${rawId.value.capitalize}"
      val result           = PrefixedFakeId.parseRequirePrefix(rawIdWithWrongId)

      assertEquals(result, Left(PrefixedIdTest.fakeIdErrorMessage))
    }
  }

  // ---

  property("PrefixedId.parsePrefixOptional succeeds with correct prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithCorrectPrefix = s"fake_${rawId.value}"
      val prefixedId             = PrefixedFakeId.fromId(rawId)
      val result                 = PrefixedFakeId.parsePrefixOptional(rawIdWithCorrectPrefix)

      assertEquals(result, Right(prefixedId))
    }
  }

  property("PrefixedId.parsePrefixOptional fails with wrong prefix only when the ID itself is invalid") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongPrefix = s"pfx_${rawId.value}"
      val result               = PrefixedFakeId.parsePrefixOptional(rawIdWithWrongPrefix)

      assertEquals(result, Left(fakeIdErrorMessage))
    }
  }

  property("PrefixedId.parsePrefixOptional doesn't fail without any prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithoutPrefix = rawId.value
      val prefixedId         = PrefixedFakeId.fromId(rawId)
      val result             = PrefixedFakeId.parsePrefixOptional(rawIdWithoutPrefix)

      assertEquals(result, Right(prefixedId))
    }
  }

  property("PrefixedId.parsePrefixOptional fails when ID itself is invalid (with prefix)") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongId = s"fake_${rawId.value.capitalize}"
      val result           = PrefixedFakeId.parsePrefixOptional(rawIdWithWrongId)

      assertEquals(result, Left(PrefixedIdTest.fakeIdErrorMessage))
    }
  }

  property("PrefixedId.parsePrefixOptional fails when ID itself is invalid (without prefix)") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongId = rawId.value.capitalize
      val result           = PrefixedFakeId.parsePrefixOptional(rawIdWithWrongId)

      assertEquals(result, Left(PrefixedIdTest.fakeIdErrorMessage))
    }
  }
}

object PrefixedIdTest {
  case class FakeId(value: String)

  val fakeIdErrorMessage = "Invalid fake ID!"

  implicit val fakeIdConverter: IdConverter[FakeId] =
    IdConverter.instance[FakeId](_.value) { string =>
      Either.cond(string.headOption.exists(_.isLower) && string.forall(_.isLetterOrDigit), FakeId(string), fakeIdErrorMessage)
    }

  val fakeIdGen: Gen[FakeId] = Gen.identifier.map(FakeId.apply)
}
