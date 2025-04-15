package com.ninni.blockstar.compat.jei;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BRecipeRegistry;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation ID = new ResourceLocation(Blockstar.MODID, "jei_plugin");
    public static final RecipeType<SoundfontConversionRecipe> SOUNDFONT_CONVERSION_RECIPE_TYPE =
            RecipeType.create(Blockstar.MODID, "soundfont_conversion", SoundfontConversionRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new SoundfontConversionRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<SoundfontConversionRecipe> recipes = recipeManager.getAllRecipesFor(BRecipeRegistry.SOUNDFONT_CONVERSION_TYPE.get());
        registration.addRecipes(SOUNDFONT_CONVERSION_RECIPE_TYPE, recipes);
    }
}