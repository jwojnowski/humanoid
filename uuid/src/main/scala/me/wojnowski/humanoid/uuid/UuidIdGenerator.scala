package me.wojnowski.humanoid.uuid

import me.wojnowski.humanoid.IdGenerator
import cats.effect.std.UUIDGen

import java.util.UUID

trait UuidIdGenerator {

  implicit def uuidIdGenerator[F[_]: UUIDGen]: IdGenerator[F, UUID] = new IdGenerator[F, UUID] {
    override def generate: F[UUID] = UUIDGen[F].randomUUID
  }

}
