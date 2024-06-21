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

import cats.effect.IO
import me.wojnowski.scuid.Cuid2Gen
import munit.CatsEffectSuite

// TODO property-based?
class PrefixedCuid2GenerationTest extends CatsEffectSuite {
  implicit val constCuid2Gen: Cuid2Gen[IO] = Cuid2Gen.custom[IO]("machine", () => IO.pure(0.5), () => IO.pure(1L), () => IO.pure(0L))

  type UserId = PrefixedCuid2["user"]
  val UserId: PrefixedCuid2Ops["user"] = PrefixedCuid2Ops["user"]

  type AccountId = PrefixedCuid2Long["acc"]
  val AccountId: PrefixedCuid2LongOps["acc"] = PrefixedCuid2LongOps["acc"]

  type CategoryId = PrefixedCuid2Custom["cat", 27]
  val CategoryId: PrefixedCuid2CustomOps["cat", 27] = PrefixedCuid2CustomOps["cat", 27]

  val rawUserIdWithoutPrefix = "niv8z1jigmjr5bp8c6xbqfl6"
  val rawUserIdWithPrefix    = s"user_$rawUserIdWithoutPrefix"

  val rawAccountIdWithoutPrefix = "nfzf4fv7xuvgduf8g2t8dp96slk8znpp"
  val rawAccountIdWithPrefix    = s"acc_$rawAccountIdWithoutPrefix"

  val rawCategoryIdWithoutPrefix = "npxwkjj4x028n0qycc25cxa98h3"
  val rawCategoryIdWithPrefix    = s"cat_$rawCategoryIdWithoutPrefix"

  test("HumanCuid2") {
    UserId.random[IO].map { (userId: UserId) =>
      assertEquals(userId, UserId.parseRequirePrefix(rawUserIdWithPrefix).toOption.get)
    }
  }

  test("HumanCuid2Long") {
    AccountId.random[IO].map { (accountId: AccountId) =>
      assertEquals(accountId, AccountId.parseRequirePrefix(rawAccountIdWithPrefix).toOption.get)
    }
  }

  test("HumanCuid2Custom") {
    CategoryId.random[IO].map { (categoryId: CategoryId) =>
      assertEquals(categoryId, CategoryId.parseRequirePrefix(rawCategoryIdWithPrefix).toOption.get)
    }
  }
}
