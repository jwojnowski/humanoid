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
