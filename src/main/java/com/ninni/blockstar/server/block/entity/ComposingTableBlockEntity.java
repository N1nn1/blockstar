package com.ninni.blockstar.server.block.entity;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import com.ninni.blockstar.server.block.ComposingTableBlock;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    public void updateSheetMusicState() {
        if (level != null && !level.isClientSide) {
            boolean hasItem = !this.getItem(0).isEmpty();
            BlockState currentState = level.getBlockState(worldPosition);

            if (currentState.getBlock() instanceof ComposingTableBlock) {
                if (currentState.getValue(ComposingTableBlock.HAS_PAPER) != hasItem) {
                    level.setBlock(worldPosition, currentState.setValue(ComposingTableBlock.HAS_PAPER, hasItem), 3);
                    if (hasItem) level.playSound(null, worldPosition, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1, 1);
                    else level.playSound(null, worldPosition, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1, 1);
                }
            }
        }
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
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return ContainerHelper.takeItem(this.items, p_18951_);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (slot == 0) updateSheetMusicState();
    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return Container.stillValidBlockEntity(this, p_18946_);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack result = ContainerHelper.removeItem(this.items, index, count);
        if (index == 0) updateSheetMusicState();
        return result;
    }

    @Override
    public void clearContent() {
        items.clear();
        updateSheetMusicState();
    }
}
