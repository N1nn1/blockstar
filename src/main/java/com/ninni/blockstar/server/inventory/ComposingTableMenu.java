package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ComposingTableMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData composingTableData;
    final Slot sheetMusicSlot;
    final Slot inkSlot;

    public ComposingTableMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(4), new SimpleContainerData(1));
    }

    public ComposingTableMenu(int id, Inventory inventory, Container container, ContainerData containerData) {
        super(BMenuRegistry.COMPOSING_TABLE.get(), id);
        this.container = container;
        this.composingTableData = containerData;
        this.addDataSlots(containerData);

        sheetMusicSlot = this.addSlot(new Slot(this.container, 0, 8, 29) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof SheetMusicItem || stack.is(Items.PAPER);
            }
            public int getMaxStackSize() {
                return 1;
            }
        });

        inkSlot = this.addSlot(new Slot(this.container, 1, 152, 29) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.INK_SAC);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 172 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 230));
        }

    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (i != this.inkSlot.index && i != this.sheetMusicSlot.index) {
                if (itemstack1.getItem() instanceof SheetMusicItem || itemstack1.is(Items.PAPER)) {
                    if (!this.moveItemStackTo(itemstack1, this.sheetMusicSlot.index, this.sheetMusicSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.is(Items.INK_SAC)) {
                    if (!this.moveItemStackTo(itemstack1, this.inkSlot.index, this.inkSlot.index + 1, false)) {
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

    public Slot getSheetMusicSlot() {
        return sheetMusicSlot;
    }

    public int getInkAmount() {
        return this.composingTableData.get(0);
    }

    public Slot getInkSlot() {
        return inkSlot;
    }
}
