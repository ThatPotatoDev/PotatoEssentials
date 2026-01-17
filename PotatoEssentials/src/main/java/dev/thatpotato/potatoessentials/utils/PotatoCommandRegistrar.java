package dev.thatpotato.potatoessentials.utils;

import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.PotatoLogger;
import dev.thatpotato.potatoessentials.objects.PotatoPlugin;
import dev.jorel.commandapi.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

public class PotatoCommandRegistrar {
    @Getter
    private final PotatoPlugin plugin;
    private final PotatoLogger logger;
    @Getter
    private final List<String> registeredCommandNames = new ArrayList<>();
    private final HashMap<Class<? extends PotatoCommand>, PotatoCommand> commandInstances = new HashMap<>();
    @Setter
    private Function<String, Boolean> commandEnabledCheck;
    @Getter
    private final List<PotatoCommand> registeredCommands = new ArrayList<>();
    @Getter
    private final List<PotatoCommand> unregisteredCommands = new ArrayList<>();
    @Setter
    @Getter
    private BiFunction<String, String, String> commandMessageGetter;

    public PotatoCommandRegistrar(PotatoPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.setCommandEnabledCheck(name -> plugin.getConfig().getBoolean("commands."+name+".enabled"));
        this.setCommandMessageGetter((name, path) -> plugin.getConfig().getString("commands."+name+"."+path));
    }

    public <T extends PotatoCommand> T getCommandInstance(Class<T> clazz) {
        return clazz.cast(commandInstances.get(clazz));
    }

    public void register(PotatoCommand command, boolean forced) {
        if (!CommandAPIBukkit.getConfiguration().getNamespace().equals(plugin.getName().toLowerCase())) {
            try {
                Field field = InternalConfig.class.getDeclaredField("namespace");
                setField(field, CommandAPIBukkit.getConfiguration(), plugin.getName().toLowerCase());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "error setting namespace", e);
            }
        }
        if (!CommandAPIPaper.getConfiguration().getLifecycleEventOwner().equals(plugin)) {
            try {
                Field pluginMetaField = InternalPaperConfig.class.getDeclaredField("pluginMeta");
                setField(pluginMetaField, CommandAPIBukkit.getConfiguration(), plugin.getPluginMeta());
                Field lifecycleEventOwnerField = InternalPaperConfig.class.getDeclaredField("lifecycleEventOwner");
                setField(lifecycleEventOwnerField, CommandAPIBukkit.getConfiguration(), plugin);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "error setting plugin", e);
            }
        }
        String name = command.getName();
        if (commandInstances.containsKey(command.getClass())) {
            logger.warning("Command '"+command+"' was registered multiple times!");
        } else if (forced || commandEnabledCheck.apply(name)) {
            for (String alias : command.getAliases()) {
                CommandAPIBukkit.unregister(alias, false, true);
                CommandAPIBukkit.unregister(alias, false, false);
            }
            CommandAPIBukkit.unregister(name, false, true);
            CommandAPIBukkit.unregister(name, false, false);
            command.registrar = this;
            command.register();
            var msg = "Registering command '%s'";
            if (command.hasAliases()) msg += " with aliases '%s'";
            logger.debug(msg.formatted(name, formatList(command.getAliases())));
            commandInstances.put(command.getClass(), command);
            registeredCommandNames.add(name);
            registeredCommands.add(command);
        } else {
            logger.debug("Command '%s' disabled in config, not registering".formatted(name));
            unregisteredCommands.add(command);
        }
    }
    private void setField(Field field, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        if (!field.canAccess(obj)) field.setAccessible(true);
        field.set(obj, value);
    }
    public void register(List<PotatoCommand> commands) {
        for (PotatoCommand command : commands) {
            this.register(command, false);
        }
    }
    public void register(PotatoCommand... commands) {
        this.register(Arrays.asList(commands));
    }

    private static String formatList(String[] list) {
        String arrStr = Arrays.toString(list);
        return arrStr.substring(1, arrStr.length() - 1);
    }
}
