package li.cil.oc.server.computer

import com.naef.jnlua.LuaRuntimeException
import li.cil.oc.OpenComputers
import li.cil.oc.api.{IItemDriver, IBlockDriver}
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import scala.Some
import scala.collection.mutable.ArrayBuffer
import scala.compat.Platform._

/**
 * This class keeps track of registered drivers and provides installation logic
 * for each registered driver.
 *
 * Each component type must register its driver with this class to be used with
 * computers, since this class is used to determine whether an object is a
 * valid component or not.
 *
 * All drivers must be installed once the game starts - in the init phase - and
 * are then injected into all computers started up past that point. A driver is
 * a set of functions made available to the computer. These functions will
 * usually require a component of the type the driver wraps to be installed in
 * the computer, but may also provide context-free functions.
 */
private[oc] object Drivers {
  /** The list of registered block drivers. */
  private val blocks = ArrayBuffer.empty[IBlockDriver]

  /** The list of registered item drivers. */
  private val items = ArrayBuffer.empty[IItemDriver]

  /** Used to keep track of whether we're past the init phase. */
  var locked = false

  /**
   * Registers a new driver for a block component.
   *
   * Whenever the neighboring blocks of a computer change, it checks if there
   * exists a driver for the changed block, and if so installs it.
   *
   * @param driver the driver for that block type.
   */
  def add(driver: IBlockDriver) {
    if (locked) throw new IllegalStateException("Please register all drivers in the init phase.")
    if (!blocks.contains(driver)) blocks += driver
  }

  /**
   * Registers a new driver for an item component.
   *
   * Item components can inserted into a computers component slots. They have
   * to specify their type, to determine into which slots they can fit.
   *
   * @param driver the driver for that item type.
   */
  def add(driver: IItemDriver) {
    if (locked) throw new IllegalStateException("Please register all drivers in the init phase.")
    if (!blocks.contains(driver)) items += driver
  }

  /**
   * Used when a new block is placed next to a computer to see if we have a
   * driver for it. If we have one, we'll return it.
   *
   * @param world the world in which the block to check lives.
   * @param x the X coordinate of the block to check.
   * @param y the Y coordinate of the block to check.
   * @param z the Z coordinate of the block to check.
   * @return the driver for that block if we have one.
   */
  def driverFor(world: World, x: Int, y: Int, z: Int) =
    blocks.find(_.worksWith(world, x, y, z)) match {
      case None => None
      case Some(driver) => Some(driver)
    }

  /**
   * Used when an item component is added to a computer to see if we have a
   * driver for it. If we have one, we'll return it.
   *
   * @param item the type of item to check for a driver for.
   * @return the driver for that item type if we have one.
   */
  def driverFor(item: ItemStack) =
    if (item != null) items.find(_.worksWith(item)) match {
      case None => None
      case Some(driver) => Some(driver)
    }
    else None

  /**
   * Used by the computer to initialize its Lua state, injecting the APIs of
   * all known drivers.
   */
  private[computer] def installOn(computer: Computer) =
    (blocks ++ items).foreach(driver => {
      driver.api match {
        case None => // Nothing to do.
        case Some(code) =>
          val name = driver.getClass.getName
          try {
            computer.lua.load(code, "=" + name, "t") // ... func
            code.close()
            computer.lua.call(0, 0) // ...
          }
          catch {
            case e: LuaRuntimeException =>
              OpenComputers.log.warning(String.format(
                "Initialization code of driver %s threw an error: %s",
                name, e.getLuaStackTrace.mkString("", EOL, EOL)))
            case e: Throwable =>
              OpenComputers.log.warning(String.format(
                "Initialization code of driver %s threw an error: %s",
                name, e.getStackTraceString))
          }
      }
    })
}
