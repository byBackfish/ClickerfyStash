package de.bybackfish.clickerfy.api.impl;

import de.bybackfish.clickerfy.api.IPlayerStash;
import de.bybackfish.clickerfy.base64.Base64Helper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class PlayerStash implements IPlayerStash {

    private ItemStack[] stash = new ItemStack[0];

    private boolean sendMessages = true;
    private long lastMessageSent = 0;

    private final long messageCooldown = 1000 * 30;

    @Override
    public ItemStack[] getItemsInStash() {
        return stash.clone();
    }

    @Override
    public void setItemsInStash(ItemStack[] items) {
        this.stash = items;
    }

    @Override
    public void addItemToStash(ItemStack item) {
        this.stash = Arrays.copyOf(stash, stash.length + 1);
        this.stash[stash.length - 1] = item;
    }

    @Override
    public ItemStack[] takeFirst(int amount) {
        amount = Math.min(amount, stash.length);

        ItemStack[] items = new ItemStack[amount];
        System.arraycopy(stash, 0, items, 0, amount);

        ItemStack[] newStash = new ItemStack[stash.length - amount];
        System.arraycopy(stash, amount, newStash, 0, stash.length - amount);

        this.stash = newStash;

        return items;
    }

    @Override
    public void clearStash() {
        this.stash = new ItemStack[0];
    }

    @Override
    public boolean shouldSendMessages() {
        return sendMessages;
    }

    @Override
    public void setSendMessages(boolean sendMessages) {
        this.sendMessages = sendMessages;
    }

    @Override
    public void saveToFile(File file) throws IOException {
        String encoded = Base64Helper.encodeMany(sendMessages, stash);
        Files.write(file.toPath(), encoded.getBytes());
    }

    @Override
    public void loadFromFile(File file) throws FileNotFoundException {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            Base64Helper.Pair<Boolean, ItemStack[]> result = Base64Helper.decodeMany(content);
            sendMessages = result.getKey();
            setItemsInStash(result.getValue());
        } catch (IOException e) {
            throw new FileNotFoundException("Could not read file");
        }
    }

    @Override
    public void sendStashMessage(Player player, boolean ignoreSetting) {
        if (stash.length == 0) return;
        if((!sendMessages || System.currentTimeMillis() - lastMessageSent < messageCooldown) && !ignoreSetting) return;

        lastMessageSent = System.currentTimeMillis();

        var sumOfItems = Arrays.stream(stash).mapToInt(ItemStack::getAmount).sum();

        Component clickHereToHoverEvent = Component.text("Click here to collect all items from your stash!").color(NamedTextColor.GRAY);

        Component component = getComponent(sumOfItems, clickHereToHoverEvent);


        Component summaryOfStashHoverEvent = Component.text("Summary of your stash:").color(NamedTextColor.GRAY)
                .appendNewline().appendNewline();

        for (int i = 0; i < Math.min(stash.length, 8); i++) {
            summaryOfStashHoverEvent = summaryOfStashHoverEvent
                    .append(Component.text(i + 1 + ". ").color(NamedTextColor.YELLOW))
                    .append(stash[i].displayName())
                    .append(Component.text(" x" + stash[i].getAmount()).color(NamedTextColor.DARK_GRAY))
                    .append(Component.newline());
        }

        if(stash.length > 8) {
            summaryOfStashHoverEvent = summaryOfStashHoverEvent
                    .append(Component.text("And " + (stash.length - 8) + " more...").color(NamedTextColor.GRAY));
        }

        component = component.hoverEvent(summaryOfStashHoverEvent);

        player.sendMessage(component);
    }

    @Override
    public void sendStashMessage(Player player) {
        sendStashMessage(player, false);
    }

    private @NotNull Component getComponent(int sumOfItems, Component clickHereToHoverEvent) {
        ClickEvent clickEvent = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/stash collect");

        return
                Component.text("You have ").color(NamedTextColor.YELLOW)
                        .append(
                                Component.text(String.format("%s materials ", prettifyNumber(sumOfItems))).color(NamedTextColor.GREEN)
                        )
                        .append(
                                Component.text("totalling ").color(NamedTextColor.YELLOW)
                        )
                        .append(
                                Component.text(String.format("%s items ", prettifyNumber(stash.length))).color(NamedTextColor.AQUA)
                        )
                        .append(
                                Component.text("stashed away! ").color(NamedTextColor.YELLOW)
                        )
                        .appendNewline()
                        .append(
                                Component.text("Click here ").color(NamedTextColor.GOLD)
                                        .clickEvent(clickEvent)
                                        .hoverEvent(clickHereToHoverEvent)
                        )
                        .append(
                                Component.text("to pick it all up!").color(NamedTextColor.YELLOW)
                                        .clickEvent(clickEvent)
                                        .hoverEvent(clickHereToHoverEvent)
                        );
    }

    private String prettifyNumber(long number) {
        return String.format("%,d", number);
    }
}
