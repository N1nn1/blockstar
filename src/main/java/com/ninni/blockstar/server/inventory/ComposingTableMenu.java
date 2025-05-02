package com.ninni.blockstar.server.inventory;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.instrument.InstrumentType;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.resources.ResourceLocation;
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
    final Slot instrumentSlot;
    final Slot soundfontSlot;

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

        instrumentSlot = this.addSlot(new InstrumentSlot(this.container, 2, 152, 110){
            public int getMaxStackSize() {
                return 1;
            }
        });
        soundfontSlot = this.addSlot(new Slot(this.container, 3, 152, 130) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ResonantPrismItem;
            }
            public int getMaxStackSize() {
                return 1;
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

            if (i == this.sheetMusicSlot.index || i == this.inkSlot.index ||
                    i == this.instrumentSlot.index || i == this.soundfontSlot.index) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof SheetMusicItem || itemstack1.is(Items.PAPER)) {
                    if (!this.moveItemStackTo(itemstack1, this.sheetMusicSlot.index, this.sheetMusicSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.inkSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, this.inkSlot.index, this.inkSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.instrumentSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, this.instrumentSlot.index, this.instrumentSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.soundfontSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, this.soundfontSlot.index, this.soundfontSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 4 && i < 31) {
                    if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 31 && i < 40 && !this.moveItemStackTo(itemstack1, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
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
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public SoundfontManager.SoundfontDefinition getSoundfont() {
        ItemStack soundfontStack = this.getSoundfontSlot().getItem();

        if (((InstrumentSlot)this.getInstrumentSlot()).getInstrument() != null) {
            return getInstrumentType().getSoundfont(soundfontStack);
        }
        else if (this.getSoundfontSlot().hasItem() && !this.getInstrumentSlot().hasItem() && soundfontStack.hasTag() && soundfontStack.getTag().contains("Soundfont")) {
            return Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(soundfontStack.getTag().getString("Soundfont")));
        }
        else {
            return Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(Blockstar.MODID, "note_block-harp"));
        }
    }

    public InstrumentType getInstrumentType() {
        if (this.getInstrumentSlot().hasItem() && ((InstrumentSlot)this.getInstrumentSlot()).getInstrument() != null) {
            return ((InstrumentSlot)this.getInstrumentSlot()).getInstrument();
        }
        return BInstrumentTypeRegistry.KEYBOARD.get();
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
    public Slot getInstrumentSlot() {
        return instrumentSlot;
    }
    public Slot getSoundfontSlot() {
        return soundfontSlot;
    }
}
