package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BRecipeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionRecipe;
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
import java.util.Optional;

public class ResonantPrism extends Item {

    public ResonantPrism(Properties properties) {
        super(properties);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Blockstar.CALLBACKS.add(() -> ItemProperties.register(this, new ResourceLocation(Blockstar.MODID, "attuned"), (stack, level, player, i) -> stack.getOrCreateTag().contains("Soundfont") ? 1.0F : 0.0F)));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack stack1, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        Level level = player.level();

        Optional<SoundfontConversionRecipe> recipe = level.getRecipeManager()
                .getAllRecipesFor(BRecipeRegistry.SOUNDFONT_CONVERSION_TYPE.get())
                .stream()
                .filter(r -> r.matches(stack, stack1))
                .findFirst();

        if (recipe.isPresent()) {
            ItemStack result = recipe.get().assemble(stack, stack1);

            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }

            if (recipe.get().shouldShrinkInputs()) {
                stack.shrink(1);
                stack1.shrink(1);
            }

            if (recipe.get().shouldPlaySound() && player instanceof LocalPlayer localPlayer) {
                ResourceLocation resourceLocation = new ResourceLocation(result.getTag().getString("Soundfont"));
                SoundfontManager.SoundfontDefinition soundfont = CommonEvents.SOUNDFONTS.get(new ResourceLocation(resourceLocation.getNamespace(), "keyboard/" + resourceLocation.getPath()));
                int sampleNote = soundfont.getClosestSampleNote(60);
                String velocity = soundfont.velocityLayers().isPresent() ? "_" + 2 : "";
                Minecraft.getInstance().getSoundManager().play(new SoundfontSound(new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(soundfont.instrumentType()).getPath() + "." + soundfont.name().getPath() + "." + sampleNote + velocity), 1.0f, 1, localPlayer));
            }

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
