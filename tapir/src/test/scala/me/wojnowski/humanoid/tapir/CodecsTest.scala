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

import me.wojnowski.humanoid.LowercaseStringId
import me.wojnowski.humanoid.PrefixedId
import me.wojnowski.humanoid.PrefixedIdOps
import munit.FunSuite
import sttp.tapir.Codec
import sttp.tapir.CodecFormat
import sttp.tapir.DecodeResult

class CodecsTest extends FunSuite {
  type PrefixId = PrefixedId["pfx", LowercaseStringId]
  val PrefixId = PrefixedIdOps["pfx", LowercaseStringId]

  val rawId: LowercaseStringId           = LowercaseStringId.fromString("idvalue").toOption.get
  val prefixId: PrefixId                 = PrefixId.fromId(rawId)
  val rawPrefixedIdWithPrefix: String    = "pfx_idvalue"
  val rawPrefixedIdWithoutPrefix: String = "idvalue"

  test("strict encodes with prefix") {
    import me.wojnowski.humanoid.tapir.strict._

    val result   = implicitly[Codec[String, PrefixId, CodecFormat.TextPlain]].encode(prefixId)
    val expected = rawPrefixedIdWithPrefix

    assertEquals(result, expected)
  }

  test("relaxed encodes with prefix") {
    import me.wojnowski.humanoid.tapir.relaxed._

    val result   = implicitly[Codec[String, PrefixId, CodecFormat.TextPlain]].encode(prefixId)
    val expected = rawPrefixedIdWithPrefix

    assertEquals(result, expected)
  }

  test("strict decodes prefixed ID") {
    import me.wojnowski.humanoid.tapir.strict._

    val result   = implicitly[Codec[String, PrefixId, CodecFormat.TextPlain]].decode(rawPrefixedIdWithPrefix)
    val expected = DecodeResult.Value(prefixId)

    assertEquals(result, expected)
  }

  test("strict decoding fails on no prefix") {
    import me.wojnowski.humanoid.tapir.strict._

    val result = implicitly[Codec[String, PrefixId, CodecFormat.TextPlain]].decode(rawPrefixedIdWithoutPrefix)

    assert(result.isInstanceOf[DecodeResult.Failure])
  }

  test("relaxed decodes prefixed ID") {
    import me.wojnowski.humanoid.tapir.relaxed._

    val result   = implicitly[Codec[String, PrefixId, CodecFormat.TextPlain]].decode(rawPrefixedIdWithPrefix)
    val expected = DecodeResult.Value(prefixId)

    assertEquals(result, expected)
  }

  test("relaxed decodes no prefix") {
    import me.wojnowski.humanoid.tapir.relaxed._

    val result   = implicitly[Codec[String, PrefixId, CodecFormat.TextPlain]].decode(rawPrefixedIdWithoutPrefix)
    val expected = DecodeResult.Value(prefixId)

    assertEquals(result, expected)
  }

  test("relaxed decodes wrong prefix if ID is valid") {
    import me.wojnowski.humanoid.tapir.relaxed._

    val result   = implicitly[Codec[String, PrefixedId["pfx", String], CodecFormat.TextPlain]].decode("other_idvalue")
    val expected = DecodeResult.Value(PrefixedIdOps["pfx", String].fromId("other_idvalue"))

    assertEquals(result, expected)
  }
}
