package com.ninni.blockstar.mixin;

import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BRecipeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
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
    private void overrideOtherStackedOnMe(ItemStack stack, ItemStack stack1, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess, CallbackInfoReturnable<Boolean> cir) {
        Level level = player.level();

        if (!stack.hasTag()) {
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

                cir.setReturnValue(true);
            }
        }

    }

}
