package dev.thatpotato.potatoessentials.utils;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.listeners.ServerListPingListener;
import dev.thatpotato.potatoessentials.objects.CustomChat;
import dev.thatpotato.potatoessentials.objects.Replacer;
import com.tchristofferson.configupdater.ConfigUpdater;
import dev.jorel.commandapi.CommandAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;

import static dev.thatpotato.potatoessentials.PotatoEssentials.LOGGER;

@SuppressWarnings("UnstableApiUsage")
public class Config {
    @Getter
    private static FileConfiguration config;
    private static PotatoEssentials potatoEssentials;
    @Getter
    private static final Map<String, String> emojis = new HashMap<>();
    public static boolean motdEnabled;
    public static boolean hoverInfoEnabled;
    public static boolean serverLinksEnabled;
    private static final List<ServerLinks.ServerLink> serverLinks = new ArrayList<>();

    public static void config(PotatoEssentials plugin) {
        potatoEssentials = plugin;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        PotatoEssentials.INSTANCE.config = config;
        try {
            List<String> ignoredList = new ArrayList<>();
            addToIgnoredListIfExists("chat.emojis", ignoredList);
            addToIgnoredListIfExists("chats.customChats", ignoredList);
            addToIgnoredListIfExists("serverLinks.serverLinks", ignoredList);
            ConfigUpdater.update(plugin,"config.yml",
                    new File(plugin.getDataFolder(), "config.yml"), ignoredList);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating config", e);
            return;
        }
        String reload = reload();
        if (!"passed".equals(reload)) LOGGER.severe("Error loading config\n"+reload);

    }

    /**
     * Reloads the config
     * @return "passed" if passed, otherwise the error message
     */
    public static String reload() {
        LOGGER.debug("Reloading config");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.removeCustomChatCompletions(Config.getEmojis().keySet());
        }
        potatoEssentials.saveDefaultConfig();
        potatoEssentials.reloadConfig();
        config = potatoEssentials.getConfig();
        PotatoEssentials.INSTANCE.config = config;

        emojiConfig();

        String channel = channelConfig();
        if (!"passed".equals(channel)) return channel;


        motdEnabled = getBoolean("serverList.motdEnabled");
        hoverInfoEnabled = getBoolean("serverList.hoverInfoEnabled");
        List<String> motd = config.getStringList("serverList.motd");
        ServerListPingListener.motd = Utils.miniMessage(motd.getFirst()+"<reset><newline>"+motd.getLast());
        ServerListPingListener.hoverInfo = config.getStringList("serverList.hoverInfo").stream()
                .map(s ->
                        LegacyComponentSerializer.legacySection().serialize(Utils.miniMessage(s))
                ).toList();

