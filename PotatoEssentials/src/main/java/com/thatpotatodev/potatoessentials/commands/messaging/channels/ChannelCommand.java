package com.thatpotatodev.potatoessentials.commands.messaging.channels;

import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.CustomChat;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class ChannelCommand extends PotatoCommand {
    public ChannelCommand() {
        super("channel",NAMESPACE+".channel", "chat");
    }

    private final Argument<CustomChat> channelArgument = new ChannelArgument("channel");

    @Override
    public void register() {
        createCommand()
                .withArguments(channelArgument)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player player, CommandArguments args) {
        CustomChat channel = args.getByArgument(channelArgument);
        Objects.requireNonNull(channel);
        if ("global".equals(channel.getKey())) {
            CustomChat.getPlayerChat().remove(player);
            Component msg = Config.replaceFormat(
                    this.getMsg("chatChannelChange"),
                    new Replacer("chat-name", channel.getName())
            );
            player.sendMessage(msg);
            return;
        }
        if (CustomChat.getPlayerIgnoredChannels(player).contains(channel)) {
            player.sendMessage(Config.replaceFormat(
                    getMsg("channelChangeIgnored"),
                    new Replacer("channel", channel.getName())
            ));
            return;
        }
        CustomChat.getPlayerChat().put(player, channel);
        Component msg = Config.replaceFormat(Config.getCmdMsg("channel", "chatChannelChange"),
                new Replacer("chat-name", channel.getName()));
        player.sendMessage(msg);
    }

    public static class ChannelArgument extends CustomArgument<CustomChat, String> {
        public ChannelArgument(String nodeName) {
            super(new StringArgument(nodeName), info -> {
                CustomChat channel = CustomChat.getChatMap().get(info.input());
                if (channel == null || !channel.checkPerm(info.sender()))
                    throw CustomArgumentException.fromAdventureComponent(
                            Config.replaceFormat(
                                    Config.getCmdMsg("channel", "chatNotFound"),
                                    new Replacer("chat-name", info.input())
                            )
                    );
                else return channel;
            });
            this.replaceSuggestions(
                    ArgumentSuggestions.strings(info ->
                        CustomChat.getCustomChats().stream()
                                .filter(chat ->
                                        chat.checkPerm(info.sender())
                                ).map(CustomChat::getKey).toArray(String[]::new)
            ));
        }
        @Override
        public Class<CustomChat> getPrimitiveType() {
            return CustomChat.class;
        }
    }
}
