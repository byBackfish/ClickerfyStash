package de.bybackfish.clickerfy;

import de.bybackfish.clickerfy.api.IStashManager;
import de.bybackfish.clickerfy.api.StashManagerFactory;
import de.bybackfish.clickerfy.commands.StashCommand;
import de.bybackfish.clickerfy.listener.PlayerConnectionListener;
import de.bybackfish.clickerfy.listener.PlayerPickupListener;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClickerfyStash extends JavaPlugin {

    public static final TextColor PRIMARY_COLOR = TextColor.color(0xFFD700);
    public static final TextColor SECONDARY_COLOR = TextColor.color(0x00FF00);
    private IStashManager stashManager;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        System.out.println("ClickerfyStash enabled");

        stashManager = StashManagerFactory.createStashManager();

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this, stashManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(stashManager), this);

        getCommand("stash").setExecutor(new StashCommand(stashManager));
        getCommand("stash").setTabCompleter(new StashCommand(stashManager));

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
