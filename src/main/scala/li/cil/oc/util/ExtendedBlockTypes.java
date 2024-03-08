package li.cil.oc.util;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ExtendedBlockTypes {
    public static ExtendedBlock extendedBlock(Block block) {
        return new ExtendedBlock(block);
    }

    public static class ExtendedBlock {
        Block block;
        public ExtendedBlock(Block block) {
            this.block = block;
        }

        @Deprecated
        public boolean isAir(BlockPosition position) {
            return block.isAir(position.world().get().getBlockState(position.toBlockPos()), position.world().get(), position.toBlockPos());
        }

        @Deprecated
        public boolean isReplaceable(BlockPosition position) {
            return block.defaultBlockState().getMaterial().isReplaceable();
        }

        @Deprecated
        public float getBlockHardness(BlockPosition position) {
            return position.world().get().getBlockState(position.toBlockPos()).getDestroySpeed(position.world().get(), position.toBlockPos());
        }

        @Deprecated
        public int getComparatorInputOverride(BlockPosition position, Direction side) {
            return block.getAnalogOutputSignal(position.world().get().getBlockState(position.toBlockPos()), position.world().get(), position.toBlockPos());
        }
    }

    public static ExtendedFluidBlock extendedFluidBlock(IFluidBlock block) {
        return new ExtendedFluidBlock(block);
    }

    public static class ExtendedFluidBlock {
        IFluidBlock block;
        public ExtendedFluidBlock(IFluidBlock block) {
            this.block = block;
        }

        public FluidStack drain(BlockPosition position, IFluidHandler.FluidAction action) {
            return block.drain(position.world().get(), position.toBlockPos(), action);
        }
        public boolean canDrain(BlockPosition position) {
            return block.canDrain(position.world().get(), position.toBlockPos());
        }
        public float getFilledPercentage(BlockPosition position) {
            return block.getFilledPercentage(position.world().get(), position.toBlockPos());
        }
    }
}
