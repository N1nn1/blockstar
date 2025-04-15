package com.ninni.blockstar.compat.jei;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.item.crafting.SoundfontConversionRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SoundfontConversionRecipeCategory implements IRecipeCategory<SoundfontConversionRecipe> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Blockstar.MODID, "textures/gui/jei_soundfont_conversion.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;
    private final IDrawable resultArrow;

    public SoundfontConversionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(150, 40);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BItemRegistry.RESONANT_PRISM.get()));
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createDrawable(TEXTURE, 0, 0, 24, 16);
        this.resultArrow = guiHelper.createDrawable(TEXTURE, 0, 16, 16, 6);
    }

    @Override
    public RecipeType<SoundfontConversionRecipe> getRecipeType() {
        return JEIPlugin.SOUNDFONT_CONVERSION_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.blockstar.soundfont_conversion");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoundfontConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 11).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 11).addIngredients(recipe.getBase());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 11).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(SoundfontConversionRecipe recipe, IRecipeSlotsView view, GuiGraphics stack, double mouseX, double mouseY) {
        slotDrawable.draw(stack, 19, 10);
        slotDrawable.draw(stack, 49, 10);
        slotDrawable.draw(stack, 109, 10);
        arrow.draw(stack, 78, 11);
        resultArrow.draw(stack, 35, 2);
    }
}