        for (ServerLinks.ServerLink serverLink : serverLinks) {
            Bukkit.getServerLinks().removeLink(serverLink);
        }
        serverLinks.clear();
        serverLinksEnabled = getBoolean("serverLinks.enabled");
        var serverLinksSection = config.getConfigurationSection("serverLinks.serverLinks");
        if (serverLinksEnabled && serverLinksSection!=null) {
            for (String key : serverLinksSection.getKeys(false)) {
                String linkConfPrefix = "serverLinks.serverLinks."+key+".";
                var name = getString(linkConfPrefix+"name");
                var compName = Utils.miniMessage(name);
                var link = URI.create(getString(linkConfPrefix+"url"));
                serverLinks.add(Bukkit.getServerLinks().addLink(compName, link));
                LOGGER.debug("Registering server link '%s' with name '%s', URL '%s'".formatted(key, name, link.toString()));
            }
        }
        return "passed";
    }
    private static String channelConfig() {
        ConfigurationSection chatsSection = config.getConfigurationSection("chats.customChats");
        for (CustomChat chat : CustomChat.getCustomChats()) {
            if (chat.getCommand()==null) continue;
            CommandAPI.unregister(chat.getCommand(), true);
        }
        CustomChat.clearAll();
        if (customChatsEnabled() && chatsSection != null) {
            for (String key : chatsSection.getKeys(false)) {
                if ("global".equals(key)) {
                    return "key 'global' is reserved for global chat.";
                }
                String chatConfPrefix = "chats.customChats."+key+".";
                LOGGER.debug("Registering custom chat '%s'".formatted(key));
                new CustomChat(key,
                        getString(chatConfPrefix+"name"),
                        getString(chatConfPrefix+"permission"),
                        getString(chatConfPrefix+"command"),
                        getInt(chatConfPrefix+"cooldown"));
            }
            new CustomChat("global", getString("chats.globalChannelName"), null, null, null);
        }
        return "passed";
    }
    private static void emojiConfig() {
        var emojiSection = config.getConfigurationSection("chat.emojis");
        emojis.clear();
        if (emojisEnabled() && emojiSection!=null) {
            for (String key : emojiSection.getKeys(false)) {
                emojis.put(key,config.getString("chat.emojis."+key));
                LOGGER.debug("Registering emoji '%s'".formatted(key));
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(PotatoEssentials.NAMESPACE+".chat.emojis")) continue;
            p.addCustomChatCompletions(Config.getEmojis().keySet());
        }
    }
    public static Boolean chatEnabled() {
        return config.getBoolean("chat.enabled");
    }
    public static Boolean formatURLs() {
        return config.getBoolean("chat.format-urls");
    }
    public static String chatFormat() {
        return config.getString("chat.format");
    }
    public static String messageSender() {
        return config.getString("commands.message.sender");
    }
    public static String messageReceiver() {
        return config.getString("commands.message.receiver");
    }
    public static String messageSocialSpy() {
        return config.getString("commands.socialspy.message");
    }
    public static String broadcastFormat() {
        return getString("commands.broadcast.message");
    }
    public static boolean emojisEnabled() {
        return getBoolean("chat.emojis-enabled");
    }

    public static String teleportedMsg() {
        return getString("commands.tp.teleported-message");
    }
    public static String teleporterMsg() {
        return getString("commands.tp.teleporter-message");
    }
    public static String muteChatMutedMsg() {
        return getString("commands.mutechat.mute");
    }
    public static String muteChatUnmutedMsg() {
        return getString("commands.mutechat.unmute");
    }
    public static String sudoSayMsg() {
        return getString("commands.sudo.say-message");
    }
    public static String sudoCmdMsg() {
        return getString("commands.sudo.command-message");
    }
    public static String joinMessageFormat() {
        return getString("connectionMessages.joinMessage");
    }
    public static String firstJoinMessageFormat() {
        return getString("connectionMessages.firstJoinMessage");
    }
    public static String quitMessageFormat() {
        return getString("connectionMessages.quitMessage");
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean modifyConnectionMessages() {
        return config.getBoolean("connectionMessages.modifyConnectionMessages");
    }
    public static String uptimeMessageFormat() {
        return config.getString("commands.uptime.message");
    }

    public static boolean chatFilterWords() {
        return config.getBoolean("chat.filterWords");
    }
    public static Collection<String> chatFilteredWords() {
        return config.getStringList("chat.filteredWords");
    }

    public static String customChatFormat() {
        return getString("chats.format");
    }
    public static boolean customChatsEnabled() {
        return getBoolean("chats.enabled");
    }
    public static boolean addEmojisToCustomChatCompletions() {
        return getBoolean("chat.addEmojisToCustomChatCompletions");
    }

    public static String getString(String s) {
        return config.getString(s);
    }
    public static boolean getBoolean(String s) {
        return config.getBoolean(s);
    }
    public static String getCmdMsg(String cmd, String field) {
        return getString("commands."+cmd+"."+field);
    }
    public static int getInt(String path) {
        return config.getInt(path);
    }


    @SuppressWarnings("PatternValidation")
    public static Component replaceFormat(String format, Replacer... replacers) {
        TagResolver.Builder resolverBuilder = TagResolver.builder();
        for (Replacer r : replacers) {
            String newText = r.getNewText();
            String oldText = r.getOldText();
            if (r.replaceRaw()) {
                format = format.replace("<"+oldText+">", newText);
                continue;
            }
            Component replacement = Utils.miniMessage(newText);
            resolverBuilder.resolver(TagResolver.resolver(oldText, Tag.inserting(replacement)));
        }

        TagResolver resolver = resolverBuilder.build();
        return Utils.miniMessage(format, resolver);
    }
    private static void addToIgnoredListIfExists(String s, List<String> list) {
        if (config.contains(s, true) && config.isConfigurationSection(s))
            list.add(s);
    }
}
