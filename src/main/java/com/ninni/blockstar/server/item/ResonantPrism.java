package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResonantPrism extends Item {
    private final List<String> nonCompatibleNoteBlockSounds = new ArrayList<>();


    public ResonantPrism(Properties properties) {
        super(properties);
        nonCompatibleNoteBlockSounds.add("basedrum");
        nonCompatibleNoteBlockSounds.add("snare");
        nonCompatibleNoteBlockSounds.add("hat");
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack stack1, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {

        if (!stack.hasTag() && stack1.getItem() instanceof BlockItem blockItem) {
            NoteBlockInstrument instrument = blockItem.getBlock().defaultBlockState().instrument();
            if (instrument.isTunable() && !instrument.worksAboveNoteBlock() && !nonCompatibleNoteBlockSounds.contains(instrument.getSerializedName())) {
                CompoundTag stackTag = new CompoundTag();
                stackTag.putString("Soundfont", new ResourceLocation(Blockstar.MODID, "note_block_" + instrument.getSerializedName()).toString());

                stack.setTag(stackTag);
                player.playNotifySound(instrument.getSoundEvent().get(), SoundSource.RECORDS, 1, 1);
                stack1.shrink(1);
                return true;
            }
        }

        return super.overrideOtherStackedOnMe(stack, stack1, slot, clickAction, player, slotAccess);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Rarity")) return Rarity.valueOf(stack.getTag().getString("Rarity"));
        return super.getRarity(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        if (stack.hasTag() && (stack.getTag().contains("Soundfont") && !stack.getTag().getString("Soundfont").isEmpty()
                || stack.getTag().contains("InstrumentType") && !stack.getTag().getString("InstrumentType").isEmpty())
        ) {
            if (stack.getTag().contains("Soundfont") && !stack.getTag().getString("Soundfont").isEmpty()) {
                ResourceLocation resourceLocation = new ResourceLocation(stack.getTag().getString("Soundfont"));
                if (resourceLocation.getPath().startsWith("note_block_")) {
                    list.add(Component.translatable(resourceLocation.getNamespace() + ".soundfont." + resourceLocation.getPath()).withStyle(Style.EMPTY.withColor(0xb76f4a)));
                } else {
                    list.add(Component.translatable(resourceLocation.getNamespace() + ".soundfont." + resourceLocation.getPath()).withStyle(ChatFormatting.YELLOW));
                }
            }
            if (stack.getTag().contains("InstrumentType") && !stack.getTag().getString("InstrumentType").isEmpty()) {
                ResourceLocation resourceLocation = new ResourceLocation(stack.getTag().getString("InstrumentType"));
                list.add(Component.translatable(resourceLocation.getNamespace() + ".instrument_type." + resourceLocation.getPath()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
