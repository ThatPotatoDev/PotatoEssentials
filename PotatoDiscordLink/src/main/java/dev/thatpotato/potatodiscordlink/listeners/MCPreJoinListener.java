package dev.thatpotato.potatodiscordlink.listeners;

import dev.thatpotato.potatodiscordlink.PotatoDiscordLink;
import dev.thatpotato.potatodiscordlink.commands.LinkCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class MCPreJoinListener implements Listener {
    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        if (!PotatoDiscordLink.config().getBoolean("linkRequiredToJoin")) return;
        var linkManager = PotatoDiscordLink.getLinkManager();
        var uuid = e.getUniqueId();
        if (linkManager.getDiscordID(uuid)!=null) return;

        linkManager.getLinkingCodes().removeValue(uuid);

        String code = linkManager.generateCode(uuid);
        e.disallow(Result.KICK_OTHER, LinkCommand.getLinkMessage(code));
    }
}
