package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.registry.BNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class StopSoundPacket {
    public final int note;
    public final int releaseTicks;
    public final int userId;

    public StopSoundPacket(int note, int releaseTicks, int userId) {
        this.note = note;
        this.releaseTicks = releaseTicks;
        this.userId = userId;
    }

    public static void encode(StopSoundPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.note);
        buf.writeInt(msg.releaseTicks);
        buf.writeInt(msg.userId);
    }

    public static StopSoundPacket decode(FriendlyByteBuf buf) {
        return new StopSoundPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(StopSoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            BNetwork.INSTANCE.send(
                    PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(sender.getX(), sender.getY(), sender.getZ(), 32, sender.level().dimension())),
                    new S2CStopSoundPacket(msg.note, msg.releaseTicks, sender.getId())
            );
        });
        ctx.get().setPacketHandled(true);
    }
}


