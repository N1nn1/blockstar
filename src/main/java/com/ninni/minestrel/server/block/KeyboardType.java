package com.ninni.minestrel.server.block;

import net.minecraft.util.StringRepresentable;

public enum KeyboardType implements StringRepresentable {
    SINGLE("single"),
    LEFT("left"),
    RIGHT("right"),
    MIDDLE("middle");

    private final String name;

    KeyboardType(String string) {
        this.name = string;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
