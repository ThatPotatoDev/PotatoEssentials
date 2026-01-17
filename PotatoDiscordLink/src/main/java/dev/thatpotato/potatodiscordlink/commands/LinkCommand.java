package dev.thatpotato.potatodiscordlink.commands;

import dev.thatpotato.potatodiscordlink.PotatoDiscordLink;
import dev.thatpotato.potatoessentials.libs.commandapi.CommandAPICommand;
import dev.thatpotato.potatoessentials.libs.commandapi.executors.CommandArguments;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.thatpotato.potatoessentials.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LinkCommand extends PotatoCommand {
    public LinkCommand() {
        super("link",NAMESPACE+".discord.link");
    }
    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player player, CommandArguments args) {
        var linkManager = PotatoDiscordLink.getLinkManager();
        String code = linkManager.generateCode(player.getUniqueId());
        if ("already linked".equals(code)) {
            player.sendMessage(
                    Utils.miniMessage(PotatoDiscordLink.config().getString("messages.minecraft.alreadyLinked"))
            );
            return;
        }
        player.sendMessage(getLinkMessage(code));
    }
    public static Component getLinkMessage(String code) {
        return Config.replaceFormat(
                        String.join("<newline>", PotatoDiscordLink.config().getStringList("messages.minecraft.linkMessage")),
                        new Replacer("code", code),
                        new Replacer("invite", PotatoDiscordLink.config().getString("discordInvite")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ClickEvent.Payload.string(code)))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.text(Objects.requireNonNull(PotatoDiscordLink.config().getString("messages.minecraft.codeHoverMsg")))));
    }
}
