package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResonantPrism extends Item {

    public ResonantPrism(Properties properties) {
        super(properties);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Blockstar.CALLBACKS.add(() -> ItemProperties.register(this, new ResourceLocation(Blockstar.MODID, "attuned"), (stack, level, player, i) -> stack.getOrCreateTag().contains("Soundfont") ? 1.0F : 0.0F)));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack stack1, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {

        if (!stack.hasTag() && stack1.getItem() instanceof BlockItem blockItem) {
            NoteBlockInstrument instrument = blockItem.getBlock().defaultBlockState().instrument();
            SoundfontManager.SoundfontDefinition soundfont = CommonEvents.SOUNDFONTS.get(new ResourceLocation(Blockstar.MODID, "keyboard/note_block_harp"));

            for (SoundfontManager.SoundfontDefinition data : CommonEvents.SOUNDFONTS.getAll()) {
                if (data.name().getPath().replace("note_block_", "").equals(instrument.getSerializedName())) {
                    soundfont = data;
                }
            }

            CompoundTag stackTag = new CompoundTag();
            stackTag.putString("Soundfont", soundfont.name().toString());

            if (soundfont.instrumentExclusive()) stackTag.putString("InstrumentType", BInstrumentTypeRegistry.get(soundfont.instrumentType()).toString());
            if (soundfont.rarity() != Rarity.COMMON) stackTag.putString("Rarity", soundfont.rarity().toString());
            ItemStack stack2 = stack.copyWithCount(1);
            stack2.setTag(stackTag);

            if (player instanceof LocalPlayer localPlayer) {
                int sampleNote = soundfont.getClosestSampleNote(60);
                String velocity = soundfont.velocityLayers().isPresent() ? "_" + 2 : "";
                Minecraft.getInstance().getSoundManager().play(new SoundfontSound(new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(soundfont.instrumentType()).getPath() + "." + soundfont.name().getPath() + "." + sampleNote + velocity), 1.0f, 1, localPlayer));
            }

            stack1.shrink(1);
            stack.shrink(1);
            if (!player.getInventory().add(stack2)) player.drop(stack2, false);

            return true;
        }

        return super.overrideOtherStackedOnMe(stack, stack1, slot, clickAction, player, slotAccess);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Rarity")) return Rarity.valueOf(stack.getTag().getString("Rarity"));
        return super.getRarity(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Soundfont")) return 1;
        return super.getMaxStackSize(stack);
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
