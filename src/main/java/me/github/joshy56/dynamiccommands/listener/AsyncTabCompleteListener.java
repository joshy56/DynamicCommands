package me.github.joshy56.dynamiccommands.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.base.Strings;
import me.github.joshy56.dynamiccommands.DynamicCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 10/2/2023.
 */
public class AsyncTabCompleteListener implements Listener {
    private final DynamicCommands plugin;
    private static final String DEBUG_TEMPLATE = AsyncTabCompleteListener.class.getSimpleName() + "#on(AsyncTabCompleteEvent)";

    public AsyncTabCompleteListener(DynamicCommands plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(AsyncTabCompleteEvent event) {
        if(event.isCancelled())
            return;

        if(plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " getBuffer():" + event.getBuffer());


        String[] args = event.getBuffer().split(" ");
        if (plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " args:" + Arrays.toString(args));

        if (args.length == 0)
            return;

        args[0] = args[0].substring(1);
        if (Strings.isNullOrEmpty(args[0]))
            return;

        if (plugin.isDebugging())
            plugin.getLogger().info(DEBUG_TEMPLATE + " filteredArgs:" + Arrays.toString(args));

        if (plugin.getServer().getCommandMap().getKnownCommands().containsKey(args[0]))
            return;

        List<String> completions = plugin.commandMap().tabComplete(event.getSender(), String.join(" ", args), event.getLocation());
        if(completions.isEmpty())
            return;

        event.setCompletions(completions);
        event.setHandled(true);
    }
}
