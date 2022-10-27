package xyz.jpenilla.wanderingtrades.config;

import io.papermc.lib.PaperLib;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.pluginbase.legacy.itembuilder.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.util.Logging;

public final class ItemStackSerialization {
    private static final List<String> COMMENT = List.of(
        "The following value was serialized from an in-game item and is not meant to be human-readable or editable. As long as it is present other options for this item will be ignored."
    );

    private ItemStackSerialization() {
    }

    public static void writeOrRemove(final FileConfiguration config, final String path, final @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            config.set(path, null);
            return;
        }
        // We don't null out an already existing section
        // Meaning if the item was originally added via config, and is later saved in serialized form, both will be in the config
        // but only the serialized form will be used. This avoids accidental data loss at the cost of config clutter which could confuse
        // some users...

        final String paperPath = path + ".itemStackAsBytes";
        final String bukkitPath = path + ".itemStack";
        final String selectedPath = PaperLib.isPaper() ? paperPath : bukkitPath;
        final Object value = PaperLib.isPaper() ? itemStack.serializeAsBytes() : itemStack.serialize();
        config.set(selectedPath, value);

        // Remove old bukkit value if re-saving as paper value or changed while on paper
        if (PaperLib.isPaper() && config.get(bukkitPath, null) != null) {
            config.set(bukkitPath, null);
        }

        try {
            config.getClass().getMethod("getComments", String.class);
            config.setComments(selectedPath, COMMENT);
        } catch (final NoSuchMethodException ignore) {
        }
    }

    public static @Nullable ItemStack read(final FileConfiguration config, final String key) {
        if (PaperLib.isPaper()) {
            final byte[] stack = (byte[]) config.get(key + ".itemStackAsBytes");
            if (stack != null) {
                return ItemStack.deserializeBytes(stack);
            }
        }

        final ConfigurationSection configSection = config.getConfigurationSection(key + ".itemStack");
        if (configSection != null) {
            return ItemStack.deserialize(configSection.getValues(true));
        }

        final String materialString = config.getString(key + ".material");
        if (materialString == null) {
            // Assume no item (ie ingredient 2)
            return null;
        }

        @Nullable ItemBuilder<?, ?> itemBuilder;
        if (materialString.startsWith("head-")) {
            itemBuilder = new HeadBuilder(materialString.substring(5));
        } else {
            final Material material = Material.getMaterial(materialString.toUpperCase());
            if (material != null) {
                itemBuilder = ItemBuilder.create(material);
            } else {
                itemBuilder = ItemBuilder.create(Material.STONE);
                Logging.logger().warn("Invalid material '{}' for item at '{}' (will use STONE)", materialString, key);
            }
        }

        final String customName = config.getString(key + ".customname");
        if (customName != null && !customName.equals("NONE") && !customName.isEmpty()) {
            itemBuilder = itemBuilder.miniMessageContext().customName(customName).exit();
        }

        final List<String> lore = config.getStringList(key + ".lore");
        if (lore.size() != 0) {
            itemBuilder = itemBuilder.miniMessageContext().lore(lore).exit();
        }

        final int amount = config.getInt(key + ".amount", 1);
        itemBuilder = itemBuilder.stackSize(amount);

        itemBuilder = applyEnchants(
            itemBuilder,
            config.getStringList(key + ".enchantments"),
            key
        );

        return itemBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private static ItemBuilder<?, ?> applyEnchants(
        ItemBuilder<?, ?> itemBuilder,
        final List<String> enchantStrings,
        final String itemKey
    ) {
        final Material material = itemBuilder.build().getType();
        for (final String enchantString : enchantStrings) {
            final EnchantWithLevel enchantment = readEnchantString(enchantString);

            if (enchantment == null) {
                Logging.logger().warn("'{}' is not a valid enchantment! (item at '{}')", enchantString, itemKey);
                continue;
            }

            if (material == Material.ENCHANTED_BOOK) {
                itemBuilder = ((ItemBuilder<?, EnchantmentStorageMeta>) itemBuilder)
                    .editMeta(meta -> meta.addStoredEnchant(enchantment.enchantment, enchantment.level, true));
            } else {
                itemBuilder = itemBuilder.addEnchant(enchantment.enchantment, enchantment.level);
            }
        }
        return itemBuilder;
    }

    private record EnchantWithLevel(Enchantment enchantment, int level) {
    }

    private static @Nullable EnchantWithLevel readEnchantString(final String enchantString) {
        if (enchantString.contains(":")) {
            final String[] args = enchantString.toLowerCase(Locale.ENGLISH).split(":");
            if (args.length == 1) {
                return new EnchantWithLevel(Enchantment.getByKey(NamespacedKey.minecraft(args[0])), 1);
            } else if (args.length == 2) {
                return new EnchantWithLevel(Enchantment.getByKey(NamespacedKey.minecraft(args[0])), Integer.parseInt(args[1]));
            } else if (args.length == 3) {
                return new EnchantWithLevel(Enchantment.getByKey(new NamespacedKey(args[0], args[1])), Integer.parseInt(args[2]));
            }
            return null;
        }
        return null;
    }
}
