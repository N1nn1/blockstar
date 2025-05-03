package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.item.MetronomeItem;
import com.ninni.blockstar.server.item.MetronomeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MetronomeUpdatePacket {
    private final int bpm;
    private final String timeSig;

    public MetronomeUpdatePacket(int bpm, String timeSig) {
        this.bpm = bpm;
        this.timeSig = timeSig;
    }

    public static void encode(MetronomeUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.bpm);
        buf.writeUtf(msg.timeSig);
    }

    public static MetronomeUpdatePacket decode(FriendlyByteBuf buf) {
        return new MetronomeUpdatePacket(buf.readInt(), buf.readUtf());
    }

    public static void handle(MetronomeUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {

                    ItemStack stack = player.getMainHandItem().getItem() instanceof MetronomeItem ? player.getMainHandItem() : player.getOffhandItem();
                    if (!stack.isEmpty()) {
                        MetronomeItem.setBPM(stack, msg.bpm);
                        MetronomeItem.setTimeSignature(stack, MetronomeItem.getTimeSigValues(msg.timeSig, true), MetronomeItem.getTimeSigValues(msg.timeSig, false));
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
