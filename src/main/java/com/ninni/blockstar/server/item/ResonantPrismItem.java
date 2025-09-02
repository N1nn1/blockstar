package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.instrument.InstrumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ResonantPrismItem extends Item {

    public ResonantPrismItem(Properties properties) {
        super(properties);
    }

    private Optional<SoundfontManager.SoundfontDefinition> lookup(ItemStack stack) {
        if (!stack.hasTag()) return Optional.empty();
        String key = stack.getTag().getString("Soundfont");
        if (key == null || key.isEmpty()) return Optional.empty();
        ResourceLocation id = ResourceLocation.tryParse(key);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(Blockstar.PROXY.getSoundfontManager().get(id));
    }

    private Optional<SoundfontManager.SoundfontDefinition> base() {
        return Optional.ofNullable(
                Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(Blockstar.MODID, "base"))
        );
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return lookup(stack)
                .map(SoundfontManager.SoundfontDefinition::rarity)
                .or(() -> base().map(SoundfontManager.SoundfontDefinition::rarity))
                .orElseGet(() -> super.getRarity(stack));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains("Soundfont") && !stack.getTag().getString("Soundfont").isEmpty()) ? 1 : super.getMaxStackSize(stack);
    }

    public static ItemStack getPrismItemFromSoundfont(SoundfontManager.SoundfontDefinition data) {
        CompoundTag stackTag = new CompoundTag();
        stackTag.putString("Soundfont", Blockstar.PROXY.getSoundfontManager().getLocation(data).toString());
        ItemStack stack = BItemRegistry.RESONANT_PRISM.get().getDefaultInstance();
        stack.setTag(stackTag);
        return stack;
    }


    public record SoundfontTooltip(ResourceLocation icon) implements TooltipComponent {}

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ResourceLocation icon = lookup(stack).map(sf -> sf.name()).orElse(new ResourceLocation(Blockstar.MODID, "empty"));
        return Optional.of(new SoundfontTooltip(icon));
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.checkerframework.checker.nullness.qual.Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        Optional<SoundfontManager.SoundfontDefinition> resolved = lookup(stack).or(() -> base());

        if (resolved.isEmpty()) {
            list.add(Component.translatable("item.blockstar.resonant_prism.unknown").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
            return;
        }

        SoundfontManager.SoundfontDefinition data = resolved.get();

        list.add(Component.translatable(data.name().getNamespace() + ".soundfont." + data.name().getPath()).withStyle(Style.EMPTY.withColor(data.color())));

        int shown = 0, total = 0;
        for (InstrumentType type : data.instrumentData().keySet()) {
            total++;
            if (shown < 2 || Blockstar.PROXY.isScreenShiftDown()) {
                ResourceLocation instrument = new ResourceLocation(BInstrumentTypeRegistry.get(type).toString());
                list.add(Component.translatable(instrument.getNamespace() + ".instrument_type." + instrument.getPath()).withStyle(ChatFormatting.BLUE));
                shown++;
            }
        }
        if (total - shown > 0 && !Blockstar.PROXY.isScreenShiftDown()) {
            list.add(Component.translatable("item.blockstar.resonant_prism.more", total - shown)
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(true)));
        }
    }
}
