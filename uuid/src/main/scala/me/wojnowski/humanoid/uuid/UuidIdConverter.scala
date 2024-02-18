package me.wojnowski.humanoid.uuid

import me.wojnowski.humanoid.IdConverter

import java.util.UUID
import scala.util.Try

trait UuidIdConverter {

  implicit val uuidIdConverter: IdConverter[UUID] = new IdConverter[UUID] {
    override def renderString(id: UUID): String = id.toString

    override def fromString(rawString: String): Either[String, UUID] =
      Try(UUID.fromString(rawString)).toEither.left.map(t => s"Invalid UUID: ${t.getMessage}")

  }

}
