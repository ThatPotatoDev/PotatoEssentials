package dev.thatpotato.potatoessentials.commands.warping;

import dev.thatpotato.potatoessentials.database.WarpsManager;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.thatpotato.potatoessentials.utils.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetWarpCommand extends PotatoCommand {

    public SetWarpCommand() {
        super("setwarp", NAMESPACE+".setwarp");
    }

    private final Argument<String> warpNameArg = new StringArgument("warp-name");

    @Override
    public void register() {

        new CommandAPICommand(name)
                .withPermission(permission)
                .withArguments(warpNameArg)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player player, CommandArguments args) {
        String warpName = args.getByArgument(warpNameArg);
        Location loc = player.getLocation();
        if (WarpsManager.warpExists(warpName)) {
            var msg = Config.replaceFormat(getMsg("warpExists"));
            player.sendMessage(msg);
            return;
        }
        WarpsManager.removeWarp(warpName);
        WarpsManager.saveWarp(warpName,loc);

        var formattedLoc = Utils.formatCoords(loc);

        var msg = Config.replaceFormat(getMsg("warpSet"),
                new Replacer("warp-name", warpName), new Replacer("location", formattedLoc));

        player.sendMessage(msg);
    }
}
