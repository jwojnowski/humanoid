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

import me.wojnowski.humanoid.HumanIdTest.FakeId
import me.wojnowski.humanoid.HumanIdTest.fakeIdErrorMessage
import me.wojnowski.humanoid.HumanIdTest.fakeIdGen
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop

class HumanIdTest extends ScalaCheckSuite {

  type HumanFakeId = HumanId["fake", FakeId]
  val HumanFakeId = HumanIdOps["fake", FakeId]

  property("Direct creation doesn't alter the original ID") {
    Prop.forAll(fakeIdGen) { rawId =>
      val humanId = HumanFakeId.fromId(rawId)

      assertEquals(humanId.id, rawId)
    }
  }

  property("HumanId.renderWithPrefix returns the original ID with the prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val humanId = HumanFakeId.fromId(rawId)

      assertEquals(humanId.renderWithPrefix, s"fake_${rawId.value}")
    }
  }

  property("HumanId.renderNoPrefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val humanId = HumanFakeId.fromId(rawId)

      assertEquals(humanId.renderNoPrefix, rawId.value)
    }
  }

  property("HumanId.parseRequirePrefix succeeds with correct prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithCorrectPrefix = s"fake_${rawId.value}"
      val humanId                = HumanFakeId.fromId(rawId)
      val result                 = HumanFakeId.parseRequirePrefix(rawIdWithCorrectPrefix)

      assertEquals(result, Right(humanId))
    }
  }

  property("HumanId.parseRequirePrefix fails with wrong prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongPrefix = s"pfx_${rawId.value}"
      val result               = HumanFakeId.parseRequirePrefix(rawIdWithWrongPrefix)

      assertEquals(result, Left(s"Unexpected prefix [pfx], expected [fake]"))
    }
  }

  property("HumanId.parseRequirePrefix fails without any prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithoutPrefix = rawId.value
      val result             = HumanFakeId.parseRequirePrefix(rawIdWithoutPrefix)

      assertEquals(result, Left("Expected prefix [fake] with exactly one underscore"))
    }
  }

  property("HumanId.parseRequirePrefix fails when ID itself is invalid") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongId = s"fake_${rawId.value.capitalize}"
      val result           = HumanFakeId.parseRequirePrefix(rawIdWithWrongId)

      assertEquals(result, Left(HumanIdTest.fakeIdErrorMessage))
    }
  }

  // ---

  property("HumanId.parsePrefixOptional succeeds with correct prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithCorrectPrefix = s"fake_${rawId.value}"
      val humanId                = HumanFakeId.fromId(rawId)
      val result                 = HumanFakeId.parsePrefixOptional(rawIdWithCorrectPrefix)

      assertEquals(result, Right(humanId))
    }
  }

  property("HumanId.parsePrefixOptional fails with wrong prefix only when the ID itself is invalid") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongPrefix = s"pfx_${rawId.value}"
      val result               = HumanFakeId.parsePrefixOptional(rawIdWithWrongPrefix)

      assertEquals(result, Left(fakeIdErrorMessage))
    }
  }

  property("HumanId.parsePrefixOptional doesn't fail without any prefix") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithoutPrefix = rawId.value
      val humanId            = HumanFakeId.fromId(rawId)
      val result             = HumanFakeId.parsePrefixOptional(rawIdWithoutPrefix)

      assertEquals(result, Right(humanId))
    }
  }

  property("HumanId.parsePrefixOptional fails when ID itself is invalid (with prefix)") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongId = s"fake_${rawId.value.capitalize}"
      val result           = HumanFakeId.parsePrefixOptional(rawIdWithWrongId)

      assertEquals(result, Left(HumanIdTest.fakeIdErrorMessage))
    }
  }

  property("HumanId.parsePrefixOptional fails when ID itself is invalid (without prefix)") {
    Prop.forAll(fakeIdGen) { rawId =>
      val rawIdWithWrongId = rawId.value.capitalize
      val result           = HumanFakeId.parsePrefixOptional(rawIdWithWrongId)

      assertEquals(result, Left(HumanIdTest.fakeIdErrorMessage))
    }
  }
}

object HumanIdTest {
  case class FakeId(value: String)

  val fakeIdErrorMessage = "Invalid fake ID!"

  implicit val fakeIdConverter: IdConverter[FakeId] =
    IdConverter.instance[FakeId](_.value) { string =>
      Either.cond(string.headOption.exists(_.isLower) && string.forall(_.isLetterOrDigit), FakeId(string), fakeIdErrorMessage)
    }

  val fakeIdGen: Gen[FakeId] = Gen.identifier.map(FakeId.apply)
}
