package de.bybackfish.clickerfy.base64;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Helper {
    public static @NotNull String encodeMany(boolean bool, ItemStack[] items) throws Base64Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeBoolean(bool);
            dataOutput.writeObject(items);
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        } catch (Exception exception) {
            throw new Base64Exception(exception);
        }
    }

    public static @NotNull Pair<Boolean, ItemStack[]> decodeMany(@NotNull String base64) throws Base64Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(base64));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            boolean bool = dataInput.readBoolean();
            ItemStack[] items = (ItemStack[]) dataInput.readObject();
            return new Pair<>(bool, items);
        } catch (Exception exception) {
            throw new Base64Exception(exception);
        }
    }

    public static class Pair<K, v> {
        private final K key;
        private final v value;

        public Pair(K a, v b) {
            this.key = a;
            this.value = b;
        }

        public K getKey() {
            return key;
        }

        public v getValue() {
            return value;
        }
    }
}
