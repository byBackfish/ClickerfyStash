package de.bybackfish.clickerfy.base64;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Helper {
    public static @NotNull String encodeMany(ItemStack[] items) throws Base64Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(items);
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        } catch (Exception exception) {
            throw new Base64Exception(exception);
        }
    }

    public static @NotNull ItemStack[] decodeMany(@NotNull String base64) throws Base64Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(base64));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack[]) dataInput.readObject();
        } catch (Exception exception) {
            throw new Base64Exception(exception);
        }
    }

}
