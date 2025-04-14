package com.ninni.minestrel.server.inventory;

import com.ninni.minestrel.registry.MBlockRegistry;
import com.ninni.minestrel.registry.MMenuRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KeyboardMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    Runnable slotUpdateListener = () -> {
    };
    public final Container container = new SimpleContainer(2) {
        public void setChanged() {
            super.setChanged();
            KeyboardMenu.this.slotsChanged(this);
            KeyboardMenu.this.slotUpdateListener.run();
        }
    };
    final Slot instrumentSlot;
    final Slot sheetMusicSlot;

    public KeyboardMenu(int id, Inventory inventory, final ContainerLevelAccess access) {
        super(MMenuRegistry.KEYBOARD.get(), id);
        this.access = access;

        instrumentSlot = this.addSlot(new Slot(this.container, 0, 7, 43) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GOLD_BLOCK);
            }
            public int getMaxStackSize() {
                return 1;
            }
        });

        sheetMusicSlot = this.addSlot(new Slot(this.container, 1, 160, 43) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.PAPER);
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

    public KeyboardMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (i != this.instrumentSlot.index && i != this.sheetMusicSlot.index) {
                if (itemstack1.is(Items.PAPER)) {
                    if (!this.moveItemStackTo(itemstack1, this.sheetMusicSlot.index, this.sheetMusicSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.is(Items.GOLD_BLOCK)) {
                    if (!this.moveItemStackTo(itemstack1, this.instrumentSlot.index, this.instrumentSlot.index + 1, false)) {
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

    public void registerUpdateListener(Runnable p_39879_) {
        this.slotUpdateListener = p_39879_;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, MBlockRegistry.KEYBOARD.get());
    }

    public Slot getInstrumentSlot() {
        return instrumentSlot;
    }

    public Slot getSheetMusicSlot() {
        return sheetMusicSlot;
    }
}
