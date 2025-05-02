package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.server.block.InstrumentBlock;
import com.ninni.blockstar.server.instrument.InstrumentType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class InstrumentSlot extends Slot {
    public InstrumentSlot(Container container, int u, int v, int i) {
        super(container, u, v, i);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof InstrumentBlock;
    }

    public InstrumentType getInstrument() {
        return this.getItem().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof InstrumentBlock instrument?  instrument.getInstrumentType() : null;
    }
}
