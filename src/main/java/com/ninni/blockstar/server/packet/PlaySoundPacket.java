package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.registry.BNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public class PlaySoundPacket {
    public final ResourceLocation soundId;
    public final float pitch;
    public final int playerId;
    public final int note;
    public final Optional<Integer> autoFadeTicks;

    public PlaySoundPacket(ResourceLocation soundId, float pitch, int playerId, int note, Optional<Integer> autoFadeTicks) {
        this.soundId = soundId;
        this.pitch = pitch;
        this.playerId = playerId;
        this.note = note;
        this.autoFadeTicks = autoFadeTicks;
    }

    public static void encode(PlaySoundPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.soundId);
        buf.writeFloat(msg.pitch);
        buf.writeInt(msg.playerId);
        buf.writeInt(msg.note);
        buf.writeOptional(msg.autoFadeTicks, FriendlyByteBuf::writeInt);
    }

    public static PlaySoundPacket decode(FriendlyByteBuf buf) {
        return new PlaySoundPacket(buf.readResourceLocation(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readOptional(FriendlyByteBuf::readInt));
    }

    public static void handle(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            if (c.getDirection().getReceptionSide().isServer()) {
                ServerPlayer sender = c.getSender();
                if (sender == null) return;

                BNetwork.INSTANCE.send(
                        PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(sender.getX(), sender.getY(), sender.getZ(), 32, sender.level().dimension())),
                        new S2CPlaySoundPacket(msg.soundId, msg.pitch, sender.getId(), msg.note, msg.autoFadeTicks)
                );
            }
        });
        c.setPacketHandled(true);
    }
}
