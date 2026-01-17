package dev.thatpotato.potatoessentials.objects;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.utils.PotatoCommandRegistrar;
import dev.jorel.commandapi.CommandAPICommand;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class PotatoCommand {
    protected static final String NAMESPACE = PotatoEssentials.NAMESPACE;
    protected final PotatoEssentials INSTANCE = PotatoEssentials.INSTANCE;
    @Getter
    protected String name;
    @Getter
    protected String[] aliases;
    @Getter
    protected String permission;
    protected boolean hasAliases;
    protected CommandAPICommand command;
    public PotatoCommandRegistrar registrar;

    protected PotatoCommand(@NotNull String name, @NotNull String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        this.hasAliases = aliases.length > 0;
    }

    public abstract void register();

    protected CommandAPICommand createCommand(CommandAPICommand... subCommands) {
        this.command = new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .withSubcommands(subCommands);
        return this.command;
    }
    protected CommandAPICommand createSubcommand(String name, @Nullable String permission) {
        var command = new CommandAPICommand(name);
        if (permission != null) command.withPermission(permission);
        return command;
    }

    protected CommandAPICommand createSubcommand(String name) {
        return createSubcommand(name, null);
    }

    public boolean hasAliases() {
        return hasAliases;
    }

    protected String getMsg(String path) {
        return this.registrar.getCommandMessageGetter().apply(this.name, path);
    }
}
