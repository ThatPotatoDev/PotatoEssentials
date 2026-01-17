package com.thatpotatodev.potatoessentials;

import com.thatpotatodev.potatoessentials.commands.*;
import com.thatpotatodev.potatoessentials.commands.messaging.*;
import com.thatpotatodev.potatoessentials.commands.*;
import com.thatpotatodev.potatoessentials.commands.messaging.*;
import com.thatpotatodev.potatoessentials.commands.messaging.channels.ChannelCommand;
import com.thatpotatodev.potatoessentials.commands.messaging.channels.ToggleChannelCommand;
import com.thatpotatodev.potatoessentials.commands.teleporting.TeleportAllCommand;
import com.thatpotatodev.potatoessentials.commands.teleporting.TeleportCenterCommand;
import com.thatpotatodev.potatoessentials.commands.teleporting.TeleportHereCommand;
import com.thatpotatodev.potatoessentials.commands.warping.DelWarpCommand;
import com.thatpotatodev.potatoessentials.commands.warping.SetWarpCommand;
import com.thatpotatodev.potatoessentials.commands.warping.WarpCommand;
import com.thatpotatodev.potatoessentials.database.KitManager;
import com.thatpotatodev.potatoessentials.listeners.ChatListener;
import com.thatpotatodev.potatoessentials.listeners.ServerListPingListener;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.PotatoPlugin;
import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.utils.PotatoCommandRegistrar;
import com.thatpotatodev.potatoessentials.objects.PotatoLogger;
import com.thatpotatodev.potatoessentials.utils.Utils;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("SameParameterValue")
public class PotatoEssentials extends PotatoPlugin {

    public static PotatoEssentials INSTANCE;
    public final static String NAMESPACE = "potatoessentials";
    @Getter
    private static Chat vaultChat = null;
    @Getter
    private static boolean vaultInstalled = false;
    public static PluginManager pluginManager;
    public FileConfiguration config;
    public static PotatoLogger LOGGER;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIPaperConfig(this));
    }

    @Override
    public void onEnable() {

        INSTANCE = this;

        config = getConfig();

        new Metrics(this, 26959);

        LOGGER = getLogger();

        Config.config(this);

        CommandAPI.onEnable();

        pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ChatListener(), INSTANCE);
        pluginManager.registerEvents(new ServerListPingListener(), INSTANCE);

        vaultInstalled = setupVaultChat();

        registerCommands();
        LOGGER.info(vaultInstalled ? "Vault found" : "Vault not found");
        LOGGER.info("Enabled");

    }


    private void registerCommands() {
        var kits = new KitManager(INSTANCE.getDataFolder());
        PotatoCommandRegistrar registrar = this.getCommandRegistrar();
        registrar.register(new MainCommand(), true);
        registrar.register(
                new MessageCommand(),
                new ReplyCommand(),
                new SocialSpyCommand(),
                new BroadcastCommand(),
                new MessageToggleCommand(),
                new EnchantCommand(),
                new GameModeCommand(),
                new HealCommand(),
                new HungerCommand(),
                new InvSeeCommand(),
                new PingCommand(),
                new SmiteCommand(),
                new FlySpeedCommand(),
                new VanishCommand(),
                new FeedCommand(),
                new ClearInventoryCommand(),
                new WarpCommand(),
                new SetWarpCommand(),
                new DelWarpCommand(),
                new MuteChatCommand(),
                new SudoCommand(),
                new BackCommand(),
                new UptimeCommand(),
                new ChannelCommand(),
                new KitCommand(kits),
                new ClearChatCommand(),
                new SkinCommand(),
                new SkullCommand(),
                new TrollCommand(),
                new TeleportCenterCommand(),
                new ToggleChannelCommand()
        );

        if (config.getBoolean("commands.tp.tphere-enabled")) new TeleportHereCommand().register();
        if (config.getBoolean("commands.tp.tpall-enabled")) new TeleportAllCommand().register();

        LOGGER.debug("Registered "+registrar.getRegisteredCommands().size()+" commands");
        boolean genDocs = false;
        //noinspection ConstantValue
        if (genDocs) {
            File file = new File(PotatoEssentials.INSTANCE.getDataFolder(), "commands-docs.md");
            if (file.exists()) //noinspection ResultOfMethodCallIgnored
                file.delete();
            List<PotatoCommand> commands = new ArrayList<>(registrar.getRegisteredCommands());
            commands.addAll(registrar.getUnregisteredCommands());
            try (FileWriter fileWriter = new FileWriter(file); BufferedWriter writer = new BufferedWriter(fileWriter)) {
                for (PotatoCommand command : commands) {
                    String s = "## `/"+command.getName()+"`";
                    s += "\n- **Syntax:** `/"+command.getName()+" TODO`";
                    if (command.hasAliases()) s += "\n- **Aliases:** `/"+ String.join("`, `/",command.getAliases())+"`";
                    s += "\n- **Description:** TODO";
                    s += "\n- **Permission:** `"+command.getPermission()+"`";
                    writer.write(s);
                    writer.newLine();
                    writer.newLine();
                }
            } catch (IOException | NoClassDefFoundError e) {
                PotatoEssentials.LOGGER.log(Level.SEVERE, "Error saving docs", e);
            }
        }
    }

    private boolean setupVaultChat() {
        if (pluginManager.getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            vaultChat = rsp.getProvider();
        }
        Utils.vaultChat = vaultChat;
        return vaultChat != null;
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
