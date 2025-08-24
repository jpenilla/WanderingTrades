package xyz.jpenilla.wanderingtrades.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

/**
 * Immutable builder for {@link ItemStack}.
 *
 * @param <I> meta type
 */
@DefaultQualifier(NonNull.class)
@SuppressWarnings("unused")
public class ItemBuilder<I extends ItemMeta> {
    private final ItemStack stack;
    private final I meta;

    public static <I extends ItemMeta> ItemBuilder<I> create(final Material material) {
        return new ItemBuilder<>(material);
    }

    public static <I extends ItemMeta> ItemBuilder<I> create(final ItemStack stack) {
        return new ItemBuilder<>(stack);
    }
    
    protected ItemBuilder(final ItemStack stack, final I meta) {
        this.stack = stack;
        this.meta = meta;
    }

    protected ItemBuilder(final Material material) {
        this(new ItemStack(material));
    }

    @SuppressWarnings("unchecked")
    protected ItemBuilder(final ItemStack stack) {
        this.stack = stack.clone();
        this.meta = (I) stack.getItemMeta();
    }

    public final MiniMessageContext miniMessageContext() {
        return new MiniMessageContext();
    }

    @SuppressWarnings("unchecked")
    protected final ItemBuilder<I> edit(final BiConsumer<ItemStack, I> consumer) {
        final ItemStack stack = this.stack.clone();
        final I meta = (I) this.meta.clone();
        consumer.accept(stack, meta);
        return this.create(stack, meta);
    }

    protected ItemBuilder<I> create(final ItemStack stack, final I meta) {
        return new ItemBuilder<>(stack, meta);
    }

    public final ItemBuilder<I> stackSize(final int amount) {
        return this.edit((stack, meta) -> stack.setAmount(amount));
    }

    public final ItemBuilder<I> customName(final ComponentLike displayName) {
        return this.editMeta(meta -> meta.customName(Components.disableItalics(displayName.asComponent())));
    }

    public final ItemBuilder<I> addLore(final List<? extends ComponentLike> lore) {
        return this.editMeta(meta -> {
            @Nullable List<Component> newLore = meta.lore();
            if (newLore != null) {
                newLore = new ArrayList<>(newLore);
            } else {
                newLore = new ArrayList<>();
            }
            newLore.addAll(
                lore.stream()
                    .map(ComponentLike::asComponent)
                    .map(Components::disableItalics)
                    .toList()
            );
            meta.lore(newLore);
        });
    }

    public final ItemBuilder<I> addLore(final ComponentLike... lore) {
        return this.addLore(Arrays.asList(lore));
    }

    public final ItemBuilder<I> lore(final List<? extends ComponentLike> lore) {
        return this.editMeta(meta -> {
            final List<Component> mapped = lore.stream()
                .map(ComponentLike::asComponent)
                .map(Components::disableItalics)
                .collect(Collectors.toList());
            meta.lore(mapped);
        });
    }

    public final ItemBuilder<I> lore(final ComponentLike... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public final ItemBuilder<I> clearLore() {
        return this.editMeta(meta -> meta.lore(new ArrayList<>()));
    }

    public final Map<Enchantment, Integer> enchants() {
        return this.meta.getEnchants();
    }

    public final ItemBuilder<I> enchants(final Map<Enchantment, Integer> enchants) {
        return this.clearEnchants().addEnchants(enchants);
    }

    public final ItemBuilder<I> addEnchant(final Enchantment enchantment, final int level) {
        return this.editMeta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public final ItemBuilder<I> addEnchants(final Map<Enchantment, Integer> enchants) {
        return this.editMeta(meta -> enchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true)));
    }

    public final ItemBuilder<I> clearEnchants() {
        return this.editMeta(meta -> meta.getEnchants().keySet().forEach(meta::removeEnchant));
    }

    public final ItemStack build() {
        final ItemStack stack = this.stack.clone();
        stack.setItemMeta(this.meta);
        return stack;
    }

    @SuppressWarnings("unchecked")
    public final I meta() {
        return (I) this.meta.clone();
    }

    @SuppressWarnings("unchecked")
    public final ItemBuilder<I> meta(final I meta) {
        return this.create(this.stack, (I) meta.clone());
    }

    @SuppressWarnings("unchecked")
    public final ItemBuilder<I> editMeta(final Consumer<I> consumer) {
        final I meta = (I) this.meta.clone();
        consumer.accept(meta);
        return this.create(
            this.stack,
            meta // A defensive clone would make sense here, but it's likely not needed
        );
    }

    public final class MiniMessageContext {
        private MiniMessageContext() {
        }

        public ItemBuilder<I> exit() {
            return ItemBuilder.this;
        }

        public ItemStack exitAndBuild() {
            return this.exit().build();
        }

        public MiniMessageContext customName(final String customName) {
            return ItemBuilder.this.customName(this.deserialize(customName)).miniMessageContext();
        }

        public MiniMessageContext addLore(final List<String> lore) {
            return ItemBuilder.this.addLore(this.deserialize(lore)).miniMessageContext();
        }

        public MiniMessageContext addLore(final String... lore) {
            return this.addLore(Arrays.asList(lore));
        }

        public MiniMessageContext lore(final List<String> lore) {
            return ItemBuilder.this.lore(this.deserialize(lore)).miniMessageContext();
        }

        public MiniMessageContext lore(final String... lore) {
            return this.lore(Arrays.asList(lore));
        }

        private Component deserialize(final String mini) {
            return miniMessage().deserialize(mini);
        }

        private List<Component> deserialize(final List<String> mini) {
            return mini.stream().map(this::deserialize).collect(Collectors.toList());
        }
    }
}
