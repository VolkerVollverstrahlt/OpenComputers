package li.cil.oc.common;


import com.google.common.base.Strings;

import li.cil.oc.*;
import li.cil.oc.common.capabilities.Capabilities;
import li.cil.oc.common.container.ContainerTypes;
import li.cil.oc.common.entity.EntityTypes;
import li.cil.oc.common.init.Items$;
import li.cil.oc.common.nanomachines.Nanomachines$;
import li.cil.oc.common.tileentity.TileEntityTypes;
import li.cil.oc.common.recipe.RecipeSerializers;
import li.cil.oc.integration.Mods;
import li.cil.oc.server.driver.Registry$;
import li.cil.oc.server.fs.FileSystem$;
import li.cil.oc.server.loot.LootFunctions;
import li.cil.oc.server.machine.Machine$;
import li.cil.oc.server.machine.luac.*;
import li.cil.oc.server.machine.luaj.LuaJLuaArchitecture;
import li.cil.oc.server.network.Network$;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.scorge.lang.ScorgeModLoadingContext;

import java.util.HashMap;
import java.util.Map;

public class Proxy {
    protected final net.minecraftforge.eventbus.api.IEventBus modBus = ScorgeModLoadingContext.get().getModEventBus();

    public Proxy() {
        modBus.register(ContainerTypes.class);
        modBus.register(EntityTypes.class);
        modBus.register(TileEntityTypes.class);
        modBus.register(RecipeSerializers.class);
        LootFunctions.init();
    }

    public void preInit() {
        OpenComputers$.MODULE$.log().info("Initializing OpenComputers API.");
        li.cil.oc.api.CreativeTab.instance = CreativeTab.tab;
        li.cil.oc.api.API.driver = Registry$.MODULE$;
        li.cil.oc.api.API.fileSystem = FileSystem$.MODULE$;
        li.cil.oc.api.API.items = Items$.MODULE$;
        li.cil.oc.api.API.machine = Machine$.MODULE$;
        li.cil.oc.api.API.nanomachines = Nanomachines$.MODULE$;
        li.cil.oc.api.API.network = Network$.MODULE$;
        li.cil.oc.api.API.config = Settings.get().getConfig();
        if (LuaStateFactory$.MODULE$.isAvailable()) {
            if (LuaStateFactory$.MODULE$.include53()) {
                li.cil.oc.api.Machine.add(NativeLua53Architecture.class);
            }
            if (LuaStateFactory.include54()) {
                li.cil.oc.api.Machine.add(NativeLua54Architecture.class);
            }
            if (LuaStateFactory.include52()) {
                li.cil.oc.api.Machine.add(NativeLua52Architecture.class);
            }
        }
        if (LuaStateFactory.includeLuaJ()) {
            li.cil.oc.api.Machine.add(LuaJLuaArchitecture.class);
        }
        li.cil.oc.api.Machine.LuaArchitecture = Settings.get().forceLuaJ ? LuaJLuaArchitecture.class : li.cil.oc.api.Machine.architectures().iterator().next();
    }

    @SubscribeEvent
    public void init(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            OpenComputers$.MODULE$.channel_$eq(NetworkRegistry.newSimpleChannel(new ResourceLocation(OpenComputers$.MODULE$.ID(), "net_main"), () -> "", ""::equals, ""::equals));
            OpenComputers$.MODULE$.channel().registerMessage(0, byte[].class,
                    (msg, buff) -> buff.writeByteArray(msg), PacketBuffer::readByteArray,
                    (msg, ctx) -> {
                        NetworkEvent.Context context = ctx.get();
                        context.enqueueWork(() -> PacketHandler$.MODULE$.handlePacket(context.getDirection(), msg, context.getSender()));
                        context.setPacketHandled(true);
                    });
            PacketHandler$.MODULE$.serverHandler_$eq(li.cil.oc.server.PacketHandler$.MODULE$);;
            Loot.init();
            Achievement.init();
            OpenComputers$.MODULE$.log().debug("Initializing mod integration.");
            Mods.init();
            OpenComputers$.MODULE$.log().info("Initializing capabilities.");
            Capabilities.init();
            li.cil.oc.api.API.isPowerEnabled = !Settings.get().ignorePower;
        });
    }

    @SubscribeEvent
    public void postInit(FMLLoadCompleteEvent e) {
        // Don't allow driver registration after this point, to avoid issues.
        Registry$.MODULE$.locked_$eq(true);
    }

    public void registerModel(Item instance, String id) {
    }

    public void registerModel(Block instance, String id) {
    }

    private final Map<String, String> blockRenames = new HashMap<String, String>() {{
            put(OpenComputers$.MODULE$.ID() + ":serverRack", Constants.BlockName$.MODULE$.Rack());
        }};

    private final Map<String, String> itemRenames = new HashMap<String, String>() {{
        put(OpenComputers$.MODULE$.ID() + ":dataCard", Constants.ItemName$.MODULE$.DataCardTier1());
        put(OpenComputers$.MODULE$.ID() + ":serverRack", Constants.BlockName$.MODULE$.Rack());
        put(OpenComputers$.MODULE$.ID() + ":wlanCard", Constants.ItemName$.MODULE$.WirelessNetworkCardTier2());
    }};

    @SubscribeEvent
    public void missingBlockMappings(MissingMappings<Block> e) {
        for (MissingMappings.Mapping<Block> missing : e.getMappings(OpenComputers$.MODULE$.ID())) {
            String name = blockRenames.get(missing.key.getPath());
            if (name != null) {
                if (Strings.isNullOrEmpty(name)) {
                    missing.ignore();
                } else {
                    missing.remap(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(OpenComputers$.MODULE$.ID(), name)));
                }
            } else {
                missing.warn();
            }
        }
    }

    @SubscribeEvent
    public void missingItemMappings(MissingMappings<Item> e) {
        for (MissingMappings.Mapping<Item> missing : e.getMappings(OpenComputers$.MODULE$.ID())) {
            String name = itemRenames.get(missing.key.getPath());
            if (name != null) {
                if (Strings.isNullOrEmpty(name)) {
                    missing.ignore();
                } else {
                    missing.remap(ForgeRegistries.ITEMS.getValue(new ResourceLocation(OpenComputers$.MODULE$.ID(), name)));
                }
            } else {
                missing.warn();
            }
        }
    }
}


