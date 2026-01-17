package com.thatpotatodev.potatoessentials.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand extends PotatoCommand {

    private final PlayerProfileArgument arg = new PlayerProfileArgument("owner");
    public SkullCommand() {
        super("skull",NAMESPACE+".skull","head");
    }
    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .withAliases(aliases)
                .withArguments(arg)
                .executesPlayer(this::getSkull)
                .register();
    }
    private void getSkull(Player player, CommandArguments args) {
        PlayerProfile profile = (PlayerProfile) args.getOptionalByArgument(arg).orElseThrow().getFirst();
        if (profile==null) return;

        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        profile.complete(true);
        is.editMeta(meta ->
            ((SkullMeta)meta).setPlayerProfile(profile)
        );

        player.give(is);
    }
}
