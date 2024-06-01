package de.bybackfish.clickerfy.listener;

import de.bybackfish.clickerfy.api.IPlayerStash;
import de.bybackfish.clickerfy.api.IStashManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class PlayerPickupListener implements Listener {

    private final IStashManager stashManager;

    public PlayerPickupListener(IStashManager stashManager) {
        this.stashManager = stashManager;
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (event.getRemaining() == 0) return;
        if (event.getItem().getItemStack().getAmount() != event.getRemaining()) return;

        event.getItem().remove();
        event.setCancelled(true);

        IPlayerStash playerStash = stashManager.getPlayerStash(event.getPlayer());

        playerStash.addItemToStash(event.getItem().getItemStack());
        playerStash.sendStashMessage(event.getPlayer());
    }
}
