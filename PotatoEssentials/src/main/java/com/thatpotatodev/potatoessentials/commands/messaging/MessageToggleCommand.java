package com.thatpotatodev.potatoessentials.commands.messaging;

import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MessageToggleCommand extends PotatoCommand {
    public MessageToggleCommand() {
        super("messagetoggle",NAMESPACE+".messagetoggle","msgtoggle", "tpm");
    }
    public static Set<UUID> messagesDisabled = new HashSet<>();

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .executesPlayer(this::execute)
                .register();

    }

    private void execute(Player player, CommandArguments args) {
        UUID uuid = player.getUniqueId();
        String toggle;
        if (messagesDisabled.contains(uuid)) {
            messagesDisabled.remove(uuid);
            toggle = "toggledOn";
        } else {
            messagesDisabled.add(uuid);
            toggle = "toggledOff";
        }
        var msg = Config.replaceFormat(Config.getCmdMsg("messagetoggle", toggle));
        player.sendMessage(msg);
    }
}
