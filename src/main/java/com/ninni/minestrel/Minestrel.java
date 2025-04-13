package com.ninni.minestrel;

import com.mojang.logging.LogUtils;
import com.ninni.minestrel.registry.MBlockEntityRegistry;
import com.ninni.minestrel.registry.MBlockRegistry;
import com.ninni.minestrel.registry.MCreativeModeTabRegistry;
import com.ninni.minestrel.registry.MItemRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Minestrel.MODID)
public class Minestrel {
    public static final String MODID = "minestrel";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Minestrel() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MBlockRegistry.DEF_REG.register(modEventBus);
        MItemRegistry.DEF_REG.register(modEventBus);
        MBlockEntityRegistry.DEF_REG.register(modEventBus);
        MCreativeModeTabRegistry.DEF_REG.register(modEventBus);
    }
}
