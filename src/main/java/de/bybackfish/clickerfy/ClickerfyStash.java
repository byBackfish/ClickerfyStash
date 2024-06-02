package de.bybackfish.clickerfy;

import de.bybackfish.clickerfy.api.IStashManager;
import de.bybackfish.clickerfy.api.StashManagerFactory;
import de.bybackfish.clickerfy.commands.StashCommand;
import de.bybackfish.clickerfy.commands.ToggleStashMessagesCommand;
import de.bybackfish.clickerfy.listener.PlayerConnectionListener;
import de.bybackfish.clickerfy.listener.PlayerPickupListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClickerfyStash extends JavaPlugin {
    private IStashManager stashManager;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        stashManager = StashManagerFactory.createStashManager();

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this, stashManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(stashManager), this);

        getCommand("stash").setExecutor(new StashCommand(stashManager));
        getCommand("stash").setTabCompleter(new StashCommand(stashManager));

        getCommand("togglestashmessages").setExecutor(new ToggleStashMessagesCommand(stashManager));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stashManager.saveAllStashes()));

        for (var player : getServer().getOnlinePlayers()) {
            stashManager.loadStash(player);
        }

        Runnable alertPlayersOfStash = () -> {
            for (var player : getServer().getOnlinePlayers()) {
                stashManager.getPlayerStash(player).sendStashMessage(player);
            }
        };

        Bukkit.getScheduler().runTaskTimer(this, alertPlayersOfStash, 0, 20 * 60 * 2);
    }

    @Override
    public void onDisable() {
        stashManager.saveAllStashes();
    }
}
