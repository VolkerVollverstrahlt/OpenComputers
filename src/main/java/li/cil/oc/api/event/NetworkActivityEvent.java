package li.cil.oc.api.event;

import li.cil.oc.api.network.Node;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Events for handling network activity and representing it on the client.
 * <br>
 * This is used to render network activity
 * indicators on some containers (e.g. computer, server).
 * <br>
 * Use this to implement rendering of disk access indicators on you own
 * containers / computers / drive bays.
 * <br>
 * Canceling this event is provided to allow registering higher priority
 * event handlers that override default behavior.
 */
public class NetworkActivityEvent extends Event {
    protected Level level;

    protected double x;

    protected double y;

    protected double z;

    protected BlockEntity blockEntity;

    protected CompoundTag data;

    /**
     * Constructor for tile entity hosted network cards.
     *
     * @param blockEntity the tile entity hosting the network card.
     * @param data       the additional data.
     */
    protected NetworkActivityEvent(BlockEntity blockEntity, CompoundTag data) {
        this.level = blockEntity.getLevel();
        this.x = blockEntity.getBlockPos().getX() + 0.5;
        this.y = blockEntity.getBlockPos().getY() + 0.5;
        this.z = blockEntity.getBlockPos().getZ() + 0.5;
        this.blockEntity = blockEntity;
        this.data = data;
    }

    /**
     * Constructor for arbitrarily hosted network cards.
     *
     * @param level the level the network card lives in.
     * @param x     the x coordinate of the network card's container.
     * @param y     the y coordinate of the network card's container.
     * @param z     the z coordinate of the network card's container.
     * @param data  the additional data.
     */
    protected NetworkActivityEvent(Level level, double x, double y, double z, CompoundTag data) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockEntity = null;
        this.data = data;
    }

    /**
     * The level the network card lives in.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * The x coordinate of the network card's container.
     */
    public double getX() {
        return x;
    }

    /**
     * The y coordinate of the network card's container.
     */
    public double getY() {
        return y;
    }

    /**
     * The z coordinate of the network card's container.
     */
    public double getZ() {
        return z;
    }

    /**
     * The tile entity hosting the network card.
     * <br>
     * <em>Important</em>: this can be <tt>null</tt>, which is usually the
     * case when the container is an entity or item.
     */
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * Addition custom data, this is used to transmit the number of the server
     * in a server rack the network card lives in, for example.
     */
    public CompoundTag getData() {
        return data;
    }

    public static final class Server extends NetworkActivityEvent {
        private Node node;

        public Server(BlockEntity tileEntity, Node node) {
            super(tileEntity, new CompoundTag());
            this.node = node;
        }

        public Server(Level level, double x, double y, double z, Node node) {
            super(level, x, y, z, new CompoundTag());
            this.node = node;
        }

        /**
         * The node of the network card that signalled activity.
         */
        public Node getNode() {
            return node;
        }
    }

    public static final class Client extends NetworkActivityEvent {
        /**
         * Constructor for tile entity hosted network card.
         *
         * @param blockEntity the tile entity hosting the network card.
         * @param data       the additional data.
         */
        public Client(BlockEntity blockEntity, CompoundTag data) {
            super(blockEntity, data);
        }

        /**
         * Constructor for arbitrarily hosted network card.
         *
         * @param level the level the file system lives in.
         * @param x     the x coordinate of the network card's container.
         * @param y     the y coordinate of the network card's container.
         * @param z     the z coordinate of the network card's container.
         * @param data  the additional data.
         */
        public Client(Level level, double x, double y, double z, CompoundTag data) {
            super(level, x, y, z, data);
        }
    }
}
