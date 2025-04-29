package com.ninni.blockstar.server.item;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
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
        if (stack.hasTag() && stack.getTag().contains("Soundfont")) {
            SoundfontManager.SoundfontDefinition data = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(stack.getTag().getString("Soundfont")));
            return data.rarity();
        }
        return super.getRarity(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Soundfont")) return 1;
        return super.getMaxStackSize(stack);
    }

    public static ItemStack getPrismItemFromSoundfont(SoundfontManager.SoundfontDefinition data) {
        CompoundTag stackTag = new CompoundTag();
        stackTag.putString("Soundfont", Blockstar.PROXY.getSoundfontManager().getLocation(data).toString());
        ItemStack stack = BItemRegistry.RESONANT_PRISM.get().getDefaultInstance();
        stack.setTag(stackTag);
        return stack;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.hasTag() && !stack.getTag().getString("Soundfont").isEmpty()) {
            SoundfontManager.SoundfontDefinition data = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(stack.getTag().getString("Soundfont")));
            return Optional.of(new SoundfontTooltip(data.name()));
        }
        return Optional.of(new SoundfontTooltip(new ResourceLocation(Blockstar.MODID, "empty")));
    }

    public record SoundfontTooltip(ResourceLocation icon) implements TooltipComponent {}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        if (stack.hasTag() && stack.getTag().contains("Soundfont") && !stack.getTag().getString("Soundfont").isEmpty()) {
            SoundfontManager.SoundfontDefinition data = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(stack.getTag().getString("Soundfont")));

            list.add(Component.translatable(data.name().getNamespace() + ".soundfont." + data.name().getPath()).withStyle(Style.EMPTY.withColor(data.color())));

            for (InstrumentType type : data.instrumentData().keySet()) {
                ResourceLocation instrument = new ResourceLocation(BInstrumentTypeRegistry.get(type).toString());
                list.add(Component.translatable(instrument.getNamespace() + ".instrument_type." + instrument.getPath()).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
