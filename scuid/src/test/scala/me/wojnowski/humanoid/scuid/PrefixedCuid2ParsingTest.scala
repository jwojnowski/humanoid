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

import munit.CatsEffectSuite

class PrefixedCuid2ParsingTest extends CatsEffectSuite {
  type UserId = PrefixedCuid2["user"]
  val UserId: PrefixedCuid2Ops["user"] = PrefixedCuid2Ops["user"]

  type AccountId = PrefixedCuid2Long["acc"]
  val AccountId: PrefixedCuid2LongOps["acc"] = PrefixedCuid2LongOps["acc"]

  type CategoryId = PrefixedCuid2Custom["cat", 10]
  val CategoryId: PrefixedCuid2CustomOps["cat", 10] = PrefixedCuid2CustomOps["cat", 10]

  val rawUserIdWithoutPrefix  = "xk3qfe06254vkcqg48tl4j5f"
  val rawUserIdWithPrefix     = s"user_$rawUserIdWithoutPrefix"
  val invalidUserIdWithPrefix = "user_---------------------"

  val rawAccountIdWithoutPrefix  = "h5jthr6swm9q3jf8a6wnriehe74eqxer"
  val rawAccountIdWithPrefix     = s"acc_$rawAccountIdWithoutPrefix"
  val invalidAccountIdWithPrefix = "acc_---------------------"

  val rawCategoryIdWithoutPrefix  = "k64if3ouy9"
  val rawCategoryIdWithPrefix     = s"cat_$rawCategoryIdWithoutPrefix"
  val invalidCategoryIdWithPrefix = "cat_---------------------"

  test("HumanCuid2 - parsing (prefix required)") {
    assertEquals(UserId.parseRequirePrefix(rawUserIdWithPrefix).map(_.renderWithPrefix), Right(rawUserIdWithPrefix))
    assert(UserId.parseRequirePrefix(rawUserIdWithoutPrefix).isLeft)
  }

  test("HumanCuid2 - parsing (optional prefix)") {
    assertEquals(UserId.parsePrefixOptional(rawUserIdWithPrefix).map(_.renderWithPrefix), Right(rawUserIdWithPrefix))
    assertEquals(UserId.parsePrefixOptional(rawUserIdWithoutPrefix).map(_.renderWithPrefix), Right(rawUserIdWithPrefix))
  }

  test("HumanCuid2 - parsing invalid ID") {
    assert(UserId.parseRequirePrefix(invalidUserIdWithPrefix).isLeft)
    assert(UserId.parsePrefixOptional(invalidUserIdWithPrefix).isLeft)
  }

  test("HumanCuid2Long - parsing (prefix required)") {
    assertEquals(AccountId.parseRequirePrefix(rawAccountIdWithPrefix).map(_.renderWithPrefix), Right(rawAccountIdWithPrefix))
    assert(AccountId.parseRequirePrefix(rawAccountIdWithoutPrefix).isLeft)
  }

  test("HumanCuid2Long - parsing (optional prefix)") {
    assertEquals(AccountId.parsePrefixOptional(rawAccountIdWithPrefix).map(_.renderWithPrefix), Right(rawAccountIdWithPrefix))
    assertEquals(AccountId.parsePrefixOptional(rawAccountIdWithoutPrefix).map(_.renderWithPrefix), Right(rawAccountIdWithPrefix))
  }

  test("HumanCuid2Long - parsing invalid ID") {
    assert(AccountId.parseRequirePrefix(invalidAccountIdWithPrefix).isLeft)
    assert(AccountId.parsePrefixOptional(invalidAccountIdWithPrefix).isLeft)
  }

  test("HumanCuid2Custom - parsing (prefix required)") {
    assertEquals(CategoryId.parseRequirePrefix(rawCategoryIdWithPrefix).map(_.renderWithPrefix), Right(rawCategoryIdWithPrefix))
    assert(CategoryId.parseRequirePrefix(rawCategoryIdWithoutPrefix).isLeft)
  }

  test("HumanCuid2Custom - parsing (optional prefix)") {
    assertEquals(CategoryId.parsePrefixOptional(rawCategoryIdWithPrefix).map(_.renderWithPrefix), Right(rawCategoryIdWithPrefix))
    assertEquals(CategoryId.parsePrefixOptional(rawCategoryIdWithoutPrefix).map(_.renderWithPrefix), Right(rawCategoryIdWithPrefix))
  }

  test("HumanCuid2Custom - parsing invalid ID") {
    assert(CategoryId.parseRequirePrefix(invalidCategoryIdWithPrefix).isLeft)
    assert(CategoryId.parsePrefixOptional(invalidCategoryIdWithPrefix).isLeft)
  }

}
