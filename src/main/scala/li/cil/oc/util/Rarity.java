package li.cil.oc.util;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.item.Rarity.*;

public class Rarity {
    private static final List<net.minecraft.item.Rarity> lookup = Arrays.asList(COMMON, UNCOMMON, RARE, EPIC);

    public static net.minecraft.item.Rarity byTier(int tier) {
        return lookup.get(Math.min(Math.max(tier, 0), lookup.size() - 1));
    }
}
