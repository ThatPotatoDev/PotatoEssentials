package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.PotatoEssentials;
import com.thatpotatodev.potatoessentials.api.event.VanishToggleEvent;
import com.thatpotatodev.potatoessentials.listeners.PlayerJoinQuitListener;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static com.thatpotatodev.potatoessentials.PotatoEssentials.pluginManager;

public class VanishCommand extends PotatoCommand {

    public VanishCommand() {
        super("vanish", NAMESPACE+".vanish");
    }

    @Getter
    private static final Set<Player> vanishedPlayers = new HashSet<>();

    private final Argument<Player> vanishArg = new EntitySelectorArgument.OnePlayer("target");

    @Override
    public void register() {
        pluginManager.registerEvents(new PlayerJoinQuitListener(), PotatoEssentials.INSTANCE);

        new CommandAPICommand(name)
                .withPermission(permission)
                .withOptionalArguments(vanishArg)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player sender, CommandArguments args) {
        Player vanisher = args.getByArgumentOrDefault(vanishArg, sender);

        boolean vanished = vanishedPlayers.add(vanisher);
        if (!vanished) vanishedPlayers.remove(vanisher);

        var event = new VanishToggleEvent(vanisher, vanished);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        var msg = Config.replaceFormat(getMsg(vanished?"vanishMsg":"unvanishMsg"),
                new Replacer("player", vanisher.getName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission+".bypass")) continue;

            if (vanished) player.hidePlayer(INSTANCE, vanisher);
            if (!vanished) player.showPlayer(INSTANCE, vanisher);
        }
        Bukkit.broadcast(msg, permission);
    }
}
