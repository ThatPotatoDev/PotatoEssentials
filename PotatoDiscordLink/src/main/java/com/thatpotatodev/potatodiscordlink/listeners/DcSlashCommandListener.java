package com.thatpotatodev.potatodiscordlink.listeners;

import com.thatpotatodev.potatodiscordlink.PotatoDiscordLink;
import com.thatpotatodev.potatoessentials.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.util.Objects;

public class DcSlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        switch (e.getName()) {
            case "link":
                String code = Objects.requireNonNull(e.getOption("code")).getAsString();
                e.reply(PotatoDiscordLink.getLinkManager().process(code, e.getUser())).queue();
                break;
            case "unlink":
                var linkManager = PotatoDiscordLink.getLinkManager();
                var id = e.getUser().getId();
                var uuid = linkManager.getUUID(id);
                if (uuid == null) {
                    e.reply(Objects.requireNonNull(PotatoDiscordLink.config().getString("messages.discord.notLinked"))).queue();
                    return;
                }
                var player = Bukkit.getOfflinePlayer(uuid);
                e.reply(Objects.requireNonNull(PotatoDiscordLink.config().getString("messages.discord.unlinked"))
                        .replace("<player>", player.getName()!=null?player.getName():"Unknown")
                        .replace("<uuid>", uuid.toString())).queue();
                linkManager.unlink(id);
                var onlinePlayer = player.getPlayer();
                if (PotatoDiscordLink.config().getBoolean("linkRequiredToJoin") && onlinePlayer!=null) {
                    onlinePlayer.kick(Utils.miniMessage(
                            PotatoDiscordLink.config().getString("messages.minecraft.kickUnlinked")
                    ));
                }
                break;
        }
    }
}
