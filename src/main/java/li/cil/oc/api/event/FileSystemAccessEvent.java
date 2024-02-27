package li.cil.oc.api.event;

import li.cil.oc.api.network.Node;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Events for handling file system access and representing it on the client.
 * <br>
 * This is used to play file system access sounds and render disk activity
 * indicators on some containers (e.g. disk drive, computer, server).
 * <br>
 * Use this to implement rendering of disk access indicators on you own
 * containers / computers / drive bays.
 * <br>
 * Canceling this event is provided to allow registering higher priority
 * event handlers that override default behavior.
 */
@Cancelable
public class FileSystemAccessEvent extends Event {
    protected String sound;

    protected Level level;

    protected double x;

    protected double y;

    protected double z;

    protected BlockEntity blockEntity;

    protected CompoundTag data;

    /**
     * Constructor for tile entity hosted file systems.
     *
     * @param sound      the name of the sound effect to play.
     * @param blockEntity the tile entity hosting the file system.
     * @param data       the additional data.
     */
    protected FileSystemAccessEvent(String sound, BlockEntity blockEntity, CompoundTag data) {
        this.sound = sound;
        this.level = blockEntity.getLevel();
        this.x = blockEntity.getBlockPos().getX() + 0.5;
        this.y = blockEntity.getBlockPos().getY() + 0.5;
        this.z = blockEntity.getBlockPos().getZ() + 0.5;
        this.blockEntity = blockEntity;
        this.data = data;
    }

    /**
     * Constructor for arbitrarily hosted file systems.
     *
     * @param sound the name of the sound effect to play.
     * @param level the level the file system lives in.
     * @param x     the x coordinate of the file system's container.
     * @param y     the y coordinate of the file system's container.
     * @param z     the z coordinate of the file system's container.
     * @param data  the additional data.
     */
    protected FileSystemAccessEvent(String sound, Level level, double x, double y, double z, CompoundTag data) {
        this.sound = sound;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockEntity = null;
        this.data = data;
    }

    /**
     * The name of the sound effect to play for the file system.
     */
    public String getSound() {
        return sound;
    }

    /**
     * The level the file system lives in.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * The x coordinate of the file system's container.
     */
    public double getX() {
        return x;
    }

    /**
     * The y coordinate of the file system's container.
     */
    public double getY() {
        return y;
    }

    /**
     * The z coordinate of the file system's container.
     */
    public double getZ() {
        return z;
    }

    /**
     * The tile entity hosting the file system.
     * <br>
     * <em>Important</em>: this can be <tt>null</tt>, which is usually the
     * case when the container is an entity or item.
     */
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * Addition custom data, this is used to transmit the number of the server
     * in a server rack the file system lives in, for example.
     */
    public CompoundTag getData() {
        return data;
    }

    public static final class Server extends FileSystemAccessEvent {
        private Node node;

        public Server(String sound, BlockEntity tileEntity, Node node) {
            super(sound, tileEntity, new CompoundTag());
            this.node = node;
        }

        public Server(String sound, Level world, double x, double y, double z, Node node) {
            super(sound, world, x, y, z, new CompoundTag());
            this.node = node;
        }

        /**
         * The node of the file system that signalled activity.
         */
        public Node getNode() {
            return node;
        }
    }

    public static final class Client extends FileSystemAccessEvent {
        /**
         * Constructor for tile entity hosted file systems.
         *
         * @param sound      the name of the sound effect to play.
         * @param tileEntity the tile entity hosting the file system.
         * @param data       the additional data.
         */
        public Client(String sound, BlockEntity tileEntity, CompoundTag data) {
            super(sound, tileEntity, data);
        }

        /**
         * Constructor for arbitrarily hosted file systems.
         *
         * @param sound the name of the sound effect to play.
         * @param world the level the file system lives in.
         * @param x     the x coordinate of the file system's container.
         * @param y     the y coordinate of the file system's container.
         * @param z     the z coordinate of the file system's container.
         * @param data  the additional data.
         */
        public Client(String sound, Level world, double x, double y, double z, CompoundTag data) {
            super(sound, world, x, y, z, data);
        }
    }
}
