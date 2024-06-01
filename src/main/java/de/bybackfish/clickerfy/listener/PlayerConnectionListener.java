package de.bybackfish.clickerfy.listener;

import de.bybackfish.clickerfy.ClickerfyStash;
import de.bybackfish.clickerfy.api.IPlayerStash;
import de.bybackfish.clickerfy.api.IStashManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final ClickerfyStash pluginInstance;
    private final IStashManager stashManager;

    public PlayerConnectionListener(ClickerfyStash pluginInstance, IStashManager iStashManager) {
        this.pluginInstance = pluginInstance;
        this.stashManager = iStashManager;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        stashManager.loadStash(event.getPlayer());

        IPlayerStash playerStash = stashManager.getPlayerStash(event.getPlayer());

        Bukkit.getScheduler().runTaskLater(pluginInstance, () -> playerStash.sendStashMessage(event.getPlayer()), 20 * 5);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        stashManager.saveStash(event.getPlayer());
    }

}
