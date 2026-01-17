package dev.thatpotato.potatoessentials.commands.warping;

import dev.thatpotato.potatoessentials.database.WarpsManager;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

public class DelWarpCommand extends PotatoCommand {

    public DelWarpCommand() {
        super("deletewarp",NAMESPACE+".deletewarp", "delwarp");
    }

    private final Argument<String> warpNameArg = new StringArgument("warp-name");

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .withArguments(warpNameArg)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player player, CommandArguments args) {
        String warpName = args.getByArgument(warpNameArg);
        WarpsManager.removeWarp(warpName);
        var msg = Config.replaceFormat(getMsg("warpDeleted"),
                new Replacer("warp-name", warpName));
        player.sendMessage(msg);
    }
}
