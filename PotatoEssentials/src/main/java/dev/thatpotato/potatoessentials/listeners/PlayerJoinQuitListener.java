package dev.thatpotato.potatoessentials.listeners;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.commands.VanishCommand;
import dev.thatpotato.potatoessentials.commands.messaging.MessageCommand;
import dev.thatpotato.potatoessentials.commands.messaging.SocialSpyCommand;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.CustomChat;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.thatpotato.potatoessentials.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player joiner = e.getPlayer();
        if (Config.addEmojisToCustomChatCompletions()
                && joiner.hasPermission(PotatoEssentials.NAMESPACE+".chat.emojis"))
            joiner.addCustomChatCompletions(Config.getEmojis().keySet());

        if (joiner.hasPermission(PotatoEssentials.NAMESPACE+".vanish.bypass")) return;
        for (Player player : VanishCommand.getVanishedPlayers()) {
            joiner.hidePlayer(PotatoEssentials.INSTANCE, player);
        }
    }
    @EventHandler
    public void alsoOnJoin(PlayerJoinEvent e) {
        if (!Config.modifyConnectionMessages()) return;
        Player joiner = e.getPlayer();
        boolean isFirstJoin = !joiner.hasPlayedBefore();
        List<Replacer> replacers = new ArrayList<>();
        replacers.add(new Replacer("name", joiner.getName()));
        replacers.add(new Replacer("prefix", Utils.getPrefix(joiner)));
        replacers.add(new Replacer("suffix", Utils.getSuffix(joiner)));
        if (isFirstJoin) {
            replacers.add(new Replacer("join-number", Bukkit.getOfflinePlayers().length+""));
        }
        String format = isFirstJoin ? Config.firstJoinMessageFormat() : Config.joinMessageFormat();
        Component msg = Config.replaceFormat(format, replacers.toArray(new Replacer[0]));
        e.joinMessage(msg);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player quiter = e.getPlayer();
        SocialSpyCommand.socialSpyList.remove(quiter);
        CustomChat.getPlayerChat().remove(quiter);
        VanishCommand.getVanishedPlayers().remove(quiter);
        MessageCommand.getMessages().remove(quiter);
        PlayerTeleportListener.lastLocation.remove(quiter);
    }
    
    @EventHandler
    public void alsoOnQuit(PlayerQuitEvent e) {
        Player quiter = e.getPlayer();
        if (!Config.modifyConnectionMessages()) return;
        Component msg = Config.replaceFormat( Config.quitMessageFormat(),
                new Replacer("name", quiter.getName()),
                new Replacer("prefix", Utils.getPrefix(quiter)),
                new Replacer("suffix", Utils.getSuffix(quiter)));
        e.quitMessage(msg);
    }
}
