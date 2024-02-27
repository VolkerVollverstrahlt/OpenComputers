package li.cil.oc.integration.cofh.foundation;

import cofh.thermal.lib.tileentity.ThermalTileAugmentable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.integration.ManagedTileEntityEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class DriverEnergyInfo extends DriverSidedTileEntity {
    @Override
    public Class<?> getBlockEntityClass() {
        return ThermalTileAugmentable.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(final Level level, final BlockPos pos, final Direction side) {
        return new Environment((ThermalTileAugmentable) level.getBlockEntity(pos));
    }

    public static final class Environment extends ManagedTileEntityEnvironment<ThermalTileAugmentable> {
        public Environment(final ThermalTileAugmentable tileEntity) {
            super(tileEntity, "energy_info");
        }

        @Callback(doc = "function():number --  Returns the amount of stored energy.")
        public Object[] getEnergy(final Context context, final Arguments args) {
            return new Object[]{blockEntity.getEnergyStorage().getEnergyStored()};
        }

        @Callback(doc = "function():number --  Returns the energy per tick.")
        public Object[] getEnergyPerTick(final Context context, final Arguments args) {
            return new Object[]{blockEntity.getCurSpeed()};
        }

        @Callback(doc = "function():number --  Returns the maximum energy per tick.")
        public Object[] getMaxEnergyPerTick(final Context context, final Arguments args) {
            return new Object[]{blockEntity.getMaxSpeed()};
        }
    }
}
