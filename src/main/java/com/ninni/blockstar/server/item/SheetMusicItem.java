package com.ninni.blockstar.server.item;

import com.ninni.blockstar.server.sheetmusic.SheetNote;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SheetMusicItem extends Item {
    public SheetMusicItem(Properties properties) {
        super(properties);
    }

    public static List<SheetNote> getNotes(ItemStack stack) {
        List<SheetNote> notes = new ArrayList<>();
        CompoundTag tag = getOrCreateSheetTag(stack);
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
        CompoundTag tag = getOrCreateSheetTag(stack);
        tag.put("Notes", list);
    }

    public static int getBPM(ItemStack stack) {
        CompoundTag tag = getOrCreateSheetTag(stack);
        return tag.contains("BPM") ? tag.getInt("BPM") : 100;
    }

    public static void setBPM(ItemStack stack, int bpm) {
        getOrCreateSheetTag(stack).putInt("BPM", bpm);
    }

    public static String getTimeSig(ItemStack stack) {
        CompoundTag tag = getOrCreateSheetTag(stack);
        return tag.getString("TimeSig").isEmpty() ? "4/4" : tag.getString("TimeSig");
    }

    public static void setTimeSignature(ItemStack stack, int num, int den) {
        getOrCreateSheetTag(stack).putString("TimeSig", num + "/" + den);
    }

    public static int getTimeSigValues(String timeSig, boolean num) {
        String[] parts = timeSig.split("/");
        try {
            return Integer.parseInt(parts[num ? 0 : 1]);
        } catch (Exception e) {
            return 4;
        }
    }

    public static String getKey(ItemStack stack) {
        CompoundTag tag = getOrCreateSheetTag(stack);
        return tag.getString("Key").isEmpty() ? "C" : tag.getString("Key");
    }

    public static void setKey(ItemStack stack, String key, boolean isMinor) {
        if (isMinor && !key.endsWith("m")) {
            key += "m";
        } else if (!isMinor && key.endsWith("m")) {
            key = key.substring(0, key.length() - 1);
        }
        getOrCreateSheetTag(stack).putString("Key", key);
    }

    public static boolean isKeyMinor(ItemStack stack) {
        return getKey(stack).endsWith("m");
    }

    public static boolean isTimeSigValid(String timeSig) {
        return timeSig.matches("^[1-9][0-9]?/[1-9][0-9]?$");
    }

    private static CompoundTag getOrCreateSheetTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Sheet")) {
            tag.put("Sheet", new CompoundTag());
        }
        return tag.getCompound("Sheet");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, list, tooltipFlag);

        list.add(Component.translatable("item.blockstar.sheet_music.key", getKey(stack)).withStyle(Style.EMPTY.withColor(isKeyMinor(stack) ? 0x9672a0 : 0xd44a62)));
        list.add(Component.translatable("item.blockstar.sheet_music.bpm", getBPM(stack)).withStyle(Style.EMPTY.withColor(0x5cb167)));
        list.add(Component.translatable("item.blockstar.sheet_music.time_sig", getTimeSig(stack)).withStyle(Style.EMPTY.withColor(0x4f75ac)));
    }
}

