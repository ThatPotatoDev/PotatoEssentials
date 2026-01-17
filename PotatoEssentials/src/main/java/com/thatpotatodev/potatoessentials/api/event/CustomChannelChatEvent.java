package com.thatpotatodev.potatoessentials.api.event;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public class CustomChannelChatEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @Getter
    private final CommandSender sender;
    private final Component message;

    @Internal
    public CustomChannelChatEvent(@NotNull CommandSender sender, @NotNull Component message, boolean async) {
        super(async);
        this.sender = sender;
        this.message = message;
    }
    public Component message() {
        return message;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
