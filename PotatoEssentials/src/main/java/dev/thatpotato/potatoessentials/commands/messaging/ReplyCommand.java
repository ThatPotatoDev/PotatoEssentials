package dev.thatpotato.potatoessentials.commands.messaging;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static dev.thatpotato.potatoessentials.commands.messaging.MessageToggleCommand.messagesDisabled;

public class ReplyCommand extends PotatoCommand {
    public ReplyCommand() {
        super("reply",PotatoEssentials.NAMESPACE+".message", "r");
    }

    private final Argument<String> stringArgument = new GreedyStringArgument("message");

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .withArguments(stringArgument)
                .executes(this::execute)
                .register();
    }
    private void execute(CommandSender sender, CommandArguments args) {
        var messageMap = MessageCommand.getMessages();
        if (!messageMap.containsKey(sender)) {
            var msg = Config.replaceFormat(getMsg("noReply"));
            sender.sendMessage(msg);
            return;
        }
        String message = Objects.requireNonNull(args.getByArgument(stringArgument));
        CommandSender lastMessaged = messageMap.get(sender);
        if (lastMessaged instanceof Player p &&  messagesDisabled.contains(p.getUniqueId()) &&
                !sender.hasPermission(NAMESPACE+".messagetoggle.bypass")) {
            var msg = Config.replaceFormat(Config.getCmdMsg("message", "messagesDisabled"),
                    new Replacer("receiver", lastMessaged.getName()));
            sender.sendMessage(msg);
            return;
        }
        MessageCommand.message(sender, message, lastMessaged);
    }
}