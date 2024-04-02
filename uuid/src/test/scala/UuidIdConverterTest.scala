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

import me.wojnowski.humanoid.HumanId
import me.wojnowski.humanoid.HumanIdOps
import munit.FunSuite

import java.util.UUID

class UuidIdConverterTest extends FunSuite {
  type CustomerId = HumanId["cus", UUID]
  val CustomerId = HumanIdOps["cus", UUID]

  val rawUuid1 = "076831d6-41a0-4161-9217-fc04ac8fa5a2"
  val uuid1    = UUID.fromString(rawUuid1)

  test("parsing correct UUID") {
    val result = HumanId.parsePrefixOptional["cus", UUID](rawUuid1)

    assertEquals(result, Right(HumanId.fromId["cus"](uuid1)))
  }

  test("parsing invalid UUID") {
    val result = HumanId.parsePrefixOptional["cus", UUID]("this-is-not-a-uuid")

    assert(result.isLeft)
  }

  test("rendering") {
    val result = HumanId.fromId["cus"](uuid1).renderWithPrefix

    assertEquals(result, s"cus_$rawUuid1")
  }
}
