package com.ninni.blockstar.server.item;

import com.ninni.blockstar.server.sheetmusic.SheetNote;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class SheetMusicItem extends Item {
    public SheetMusicItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static List<SheetNote> getNotes(ItemStack stack) {
        List<SheetNote> notes = new ArrayList<>();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("Notes")) {
            ListTag list = tag.getList("Notes", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                notes.add(SheetNote.fromNBT((CompoundTag) t));
            }
        }
        return notes;
    }

    public static void setNotes(ItemStack stack, List<SheetNote> notes) {
        ListTag list = new ListTag();
        for (SheetNote note : notes) {
            list.add(note.toNBT());
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("Notes", list);
    }
}
