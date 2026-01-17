package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.listeners.PlayerTeleportListener;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import static dev.thatpotato.potatoessentials.PotatoEssentials.pluginManager;
import static dev.thatpotato.potatoessentials.listeners.PlayerTeleportListener.lastLocation;

public class BackCommand extends PotatoCommand {
    public BackCommand() {
        super("back", NAMESPACE+".back");
    }
    @Override
    public void register() {
        pluginManager.registerEvents(new PlayerTeleportListener(), PotatoEssentials.INSTANCE);

        new CommandAPICommand(name)
                .withPermission(permission)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player player, CommandArguments args) {
        if (lastLocation.get(player)!=null) {
            var msg = Config.replaceFormat(Config.getCmdMsg("back", "message"));
            player.sendMessage(msg);
            player.teleport(lastLocation.get(player));
        } else player.sendRichMessage("<red>No last location found!");
    }
}
