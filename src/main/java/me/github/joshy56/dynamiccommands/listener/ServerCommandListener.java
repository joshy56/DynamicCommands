package me.github.joshy56.dynamiccommands.listener;

import com.google.common.base.Strings;
import me.github.joshy56.dynamiccommands.DynamicCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/2/2023.
 */
public class ServerCommandListener implements Listener {
    private final DynamicCommands plugin;
    private static final String DEBUG_TEMPLATE = ServerCommandListener.class.getSimpleName() + "#on(ServerCommandEvent)";

    public ServerCommandListener(DynamicCommands plugin) {
        if (!plugin.isEnabled()) throw new IllegalStateException("Plugin isn't enabled.");

        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ServerCommandEvent event) {
        if (event.isCancelled())
            return;

        if (plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " getCommand():" + event.getCommand());

        String[] args = event.getCommand().split(" ");
        if (plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " args:" + Arrays.toString(args));

        if (args.length == 0)
            return;

        if (Strings.isNullOrEmpty(args[0]))
            return;

        if (plugin.getServer().getCommandMap().getKnownCommands().containsKey(args[0]))
            return;

        plugin.commandMap().dispatch(event.getSender(), String.join(" ", args));
    }
}
