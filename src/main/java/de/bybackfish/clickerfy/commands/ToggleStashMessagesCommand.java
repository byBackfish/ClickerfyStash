package de.bybackfish.clickerfy.commands;

import de.bybackfish.clickerfy.api.IPlayerStash;
import de.bybackfish.clickerfy.api.IStashManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleStashMessagesCommand implements CommandExecutor {

    private final IStashManager stashManager;

    public ToggleStashMessagesCommand(IStashManager stashManager) {
        this.stashManager = stashManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player player)) return false;

        IPlayerStash playerStash = stashManager.getPlayerStash(player);

        playerStash.setSendMessages(!playerStash.shouldSendMessages());

        Component message = Component.text("Stash messages are now ").color(NamedTextColor.GRAY)
                .append(
                        playerStash.shouldSendMessages() ? Component.text("enabled").color(NamedTextColor.GREEN) : Component.text("disabled").color(NamedTextColor.RED)
                );

        player.sendMessage(message);
        return true;
    }
}
