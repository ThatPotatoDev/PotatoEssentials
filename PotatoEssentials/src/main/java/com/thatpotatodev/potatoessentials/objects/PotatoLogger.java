package com.thatpotatodev.potatoessentials.objects;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class PotatoLogger extends Logger {
    private final FileConfiguration config;

    public PotatoLogger(Logger logger, FileConfiguration config) {
        super(logger.getName(), logger.getResourceBundleName());
        this.setParent(logger);
        this.config = config;
    }

    public void debug(String msg) {
        if (!this.config.getBoolean("debug")) return;
        this.info("[DEBUG] %s".formatted(msg));
    }
}
