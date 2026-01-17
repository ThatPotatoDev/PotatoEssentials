package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class HealCommand extends PotatoCommand {
    public HealCommand() {
        super("heal",NAMESPACE+".heal");
    }

    private final EntitySelectorArgument.ManyPlayers playerArg = new EntitySelectorArgument
            .ManyPlayers("target", false);
    private final Argument<Double> doubleArg = new DoubleArgument("amount");

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .withOptionalArguments(playerArg)
                .withOptionalArguments(doubleArg)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player sender, CommandArguments args) {
        Collection<Player> players = args.getByArgumentOrDefault(playerArg, List.of(sender));

        double amount = args.getByArgumentOrDefault(doubleArg, 20d);

        for (Player player : players) {
            player.heal(amount);
        }
        String msg = players.size()<2?((Player) players.toArray()[0]).getName()
                : players.size()+" Players";

        sender.sendRichMessage("<green>Successfully<gray> healed <yellow>"+msg);

    }
}
