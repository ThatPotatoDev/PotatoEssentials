package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.PotatoEssentials;
import com.thatpotatodev.potatoessentials.listeners.PlayerTeleportListener;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import static com.thatpotatodev.potatoessentials.PotatoEssentials.pluginManager;
import static com.thatpotatodev.potatoessentials.listeners.PlayerTeleportListener.lastLocation;

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
