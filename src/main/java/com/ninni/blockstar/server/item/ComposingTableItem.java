package com.ninni.blockstar.server.item;

import com.ninni.blockstar.registry.BBlockRegistry;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ComposingTableItem extends BlockItem {

    public ComposingTableItem() {
        super(BBlockRegistry.COMPOSING_TABLE.get(), new Properties());
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").getInt("InkAmount") > 0) {
            return Optional.of(new ComposingTableTooltip(stack.getTag().getCompound("BlockEntityTag").getInt("InkAmount")));
        }
        return super.getTooltipImage(stack);
    }

    public record ComposingTableTooltip(int amount) implements TooltipComponent {}

}
