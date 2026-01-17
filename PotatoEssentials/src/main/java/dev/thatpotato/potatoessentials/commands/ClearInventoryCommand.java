package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.thatpotato.potatoessentials.utils.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class ClearInventoryCommand extends PotatoCommand {
    public ClearInventoryCommand() {
        super("clearinventory",NAMESPACE+".clearinventory", "ci");
    }

    private final EntitySelectorArgument.ManyPlayers playerArg = new EntitySelectorArgument
            .ManyPlayers("target", false);

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .withOptionalArguments(playerArg)
                .executesPlayer(this::execute)
                .register();

    }
    private void execute(Player sender, CommandArguments args) {
        Collection<Player> players = args.getByArgumentOrDefault(playerArg, List.of(sender));
        for (Player p : players) {
            p.getInventory().clear();
        }
        var msg = Config.replaceFormat(getMsg("clearInventory"),
                new Replacer("target", Utils.nameFormat(players)));
        sender.sendMessage(msg);
    }
}
