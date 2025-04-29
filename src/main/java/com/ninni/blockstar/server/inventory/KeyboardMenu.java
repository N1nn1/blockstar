package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.instrument.InstrumentType;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KeyboardMenu extends AbstractContainerMenu {
    private final Container container;
    final Slot soundfontSlot;
    final Slot sheetMusicSlot;
    final InstrumentType instrumentType;

    public KeyboardMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(2));
    }

    public KeyboardMenu(int id, Inventory inventory, Container container) {
        super(BMenuRegistry.KEYBOARD.get(), id);
        this.container = container;
        this.instrumentType = BInstrumentTypeRegistry.KEYBOARD.get();

        soundfontSlot = this.addSlot(new InstrumentSlot(this.container, 0, 7, 43, this.instrumentType));
        sheetMusicSlot = this.addSlot(new Slot(this.container, 1, 160, 43) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof SheetMusicItem;
            }
            public int getMaxStackSize() {
                return 1;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 12 + j * 18, 172 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 12 + k * 18, 230));
        }

    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (i != this.soundfontSlot.index && i != this.sheetMusicSlot.index) {
                if (itemstack1.getItem() instanceof SheetMusicItem) {
                    if (!this.moveItemStackTo(itemstack1, this.sheetMusicSlot.index, this.sheetMusicSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.instrumentType.isValidSoundfontForInstrumentType(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, this.soundfontSlot.index, this.soundfontSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 2 && i < 29) {
                    if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 29 && i < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return this.container.stillValid(p_38874_);
    }

    public Slot getSoundfontSlot() {
        return soundfontSlot;
    }

    public Slot getSheetMusicSlot() {
        return sheetMusicSlot;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }
}
