package dev.thatpotato.potatoessentials.commands.teleporting;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.thatpotato.potatoessentials.utils.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyEntities;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class TeleportHereCommand extends PotatoCommand {

    public TeleportHereCommand() {
        super("tphere",NAMESPACE+".tphere", "teleporthere");
    }
    private final ManyEntities targetsArg = new ManyEntities("target", false);

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withAliases(aliases)
                .withPermission(permission)
                .withArguments(targetsArg)
                .executesPlayer(this::execute)
                .register();
    }
    private void execute(Player sender, CommandArguments args) {
        Collection<Entity> entities = args.getByArgumentOrDefault(targetsArg, List.of(sender));
        Component tpedMsg = Config.replaceFormat(Config.teleportedMsg(),
                new Replacer("teleporter", sender.getName()));

        for (Entity player : entities) {
            player.teleport(sender.getLocation());
            player.sendMessage(tpedMsg);
        }
        Component tperMsg = Config.replaceFormat(Config.teleporterMsg(),
                new Replacer("teleported", Utils.nameFormat(entities, true)));
        sender.sendMessage(tperMsg);
    }
}
