package me.github.joshy56.dynamiccommands.command.commands;

import com.google.common.collect.Lists;
import me.github.joshy56.dynamiccommands.DynamicCommands;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 11/2/2023.
 */
public class DebugCommand extends Command {
    private final DynamicCommands plugin;

    public DebugCommand(DynamicCommands plugin) {
        super(
                "debug",
                "Enable debug mode of DynamicCommands",
                "debug <booleanValue>",
                Lists.newArrayList()
        );

        this.plugin = plugin;
        setPermission("dcmd.cmd.debug");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("Incomplete command, an argument is missing. Please chose true or false");
            return false;
        }

        boolean value = Boolean.parseBoolean(args[0]);
        plugin.debug(value);

        sender.sendMessage("Now debug is " + ((plugin.isDebugging()) ? "enable" : "disable"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return (args.length == 0) ? Lists.newArrayList("true", "false") : Lists.newArrayList();
    }
}
