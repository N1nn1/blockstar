package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import com.ninni.blockstar.server.intstrument.InstrumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerPlayNoteSoundPacket {
    private final BlockPos pos;
    private final int note;
    private final ResourceLocation soundfontName;
    private final InstrumentType instrumentType;

    public ServerPlayNoteSoundPacket(BlockPos pos, int note, ResourceLocation soundfontName, InstrumentType instrumentType) {
        this.pos = pos;
        this.note = note;
        this.soundfontName = soundfontName;
        this.instrumentType = instrumentType;
    }

    public static void encode(ServerPlayNoteSoundPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeInt(pkt.note);
        buf.writeResourceLocation(pkt.soundfontName);
        BInstrumentTypeRegistry.writeInstrument(buf, pkt.instrumentType);
    }

    public static ServerPlayNoteSoundPacket decode(FriendlyByteBuf buf) {
        return new ServerPlayNoteSoundPacket(buf.readBlockPos(), buf.readInt(), buf.readResourceLocation(), BInstrumentTypeRegistry.readInstrument(buf));
    }

    public static void handle(ServerPlayNoteSoundPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;


            if (level != null) {
                ResourceLocation id = new ResourceLocation(pkt.soundfontName.getNamespace(), BInstrumentTypeRegistry.get(pkt.instrumentType).getPath() + "/" + pkt.soundfontName.getPath());
                SoundfontManager.SoundfontDefinition soundfont = CommonEvents.SOUNDFONTS.get(id);
                if (soundfont != null) {
                    Minecraft.getInstance().getSoundManager().play(InstrumentType.getSoundfontSound(soundfont, pkt.note));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

