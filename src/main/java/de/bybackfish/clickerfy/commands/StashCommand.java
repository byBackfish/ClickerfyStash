package de.bybackfish.clickerfy.commands;

import de.bybackfish.clickerfy.ClickerfyStash;
import de.bybackfish.clickerfy.api.IPlayerStash;
import de.bybackfish.clickerfy.api.IStashManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class StashCommand implements TabCompleter, CommandExecutor {

    private final Map<UUID, Boolean> clearMap = new HashMap<>();

    private final IStashManager stashManager;

    private final int MAX_ITEMS = 25;

    public StashCommand(IStashManager stashManager) {
        this.stashManager = stashManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return false;

        IPlayerStash playerStash = stashManager.getPlayerStash(player);

        if (args.length == 0) {
            playerStash.sendStashMessage(player, true);
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (clearMap.getOrDefault(player.getUniqueId(), false)) {
                playerStash.clearStash();
                stashManager.saveStash(player);

                player.sendMessage(Component.text("Cleared your stash!"));
                clearMap.put(player.getUniqueId(), false);
                return true;
            }

            player.sendMessage(Component.text("Are you sure you want to clear your stash? This action cannot be undone.").color(NamedTextColor.RED)
                    .append(Component.newline())
                    .append(Component.text("Type /stash clear again to confirm.").color(NamedTextColor.DARK_RED)));

            clearMap.put(player.getUniqueId(), true);
            return true;
        }

        clearMap.remove(player.getUniqueId());

        if (args[0].equalsIgnoreCase("view")) {
            TextComponent component = Component.text("Items in your stash:").color(NamedTextColor.GREEN)
                    .append(Component.newline());

            if(playerStash.getItemsInStash().length == 0) {
                player.sendMessage(Component.text("You don't have any items in your stash. :(").color(NamedTextColor.RED));
                return true;
            }

            for (int i = 0; i < Math.min(playerStash.getItemsInStash().length, MAX_ITEMS); i++) {
                component = component.append(Component.text(i + 1 + ". ").color(NamedTextColor.YELLOW))
                        .append(playerStash.getItemsInStash()[i].displayName())
                        .append(Component.text(" x" + playerStash.getItemsInStash()[i].getAmount()).color(NamedTextColor.DARK_GRAY))
                        .append(Component.newline());
            }

            if(playerStash.getItemsInStash().length > MAX_ITEMS) {
                component = component.append(Component.text(String.format("And %d more...", playerStash.getItemsInStash().length - MAX_ITEMS)).color(NamedTextColor.GRAY));
            }

            player.sendMessage(component);
            return true;
        }

        if (args[0].equalsIgnoreCase("collect")) {
            if (playerStash.getItemsInStash().length == 0) {
                player.sendMessage(Component.text("You don't have any items in your stash.").color(NamedTextColor.RED));
                return true;
            }

            long availableSlots = Arrays.stream(player.getInventory().getStorageContents())
                    .filter(Objects::isNull)
                    .count();

            if (availableSlots == 0) {
                player.sendMessage(Component.text("You don't have any available slots in your inventory.").color(NamedTextColor.RED));
                return true;
            }

            ItemStack[] items = playerStash.takeFirst((int) availableSlots);

            for (ItemStack item : items) {
                player.getInventory().addItem(item);
            }

            Component component = Component.text("Collected ").color(NamedTextColor.GREEN)
                    .append(Component.text(items.length).color(NamedTextColor.GOLD))
                    .append(Component.text(" items from your stash!")).color(NamedTextColor.GREEN);

            player.sendMessage(component);
            player.sendMessage(Component.empty());
            playerStash.sendStashMessage(player);
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1)
            return Stream.of(
                "view",
                "collect",
                "clear"
            ).filter(e -> e.startsWith(strings[0])).toList();
        return Collections.emptyList();
    }
}
