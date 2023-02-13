package me.github.joshy56.dynamiccommands.command.commands;

import com.google.common.collect.Lists;
import me.github.joshy56.dynamiccommands.DynamicCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 11/2/2023.
 */
public class MainCommand extends Command {
    private final DynamicCommands plugin;

    public MainCommand(DynamicCommands plugin) {
        super(
                "dynamiccommands",
                "Main command of DynamicCommands",
                "",
                Lists.newArrayList("dcmd")
        );
        this.plugin = plugin;
        setPermission("dcmd.cmd.main");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 0)
            sender.sendMessage(plugin.getName() + " version:" + plugin.getDescription().getVersion());
        return true;
    }
}
