package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.database.KitManager;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import com.thatpotatodev.potatoessentials.utils.Utils;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyPlayers;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class KitCommand extends PotatoCommand {
    private final KitManager kitManager;
    public KitCommand(KitManager kitManager) {
        super("kit", NAMESPACE+".kit");
        this.kitManager = kitManager;
        this.kitNameArg = new StringArgument("kitName").
                replaceSuggestions(ArgumentSuggestions.strings(kitManager.getKitNames()));
    }

    private final Argument<String> kitNameArg;
    private final ManyPlayers kitGivePlayersArg = new ManyPlayers("target", false);

    @Override
    public void register() {
        createCommand(
                createSubcommand("create", permission+".create")
                        .withArguments(kitNameArg)
                        .executesPlayer(this::executeCreate),
                createSubcommand("give", permission+".give")
                        .withArguments(kitNameArg)
                        .withOptionalArguments(kitGivePlayersArg)
                        .executesPlayer(this::executeGive),
                createSubcommand("delete", permission+".delete")
                        .withArguments(kitNameArg)
                        .executesPlayer(this::executeDelete)
        ).register();
    }
    private void executeCreate(Player player, CommandArguments args) {
        String kitName = args.getByArgument(kitNameArg);
        if (kitManager.getKitNames().contains(kitName)) {
            var msg = Config.replaceFormat(Config.getCmdMsg("kit", "kitAlreadyExists"),
                    new Replacer("kit-name", kitName));
            player.sendMessage(msg);
            return;
        }
        if (player.getInventory().isEmpty()) {
            var msg = Config.replaceFormat(Config.getCmdMsg("kit", "emptyInventory"));
            player.sendMessage(msg);
            return;
        }
        var msg = Config.replaceFormat(Config.getCmdMsg("kit", "kitCreate"),
                new Replacer("kit-name", kitName));
        player.sendMessage(msg);
        kitManager.saveKit(kitName, player);
    }

    private void executeGive(Player player, CommandArguments args) {
        String kitName = args.getByArgument(kitNameArg);
        Collection<Player> players = args.getByArgumentOrDefault(kitGivePlayersArg, List.of(player));
        if (!kitManager.getKitNames().contains(kitName)) {
            var msg = Config.replaceFormat(Config.getCmdMsg("kit", "kitNotFound"),
                    new Replacer("kit-name", kitName));
            player.sendMessage(msg);
            return;
        }
        var msg = Config.replaceFormat(Config.getCmdMsg("kit","kitGiveTo"),
                new Replacer("kit-name", kitName),
                new Replacer("target", Utils.nameFormat(players)));
        player.sendMessage(msg);
        players.forEach(p -> kitManager.giveKit(kitName, p));
    }

    private void executeDelete(Player player, CommandArguments args) {
        String kitName = args.getByArgument(kitNameArg);
        if (!kitManager.getKitNames().contains(kitName)) {
            var msg = Config.replaceFormat(Config.getCmdMsg("kit", "kitNotFound"),
                    new Replacer("kit-name", kitName));
            player.sendMessage(msg);
            return;
        }
        kitManager.deleteKit(kitName);
        var msg = Config.replaceFormat(Config.getCmdMsg("kit", "kitDelete"),
                new Replacer("kit-name", kitName));
        player.sendMessage(msg);
    }
}
