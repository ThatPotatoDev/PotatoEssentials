package dev.thatpotato.potatodiscordlink;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.thatpotato.potatodiscordlink.commands.LinkCommand;
import dev.thatpotato.potatodiscordlink.commands.LinkedCommand;
import dev.thatpotato.potatodiscordlink.commands.UnlinkCommand;
import dev.thatpotato.potatodiscordlink.listeners.DcSlashCommandListener;
import dev.thatpotato.potatodiscordlink.listeners.MCPreJoinListener;
import dev.thatpotato.potatoessentials.objects.PotatoPlugin;
import dev.thatpotato.potatoessentials.libs.configupdater.ConfigUpdater;
import dev.thatpotato.potatoessentials.objects.PotatoLogger;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Level;

public class PotatoDiscordLink extends PotatoPlugin {
    private static FileConfiguration config;
    public static PotatoDiscordLink INSTANCE;
    public static PotatoLogger LOGGER;
    @Getter
    private static JDA jda;
    @Getter
    private static LinkManager linkManager;
    @SneakyThrows
    @Override
    public void onEnable() {
        saveDefaultConfig();
        INSTANCE = this;
        try {
            ConfigUpdater.update(INSTANCE, "config.yml",
                    new File(INSTANCE.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating config", e);
        }
        config = getConfig();
        LOGGER = getLogger();
        String token = config.getString("botToken");
        if (token == null || token.equalsIgnoreCase("TOKEN_HERE") || token.isEmpty()) {
            disablePlugin("Disabling due to token being not set");
            return;
        }

        ForkJoinPool callbackThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("PotatoDiscordLink - JDA Callback " + worker.getPoolIndex());
            return worker;
        }, null, true);

        ThreadFactory gatewayThreadFactory = new ThreadFactoryBuilder().setNameFormat("PotatoDiscordLink - JDA Gateway").build();
        ScheduledExecutorService gatewayThreadPool = Executors.newSingleThreadScheduledExecutor(gatewayThreadFactory);
        jda = JDABuilder.create(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EXPRESSIONS, GatewayIntent.SCHEDULED_EVENTS)
                .setCallbackPool(callbackThreadPool, false)
                .setGatewayPool(gatewayThreadPool, true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new DcSlashCommandListener())
                .build();

        jda.awaitReady();
        var linkedRoleId = config.getString("linkedRole");
        if (linkedRoleId!=null && !linkedRoleId.isEmpty()) {
            linkedRole = jda.getRoleById(linkedRoleId);
        }
        primaryGuild = jda.getGuilds().getFirst();


        var commands = jda.updateCommands();
        //noinspection ResultOfMethodCallIgnored
        commands.addCommands(
                Commands.slash("link", "Links your Discord to your Minecraft")
                        .addOption(
                                OptionType.STRING,
                                "code",
                                "The code you got by running /link in-game", true
                        ),
                Commands.slash("unlink", "Unlinks your Discord from your Minecraft")
        );
        commands.queue();

        linkManager = new LinkManager();
        Bukkit.getPluginManager().registerEvents(new MCPreJoinListener(), INSTANCE);
        var registrar = this.getCommandRegistrar();
        registrar.register(new LinkCommand(),
                new UnlinkCommand(),
                new LinkedCommand()
        );
    }
    @Override
    public void onDisable() {
        if (linkManager == null) return;
        linkManager.save();
    }
    public void disablePlugin(String msg) {
        Bukkit.getScheduler().runTask(this, () -> {
            LOGGER.severe(msg);
            Bukkit.getPluginManager().disablePlugin(this);
        });
    }
    public static FileConfiguration config() {
        return config;
    }
    private static Role linkedRole;
    @Nullable
    public static Role getLinkedRole() {
        return linkedRole;
    }
    @Getter
    private static Guild primaryGuild;

}
