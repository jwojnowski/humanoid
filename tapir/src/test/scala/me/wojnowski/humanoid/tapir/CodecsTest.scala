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

package me.wojnowski.humanoid.tapir

import me.wojnowski.humanoid.HumanId
import me.wojnowski.humanoid.HumanIdOps
import me.wojnowski.humanoid.LowercaseStringId
import munit.FunSuite
import sttp.tapir.Codec
import sttp.tapir.CodecFormat
import sttp.tapir.DecodeResult

class CodecsTest extends FunSuite {
  type PrefixedId = HumanId["pfx", LowercaseStringId]
  val PrefixedId = HumanIdOps["pfx", LowercaseStringId]

  val rawId: LowercaseStringId        = LowercaseStringId.fromString("idvalue").toOption.get
  val humanId: PrefixedId             = PrefixedId.fromId(rawId)
  val rawHumanIdWithPrefix: String    = "pfx_idvalue"
  val rawHumanIdWithoutPrefix: String = "idvalue"

  test("strict encodes with prefix") {
    import me.wojnowski.humanoid.tapir.strict.*

    val result   = implicitly[Codec[String, PrefixedId, CodecFormat.TextPlain]].encode(humanId)
    val expected = rawHumanIdWithPrefix

    assertEquals(result, expected)
  }

  test("relaxed encodes with prefix") {
    import me.wojnowski.humanoid.tapir.relaxed.*

    val result   = implicitly[Codec[String, PrefixedId, CodecFormat.TextPlain]].encode(humanId)
    val expected = rawHumanIdWithPrefix

    assertEquals(result, expected)
  }

  test("strict decodes prefixed ID") {
    import me.wojnowski.humanoid.tapir.strict.*

    val result   = implicitly[Codec[String, PrefixedId, CodecFormat.TextPlain]].decode(rawHumanIdWithPrefix)
    val expected = DecodeResult.Value(humanId)

    assertEquals(result, expected)
  }

  test("strict decoding fails on no prefix") {
    import me.wojnowski.humanoid.tapir.strict.*

    val result = implicitly[Codec[String, PrefixedId, CodecFormat.TextPlain]].decode(rawHumanIdWithoutPrefix)

    assert(result.isInstanceOf[DecodeResult.Failure])
  }

  test("relaxed decodes prefixed ID") {
    import me.wojnowski.humanoid.tapir.relaxed.*

    val result   = implicitly[Codec[String, PrefixedId, CodecFormat.TextPlain]].decode(rawHumanIdWithPrefix)
    val expected = DecodeResult.Value(humanId)

    assertEquals(result, expected)
  }

  test("relaxed decodes no prefix") {
    import me.wojnowski.humanoid.tapir.relaxed.*

    val result   = implicitly[Codec[String, PrefixedId, CodecFormat.TextPlain]].decode(rawHumanIdWithoutPrefix)
    val expected = DecodeResult.Value(humanId)

    assertEquals(result, expected)
  }

  test("relaxed decodes wrong prefix if ID is valid") {
    import me.wojnowski.humanoid.tapir.relaxed.*

    val result   = implicitly[Codec[String, HumanId["pfx", String], CodecFormat.TextPlain]].decode("other_idvalue")
    val expected = DecodeResult.Value(HumanIdOps["pfx", String].fromId("other_idvalue"))

    assertEquals(result, expected)
  }
}
