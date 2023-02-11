package me.github.joshy56.dynamiccommands;

import me.github.joshy56.dynamiccommands.command.SubCommandMap;
import me.github.joshy56.dynamiccommands.listener.AsyncTabCompleteListener;
import me.github.joshy56.dynamiccommands.listener.PlayerCommandPreprocessListener;
import me.github.joshy56.dynamiccommands.listener.ServerCommandListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DynamicCommands extends JavaPlugin {
    private SubCommandMap commandMap;
    private boolean debugging;

    @Override
    public void onEnable() {
        // Plugin startup logic
        commandMap = new SubCommandMap(new HashMap<>(), new HashMap<>(), this);
        debugging = false;
        listeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void listeners() {
        getServer().getPluginManager().registerEvents(
                new PlayerCommandPreprocessListener(this),
                this
        );
        getServer().getPluginManager().registerEvents(
                new ServerCommandListener(this),
                this
        );
        getServer().getPluginManager().registerEvents(
                new AsyncTabCompleteListener(this),
                this
        );
    }

    public SubCommandMap commandMap() {
        return commandMap;
    }

    public boolean isDebugging() {
        return debugging;
    }

}
