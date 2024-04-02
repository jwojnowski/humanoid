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

import cats.effect.std.UUIDGen
import me.wojnowski.humanoid.HumanId
import me.wojnowski.humanoid.HumanIdOps
import me.wojnowski.humanoid.uuid.*
import munit.FunSuite

import java.util.UUID
import scala.util.Success
import scala.util.Try

class UuidGeneratorTest extends FunSuite {
  type CustomerId = HumanId["cus", UUID]
  val CustomerId = HumanIdOps["cus", UUID]

  val uuid1 = UUID.fromString("076831d6-41a0-4161-9217-fc04ac8fa5a2")

  implicit val constTryUuidGen: UUIDGen[Try] = new UUIDGen[Try] {
    override def randomUUID: Try[UUID] = Success(uuid1)
  }

  test("Uses UUIDGen") {

    CustomerId.random[Try].map { uuid =>
      assertEquals(uuid.id, uuid1)
    }
  }
}
