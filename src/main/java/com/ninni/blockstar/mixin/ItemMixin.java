package com.ninni.blockstar.mixin;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BRecipeRegistry;
import com.ninni.blockstar.registry.BSoundEventRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.instrument.InstrumentType;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(at = @At("HEAD"), method = "overrideOtherStackedOnMe", cancellable = true)
    private void B$overrideOtherStackedOnMe(ItemStack stack, ItemStack stack1, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess, CallbackInfoReturnable<Boolean> cir) {
        Level level = player.level();

        if (!stack.hasTag() && !stack1.hasTag()) {
            Optional<SoundfontConversionRecipe> recipe = level.getRecipeManager()
                    .getAllRecipesFor(BRecipeRegistry.SOUNDFONT_CONVERSION_TYPE.get())
                    .stream()
                    .filter(r -> r.matches(stack, stack1))
                    .findFirst();

            if (recipe.isPresent()) {
                ItemStack result = recipe.get().assemble(stack, stack1);

                if (stack.getCount() == 1 && recipe.get().shouldShrinkInputs()) {
                    slot.set(result);
                    stack1.shrink(1);
                } else {

                    if (!player.getInventory().add(result)) player.drop(result, false);

                    if (recipe.get().shouldShrinkInputs()) {
                        stack.shrink(1);
                        stack1.shrink(1);
                    }
                }

                if (recipe.get().shouldPlaySound()) {
                    SoundfontManager.SoundfontDefinition soundfont = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(result.getTag().getString("Soundfont")));

                    InstrumentType instrumentType = soundfont.instrumentData().keySet().stream().findFirst().get();
                    SoundfontManager.InstrumentSoundfontData forInstrument = soundfont.getForInstrument(instrumentType);
                    String velocity = forInstrument.velocityLayers().isPresent() ? "_" + forInstrument.velocityLayers().get() : "";
                    ResourceLocation resourceLocation = new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(instrumentType).getPath() + "." + soundfont.name().getPath() + "." + forInstrument.getClosestSampleNote(60) + velocity);

                    //Minecraft.getInstance().getSoundManager().play(new SoundfontSound(60, resourceLocation, 1, 1, player, Optional.empty()));
                    player.playNotifySound(BSoundEventRegistry.RESONANT_PRISM_TUNE.get(), SoundSource.PLAYERS, 1,1);
                }

                cir.setReturnValue(true);
            }
        }

    }

}
