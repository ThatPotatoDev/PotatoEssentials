package dev.thatpotato.potatoessentials.utils;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static Component miniMessage(String s) {
        return MiniMessage.miniMessage().deserialize(s);
    }
    public static Component miniMessage(String s, TagResolver... tagResolvers) {
        return MiniMessage.miniMessage().deserialize(s, tagResolvers);
    }
    public static String escapeTags(String s) {
        return MiniMessage.miniMessage().escapeTags(s);
    }
    public static Chat vaultChat;
    public static String formatCoords(Location loc) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(loc.x()) + ", " + df.format(loc.y()) + ", " + df.format(loc.z());
    }
    public static String serialize(Component c) { return MiniMessage.miniMessage().serialize(c); }

    public static String getPrefix(CommandSender sender) {
        if (!(sender instanceof Player player)) return "<white>";
        if (!PotatoEssentials.isVaultInstalled()) return "vaultNotInstalled";
        return vaultChat.getPlayerPrefix(player);
    }

    public static String getSuffix(CommandSender sender) {
        if (!(sender instanceof Player player)) return "<white>";
        if (!PotatoEssentials.isVaultInstalled()) return "vaultNotInstalled";
        return vaultChat.getPlayerSuffix(player);
    }
    public static String nameFormat(Collection<? extends CommandSender> senders, boolean canBeEntity) {
        return senders.size()==1
                ? senders.toArray(new CommandSender[0])[0].getName()
                : senders.size()+(canBeEntity?" entities":" players");
    }
    public static String nameFormat(Collection<? extends CommandSender> senders) {
        return nameFormat(senders, false);
    }
    public static String possessivePlayerNameFormat(Collection<Player> players) {
        return players.size()==1?players.toArray(new Player[0])[0].getName()+"'s":players.size()+" Players'";
    }

    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "(https?://)?([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})(/[a-zA-Z0-9@:%_+.~#?&/=\\-]*)?");

    public static String formatChatMessage(CommandSender player, String message) {
        boolean miniMessage = player.hasPermission(PotatoEssentials.NAMESPACE+".chat.minimessage");
        boolean canUseEmojis = player.hasPermission(PotatoEssentials.NAMESPACE+".chat.emojis");
        boolean canUseURLs = player.hasPermission( PotatoEssentials.NAMESPACE+".chat.urls");
        boolean canBypassChatFilter = player.hasPermission(PotatoEssentials.NAMESPACE+".chat.bypassFilter");
        if (!miniMessage) message = Utils.escapeTags(message);
        if (Config.chatFilterWords() && !canBypassChatFilter) {
            Collection<String> filteredWords = Config.chatFilteredWords();
            String regex = "\\b("+String.join("|", filteredWords)+")s?\\b";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            message = pattern.matcher(message).replaceAll(matchResult ->
                    "*".repeat(matchResult.group().length()));
        }


        if (Config.emojisEnabled() && canUseEmojis) {
            Map<String, String> emojis = Config.getEmojis();
            for (Map.Entry<String, String> entry : emojis.entrySet()) {
                String escapedKey = Pattern.quote(entry.getKey());
                Pattern pattern = Pattern.compile(escapedKey, Pattern.CASE_INSENSITIVE);
                message = pattern.matcher(message).replaceAll(entry.getValue());
            }
        }

        if (Config.formatURLs() && canUseURLs) {

            Matcher matcher = DOMAIN_PATTERN.matcher(message);
            StringBuilder formattedMessage = new StringBuilder();

            while (matcher.find()) {
                String fullMatch = matcher.group();
                String clickableUrl =
                        (fullMatch.startsWith("https://") || fullMatch.startsWith("http://"))
                        ? fullMatch
                        : "https://"+fullMatch;


                String miniMessageFormattedUrl =
                        "<#00b1fc><hover:show_text:'%s'><u><click:open_url:'%s'>%s</click></u></hover></#00b1fc>"
                                .formatted(clickableUrl, clickableUrl, fullMatch);

                matcher.appendReplacement(formattedMessage, miniMessageFormattedUrl);
            }
            matcher.appendTail(formattedMessage);

            message = formattedMessage.toString();
        }
        return message;
    }
}
