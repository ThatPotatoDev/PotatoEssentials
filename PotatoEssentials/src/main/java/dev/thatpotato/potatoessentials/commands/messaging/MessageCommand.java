package dev.thatpotato.potatoessentials.commands.messaging;

import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.thatpotato.potatoessentials.utils.Utils;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static dev.thatpotato.potatoessentials.commands.messaging.SocialSpyCommand.socialSpyList;

public class MessageCommand extends PotatoCommand {
    public MessageCommand() {
        super("message",NAMESPACE+".message","msg","tell", "whisper", "w");
    }

    @Getter
    private static final Map<CommandSender, CommandSender> messages = new DualHashBidiMap<>();
    private final Argument<Player> playerArgument = new EntitySelectorArgument.OnePlayer("player");
    private final Argument<String> stringArgument = new GreedyStringArgument("message");

    @Override
    public void register() {
        createCommand()
                .withArguments(playerArgument, stringArgument)
                .executes(this::execute)
                .register();
    }

    private void execute(CommandSender sender, CommandArguments args) {
        Player receiver = args.getOptionalByArgument(playerArgument).orElseThrow();
        if (!sender.hasPermission(NAMESPACE+".messagetoggle.bypass")
                && MessageToggleCommand.messagesDisabled.contains(receiver.getUniqueId())) {
            var msg = Config.replaceFormat(getMsg("messagesDisabled"),
                    new Replacer("receiver", receiver.getName())
            );
            sender.sendMessage(msg);
            return;
        }
        String message = args.getOptionalByArgument(stringArgument).orElseThrow();
        message(sender, message, receiver);
    }

    public static void message(CommandSender sender, String message, CommandSender receiver) {
        message = Utils.formatChatMessage(sender, message);
        Replacer[] replacers = new Replacer[]{
                new Replacer("sender", sender.getName()),
                new Replacer("sender-prefix", Utils.getPrefix(sender)),
                new Replacer("sender-suffix", Utils.getSuffix(sender)),
                new Replacer("receiver", receiver.getName()),
                new Replacer("receiver-prefix", Utils.getPrefix(receiver)),
                new Replacer("receiver-suffix", Utils.getSuffix(receiver)),
                new Replacer("message", message, false) };

        Component senderMsg = Config.replaceFormat(Config.messageSender(), replacers);

        Component receiverMsg = Config.replaceFormat(Config.messageReceiver(), replacers);

        Component socialSpyMsg = Config.replaceFormat(Config.messageSocialSpy(), replacers);

        for (CommandSender potentialSocialSpyReceiver : socialSpyList) {
            if(!socialSpyList.contains(potentialSocialSpyReceiver)) continue;
            if(receiver==potentialSocialSpyReceiver) continue;
            if(sender==potentialSocialSpyReceiver) continue;
            potentialSocialSpyReceiver.sendMessage(socialSpyMsg);
        }

        messages.put(receiver, sender);

        messages.put(sender, receiver);

        sender.sendMessage(senderMsg);
        receiver.sendMessage(receiverMsg);
    }

}
