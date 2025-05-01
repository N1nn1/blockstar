package com.ninni.blockstar.server.block.entity;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.inventory.KeyboardMenu;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ComposingTableBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public ComposingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BBlockEntityRegistry.COMPOSING_TABLE.get(), pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new ComposingTableMenu(i, inventory, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("blockstar.container.composing_table");
    }

    public void load(CompoundTag p_155496_) {
        super.load(p_155496_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155496_, this.items);
    }

    protected void saveAdditional(CompoundTag p_187498_) {
        super.saveAdditional(p_187498_);
        ContainerHelper.saveAllItems(p_187498_, this.items);
    }

    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return items.get(p_18941_);
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return ContainerHelper.removeItem(this.items, p_18942_, p_18943_);
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return ContainerHelper.takeItem(this.items, p_18951_);
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {
        items.set(p_18944_, p_18945_);
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return Container.stillValidBlockEntity(this, p_18946_);
    }

    @Override
    public void clearContent() {
        items.clear();
    }
}
