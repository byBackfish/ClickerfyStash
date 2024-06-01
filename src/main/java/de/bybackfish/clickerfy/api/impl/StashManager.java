package de.bybackfish.clickerfy.api.impl;

import de.bybackfish.clickerfy.api.IPlayerStash;
import de.bybackfish.clickerfy.api.IStashManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StashManager implements IStashManager {

    private final Map<UUID, PlayerStash> playerStashMap = new ConcurrentHashMap<>();

    @Override
    public IPlayerStash getPlayerStash(Player player) {
        return getPlayerStash(player.getUniqueId());
    }

    @Override
    public IPlayerStash getPlayerStash(UUID uuid) {
        return playerStashMap.computeIfAbsent(uuid, __ -> new PlayerStash());
    }

    @Override
    public void saveStash(Player player) {
        saveStash(player.getUniqueId());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void saveStash(UUID uuid) {
        File file = Paths.get("plugins", "clickerfy-stash", uuid + ".stash").toFile();
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            getPlayerStash(uuid).saveToFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadStash(UUID uuid) {
        File file = Paths.get("plugins", "clickerfy-stash", uuid + ".stash").toFile();
        if (!file.exists()) return;

        try {
            getPlayerStash(uuid).loadFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadStash(Player player) {
        loadStash(player.getUniqueId());
    }

    @Override
    public void saveAllStashes() {
        playerStashMap.keySet().forEach(this::saveStash);
    }
}
