package com.thatpotatodev.potatoessentials.commands.messaging;

import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class SocialSpyCommand extends PotatoCommand {
    public SocialSpyCommand() {
        super("socialspy", NAMESPACE+".socialspy");
    }

    public static Set<CommandSender> socialSpyList = new HashSet<>();

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .executes(this::execute)
                .register();
    }
    private void execute(CommandSender sender, CommandArguments args) {
        if (!socialSpyList.contains(sender)){
            socialSpyList.add(sender);
        } else {
            socialSpyList.remove(sender);
        }
        boolean enabled = socialSpyList.contains(sender);
        var msg = Config.replaceFormat(getMsg(enabled?"socialSpyEnabled":"socialSpyDisabled"));
        sender.sendMessage(msg);
    }
}
