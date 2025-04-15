package com.ninni.blockstar.server.item.crafting;

import com.ninni.blockstar.registry.BRecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SoundfontConversionRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient base;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final boolean playSound;
    private final boolean shrinkInputs;

    public SoundfontConversionRecipe(ResourceLocation id, Ingredient base, Ingredient ingredient, ItemStack result, boolean playSound, boolean shrinkInputs) {
        this.id = id;
        this.base = base;
        this.ingredient = ingredient;
        this.result = result;
        this.playSound = playSound;
        this.shrinkInputs = shrinkInputs;
    }

    public boolean matches(ItemStack stack1, ItemStack stack2) {
        return (base.test(stack1) && ingredient.test(stack2)) || (base.test(stack2) && ingredient.test(stack1));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return matches(container.getItem(0), container.getItem(1));
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return result.copy();
    }

    public ItemStack assemble(ItemStack baseStack, ItemStack ingredientStack) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BRecipeRegistry.SOUNDFONT_CONVERSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BRecipeRegistry.SOUNDFONT_CONVERSION_TYPE.get();
    }

    public boolean shouldPlaySound() {
        return playSound;
    }

    public boolean shouldShrinkInputs() {
        return shrinkInputs;
    }

    public Ingredient getBase() {
        return base;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }
}

