package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.listeners.HungerChangeListener;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HungerCommand extends PotatoCommand {

    public HungerCommand() {
        super("hunger",NAMESPACE+".hunger");
    }

    public static boolean hungerEnabled = true;

    @Override
    public void register() {

        PotatoEssentials.pluginManager
                .registerEvents(new HungerChangeListener(), PotatoEssentials.INSTANCE);

        new CommandAPICommand(name)
                .withPermission(permission)
                .executes(this::execute)
                .register();
    }
    private void execute(CommandSender sender, CommandArguments args) {
        hungerEnabled=!hungerEnabled;
        //todo: smth needs to change here...
        // and make this msg configurable

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(PotatoEssentials.NAMESPACE+".hunger")) continue;
            player.sendRichMessage("<gray>[<gold>Hunger</gold>] " +
                    (hungerEnabled ? "<green>Enabled" : "<red>Disabled"));
        }
    }
}
