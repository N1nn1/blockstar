package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.item.SheetMusicItem;
import com.ninni.blockstar.server.sheetmusic.SheetNote;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class SheetNoteEditPacket {
    public enum Action {
        ADD,
        UPDATE,
        REMOVE
    }
    private final Action action;
    private final int tick;
    private final int pitch;
    private final int duration;
    private final int velocity;

    public SheetNoteEditPacket(Action action, int tick, int pitch, int duration, int velocity) {
        this.action = action;
        this.tick = tick;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
    }

    public static void encode(SheetNoteEditPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeInt(msg.tick);
        buf.writeInt(msg.pitch);
        buf.writeInt(msg.duration);
        buf.writeInt(msg.velocity);
    }

    public static SheetNoteEditPacket decode(FriendlyByteBuf buf) {
        return new SheetNoteEditPacket(buf.readEnum(Action.class), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(SheetNoteEditPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                if (ctx.get().getSender() != null && ctx.get().getSender().containerMenu instanceof ComposingTableMenu menu) {
                    ItemStack sheet = menu.getSheetMusicSlot().getItem();
                    if (!sheet.isEmpty()) {
                        if (sheet.is(Items.PAPER)) {
                            menu.getSheetMusicSlot().set(BItemRegistry.SHEET_MUSIC.get().getDefaultInstance());
                            BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                                    msg.action, msg.tick, msg.pitch, msg.duration, msg.velocity
                            ));
                        }

                        List<SheetNote> notes = SheetMusicItem.getNotes(sheet);

                        if (msg.action == Action.ADD) {
                            boolean exists = notes.stream().anyMatch(n -> n.tick == msg.tick && n.pitch == msg.pitch);
                            if (!exists && menu.getInkAmount() > 0) {
                                menu.setData(0, menu.getInkAmount() - 1);
                                notes.add(new SheetNote(msg.tick, msg.pitch, msg.duration, msg.velocity));
                            }
                        } else if (msg.action == Action.UPDATE) {
                            notes.stream()
                                    .filter(n -> n.tick == msg.tick && n.pitch == msg.pitch)
                                    .findFirst()
                                    .ifPresent(n -> {
                                        n.tick = msg.tick;
                                        n.pitch = msg.pitch;
                                        n.duration = msg.duration;
                                        n.velocity = msg.velocity;
                                    });
                        } else if (msg.action == Action.REMOVE) {
                            notes.removeIf(n -> n.tick == msg.tick && n.pitch == msg.pitch);
                        }

                        SheetMusicItem.setNotes(sheet, notes);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
