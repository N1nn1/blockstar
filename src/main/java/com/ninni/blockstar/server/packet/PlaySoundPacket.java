package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.Blockstar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class PlaySoundPacket {
    public final ResourceLocation soundLocation;
    public final float pitch;
    public final int playerId;
    public final int note;
    public final Optional<Integer> autoFadeTicks;

    public PlaySoundPacket(ResourceLocation soundLocation, float pitch, int playerId, int note, Optional<Integer> autoFadeTicks) {
        this.soundLocation = soundLocation;
        this.pitch = pitch;
        this.playerId = playerId;
        this.note = note;
        this.autoFadeTicks = autoFadeTicks;
    }

    public static void encode(PlaySoundPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.soundLocation);
        buf.writeFloat(msg.pitch);
        buf.writeInt(msg.playerId);
        buf.writeInt(msg.note);
        buf.writeOptional(msg.autoFadeTicks, FriendlyByteBuf::writeInt);
    }

    public static PlaySoundPacket decode(FriendlyByteBuf buf) {
        return new PlaySoundPacket(buf.readResourceLocation(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readOptional(FriendlyByteBuf::readInt));
    }

    public static void handle(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                Blockstar.PROXY.handlePlaySoundPacket(msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
