package com.thatpotatodev.potatodiscordlink.commands;

import com.thatpotatodev.potatodiscordlink.PotatoDiscordLink;
import com.thatpotatodev.potatoessentials.libs.commandapi.CommandAPICommand;
import com.thatpotatodev.potatoessentials.libs.commandapi.executors.CommandArguments;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import org.bukkit.entity.Player;

public class UnlinkCommand extends PotatoCommand {
    public UnlinkCommand() {
        super("unlink",NAMESPACE+".discord.unlink");
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
        var uuid = player.getUniqueId();
        if (linkManager.getDiscordID(uuid) == null) {
            player.sendMessage(Config.replaceFormat(
                    PotatoDiscordLink.config().getString("messages.minecraft.notLinked"))
            );
            return;
        }
        player.sendMessage(Config.replaceFormat(PotatoDiscordLink.config().getString("messages.minecraft.unlinked")));
        linkManager.unlink(uuid);
        if (PotatoDiscordLink.config().getBoolean("linkRequiredToJoin")) {
            player.kick(Config.replaceFormat(
                    PotatoDiscordLink.config().getString("messages.minecraft.kickUnlinked")
            ));
        }
    }
}
