package com.thatpotatodev.potatoessentials.commands.teleporting;

import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.RotationArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.Rotation;
import org.bukkit.entity.Player;

public class TeleportCenterCommand extends PotatoCommand {

    public TeleportCenterCommand() {
        super("teleportcenter",NAMESPACE+".teleportcenter", "tpcenter");
    }

    private final Argument<Rotation> rotArg = new RotationArgument("rotation");

    @Override
    public void register() {
        createCommand()
                .withOptionalArguments(rotArg)
                .executesPlayer(this::execute)
                .register();

    }
    private void execute(Player p, CommandArguments args) {
        var rot = args.getByArgumentOrDefault(rotArg, new Rotation(p.getYaw(), p.getPitch()));
        var loc = p.getLocation().toCenterLocation();
        loc.setYaw(rot.getYaw());
        loc.setPitch(rot.getPitch());
        p.teleport(loc);
    }
}