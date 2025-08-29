package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;
import xyz.jpenilla.wanderingtrades.util.Components;
import xyz.jpenilla.wanderingtrades.util.ItemBuilder;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@NullMarked
public final class MainConfigInterface extends BaseInterface {
    public MainConfigInterface(final WanderingTrades plugin) {
        super(plugin);
    }

    @Override
    protected ChestInterface buildInterface() {
        return ChestInterface.builder()
            .rows(5)
            .title(Messages.GUI_CONFIG_TITLE.asComponent())
            .addTransform(this.parts.fill())
            .addTransform(this.parts.toggle(
                Messages.GUI_CONFIG_ENABLED,
                Messages.GUI_CONFIG_DISABLED,
                this.plugin.config()::enabled,
                this.saveConfig(value -> {
                    this.plugin.config().enabled(value);
                    this.plugin.listeners().reload();
                }),
                1,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_CONFIG_ALLOW_MULTIPLE_SETS,
                Messages.GUI_CONFIG_DISALLOW_MULTIPLE_SETS,
                this.plugin.config()::allowMultipleSets,
                this.saveConfig(this.plugin.config()::allowMultipleSets),
                3,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_CONFIG_REMOVE_ORIGINAL,
                Messages.GUI_CONFIG_KEEP_ORIGINAL,
                this.plugin.config()::removeOriginalTrades,
                this.saveConfig(this.plugin.config()::removeOriginalTrades),
                5,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_CONFIG_REFRESH,
                Messages.GUI_CONFIG_NO_REFRESH,
                this.plugin.config()::refreshCommandTraders,
                this.saveConfig(value -> {
                    this.plugin.config().refreshCommandTraders(value);
                    this.plugin.listeners().reload();
                }),
                7,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_CONFIG_PREVENT_NIGHT_INVISIBILITY,
                Messages.GUI_CONFIG_ALLOW_NIGHT_INVISIBILITY,
                this.plugin.config()::preventNightInvisibility,
                this.saveConfig(this.plugin.config()::preventNightInvisibility),
                1,
                3
            ))
            .addTransform(chestItem(this::refreshMinutesElement, 3, 3))
            .addTransform(this.parts.toggle(
                Messages.GUI_CONFIG_WG_WHITE,
                Material.WHITE_STAINED_GLASS_PANE,
                Messages.GUI_CONFIG_WG_BLACK,
                Material.BEDROCK,
                this.plugin.config()::wgWhitelist,
                this.saveConfig(this.plugin.config()::wgWhitelist),
                5,
                3
            ))
            .addTransform(chestItem(this::wgListElement, 7, 3))
            .addTransform(this.parts.closeButton())
            .build();
    }

    private BooleanConsumer saveConfig(final BooleanConsumer setter) {
        return value -> {
            setter.accept(value);
            this.plugin.config().save();
        };
    }

    private ItemStackElement<ChestPane> refreshMinutesElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .customName(Messages.GUI_CONFIG_REFRESH_MINUTES)
                .lore(
                    Messages.GUI_CONFIG_REFRESH_MINUTES_LORE.withPlaceholders(Components.placeholder("value", this.plugin.config().refreshCommandTradersMinutes())),
                    Messages.GUI_EDIT_LORE
                )
                .build(),
            this::refreshMinutesClick
        );
    }

    private ItemStackElement<ChestPane> wgListElement() {
        final List<ComponentLike> wgListLore = new ArrayList<>(List.of(
            Messages.GUI_CONFIG_WG_LIST_LORE,
            Component.empty()
        ));
        for (final String region : this.plugin.config().wgRegionList()) {
            wgListLore.add(Component.textOfChildren(
                Component.text(" - ", NamedTextColor.AQUA),
                Component.text(region, NamedTextColor.WHITE)
            ));
        }

        return ItemStackElement.of(
            ItemBuilder.create(Material.PAPER)
                .customName(Messages.GUI_CONFIG_WG_LIST)
                .lore(wgListLore)
                .build(),
            this::wgListClick
        );
    }

    private void refreshMinutesClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_SET_REFRESH_DELAY_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.plugin.config().refreshCommandTradersMinutes())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateIntGTE0)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.plugin.config().refreshCommandTradersMinutes(Integer.parseInt(s));
                this.plugin.config().save();
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void wgListClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        final Config config = this.plugin.config();

        if (context.click().rightClick()) {
            // Possible improvement: have user type name of element to remove instead of removing from tail
            List<String> l = config.wgRegionList();
            if (!(l.size() - 1 < 0)) {
                l.remove(l.size() - 1);
            }
            config.wgRegionList(config.wgRegionList());
            config.save();
            context.view().update();
            return;
        }

        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_ADD_WG_REGION);
                return "";
            })
            .onValidateInput((player, input) -> {
                if (input.contains(" ")) {
                    player.sendMessage(Messages.MESSAGE_NO_SPACES);
                    return false;
                }
                if (TextUtil.containsCaseInsensitive(input, config.wgRegionList())) {
                    player.sendMessage(Messages.MESSAGE_CREATE_UNIQUE);
                    return false;
                }
                return true;
            })
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                List<String> temp = config.wgRegionList();
                temp.add(s);
                config.wgRegionList(temp);
                config.save();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }
}
