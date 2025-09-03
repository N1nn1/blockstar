package com.ninni.blockstar.server.item;

import com.ninni.blockstar.client.ClientHandler;
import com.ninni.blockstar.registry.BBlockRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;

public class MetronomeItem extends BlockItem {

    public MetronomeItem() {
        super(BBlockRegistry.METRONOME.get(), new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player instanceof LocalPlayer localPlayer) {
            ClientHandler.openMetronomeScreen(localPlayer, itemStack);
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        getOrCreateUniqueID(stack);
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    public static int getBPM(ItemStack stack) {
        CompoundTag tag = getOrCreateBlockEntityTag(stack);
        return tag.contains("BPM") ? tag.getInt("BPM") : 100;
    }

    public static void setBPM(ItemStack stack, int bpm) {
        getOrCreateBlockEntityTag(stack).putInt("BPM", bpm);
    }

    public static String getTimeSig(ItemStack stack) {
        CompoundTag tag = getOrCreateBlockEntityTag(stack);
        return tag.getString("TimeSig").isEmpty() ? "4/4" : tag.getString("TimeSig");
    }

    public static void setTimeSignature(ItemStack stack, int num, int den) {
        getOrCreateBlockEntityTag(stack).putString("TimeSig", num + "/" + den);
    }

    public static int getTimeSigValues(String timeSig, boolean num) {
        String[] parts = timeSig.split("/");
        try {
            return Integer.parseInt(parts[num ? 0 : 1]);
        } catch (Exception e) {
            return 4;
        }
    }

    public static boolean isTimeSigValid(String timeSig) {
        return timeSig.matches("^[1-9][0-9]?/[1-9][0-9]?$");
    }

    private static CompoundTag getOrCreateBlockEntityTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("BlockEntityTag")) tag.put("BlockEntityTag", new CompoundTag());
        return tag.getCompound("BlockEntityTag");
    }

    public static boolean isTicking(ItemStack stack) {
        return getOrCreateBlockEntityTag(stack).getBoolean("Ticking");
    }

    public static void setTicking(ItemStack stack, boolean active) {
        getOrCreateBlockEntityTag(stack).putBoolean("Ticking", active);
    }

    public static UUID getOrCreateUniqueID(ItemStack stack) {
        CompoundTag tag = getOrCreateBlockEntityTag(stack);
        if (!tag.hasUUID("UUID")) {
            UUID uuid = UUID.randomUUID();
            tag.putUUID("UUID", uuid);
            return uuid;
        }
        return tag.getUUID("UUID");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        if (isTicking(stack)) list.add(Component.translatable("item.blockstar.metronome.ticking").withStyle(Style.EMPTY.withColor(0x66cc66)));
        list.add(Component.translatable("item.blockstar.desc.bpm", getBPM(stack)).withStyle(Style.EMPTY.withColor(0x616a83)));
        list.add(Component.translatable("item.blockstar.desc.time_sig", getTimeSig(stack)).withStyle(Style.EMPTY.withColor(0x616a83)));
    }
}
