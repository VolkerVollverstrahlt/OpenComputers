package li.cil.oc.util;

import net.minecraft.util.Direction;

import java.util.Arrays;
import java.util.List;

public class ExtendedEnumFacing {
    private static List<List<Integer>> ROTATION_MATRIX = Arrays.asList(
            Arrays.asList(0, 1, 4, 5, 3, 2, 6),
            Arrays.asList(0, 1, 5, 4, 2, 3, 6),
            Arrays.asList(5, 4, 2, 3, 0, 1, 6),
            Arrays.asList(4, 5, 2, 3, 1, 0, 6),
            Arrays.asList(2, 3, 1, 0, 4, 5, 6),
            Arrays.asList(3, 2, 0, 1, 4, 5, 6),
            Arrays.asList(0, 1, 2, 3, 4, 5, 6));
    private final Direction facing;
    public ExtendedEnumFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getRotation(Direction axis) {
        return Direction.from3DDataValue(ROTATION_MATRIX.get(axis.ordinal()).get(facing.ordinal()));
    }
}
