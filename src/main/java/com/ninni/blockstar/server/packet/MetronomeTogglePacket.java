package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.server.item.MetronomeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MetronomeTogglePacket {
    private final UUID metronomeID;

    public MetronomeTogglePacket(UUID metronomeID) {
        this.metronomeID = metronomeID;
    }

    public static void encode(MetronomeTogglePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.metronomeID);
    }

    public static MetronomeTogglePacket decode(FriendlyByteBuf buf) {
        return new MetronomeTogglePacket(buf.readUUID());
    }

    public static void handle(MetronomeTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof MetronomeItem &&
                        stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").hasUUID("UUID") &&
                        stack.getTag().getCompound("BlockEntityTag").getUUID("UUID").equals(msg.metronomeID)) {

                    boolean current = MetronomeItem.isTicking(stack);
                    MetronomeItem.setTicking(stack, !current);
                    player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, current ? 0.8F : 1.2F);
                    return;
                }
            }

            player.containerMenu.slots.stream()
                    .map(Slot::getItem)
                    .filter(stack -> stack.getItem() instanceof MetronomeItem)
                    .filter(stack -> stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").hasUUID("UUID") &&
                            stack.getTag().getCompound("BlockEntityTag").getUUID("UUID").equals(msg.metronomeID))
                    .findFirst()
                    .ifPresent(stack -> {
                        boolean current = MetronomeItem.isTicking(stack);
                        MetronomeItem.setTicking(stack, !current);
                        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, current ? 0.8F : 1.2F);
                    });
        });
        ctx.get().setPacketHandled(true);
    }

}