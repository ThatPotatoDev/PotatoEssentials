package dev.thatpotato.potatoessentials.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyPlayers;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class SkinCommand extends PotatoCommand {

    private final Argument<String> skinArg = new StringArgument("skin")
            .replaceSuggestions(ArgumentSuggestions.strings(
                            Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)
            ));

    private final ManyPlayers playersArg = new ManyPlayers("targets", false);

    public SkinCommand() {
        super("skin",NAMESPACE+".skin");
    }

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .withArguments(skinArg)
                .withOptionalArguments(playersArg)
                .executesPlayer(this::executeMain)
                .withSubcommand(
                        new CommandAPICommand("reset")
                                .executesPlayer(this::executeReset)
                )
                .register();
        createCommand(
                createSubcommand("reset")
                        .withArguments(playersArg)
                        .executesPlayer(this::executeReset)
        ).withArguments(skinArg)
                .withOptionalArguments(playersArg)
                .executesPlayer(this::executeMain);
    }

    private void executeMain(Player sender, CommandArguments args) {
        String skin = args.getByArgumentOrDefault(skinArg, "");
        Collection<Player> players = args.getByArgumentOrDefault(playersArg, List.of(sender));
        var skinFromPlayer = Bukkit.getOnlinePlayers().stream().filter(p ->
                p.getName().equalsIgnoreCase(skin)
        ).findFirst();

        String skinName;
        if (skinFromPlayer.isPresent()) {
            skinName = skinFromPlayer.get().getName();
            for (Player player : players)
                setSkin(player, skinFromPlayer.get());
        } else {
            try {
                skinName = setSkin(players, skin);
            } catch (Exception e) {
                var msg = Config.replaceFormat(Config.getCmdMsg("skin", "skinNotFound"));
                sender.sendMessage(msg);
                for (Player player : players)
                    executeReset(player, null);
                return;
            }
        }
        var msg = Config.replaceFormat(Config.getCmdMsg("skin", "skinSet"),
                new Replacer("skin", skinName));
        for (Player player : players)
            player.sendMessage(msg);
    }

    private void executeReset(Player player, @Nullable CommandArguments args) {
        try {
            if (args!=null)
                setSkin(args.getByArgumentOrDefault(playersArg, List.of(player)), player.getName());
            else setSkin(player, player.getName());
        } catch (Exception e) {
            player.sendRichMessage("<red>Failed to reset skin: %s, %s".formatted(e.getClass().getName(), e.getMessage()));
        }
    }
    private void setSkin(Player player, String name) throws Exception {
        setSkin(List.of(player), name);
    }
    private String setSkin(Collection<Player> players, String name) throws URISyntaxException, IOException {
        var uuidUrl = new URI("https://api.mojang.com/users/profiles/minecraft/"+name).toURL();

        var uuidReader = new InputStreamReader(uuidUrl.openStream());

        String uuid = JsonParser.parseReader(uuidReader).getAsJsonObject().get("id").getAsString();

        URL texturesUrl = new URI("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid+"?unsigned=false").toURL();
        InputStreamReader texturesReader = new InputStreamReader(texturesUrl.openStream());
        JsonObject responseJson = JsonParser.parseReader(texturesReader).getAsJsonObject();

        String realName = responseJson.get("name").getAsString();

        JsonObject properties = responseJson.get("properties").getAsJsonArray().get(0).getAsJsonObject();

        String value = properties.get("value").getAsString();
        String signature = properties.get("signature").getAsString();

        for (Player player : players) {
            PlayerProfile playerProfile = player.getPlayerProfile();
            playerProfile.setProperty(new ProfileProperty("textures", value, signature));
            player.setPlayerProfile(playerProfile);
        }
        return realName;
    }
    public void setSkin(Player player, Player newSkin) {
        var profile = player.getPlayerProfile();
        var newProfile = newSkin.getPlayerProfile();

        var properties = newProfile.getProperties();
        properties.stream()
                .filter(p -> p.getName().equals("textures"))
                .findFirst().ifPresent(newTextures ->
                        profile.setProperty(new ProfileProperty(
                                "textures", newTextures.getValue(), newTextures.getSignature())));

        player.setPlayerProfile(profile);
    }

}
