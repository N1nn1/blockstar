package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.server.intstrument.InstrumentType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InstrumentSlot extends Slot {
    private InstrumentType instrumentType;

    public InstrumentSlot(Container container, int u, int v, int i, InstrumentType instrumentType) {
        super(container, u, v, i);
        this.instrumentType = instrumentType;
    }

    public boolean mayPlace(ItemStack stack) {
        return this.instrumentType.isValidSoundfontForInstrumentType(stack);
    }
    public int getMaxStackSize() {
        return 1;
    }
}
