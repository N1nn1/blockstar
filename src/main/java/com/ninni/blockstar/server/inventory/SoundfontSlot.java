package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.server.instrument.InstrumentType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SoundfontSlot extends Slot {
    private final InstrumentType instrumentType;

    public SoundfontSlot(Container container, int u, int v, int i, InstrumentType instrumentType) {
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
