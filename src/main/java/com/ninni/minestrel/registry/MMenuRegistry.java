package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.server.inventory.KeyboardMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MMenuRegistry {
    public static final DeferredRegister<MenuType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Minestrel.MODID);

    public static final RegistryObject<MenuType<KeyboardMenu>> KEYBOARD = DEF_REG.register("keyboard", () -> new MenuType<>(KeyboardMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
