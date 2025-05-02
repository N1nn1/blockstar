package com.ninni.blockstar.server.block.entity;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.block.ComposingTableBlock;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.packet.BlockEntitySyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ComposingTableBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    public int inkAmount;
    protected final ContainerData dataAccess;

    public ComposingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BBlockEntityRegistry.COMPOSING_TABLE.get(), pos, state);

        this.dataAccess = new ContainerData() {
            public int get(int i) {
                if (i == 0) return ComposingTableBlockEntity.this.inkAmount;
                return 0;
            }

            public void set(int i, int amount) {
                if (i == 0) ComposingTableBlockEntity.this.inkAmount = amount;
            }

            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new ComposingTableMenu(i, inventory, this, this.dataAccess);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("blockstar.container.composing_table");
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

    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        if (tag.contains("InkAmount")) this.inkAmount = tag.getInt("InkAmount");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("InkAmount", inkAmount);
    }

    public void sync() {
        setChanged();
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        if (level == null) return;
        if (!level.isClientSide()) {
            BNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new BlockEntitySyncPacket(worldPosition, tag));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (pkt.getTag() != null) handleUpdateTag(pkt.getTag());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
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


    public static void tick(Level level, BlockPos pos, BlockState state, ComposingTableBlockEntity entity) {
        ItemStack stack = entity.getItem(1);
        if (!stack.isEmpty() && entity.inkAmount <= 180) {
            entity.dataAccess.set(0, entity.inkAmount + 40);
            stack.shrink(1);
            entity.level.playSound(null, entity.worldPosition, SoundEvents.SQUID_HURT, SoundSource.BLOCKS, 1, 1);
            entity.sync();
        }

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
