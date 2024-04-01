package li.cil.oc.client

import li.cil.oc.common
import net.minecraft.world.level.Level

object ComponentTracker extends common.ComponentTracker {
  override protected def clear(level: Level) = if (level.isClientSide) super.clear(level)
}
