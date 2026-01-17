package com.thatpotatodev.potatoessentials.commands.teleporting;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import com.thatpotatodev.potatoessentials.utils.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class TeleportAllCommand extends PotatoCommand {
    public TeleportAllCommand() {
        super("tpall",NAMESPACE + ".tpall", "teleportall");
    }

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .executesPlayer(this::execute)
                .register();
    }

    private void execute(Player sender, CommandArguments args) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Component tpedMsg = Config.replaceFormat(Config.teleportedMsg(),
                new Replacer("teleporter", sender.getName()));

        for (Player player : players) {
            player.teleport(sender.getLocation());
            player.sendMessage(tpedMsg);
        }
        String msg = Utils.nameFormat(players);
        Component tperMsg = Config.replaceFormat(Config.teleporterMsg(),
                new Replacer("teleported", msg));
        sender.sendMessage(tperMsg);
    }
}
