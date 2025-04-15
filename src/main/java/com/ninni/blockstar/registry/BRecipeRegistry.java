package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionRecipe;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> DEF_REG_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Blockstar.MODID);
    public static final DeferredRegister<RecipeType<?>> DEF_REG_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Blockstar.MODID);

    public static final RegistryObject<RecipeSerializer<SoundfontConversionRecipe>> SOUNDFONT_CONVERSION_SERIALIZER =
            DEF_REG_SERIALIZERS.register("soundfont_conversion", SoundfontConversionSerializer::new);

    public static final RegistryObject<RecipeType<SoundfontConversionRecipe>> SOUNDFONT_CONVERSION_TYPE =
            DEF_REG_TYPES.register("soundfont_conversion", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Blockstar.MODID + ":soundfont_conversion";
                }
            });

    public static void register(IEventBus modEventBus) {
        DEF_REG_SERIALIZERS.register(modEventBus);
        DEF_REG_TYPES.register(modEventBus);
    }
}
