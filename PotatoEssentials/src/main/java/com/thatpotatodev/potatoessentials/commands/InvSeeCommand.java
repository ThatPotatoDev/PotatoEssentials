package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class InvSeeCommand extends PotatoCommand {

    public InvSeeCommand() {
        super("invsee",NAMESPACE+".invsee");
    }

    private final Argument<Player> arg = new EntitySelectorArgument.OnePlayer("target");

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .withArguments(arg)
                .executesPlayer(this::execute)
                .register();
    }

    private void execute(Player sender, CommandArguments args) {
        Player player = Objects.requireNonNull(args.getByArgument(arg));
        if (sender==player) {
            sender.sendRichMessage("<red>You cannot /invsee yourself!");
            return;
        }
        Inventory inv = player.getInventory();
        sender.openInventory(inv);
    }
}
