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

package me.wojnowski.humanoid.circe

import io.circe.parser.decode
import io.circe.syntax._
import me.wojnowski.humanoid.HumanId
import me.wojnowski.humanoid.HumanIdOps
import me.wojnowski.humanoid.LowercaseStringId
import munit.FunSuite

class CodecTest extends FunSuite {

  val rawId: LowercaseStringId                   = LowercaseStringId.fromString("idvalue").toOption.get
  val humanId: HumanId["pfx", LowercaseStringId] = HumanIdOps["pfx", LowercaseStringId].fromId(rawId)
  val humanIdJsonWithPrefix: String              = "\"pfx_idvalue\""
  val humanIdJsonWithoutPrefix: String           = "\"idvalue\""

  test("encoder encodes with prefix") {
    import me.wojnowski.humanoid.circe.strict._

    val result = humanId.asJson.noSpaces

    assertEquals(result, humanIdJsonWithPrefix)
  }

  test("relaxed decoder accepts ID without prefix") {
    import me.wojnowski.humanoid.circe.relaxed._

    val result = decode[HumanId["pfx", LowercaseStringId]](humanIdJsonWithoutPrefix)

    assertEquals(result, Right(humanId))
  }

  test("relaxed decoder accepts ID with wrong prefix if it parses as valid ID") {
    import me.wojnowski.humanoid.circe.relaxed._

    val result   = decode[HumanId["other", String]](humanIdJsonWithPrefix)
    val expected = Right(HumanIdOps["other", String].fromId(humanId.renderWithPrefix))

    assertEquals(result, expected)
  }

  test("strict decoder accepts ID with correct prefix") {
    import me.wojnowski.humanoid.circe.strict._

    val result = decode[HumanId["pfx", LowercaseStringId]](humanIdJsonWithPrefix)

    assertEquals(result, Right(humanId))
  }

  test("strict decoder rejects ID with incorrect prefix") {
    import me.wojnowski.humanoid.circe.strict._

    val result = decode[HumanId["other", String]](humanIdJsonWithPrefix)
    assert(result.isLeft)
  }

  test("strict decoder rejects ID without prefix") {
    import me.wojnowski.humanoid.circe.strict._

    val result = decode[HumanId["other", LowercaseStringId]](humanIdJsonWithoutPrefix)
    assert(result.isLeft)
  }

}
