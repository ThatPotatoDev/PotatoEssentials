package dev.thatpotato.potatoessentials.objects;

import dev.thatpotato.potatoessentials.utils.PotatoCommandRegistrar;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class PotatoPlugin extends JavaPlugin {
    private final PotatoLogger logger = new PotatoLogger(super.getLogger(), this.getConfig());
    @Getter
    private final PotatoCommandRegistrar commandRegistrar = new PotatoCommandRegistrar(this);

    @Override
    public @NotNull PotatoLogger getLogger() {
        return this.logger;
    }
}
