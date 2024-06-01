package de.bybackfish.clickerfy.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IPlayerStash {

    ItemStack[] getItemsInStash();

    void setItemsInStash(ItemStack[] items);

    void addItemToStash(ItemStack item);

    ItemStack[] takeFirst(int amount);

    void clearStash();

    void saveToFile(File file) throws IOException;

    void loadFromFile(File file) throws FileNotFoundException;

    void sendStashMessage(Player player);
}
