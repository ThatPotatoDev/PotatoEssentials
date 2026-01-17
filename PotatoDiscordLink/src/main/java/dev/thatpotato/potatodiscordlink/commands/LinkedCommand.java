package dev.thatpotato.potatodiscordlink.commands;

import dev.thatpotato.potatodiscordlink.PotatoDiscordLink;
import dev.thatpotato.potatoessentials.libs.commandapi.CommandAPICommand;
import dev.thatpotato.potatoessentials.libs.commandapi.executors.CommandArguments;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.*;

public class LinkedCommand extends PotatoCommand {
    public LinkedCommand() {
        super("linked",NAMESPACE+".discord.linked");
    }
    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .executes(this::execute)
                .register();
    }
    private void execute(CommandSender sender, CommandArguments args) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, UUID> entry : PotatoDiscordLink.getLinkManager().getLinkedAccounts().entrySet()) {
            String name = Optional.ofNullable(Bukkit.getOfflinePlayer(entry.getValue()).getName()).orElse("Unknown");
            User user = PotatoDiscordLink.getJda().retrieveUserById(entry.getKey()).useCache(true).complete();
            list.add(String.valueOf(PotatoDiscordLink.config().getString("commands.linked.listFormat"))
                    .replace("<name>", name)
                    .replace("<discord>", user != null ? user.getName() : "Unknown"));
        }
        if (list.isEmpty()) {
            list.add("None");
        }
        String delimiter = Optional.ofNullable(PotatoDiscordLink.config().getString("commands.linked.listDelimiter")).orElse(", ");
        String prefix = String.valueOf(PotatoDiscordLink.config().getString("commands.linked.listPrefix"));
        String strList = String.join(delimiter, list);
        sender.sendMessage(Config.replaceFormat(prefix+strList));
    }
}
