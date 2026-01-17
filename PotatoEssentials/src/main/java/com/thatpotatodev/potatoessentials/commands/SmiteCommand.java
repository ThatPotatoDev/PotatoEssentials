package com.thatpotatodev.potatoessentials.commands;

import com.thatpotatodev.potatoessentials.utils.Config;
import com.thatpotatodev.potatoessentials.objects.PotatoCommand;
import com.thatpotatodev.potatoessentials.objects.Replacer;
import com.thatpotatodev.potatoessentials.utils.Utils;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.ManyEntities;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class SmiteCommand extends PotatoCommand {

    public SmiteCommand() {
        super("smite",NAMESPACE+".smite");
    }

    private final ManyEntities manyEntitiesArg = new ManyEntities("targets", false);
    private final FloatArgument floatArgument = new FloatArgument("damage");

    @Override
    public void register() {
        createCommand()
                .withArguments(manyEntitiesArg)
                .withOptionalArguments(floatArgument)
                .executes(this::execute)
                .register();
    }
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, CommandArguments args) {
        Collection<Entity> entities = args.getByArgument(manyEntitiesArg);
        float damage = args.getByArgumentOrDefault(floatArgument, 1f);
        if (entities==null) return;
        for (Entity entity : entities) {
            Location loc = entity.getLocation();
            loc.getWorld().strikeLightningEffect(loc);
            if (entity instanceof Damageable damageable)
                damageable.damage(damage);
        }
        String playerMsg = Utils.nameFormat(entities, true);
        Component msg = Config.replaceFormat(getMsg("smiteMessage"),
                new Replacer("targets", playerMsg));
        sender.sendMessage(msg);
    }
}