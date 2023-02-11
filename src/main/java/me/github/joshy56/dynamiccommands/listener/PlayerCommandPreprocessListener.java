package me.github.joshy56.dynamiccommands.listener;

import com.google.common.base.Strings;
import me.github.joshy56.dynamiccommands.DynamicCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/2/2023.
 */
public class PlayerCommandPreprocessListener implements Listener {
    private final DynamicCommands plugin;
    private static final String DEBUG_TEMPLATE = PlayerCommandPreprocessListener.class.getSimpleName() + "#on(PlayerCommandPreprocessEvent)";

    public PlayerCommandPreprocessListener(DynamicCommands plugin) {
        if (!plugin.isEnabled()) throw new IllegalStateException("Plugin isn't enabled.");

        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;

        if (plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " getMessage():" + event.getMessage());

        String[] args = event.getMessage().split(" ");
        if (plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " args:" + Arrays.toString(args));

        if (args.length == 0)
            return;

        args[0] = args[0].substring(args[0].indexOf("/"));
        if (Strings.isNullOrEmpty(args[0]))
            return;

        if (plugin.getServer().getCommandMap().getKnownCommands().containsKey(args[0]))
            return;

        plugin.commandMap().dispatch(event.getPlayer(), String.join(" ", args));
    }
}
