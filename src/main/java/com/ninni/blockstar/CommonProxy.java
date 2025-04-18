package com.ninni.blockstar;

import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.Function;

public class CommonProxy {
    @Nullable
    private SoundfontManager tapestryVariantManager;

    public void init() {
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public SoundfontManager getSoundfontManager() {
        if (tapestryVariantManager == null) {
            tapestryVariantManager = new SoundfontManager();
        }
        return tapestryVariantManager;
    }
}
