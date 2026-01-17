package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearChatCommand extends PotatoCommand {
    public ClearChatCommand() {
        super("clearchat", NAMESPACE+".clearchat");
    }
    private final String lineBreaks = "\n ".repeat(256);

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .executes((sender, args) -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(lineBreaks);
                    }
                    var msg = Config.replaceFormat(Config.getCmdMsg("clearchat", "message"),
                            new Replacer("name", sender.getName()));
                    Bukkit.broadcast(msg);
                })
                .register();
    }
}
