package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SheetSettingsUpdatePacket {
    private final int bpm;
    private final String timeSig;
    private final String key;
    private final boolean minor;

    public SheetSettingsUpdatePacket(int bpm, String timeSig, String key, boolean minor) {
        this.bpm = bpm;
        this.timeSig = timeSig;
        this.key = key;
        this.minor = minor;
    }

    public static void encode(SheetSettingsUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.bpm);
        buf.writeUtf(msg.timeSig);
        buf.writeUtf(msg.key);
        buf.writeBoolean(msg.minor);
    }

    public static SheetSettingsUpdatePacket decode(FriendlyByteBuf buf) {
        return new SheetSettingsUpdatePacket(buf.readInt(), buf.readUtf(), buf.readUtf(), buf.readBoolean());
    }

    public static void handle(SheetSettingsUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player != null && ctx.get().getSender().containerMenu instanceof ComposingTableMenu menu) {
                    ItemStack stack = menu.getSheetMusicSlot().getItem();
                    if (!stack.isEmpty() && stack.getItem() instanceof SheetMusicItem) {
                        SheetMusicItem.setBPM(stack, msg.bpm);
                        SheetMusicItem.setTimeSignature(stack, SheetMusicItem.getTimeSigValues(msg.timeSig, true), SheetMusicItem.getTimeSigValues(msg.timeSig, false));
                        SheetMusicItem.setKey(stack, msg.key, msg.minor);

                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
