package me.github.joshy56.dynamiccommands.command;

import co.aikar.timings.TimingsManager;
import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import com.destroystokyo.paper.exception.ServerCommandException;
import com.destroystokyo.paper.exception.ServerTabCompleteException;
import com.google.common.base.Strings;
import me.github.joshy56.dynamiccommands.DynamicCommands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/2/2023.
 */
public class SubCommandMap implements CommandMap {
    private final Map<String, Command> commands;
    private final Map<String, String> commandsAliases;
    private final DynamicCommands plugin;

    public SubCommandMap(Map<String, Command> commands, Map<String, String> commandsAliases, DynamicCommands plugin) {
        if(!plugin.isEnabled()) throw new IllegalStateException("Plugin isn't enabled.");

        this.plugin = plugin;
        this.commands = commands;
        this.commandsAliases = commandsAliases;
    }

    @Override
    public void registerAll(String fallbackPrefix, List<Command> commands) {
        commands.parallelStream()
                .forEach(command -> register(fallbackPrefix, command));
    }

    @Override
    public boolean register(String label, String fallbackPrefix, Command command) {
        if(!command.getLabel().equalsIgnoreCase(label))
            return false;

        String identifier = label;
        if(!Strings.isNullOrEmpty(fallbackPrefix))
            identifier = fallbackPrefix + ":" + label;

        if(commands.containsKey(identifier))
            return false;

        commands.put(identifier, command);
        command.register(this);


        if(Strings.isNullOrEmpty(fallbackPrefix))
            for (String alias : command.getAliases()) {
                if (!command.getLabel().equalsIgnoreCase(alias))
                    continue;

                commandsAliases.put(alias, identifier);
            }
        else
            for(String alias : command.getAliases()) {
                if(command.getLabel().equalsIgnoreCase(alias))
                    continue;

                commandsAliases.put(
                        fallbackPrefix + ":" + alias,
                        identifier
                );
            }

        return true;
    }

    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return register(command.getLabel(), fallbackPrefix, command);
    }

    @Override
    public boolean dispatch(CommandSender sender, String cmdLine) throws CommandException {
        String[] args = cmdLine.split(" ");
        Map.Entry<Command, String[]> track = trackCommand(args);
        if(track == null) return false;

        Command command = track.getKey();
        String[] subArgs = track.getValue();

        if(command.timings == null)
            command.timings = TimingsManager.getCommandTiming(plugin.getName(), command);

        try {
            return command.execute(sender, command.getLabel(), subArgs);
        } catch (CommandException ok) {
            plugin.getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerCommandException(ok, command, sender, args)));
            throw ok;
        } catch (Throwable ok) {
            String msg = "Unhandled exception executing '" + cmdLine + "' in " + command;
            plugin.getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerCommandException(ok, command, sender, args)));
            throw new CommandException(msg, ok);
        }
    }

    @Override
    public void clearCommands() {
        commands.values().parallelStream().forEach(
                command -> command.unregister(this)
        );

        commandsAliases.clear();
        commands.clear();
    }

    @Override
    public Command getCommand(String name) {
        String alias = commandsAliases.get(name);
        return (Strings.isNullOrEmpty(alias)) ? commands.get(name) : commands.get(alias);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) throws IllegalArgumentException {
        return tabComplete(sender, cmdLine, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) throws IllegalArgumentException {
        String prefix = (sender instanceof Player) ? "/" : "";

        String[] args = cmdLine.split(" ");
        Map.Entry<Command, String[]> track = trackCommand(args);
        if(track == null)
            return getKnownCommands().entrySet().parallelStream()
                    .filter(entry -> entry.getValue().testPermissionSilent(sender))
                    .filter(
                            entry -> StringUtil.startsWithIgnoreCase(
                                    (args.length > 0) ? args[0].toLowerCase() : entry.getKey(),
                                    entry.getKey()
                            )
                    )
                    .map(entry -> prefix + entry.getKey())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

        Command command = track.getKey();
        String[] subArgs = track.getValue();

        if(!command.testPermissionSilent(sender)) return null;

        try {
            return command.tabComplete(sender, command.getLabel(), subArgs, location);
        } catch (CommandException ok) {
            throw ok;
        } catch (Throwable ok) {
            String msg = "Unhandled exception executing tab-completer for '" + cmdLine + "' in " + command;
            plugin.getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerTabCompleteException(msg, ok, command, sender, subArgs)));
            throw new CommandException(msg, ok);
        }
    }

    @Override
    public Map<String, Command> getKnownCommands() {
        return commands;
    }

    protected Map.Entry<Command, String[]> trackCommand(String... args) {
        if(args.length == 0) return null;

        Command command = null;
        String[] subArgs = new String[0];
        for (int i = 0; i < args.length; i++) {
            if(commands.containsKey(args[i].toLowerCase()))
                command = getCommand(args[i].toLowerCase());
            else if (command != null)
                if(commands.containsKey(command.getLabel().toLowerCase() + ":" + args[i].toLowerCase()))
                    command = getCommand(command.getLabel().toLowerCase() + ":" + args[i].toLowerCase());
                else {
                    subArgs = Arrays.copyOfRange(args, i, args.length);
                    break;
                }
        }

        if(command == null) return null;

        return new AbstractMap.SimpleEntry<>(command, subArgs);
    }
}
