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

  implicit val constTryUuidGen = new UUIDGen[Try] {
    override def randomUUID: Try[UUID] = Success(uuid1)
  }

  test("Uses UUIDGen") {

    CustomerId.random[Try].map { uuid =>
      assertEquals(uuid.id, uuid1)
    }
  }
}
