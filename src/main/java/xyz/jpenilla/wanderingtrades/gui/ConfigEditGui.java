package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.Lang;

@DefaultQualifier(NonNull.class)
public final class ConfigEditGui extends BaseGui {
    private final ItemStack enabledEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_ENABLED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack enabledDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_DISABLED))
        .setLore(this.toggleLore)
        .build();

    private final ItemStack allowMultipleSets = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_ALLOW_MULTIPLE_SETS))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack disallowMultipleSets = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_DISALLOW_MULTIPLE_SETS))
        .setLore(this.toggleLore)
        .build();

    private final ItemStack removeOriginalTradesEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_REMOVE_ORIGINAL))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack removeOriginalTradesDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_KEEP_ORIGINAL))
        .setLore(this.toggleLore)
        .build();

    private final ItemStack refreshTradesEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_REFRESH))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack refreshTradesDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_NO_REFRESH))
        .setLore(this.toggleLore)
        .build();

    private final ItemStack preventNightInvisibilityEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_PREVENT_NIGHT_INVISIBILITY))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack preventNightInvisibilityDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_ALLOW_NIGHT_INVISIBILITY))
        .setLore(this.toggleLore)
        .build();

    private final ItemStack wgWhitelist = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_WG_WHITE))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack wgBlacklist = new ItemBuilder(Material.BEDROCK)
        .setName(this.lang.get(Lang.GUI_CONFIG_WG_BLACK))
        .setLore(this.toggleLore).build();

    private final ItemStack wgList = new ItemBuilder(Material.PAPER)
        .setName(this.lang.get(Lang.GUI_CONFIG_WG_LIST))
        .build();

    private final ItemStack refreshTradersMinutes = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_CONFIG_REFRESH_MINUTES))
        .build();

    public ConfigEditGui(final WanderingTrades plugin) {
        super(plugin, plugin.langConfig().get(Lang.GUI_CONFIG_TITLE), 45);
    }

    @Override
    public Inventory getInventory() {
        this.inventory.clear();
        final Config config = this.plugin.config();

        this.inventory.setItem(10, config.enabled() ? this.enabledEnabled : this.enabledDisabled);
        this.inventory.setItem(12, config.allowMultipleSets() ? this.allowMultipleSets : this.disallowMultipleSets);
        this.inventory.setItem(14, config.removeOriginalTrades() ? this.removeOriginalTradesEnabled : this.removeOriginalTradesDisabled);
        this.inventory.setItem(16, config.refreshCommandTraders() ? this.refreshTradesEnabled : this.refreshTradesDisabled);
        this.inventory.setItem(28, config.preventNightInvisibility() ? this.preventNightInvisibilityEnabled : this.preventNightInvisibilityDisabled);

        final List<String> refreshLore = List.of(
            this.lang.get(Lang.GUI_CONFIG_REFRESH_MINUTES_LORE).replace("{VALUE}", String.valueOf(config.refreshCommandTradersMinutes())),
            this.lang.get(Lang.GUI_EDIT_LORE)
        );
        this.inventory.setItem(30, new ItemBuilder(this.refreshTradersMinutes).setLore(refreshLore).build());

        this.inventory.setItem(32, config.wgWhitelist() ? this.wgWhitelist : this.wgBlacklist);

        final List<String> wgListLore = new ArrayList<>(List.of(
            this.lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
            ""
        ));
        config.wgRegionList().forEach(region -> wgListLore.add(" <aqua>-</aqua> <white>" + region));
        this.inventory.setItem(34, new ItemBuilder(this.wgList).setLore(wgListLore).build());

        this.inventory.setItem(this.inventory.getSize() - 1, this.closeButton);

        this.fillEmptySlots();
        return this.inventory;
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();
        final ClickType click = event.getClick();

        if (this.closeButton.isSimilar(item)) {
            player.closeInventory();
        }

        final Config config = this.plugin.config();

        if (this.enabledEnabled.isSimilar(item)) {
            config.enabled(false);
        } else if (this.enabledDisabled.isSimilar(item)) {
            config.enabled(true);
        }

        if (this.allowMultipleSets.isSimilar(item)) {
            config.allowMultipleSets(false);
        } else if (this.disallowMultipleSets.isSimilar(item)) {
            config.allowMultipleSets(true);
        }

        if (this.removeOriginalTradesEnabled.isSimilar(item)) {
            config.removeOriginalTrades(false);
        } else if (this.removeOriginalTradesDisabled.isSimilar(item)) {
            config.removeOriginalTrades(true);
        }

        if (this.refreshTradesEnabled.isSimilar(item)) {
            config.refreshCommandTraders(false);
        } else if (this.refreshTradesDisabled.isSimilar(item)) {
            config.refreshCommandTraders(true);
        }

        if (this.preventNightInvisibilityEnabled.isSimilar(item)) {
            config.preventNightInvisibility(false);
        } else if (this.preventNightInvisibilityDisabled.isSimilar(item)) {
            config.preventNightInvisibility(true);
        }

        if (this.wgBlacklist.isSimilar(item)) {
            config.wgWhitelist(true);
        } else if (this.wgWhitelist.isSimilar(item)) {
            config.wgWhitelist(false);
        }

        if (this.refreshTradersMinutes.isSimilar(item)) {
            this.refreshMinutesClick(player, config);
        }

        if (this.wgList.isSimilar(item)) {
            this.wgListClick(player, click, config);
        }

        config.save();

        this.getInventory();
    }

    private void refreshMinutesClick(final Player p, final Config config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_REFRESH_DELAY_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.refreshCommandTradersMinutes()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateIntGTE0)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                config.refreshCommandTradersMinutes(Integer.parseInt(s));
                config.save();
                this.open(p);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void wgListClick(final Player p, final ClickType click, final Config config) {
        if (click.isRightClick()) {
            List<String> l = config.wgRegionList();
            if (!(l.size() - 1 < 0)) {
                l.remove(l.size() - 1);
            }
            config.wgRegionList(config.wgRegionList());
        } else {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ADD_WG_REGION));
                    return "";
                })
                .onValidateInput((player, input) -> {
                    if (input.contains(" ")) {
                        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NO_SPACES));
                        return false;
                    }
                    if (TextUtil.containsCaseInsensitive(input, config.wgRegionList())) {
                        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                        return false;
                    }
                    return true;
                })
                .onConfirmText(this::confirmYesNo)
                .onAccepted((player, s) -> {
                    List<String> temp = config.wgRegionList();
                    temp.add(s);
                    config.wgRegionList(temp);
                    config.save();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    this.open(p);
                })
                .onDenied(this::editCancelled)
                .start(p);
        }
    }

    @Override
    public void reOpen(final Player player) {
        new ConfigEditGui(this.plugin).open(player);
    }
}
