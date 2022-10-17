package xyz.jpenilla.wanderingtrades.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;
import xyz.jpenilla.wanderingtrades.util.Logging;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@DefaultQualifier(NonNull.class)
public final class TradeConfigSettingsInterface extends BaseInterface {
    private final TradeConfig tradeConfig;

    public TradeConfigSettingsInterface(final WanderingTrades plugin, final TradeConfig tradeConfig) {
        super(plugin);
        this.tradeConfig = tradeConfig;
    }

    @Override
    protected ChestInterface buildInterface() {
        return ChestInterface.builder()
            .rows(5)
            .title(this.plugin.miniMessage().deserialize(this.lang.get(Lang.GUI_TC_EDIT_TITLE) + this.tradeConfig.configName()))
            .addTransform(this.parts.fill())
            .addTransform(this.parts.toggle(
                this.lang.get(Lang.GUI_TC_EDIT_ENABLED),
                this.lang.get(Lang.GUI_TC_EDIT_DISABLED),
                this.tradeConfig::enabled,
                this.saveConfig(this.tradeConfig::enabled),
                1,
                1
            ))
            .addTransform(this.parts.toggle(
                this.lang.get(Lang.GUI_TC_EDIT_RANDOMIZED),
                this.lang.get(Lang.GUI_TC_EDIT_NOT_RANDOMIZED),
                this.tradeConfig::randomized,
                this.saveConfig(this.tradeConfig::randomized),
                3,
                1
            ))
            .addTransform(this.parts.toggle(
                this.lang.get(Lang.GUI_TC_EDIT_INVINCIBLE),
                this.lang.get(Lang.GUI_TC_EDIT_NOT_INVINCIBLE),
                this.tradeConfig::invincible,
                this.saveConfig(this.tradeConfig::invincible),
                5,
                1
            ))
            .addTransform(chestItem(this::randomAmountElement, 7, 1))
            .addTransform(chestItem(this::chanceElement, 1, 3))
            .addTransform(chestItem(this::customNameElement, 3, 3))
            .addTransform(chestItem(this.deleteElement(), 7, 3))
            .addTransform(this.parts.backButton(context -> new ListTradesInterface(this.plugin, this.tradeConfig).replaceActiveScreen(context)))
            .build();
    }

    private ItemStackElement<ChestPane> randomAmountElement() {
        return ItemStackElement.of(
            new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .setName(this.lang.get(Lang.GUI_TC_EDIT_RANDOM_AMOUNT))
                .setLore(List.of(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.tradeConfig.randomAmount(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                ))
                .build(),
            this::randAmountClick
        );
    }

    private ItemStackElement<ChestPane> chanceElement() {
        return ItemStackElement.of(
            new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE)
                .setName(this.lang.get(Lang.GUI_TC_EDIT_CHANCE))
                .setLore(List.of(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.tradeConfig.chance(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                ))
                .build(),
            this::chanceClick
        );
    }

    private ItemStackElement<ChestPane> customNameElement() {
        return ItemStackElement.of(
            new ItemBuilder(Material.PINK_STAINED_GLASS_PANE)
                .setName(this.lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME))
                .setLore(List.of(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<white>" + this.tradeConfig.customName(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                ))
                .build(),
            this::customNameClick
        );
    }

    private ItemStackElement<ChestPane> deleteElement() {
        return ItemStackElement.of(
            new HeadBuilder(HeadSkins.RED_RECYCLE_BIN_FULL)
                .setName(this.lang.get(Lang.GUI_TRADE_DELETE))
                .setLore(this.lang.get(Lang.GUI_CONFIG_DELETE_LORE))
                .build(),
            this::deleteClick
        );
    }

    private BooleanConsumer saveConfig(final BooleanConsumer setter) {
        return value -> {
            setter.accept(value);
            this.tradeConfig.save();
        };
    }

    private void randAmountClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_RAND_AMOUNT_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.tradeConfig.randomAmount()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                return "";
            })
            .onValidateInput(this.validators::validateIntRange)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeConfig.randomAmount(s);
                this.tradeConfig.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void chanceClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_CHANCE_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.tradeConfig.chance()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this.validators::validateDouble0T1)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeConfig.chance(Double.parseDouble(s));
                this.tradeConfig.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void customNameClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_CREATE_TITLE_OR_NONE_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + "<reset>" + this.tradeConfig.customName());
                return "";
            })
            .onValidateInput((pl, s) -> true)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, string) -> {
                this.tradeConfig.customName(string);
                this.tradeConfig.save();
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void deleteClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_DELETE_PROMPT).replace("{TRADE_NAME}", this.tradeConfig.configName()));
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CONFIRM).replace("{KEY}", this.lang.get(Lang.MESSAGE_CONFIRM_KEY)));
                return "";
            })
            .onValidateInput((player, s) -> {
                if (s.equals(this.lang.get(Lang.MESSAGE_CONFIRM_KEY))) {
                    final Path tcFile = this.plugin.dataPath().resolve("trades/" + this.tradeConfig.configName() + ".yml");
                    try {
                        Files.delete(tcFile);
                    } catch (Exception e) {
                        Logging.logger().warn("File delete failed", e);
                    }
                    this.plugin.configManager().reload();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    new ListTradeConfigsInterface(this.plugin).open(player);
                } else {
                    this.validators.editCancelled(player, s);
                }
                return true;
            })
            .start(context.viewer().player());
    }
}
