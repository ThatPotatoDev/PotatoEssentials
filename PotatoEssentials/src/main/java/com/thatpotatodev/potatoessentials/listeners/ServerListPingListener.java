package com.thatpotatodev.potatoessentials.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent.ListedPlayerInfo;
import com.thatpotatodev.potatoessentials.utils.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class ServerListPingListener implements Listener {

    public static Component motd;
    public static List<String> hoverInfo;

    @EventHandler
    public void onServerListPing(PaperServerListPingEvent e) {
        if (Config.motdEnabled)
            e.motd(motd);
        if (!Config.hoverInfoEnabled) return;
        e.getListedPlayers().clear();
        hoverInfo.forEach(info ->
            e.getListedPlayers().add(new ListedPlayerInfo(info, UUID.randomUUID()))
        );
    }

}
