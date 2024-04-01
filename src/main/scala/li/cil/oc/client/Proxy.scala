package li.cil.oc.client

import com.mojang.blaze3d.pipeline.RenderCall
import com.mojang.blaze3d.systems.RenderSystem
import li.cil.oc.OpenComputers
import li.cil.oc.api
import li.cil.oc.client
import li.cil.oc.client.gui.GuiTypes
import li.cil.oc.client.renderer.HighlightRenderer
import li.cil.oc.client.renderer.MFUTargetRenderer
import li.cil.oc.client.renderer.PetRenderer
import li.cil.oc.client.renderer.TextBufferRenderCache
import li.cil.oc.client.renderer.WirelessNetworkDebugRenderer
import li.cil.oc.client.renderer.block.ModelInitialization
import li.cil.oc.client.renderer.block.NetSplitterModel
import li.cil.oc.client.renderer.entity.DroneRenderer
import li.cil.oc.client.renderer.tileentity._
import li.cil.oc.common
import li.cil.oc.common.{PacketHandler => CommonPacketHandler}
import li.cil.oc.common.{Proxy => CommonProxy}
import li.cil.oc.common.component.TextBuffer
import li.cil.oc.common.entity.Drone
import li.cil.oc.common.entity.EntityTypes
import li.cil.oc.common.event.NanomachinesHandler
import li.cil.oc.common.event.RackMountableRenderHandler
import li.cil.oc.common.tileentity
import li.cil.oc.util.Audio
import net.minecraft.world.level.block.Block
import net.minecraft.client.renderer.entity.{EntityRenderer, EntityRenderDispatcher}
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraftforge.client.ClientRegistry
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.network.NetworkRegistry

private[oc] class Proxy extends CommonProxy {
  modBus.register(classOf[GuiTypes])
  modBus.register(ModelInitialization)
  modBus.register(NetSplitterModel)
  modBus.register(Textures)

  override def preInit() {
    super.preInit()

    api.API.manual = client.Manual
  }
  override def init(e: FMLCommonSetupEvent) {
    super.init(e)

    CommonPacketHandler.clientHandler = PacketHandler

    e.enqueueWork((() => {
      ModelInitialization.preInit()

      ColorHandler.init()

      RegisterRenderers.registerEntityRenderer(EntityTypes.DRONE, new EntityRendererProvider[Drone] {
        override def createRenderFor(manager: EntityRenderDispatcher): EntityRenderer[_ >: Drone] = new DroneRenderer(manager)
      })

      BlockEntityRenderers.register(tileentity.TileEntityTypes.ADAPTER, AdapterRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.ASSEMBLER, AssemblerRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.CASE, CaseRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.CHARGER, ChargerRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.DISASSEMBLER, DisassemblerRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.DISK_DRIVE, DiskDriveRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.GEOLYZER, GeolyzerRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.HOLOGRAM, HologramRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.MICROCONTROLLER, MicrocontrollerRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.NET_SPLITTER, NetSplitterRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.POWER_DISTRIBUTOR, PowerDistributorRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.PRINTER, PrinterRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.RAID, RaidRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.RACK, RackRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.RELAY, RelayRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.ROBOT, RobotRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.SCREEN, ScreenRenderer)
      BlockEntityRenderers.register(tileentity.TileEntityTypes.TRANSPOSER, TransposerRenderer)

      ClientRegistry.registerKeyBinding(KeyBindings.extendedTooltip)
      ClientRegistry.registerKeyBinding(KeyBindings.analyzeCopyAddr)
      ClientRegistry.registerKeyBinding(KeyBindings.clipboardPaste)

      MinecraftForge.EVENT_BUS.register(HighlightRenderer)
      MinecraftForge.EVENT_BUS.register(NanomachinesHandler.Client)
      MinecraftForge.EVENT_BUS.register(PetRenderer)
      MinecraftForge.EVENT_BUS.register(RackMountableRenderHandler)
      MinecraftForge.EVENT_BUS.register(Sound)
      MinecraftForge.EVENT_BUS.register(TextBuffer)
      MinecraftForge.EVENT_BUS.register(MFUTargetRenderer)
      MinecraftForge.EVENT_BUS.register(WirelessNetworkDebugRenderer)
      MinecraftForge.EVENT_BUS.register(Audio)
      MinecraftForge.EVENT_BUS.register(HologramRenderer)
    }): Runnable)

    RenderSystem.recordRenderCall(() => MinecraftForge.EVENT_BUS.register(TextBufferRenderCache))
  }

  override def registerModel(instance: Item, id: String): Unit = ModelInitialization.registerModel(instance, id)

  override def registerModel(instance: Block, id: String): Unit = ModelInitialization.registerModel(instance, id)
}
