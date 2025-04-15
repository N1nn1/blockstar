package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KeyboardMenu extends AbstractContainerMenu {
    private final Container container;
    final Slot instrumentSlot;
    final Slot sheetMusicSlot;

    public KeyboardMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(2));
    }

    public KeyboardMenu(int id, Inventory inventory, Container container) {
        super(BMenuRegistry.KEYBOARD.get(), id);
        this.container = container;

        instrumentSlot = this.addSlot(new Slot(this.container, 0, 7, 43) {
            public boolean mayPlace(ItemStack stack) {
                return stack.hasTag() && stack.getTag().contains("Soundfont") || stack.is(BItemRegistry.RESONANT_PRISM.get());
            }
            public int getMaxStackSize() {
                return 1;
            }
        });

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

            if (i != this.instrumentSlot.index && i != this.sheetMusicSlot.index) {
                if (itemstack1.getItem() instanceof SheetMusicItem) {
                    if (!this.moveItemStackTo(itemstack1, this.sheetMusicSlot.index, this.sheetMusicSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.hasTag() && itemstack1.getTag().contains("Soundfont") || itemstack1.is(BItemRegistry.RESONANT_PRISM.get())) {
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

    public SoundfontManager.SoundfontDefinition getKeyboardSoundfont() {
        SoundfontManager.SoundfontDefinition soundfontDefinition = null;

        if (this.instrumentSlot.hasItem()) {
            ItemStack stack = this.instrumentSlot.getItem();
            if (stack.hasTag() && stack.getTag().contains("Soundfont")) {
                ResourceLocation resourceLocation = new ResourceLocation(stack.getTag().getString("Soundfont"));
                soundfontDefinition = CommonEvents.SOUNDFONTS.get(new ResourceLocation(resourceLocation.getNamespace(), "keyboard/" + resourceLocation.getPath()));
            } else {
                if (stack.is(BItemRegistry.RESONANT_PRISM.get())) soundfontDefinition = CommonEvents.SOUNDFONTS.get(new ResourceLocation(Blockstar.MODID, "keyboard/note_block_harp"));
            }
        } else {
            soundfontDefinition = CommonEvents.SOUNDFONTS.get(new ResourceLocation(Blockstar.MODID, "keyboard/base"));
        }

        if (soundfontDefinition != null) return soundfontDefinition;
        else return CommonEvents.SOUNDFONTS.get(new ResourceLocation(Blockstar.MODID, "keyboard/base"));
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return this.container.stillValid(p_38874_);
    }

    public Slot getInstrumentSlot() {
        return instrumentSlot;
    }

    public Slot getSheetMusicSlot() {
        return sheetMusicSlot;
    }
}
