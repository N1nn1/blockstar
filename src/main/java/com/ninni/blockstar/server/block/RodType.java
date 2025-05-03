package com.ninni.blockstar.server.block;

import net.minecraft.util.StringRepresentable;

public enum RodType implements StringRepresentable {
    LEFT("left"),
    RIGHT("right"),
    MIDDLE("middle");

    private final String name;

    RodType(String string) {
        this.name = string;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
