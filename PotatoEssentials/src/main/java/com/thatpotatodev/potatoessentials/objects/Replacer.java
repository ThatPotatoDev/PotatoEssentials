package com.thatpotatodev.potatoessentials.objects;

import lombok.Getter;

public class Replacer {
    @Getter
    private final String oldText;
    @Getter
    private final String newText;
    private final boolean replaceRaw;

    public Replacer(String oldText, String newText) {
        this(oldText, newText, true);
    }
    public Replacer(String oldText, String newText, boolean replaceRaw) {
        this.oldText = oldText;
        this.newText = newText;
        this.replaceRaw = replaceRaw;
    }
    public boolean replaceRaw() {
        return replaceRaw;
    }
}
