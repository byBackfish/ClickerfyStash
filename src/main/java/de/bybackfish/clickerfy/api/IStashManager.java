package de.bybackfish.clickerfy.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IStashManager {
    IPlayerStash getPlayerStash(Player player);

    IPlayerStash getPlayerStash(UUID uuid);

    void saveStash(Player player);

    void loadStash(Player player);

    void saveStash(UUID uuid);

    void loadStash(UUID uuid);

    void saveAllStashes();
}
