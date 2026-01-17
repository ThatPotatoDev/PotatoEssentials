package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.PotatoEssentials;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyPlayers;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OnePlayer;
import dev.jorel.commandapi.executors.CommandArguments;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unchecked")
public class TrollCommand extends PotatoCommand {
    public TrollCommand() {
        super("troll", NAMESPACE+".troll");
    }

    private final OnePlayer onePlayerArg = new OnePlayer("target");
    private final ManyPlayers manyPlayersArg = new ManyPlayers("targets", false);
    @Override
    public void register() {
        createCommand(
                createSubcommand("crash").executes(this::crash).withArguments(onePlayerArg),
                createSubcommand("demo").executes(this::demo).withArguments(manyPlayersArg)
        ).register();
    }
    private void crash(CommandSender sender, CommandArguments args) {
        Player toBeCrashed = args.getByArgument(onePlayerArg);
        if (toBeCrashed==null) return;
        if (sender instanceof Player senderP && toBeCrashed.equals(senderP)) {
            sender.sendMessage(Config.replaceFormat(getMsg("attemptCrashSelf")));
            return;
        }
        sender.sendMessage(Config.replaceFormat(getMsg("crash"), new Replacer("target", toBeCrashed.getName())));

        var connection = ((CraftPlayer) toBeCrashed).getHandle().connection;

        double x = toBeCrashed.getX(), y = toBeCrashed.getY(), z = toBeCrashed.getZ();
        var type = EntityType.ENDER_DRAGON;
        Vec3 vec3 = new Vec3(0,0,0);
        new BukkitRunnable() {
            private int iter = 0;
            @Override
            public void run() {
                if (!toBeCrashed.isOnline()) cancel();
                if (iter>15) cancel();
                for (int i = 0; i < 5000; i++) {
                    int id = ThreadLocalRandom.current().nextInt(1, 99999);
                    var packet = new ClientboundAddEntityPacket(
                            id, UUID.randomUUID(), x, y, z,
                            0,0 , type, 0, vec3, 0);
                    connection.send(packet);
                }
                iter++;
            }
        }.runTaskTimer(PotatoEssentials.INSTANCE, 20, 40);
    }
    private void demo(CommandSender sender, CommandArguments args) {
        List<Player> players = args.getByArgumentOrDefault(manyPlayersArg, List.of()).stream().toList();

        var msg = players.size()==1 ? Config.replaceFormat(getMsg("demoSingle"),
                new Replacer("target", players.getFirst().getName()))
                : Config.replaceFormat(getMsg("demoMany"), new Replacer("count", players.size()+""));
        sender.sendMessage(msg);

        var packet = new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 0);
        for (Player player : players) ((CraftPlayer) player).getHandle().connection.send(packet);

    }
}
