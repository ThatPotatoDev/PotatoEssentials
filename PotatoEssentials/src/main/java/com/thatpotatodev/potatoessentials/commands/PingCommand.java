package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import com.thatpotatodev.potatoessentials.utils.Config;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OnePlayer;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

public class PingCommand extends PotatoCommand {
    public PingCommand() {
        super("ping",NAMESPACE+".ping");
    }

    private final Argument<Player> playerArgument = new OnePlayer("player");

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .withOptionalArguments(playerArgument)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player sender, CommandArguments args) {
        Player arg = args.getByArgumentOrDefault(playerArgument, sender);
        var msg = Config.replaceFormat(getMsg("pingMessage"),
                new Replacer("player", arg.getName()), new Replacer("ping", arg.getPing()+"")
        );
        sender.sendMessage(msg);
    }
}
