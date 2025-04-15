package com.ninni.blockstar.server.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class SoundfontConversionSerializer implements RecipeSerializer<SoundfontConversionRecipe> {

    @Override
    public SoundfontConversionRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient base = Ingredient.fromJson(json.get("base"));
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        JsonObject resultObj = json.getAsJsonObject("result");
        ItemStack result = ShapedRecipe.itemStackFromJson(resultObj);

        boolean playSound = GsonHelper.getAsBoolean(json, "play_sound", true);
        boolean shrinkInputs = GsonHelper.getAsBoolean(json, "shrink_inputs", true);

        return new SoundfontConversionRecipe(id, base, ingredient, result, playSound, shrinkInputs);
    }

    @Override
    public SoundfontConversionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        Ingredient base = Ingredient.fromNetwork(buffer);
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        ItemStack result = buffer.readItem();
        boolean playSound = buffer.readBoolean();
        boolean shrinkInputs = buffer.readBoolean();

        return new SoundfontConversionRecipe(id, base, ingredient, result, playSound, shrinkInputs);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, SoundfontConversionRecipe recipe) {
        recipe.getBase().toNetwork(buffer);
        recipe.getIngredient().toNetwork(buffer);
        buffer.writeItem(recipe.getResult());
        buffer.writeBoolean(recipe.shouldPlaySound());
        buffer.writeBoolean(recipe.shouldShrinkInputs());
    }
}

