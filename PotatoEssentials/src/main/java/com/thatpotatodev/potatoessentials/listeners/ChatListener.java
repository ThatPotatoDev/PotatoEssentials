package com.thatpotatodev.potatoessentials.listeners;

import com.thatpotatodev.potatoessentials.PotatoEssentials;
import com.thatpotatodev.potatoessentials.api.event.ChatCooldownEvent;
import com.thatpotatodev.potatoessentials.commands.messaging.MuteChatCommand;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.CustomChat;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import com.thatpotatodev.potatoessentials.utils.Utils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ChatListener implements Listener {

    private final HashMap<Player, Long> lastChatTime = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent e) {
        if (!Config.chatEnabled()) return;
        e.setCancelled(true);
        int cooldown = Config.getInt("chat.cooldown");
        Player player = e.getPlayer();
        boolean canBypassMuteChat = player.hasPermission(PotatoEssentials.NAMESPACE+".chat.bypassmutedchat");
        if (MuteChatCommand.chatMuted && !canBypassMuteChat) {
            player.sendRichMessage("<red>The chat is currently muted!");
            return;
        }
        boolean canBypassCooldown = player.hasPermission(PotatoEssentials.NAMESPACE+".chat.bypass-cooldown");
        if (!CustomChat.getPlayerChat().containsKey(player) && lastChatTime.containsKey(player) && !canBypassCooldown) {
            long lastTime = lastChatTime.get(player);
            long currentTime = System.currentTimeMillis();
            long cooldownMillis = cooldown * 1000L;
            long timeElapsed = currentTime - lastTime;
            long cooldownRemainderMillis = (cooldownMillis - timeElapsed);

            if (timeElapsed < cooldownMillis) {
                DecimalFormat df = new DecimalFormat("0.0");
                double cooldownRemainder = cooldownRemainderMillis / 1000D;
                var msg = Config.replaceFormat(Config.getString("chat.cooldownMessage"),
                        new Replacer("cooldown", df.format(cooldownRemainder)));
                var event = new ChatCooldownEvent(player, msg, cooldownRemainder,"global", e.isAsynchronous());
                Bukkit.getPluginManager().callEvent(event);
                player.sendMessage(msg);
                return;
            }
        }

        lastChatTime.put(player, System.currentTimeMillis());
        String message = PlainTextComponentSerializer.plainText().serialize(e.message());

        message = Utils.formatChatMessage(player, message);

        Component finalMessage = Config.replaceFormat(Config.chatFormat(),
                new Replacer("prefix", Utils.getPrefix(player)),
                new Replacer("name", player.getName()),
                new Replacer("suffix", Utils.getSuffix(player)),
                new Replacer("message", message, false));
        Map<Player, CustomChat> playerChatMap = CustomChat.getPlayerChat();
        if (Config.customChatsEnabled() && playerChatMap.containsKey(player)) {
            playerChatMap.get(player).sendMessage(player, message, e.isAsynchronous());
            return;
        }
        Bukkit.broadcast(finalMessage);
    }

}
