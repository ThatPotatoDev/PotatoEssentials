package com.thatpotatodev.potatoessentials.commands;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class GameModeCommand extends PotatoCommand {

    public GameModeCommand() {
        super("gamemode",NAMESPACE+".gamemode", "gm");
    }

    @Override
    public void register() {

        Argument<String> gmArg = new MultiLiteralArgument("gamemode",
                "adventure", "creative", "spectator", "survival",
                "a", "c", "sp", "s", "0", "1", "2", "3");
        EntitySelectorArgument.ManyPlayers playerArg = new EntitySelectorArgument
                .ManyPlayers("target", false);

        new CommandAPICommand(name)
                .withPermission(permission)
                .withArguments(gmArg)
                .withAliases(aliases)
                .withOptionalArguments(playerArg)
                .executesPlayer((player, args) -> {
                    Collection<Player> players = args.getByArgument(playerArg) != null ?
                            Objects.requireNonNull(args.getByArgument(playerArg)) : List.of(player);
                    switch (args.getByArgument(gmArg)) {
                        case "adventure", "2", "a":
                            setGameModes(players, GameMode.ADVENTURE, player);
                            break;
                        case "creative", "1", "c":
                            setGameModes(players, GameMode.CREATIVE, player);
                            break;
                        case "spectator", "3", "sp":
                            setGameModes(players, GameMode.SPECTATOR, player);
                            break;
                        case "survival", "0", "s":
                            setGameModes(players, GameMode.SURVIVAL, player);
                            break;
                        case null, default:
                            Bukkit.broadcast(Component.text("Err: idk"));
                            break;
                    }
                })
                .register();
        new CommandAPICommand("gma")
                .withPermission(permission)
                .withOptionalArguments(playerArg)
                .executesPlayer((player, args) -> {
                    Collection<Player> players = args.getByArgument(playerArg) != null ? Objects.requireNonNull(args.getByArgument(playerArg)) : List.of(player);
                    setGameModes(players, GameMode.ADVENTURE, player);
                })
                .register();
        new CommandAPICommand("gmc")
                .withPermission(permission)
                .withOptionalArguments(playerArg)
                .executesPlayer((player, args) -> {
                    Collection<Player> players = args.getByArgument(playerArg) != null ? Objects.requireNonNull(args.getByArgument(playerArg)) : List.of(player);
                    setGameModes(players, GameMode.CREATIVE, player);
                })
                .register();
        new CommandAPICommand("gmsp")
                .withPermission(permission)
                .withOptionalArguments(playerArg)
                .executesPlayer((player, args) -> {
                    Collection<Player> players = args.getByArgument(playerArg) != null ? Objects.requireNonNull(args.getByArgument(playerArg)) : List.of(player);
                    setGameModes(players, GameMode.SPECTATOR, player);
                })
                .register();
        new CommandAPICommand("gms")
                .withPermission(permission)
                .withOptionalArguments(playerArg)
                .executesPlayer((player, args) -> {
                    Collection<Player> players = args.getByArgument(playerArg) != null ? Objects.requireNonNull(args.getByArgument(playerArg)) : List.of(player);
                    setGameModes(players, GameMode.SURVIVAL, player);
                })
                .register();
    }
    private void setGameModes(Collection<Player> players, GameMode gameMode, Player senderPlayer) {
        for (Player player : players) {
            player.setGameMode(gameMode);
        }
        String msg = players.size() < 2 ? ((Player) players.toArray()[0]).getName()+"</yellow>'s"
                : players.size()+" Players</yellow>'";

        senderPlayer.sendRichMessage("<gray>Set<yellow> " + msg +
                "<gray> Gamemode to<yellow> <translate:"+gameMode.translationKey()+">");
    }
}
