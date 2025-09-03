// S2CStopSoundPacket.java
package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.ClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CStopSoundPacket {
    public final int note;
    public final int releaseTicks;
    public final int userId;

    public S2CStopSoundPacket(int note, int releaseTicks, int userId) {
        this.note = note;
        this.releaseTicks = releaseTicks;
        this.userId = userId;
    }

    public static void encode(S2CStopSoundPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.note);
        buf.writeInt(msg.releaseTicks);
        buf.writeInt(msg.userId);
    }

    public static S2CStopSoundPacket decode(FriendlyByteBuf buf) {
        return new S2CStopSoundPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(S2CStopSoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null) return;

            ClientHandler.handleStopSoundPacket(msg);
        });
        ctx.get().setPacketHandled(true);
    }
}
