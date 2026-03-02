package dev.thatpotato.potatoessentials.api.event;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Called when a player messages another player using /message
 *
 */

public class PlayerMessagePlayerEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    @Getter
    private final Player sender;
    @Getter
    private final Player receiver;
    @Getter
    private final String messageContent;
    /**
     * The message sent to the sender.
     */
    @Getter
    private final Component senderMessage;
    /**
     * The message sent to the receiver.
     */
    @Getter
    private final Component receiverMessage;
    @Getter
    private final boolean reply;

    @Internal
    public PlayerMessagePlayerEvent(
            @NotNull Player sender,
            @NotNull Player receiver,
            String messageContent,
            Component senderMessage,
            Component receiverMessage,
            boolean isReply
    ) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        this.receiverMessage = receiverMessage;
        this.senderMessage = senderMessage;
        this.reply = isReply;
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
