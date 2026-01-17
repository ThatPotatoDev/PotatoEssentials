package com.thatpotatodev.potatoessentials.commands.warping;

import com.thatpotatodev.potatoessentials.database.WarpsManager;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class WarpCommand extends PotatoCommand {

    public WarpCommand() {
        super("warp",NAMESPACE+".warp");
    }

    private final Argument<String> warpNameArg = new StringArgument("warp-name")
            .replaceSuggestions(ArgumentSuggestions.strings(info ->
                    Arrays.stream(WarpsManager.getWarps().clone()).filter(warp ->
                            info.sender().hasPermission(this.permission)
                    ).toArray(String[]::new)
            ));
    @Override
    public void register() {
        createCommand()
                .withArguments(warpNameArg)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player sender, CommandArguments args) {
        String warpName = args.getByArgument(warpNameArg);
        Location warpLocation = WarpsManager.getWarp(warpName);
        String msg;
        if (warpLocation != null) {
            sender.teleport(warpLocation);
            msg = "warpWarp";
        } else msg = "warpNotSet";
        var message = Config.replaceFormat(getMsg(msg), new Replacer("warp-name", warpName));
        sender.sendMessage(message);
    }

}
