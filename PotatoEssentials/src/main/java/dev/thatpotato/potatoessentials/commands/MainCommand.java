package dev.thatpotato.potatoessentials.commands;

import dev.thatpotato.potatoessentials.PotatoEssentials;
import dev.thatpotato.potatoessentials.utils.Config;
import dev.thatpotato.potatoessentials.objects.PotatoCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class MainCommand extends PotatoCommand {

    private final static String prefix = "<gold>PotatoEssentials<gray> >";

    public MainCommand() {
        super("potatoessentials", NAMESPACE+".potatoessentials");
    }

    @Override
    public void register() {
        new CommandAPICommand(name)
                .withSubcommand(
                        new CommandAPICommand("info")
                                .executes(this::info)
                )
                .withSubcommand(
                        new CommandAPICommand("reload")
                                .withPermission(NAMESPACE+".potatoessentials")
                                .executes(this::reload)
                ).register();
    }

    public void reload(CommandSender sender, CommandArguments ca) {
        String reload = Config.reload();
        if (!reload.equals("passed")) {
            sender.sendRichMessage(
                    prefix+" <red>Invalid config! Error:<newline>" +
                            reload);
            return;
        }
        sender.sendRichMessage(
                prefix+" <green>Successfully reloaded config!<newline>"+
                        "<gold>Note: If you have enabled/disabled any <b>commands</b> in the " +
                        "<b>config</b> a <b>restart</b> is required to actually enable/disable them.");

    }
    public void info(CommandSender sender, CommandArguments ca) {
        String ver = PotatoEssentials.INSTANCE.getPluginMeta().getVersion();
        sender.sendRichMessage(prefix+"<gold> Version:<green> "+ver);
        sender.sendRichMessage(prefix+"<gold> Author:<yellow> ThatPotatoDev");
        sender.sendRichMessage(prefix+
                "<gold> Github: <#00b1fc><click:open_url:'https://github.com/ThatPotatoDev/PotatoEssentials'>" +
                "https://github.com/ThatPotatoDev/PotatoEssentials");
    }
}
