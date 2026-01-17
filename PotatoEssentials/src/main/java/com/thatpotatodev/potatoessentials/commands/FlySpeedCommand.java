package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Objects;

public class FlySpeedCommand extends PotatoCommand {

    public FlySpeedCommand() {
        super("flyspeed", NAMESPACE+".flyspeed");
    }

    private final Argument<Integer> intArg = new IntegerArgument("speed", 1, 10);

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .withOptionalArguments(intArg)
                .executesPlayer(this::execute)
                .register();
    }

    private void execute(Player player, CommandArguments args) {
        int speed = args.getByArgument(intArg)!=null?Objects.requireNonNull(args.getByArgument(intArg)):2;
        float actualSpeed = speed/10.0f;
        player.setFlySpeed(actualSpeed);
        player.sendRichMessage("<gray>Set your fly speed to<yellow> "+speed);
    }
}
