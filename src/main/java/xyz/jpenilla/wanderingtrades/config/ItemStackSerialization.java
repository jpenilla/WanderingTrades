package xyz.jpenilla.wanderingtrades.config;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.util.HeadBuilder;
import xyz.jpenilla.wanderingtrades.util.ItemBuilder;
import xyz.jpenilla.wanderingtrades.util.Logging;

public final class ItemStackSerialization {
    private static final List<String> COMMENT = List.of(
        "The following value was serialized from an in-game item and is not meant to be human-readable or editable. As long as it is present other options for this item will be ignored."
    );

    private ItemStackSerialization() {
    }

    public static void writeOrRemove(final FileConfiguration config, final String path, final @Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            config.set(path, null);
            return;
        }
        // We don't null out an already existing section
        // Meaning if the item was originally added via config, and is later saved in serialized form, both will be in the config
        // but only the serialized form will be used. This avoids accidental data loss at the cost of config clutter which could confuse
        // some users...
        
        final String paperPath = path + ".itemStackAsBytes";
        final String bukkitPath = path + ".itemStack";
        final String selectedPath = true ? paperPath : bukkitPath;
        final Object value = true ? itemStack.serializeAsBytes() : itemStack.serialize();
        config.set(selectedPath, value);

        // Remove old bukkit value if re-saving as paper value or changed while on paper
        if (true && config.get(bukkitPath, null) != null) {
            config.set(bukkitPath, null);
        }
        try {
            config.getClass().getMethod("getComments", String.class);
            config.setComments(selectedPath, COMMENT);
        } catch (final NoSuchMethodException ignore) {
        }
    }

    public static @Nullable ItemStack read(final FileConfiguration config, final String key) {
        if (true) {
            final byte[] stack = (byte[]) config.get(key + ".itemStackAsBytes");
            if (stack != null) {
                return ItemStack.deserializeBytes(stack);
            }
        }
        final ConfigurationSection configSection = config.getConfigurationSection(key + ".itemStack");
        if (configSection != null) {
            return ItemStack.deserialize(configSection.getValues(true));
        }

        final String materialString = config.getString(key + ".material", null);
        if (materialString == null) {
            // Assume no item (ie ingredient 2)
            return null;
        }

        @Nullable ItemBuilder<?> itemBuilder;
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
        if (!lore.isEmpty()) {
            itemBuilder = itemBuilder.miniMessageContext().lore(lore).exit();
        }

        final int amount = config.getInt(key + ".amount", 1);
        itemBuilder = itemBuilder.stackSize(amount);

        itemBuilder = applyEnchants(itemBuilder, config.getStringList(key + ".enchantments"), key);

        return itemBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private static ItemBuilder<?> applyEnchants(
        ItemBuilder<?> itemBuilder,
        final List<String> enchantStrings,
        final String itemKey
    ) {
        if (enchantStrings.isEmpty()) {
            return itemBuilder;
        }

        final Material material = itemBuilder.type();
        final List<EnchantWithLevel> parsed = new ArrayList<>(enchantStrings.size());
        for (final String s : enchantStrings) {
            final EnchantWithLevel ewl = readEnchantString(s);
            if (ewl == null || ewl.enchantment == null) {
                Logging.logger().warn("'{}' is not a valid enchantment! (item at '{}')", s, itemKey);
                continue;
            }
            parsed.add(ewl);
        }
        if (parsed.isEmpty()) {
            return itemBuilder;
        }

        if (material == Material.ENCHANTED_BOOK) {
            itemBuilder = ((ItemBuilder<EnchantmentStorageMeta>) itemBuilder).editMeta(meta -> {
                for (final EnchantWithLevel e : parsed) {
                    meta.addStoredEnchant(e.enchantment, e.level, true);
                }
            });
        } else {
            itemBuilder = itemBuilder.editMeta(meta -> {
                for (final EnchantWithLevel e : parsed) {
                    meta.addEnchant(e.enchantment, e.level, true);
                }
            });
        }
        return itemBuilder;
    }

    private record EnchantWithLevel(Enchantment enchantment, int level) {}

    private static @Nullable EnchantWithLevel readEnchantString(final String raw) {
        final String[] args = raw.toLowerCase(Locale.ENGLISH).split(":");
        final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

        NamespacedKey key;
        int level;

        try {
            if (args.length == 1) {
                key = NamespacedKey.minecraft(args[0]);
                level = 1;
            } else if (args.length == 2) {
                key = NamespacedKey.minecraft(args[0]);
                level = Integer.parseInt(args[1]);
            } else if (args.length == 3) {
                key = new NamespacedKey(args[0], args[1]);
                level = Integer.parseInt(args[2]);
            } else {
                return null;
            }
        } catch (final NumberFormatException e) {
            return null;
        }

        final Enchantment ench = registry.get(key);
        if (ench == null) {
            return null;
        }
        return new EnchantWithLevel(ench, level);
    }
}
