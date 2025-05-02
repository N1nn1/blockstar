package com.ninni.blockstar.server.block;

import com.ninni.blockstar.server.instrument.InstrumentType;
import net.minecraft.world.level.block.BaseEntityBlock;

import java.util.function.Supplier;

public abstract class InstrumentBlock extends BaseEntityBlock {
    private final Supplier<InstrumentType> instrumentType;

    protected InstrumentBlock(Properties properties, Supplier<InstrumentType> instrumentType) {
        super(properties);
        this.instrumentType = instrumentType;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType.get();
    }
}
