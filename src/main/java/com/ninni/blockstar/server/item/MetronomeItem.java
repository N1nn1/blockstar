package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.MetronomeItemScreen;
import com.ninni.blockstar.client.gui.SheetSettingsScreen;
import com.ninni.blockstar.registry.BBlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetronomeItem extends BlockItem {

    public MetronomeItem() {
        super(BBlockRegistry.METRONOME.get(), new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player instanceof LocalPlayer localPlayer) {
            localPlayer.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, 1);
            Minecraft.getInstance().setScreen(new MetronomeItemScreen(itemStack));
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, interactionHand);
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        list.add(Component.translatable("item.blockstar.desc.bpm", getBPM(stack)).withStyle(Style.EMPTY.withColor(0x616a83)));
        list.add(Component.translatable("item.blockstar.desc.time_sig", getTimeSig(stack)).withStyle(Style.EMPTY.withColor(0x616a83)));
    }
}
