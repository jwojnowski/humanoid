package me.wojnowski.humanoid.uuid

import me.wojnowski.humanoid.HumanIdOps

import java.util.UUID

class HumanUuidOps[P <: String: ValueOf] extends HumanIdOps[P, UUID]

object HumanUuidOps {
  def apply[P <: String: ValueOf]: HumanUuidOps[P] = new HumanUuidOps[P]
}
