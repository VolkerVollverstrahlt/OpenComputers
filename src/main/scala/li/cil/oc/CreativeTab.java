package li.cil.oc;

import li.cil.oc.common.init.Items;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import net.minecraft.util.NonNullList;

import java.util.Objects;

public class CreativeTab extends ItemGroup {
    public static final CreativeTab tab = new CreativeTab(OpenComputers.Name);

    private static ItemStack stack = null;

    public CreativeTab(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        if (stack == null) {
            stack = Objects.requireNonNull(li.cil.oc.api.Items.get(Constants.BlockName$.MODULE$.CaseTier1())).createItemStack(1);
        }
        return stack;
    }

    public void fillItemList(NonNullList<ItemStack> list) {
        super.fillItemList(list);
        Items.decorateCreativeTab(list);
    }
}
