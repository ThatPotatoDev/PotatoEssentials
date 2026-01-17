package com.thatpotatodev.potatoessentials.commands.messaging;

import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MuteChatCommand extends PotatoCommand {
    public MuteChatCommand() {
        super("mutechat", NAMESPACE+".mutechat");
    }

    public static boolean chatMuted = false;

    @Override
    public void register() {

        new CommandAPICommand(name)
                .withPermission(permission)
                .executes(this::execute)
                .register();
    }
    private void execute(CommandSender sender, CommandArguments args) {
        chatMuted = !chatMuted;
        Component msg = Config.replaceFormat(chatMuted?Config.muteChatMutedMsg()
                        : Config.muteChatUnmutedMsg(),
                new Replacer("sender", sender.getName()));
        Bukkit.broadcast(msg);
    }
}
