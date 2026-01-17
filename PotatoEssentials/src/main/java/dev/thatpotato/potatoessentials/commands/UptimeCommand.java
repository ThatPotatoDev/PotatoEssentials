package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.thatpotato.potatoessentials.objects.Replacer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

public class UptimeCommand extends PotatoCommand {
    public UptimeCommand() {
        super("uptime",NAMESPACE+".uptime");
    }
    @Override
    public void register() {
        new CommandAPICommand(name)
                .withPermission(permission)
                .executes(this::execute)
                .register();
    }
    private void execute(CommandSender sender, CommandArguments args) {
        Component msg = Config.replaceFormat(Config.uptimeMessageFormat(),
                new Replacer("uptime", timeSinceBoot()));
        sender.sendMessage(msg);
    }
    private String timeSinceBoot() {
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();

        Instant startTime = Instant.now().minusMillis(uptimeMillis);

        Duration dur = Duration.between(startTime, Instant.now());

        long hours = dur.toHours();
        long minutes = dur.toMinutes() % 60;
        long seconds = dur.getSeconds() % 60;

        String hoursMsg = hours==0?"":(hours+(hours==1?" hour, ":" hours, "));
        String minutesMsg = minutes==0?"":(minutes+(minutes==1?" minute, ":" minutes, "));
        String secondsMsg = seconds+(seconds==1?" second ":" seconds");

        return hoursMsg+minutesMsg+secondsMsg;
    }
}
