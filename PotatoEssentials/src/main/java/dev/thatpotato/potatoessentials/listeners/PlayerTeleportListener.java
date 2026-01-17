package dev.thatpotato.potatoessentials.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.HashMap;
import java.util.Map;

public class PlayerTeleportListener implements Listener {
    public static Map<Player, Location> lastLocation = new HashMap<>();
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.PLUGIN || e.getCause() == TeleportCause.COMMAND )
            lastLocation.put(e.getPlayer(), e.getFrom());
    }
}
