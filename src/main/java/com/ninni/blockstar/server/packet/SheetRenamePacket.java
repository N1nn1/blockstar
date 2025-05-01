package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SheetRenamePacket {
    private final String name;

    public SheetRenamePacket(String name) {
        this.name = name;
    }

    public static void encode(SheetRenamePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
    }

    public static SheetRenamePacket decode(FriendlyByteBuf buf) {
        return new SheetRenamePacket(buf.readUtf(32));
    }

    public static void handle(SheetRenamePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player != null && player.containerMenu instanceof ComposingTableMenu menu) {
                    ItemStack stack = menu.getSheetMusicSlot().getItem();
                    if (!stack.isEmpty()) stack.setHoverName(Component.literal(msg.name));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

