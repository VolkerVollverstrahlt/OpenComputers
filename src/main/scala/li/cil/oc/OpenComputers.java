package li.cil.oc;

import li.cil.oc.common.IMC;
import li.cil.oc.common.Proxy;
import li.cil.oc.common.init.Blocks;
import li.cil.oc.common.init.Items;
import li.cil.oc.integration.Mods;
import li.cil.oc.util.ThreadPoolFactory$;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.scorge.lang.ScorgeModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.Optional;

public class OpenComputers {
    // scala object part
    public static final String ID = "opencomputers";

    public static final String Name = "OpenComputers";

    public static final String McVersion = "@MCVERSION@-forge";

    public static final String Version = "@VERSION@";

    public static Logger log() {
        return LogManager.getLogger(Name);
    }

    private static Proxy _proxy = null;
    public static Proxy proxy() {
        if (_proxy == null) {
            try {
                Class<?> cls;
                if (Environment.get().getDist() == Dist.CLIENT)
                    cls = Class.forName("li.cil.oc.client.Proxy");
                else
                    cls = Class.forName("li.cil.oc.common.Proxy");

                _proxy = (Proxy)cls.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return _proxy;
    }

    public static SimpleChannel channel = null;

    private static Optional<OpenComputers> instance = Optional.empty();
    public static OpenComputers get() {
        if (instance.isPresent()) {
            return instance.get();
        }
        throw new IllegalStateException("not initialized");
    }

    // scala class part
    public ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
    public OpenComputers() {
        ScorgeModLoadingContext.get().getModEventBus().register(this);
        OpenComputers.instance = Optional.of(this);

        MinecraftForge.EVENT_BUS.register(OpenComputers.proxy());
        ScorgeModLoadingContext.get().getModEventBus().register(OpenComputers.proxy());
        Settings.load(FMLPaths.CONFIGDIR.get().resolve(Paths.get("opencomputers", "settings.conf")).toFile());
        OpenComputers.proxy().preInit();
        MinecraftForge.EVENT_BUS.register(ThreadPoolFactory$.MODULE$);
        Mods.preInit(); // Must happen after loading Settings but before registry events are fired.
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        Blocks.init();
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        Items.init();
    }

    @SubscribeEvent
    public void imc(InterModProcessEvent e) {
        // Technically requires synchronization because IMC.sendTo doesn't check the loading stage.
        e.enqueueWork(() -> InterModComms.getMessages(OpenComputers.ID).sequential().iterator().forEachRemaining(IMC::handleMessage));
    }
}
