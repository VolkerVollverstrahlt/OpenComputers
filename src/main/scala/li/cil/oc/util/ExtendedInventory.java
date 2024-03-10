package li.cil.oc.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;
import java.util.stream.IntStream;

// TODO: Verify semantics
public class ExtendedInventory {
    private final IInventory inventory;
    public ExtendedInventory(IInventory inventory) {
        this.inventory = inventory;
    }

    public int length() {
        return inventory.getContainerSize();
    }

    public void set(int idx, ItemStack elem) {
        inventory.setItem(idx, elem);
    }

    public ItemStack get(int idx) {
        return inventory.getItem(idx);
    }

    public int[] indices() {
        return IntStream.range(0, inventory.getContainerSize()).toArray();
    }

    public boolean exists(Predicate<ItemStack> pred) {
        for (int i: indices()) {
            if (pred.test(inventory.getItem(i)))
                return true;
        }
        return false;
    }

    public int indexOf(ItemStack stack) {
        for (int i: indices()) {
            if (stack.equals((inventory.getItem(i))))
                return i;
        }
        return -1;
    }
}
