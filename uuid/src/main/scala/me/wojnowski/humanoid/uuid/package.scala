package me.wojnowski.humanoid

import java.util.UUID

package object uuid extends UuidIdConverter with UuidIdGenerator {
  type HumanUuid[P <: String] = HumanId[P, UUID]
}
