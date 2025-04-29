package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.intstrument.InstrumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ResonantPrismItem extends Item {

    public ResonantPrismItem(Properties properties) {
        super(properties);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Blockstar.CALLBACKS.add(() -> ItemProperties.register(this, new ResourceLocation(Blockstar.MODID, "attuned"), (stack, level, player, i) -> stack.getOrCreateTag().contains("Soundfont") ? 1.0F : 0.0F)));
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
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.hasTag() && !stack.getTag().getString("Soundfont").isEmpty()) {
            if (!stack.getTag().getString("Soundfont").isEmpty()) {
                return Optional.of(new SoundfontTooltip(new ResourceLocation(stack.getTag().getString("Soundfont"))));
            }
        }
        return Optional.of(new SoundfontTooltip(new ResourceLocation(Blockstar.MODID, "base/empty")));
    }

    public record SoundfontTooltip(ResourceLocation icon) implements TooltipComponent {}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        if (stack.hasTag() && (stack.getTag().contains("Soundfont") && !stack.getTag().getString("Soundfont").isEmpty()
                || stack.getTag().contains("InstrumentType") && !stack.getTag().getString("InstrumentType").isEmpty())) {

            if (stack.getTag().contains("Soundfont") && !stack.getTag().getString("Soundfont").isEmpty()) {
                ResourceLocation resourceLocation = new ResourceLocation(stack.getTag().getString("Soundfont"));
                if (stack.getTag().contains("Color")) {
                    list.add(Component.translatable(resourceLocation.getNamespace() + ".soundfont." + resourceLocation.getPath()).withStyle(Style.EMPTY.withColor(TextColor.parseColor(stack.getTag().getString("Color")))));
                } else {
                    list.add(Component.translatable(resourceLocation.getNamespace() + ".soundfont." + resourceLocation.getPath()).withStyle(ChatFormatting.GRAY));
                }
            }
            if (stack.getTag().contains("InstrumentType") && !stack.getTag().getString("InstrumentType").isEmpty()) {
                ResourceLocation resourceLocation = new ResourceLocation(stack.getTag().getString("InstrumentType"));
                list.add(Component.translatable(resourceLocation.getNamespace() + ".instrument_type." + resourceLocation.getPath()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
