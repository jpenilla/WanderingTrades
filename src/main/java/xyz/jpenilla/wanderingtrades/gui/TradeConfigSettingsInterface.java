package xyz.jpenilla.wanderingtrades.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;
import xyz.jpenilla.wanderingtrades.util.Components;
import xyz.jpenilla.wanderingtrades.util.HeadBuilder;
import xyz.jpenilla.wanderingtrades.util.ItemBuilder;
import xyz.jpenilla.wanderingtrades.util.Logging;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
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
            .title(Component.textOfChildren(Messages.GUI_TC_EDIT_TITLE, Component.text(this.tradeConfig.configName())))
            .addTransform(this.parts.fill())
            .addTransform(this.parts.toggle(
                Messages.GUI_TC_EDIT_ENABLED,
                Messages.GUI_TC_EDIT_DISABLED,
                this.tradeConfig::enabled,
                this.saveConfig(this.tradeConfig::enabled),
                1,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_TC_EDIT_RANDOMIZED,
                Messages.GUI_TC_EDIT_NOT_RANDOMIZED,
                this.tradeConfig::randomized,
                this.saveConfig(this.tradeConfig::randomized),
                3,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_TC_EDIT_INVINCIBLE,
                Messages.GUI_TC_EDIT_NOT_INVINCIBLE,
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
        final ItemStack stack = ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
            .customName(Messages.GUI_TC_EDIT_RANDOM_AMOUNT)
            .lore(
                this.parts.valueLore(this.tradeConfig.randomAmount(), TextColor.color(0x0092FF)),
                Messages.GUI_EDIT_LORE
            )
            .build();
        return ItemStackElement.of(stack, this::randAmountClick);
    }

    private ItemStackElement<ChestPane> chanceElement() {
        final ItemStack stack = ItemBuilder.create(Material.PURPLE_STAINED_GLASS_PANE)
            .customName(Messages.GUI_TC_EDIT_CHANCE)
            .lore(
                this.parts.valueLore(this.tradeConfig.chance(), TextColor.color(0x0092FF)),
                Messages.GUI_EDIT_LORE
            )
            .build();
        return ItemStackElement.of(stack, this::chanceClick);
    }

    private ItemStackElement<ChestPane> customNameElement() {
        final ItemStack stack = ItemBuilder.create(Material.PINK_STAINED_GLASS_PANE)
            .customName(Messages.GUI_TC_EDIT_CUSTOM_NAME)
            .lore(
                this.parts.valueLore(miniMessage().deserialize("<white>" + this.tradeConfig.customName())),
                Messages.GUI_EDIT_LORE
            )
            .build();
        return ItemStackElement.of(stack, this::customNameClick);
    }

    private ItemStackElement<ChestPane> deleteElement() {
        final ItemStack stack = new HeadBuilder(HeadSkins.RED_RECYCLE_BIN_FULL)
            .customName(Messages.GUI_TRADE_DELETE)
            .lore(Messages.GUI_CONFIG_DELETE_LORE)
            .build();
        return ItemStackElement.of(stack, this::deleteClick);
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
                player.sendMessage(Messages.MESSAGE_SET_RAND_AMOUNT_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.tradeConfig.randomAmount())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER_OR_RANGE);
                return "";
            })
            .onValidateInput(this.validators::validateIntRange)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeConfig.randomAmount(s);
                this.tradeConfig.save();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void chanceClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_SET_CHANCE_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.tradeConfig.chance())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateDouble0T1)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeConfig.chance(Double.parseDouble(s));
                this.tradeConfig.save();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void customNameClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_CREATE_TITLE_OR_NONE_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(
                    Components.valuePlaceholder(miniMessage().deserialize(this.tradeConfig.customName()))
                ));
                return "";
            })
            .onValidateInput((pl, s) -> true)
            .onConfirmText(this.validators.confirmYesNo(miniMessage()::deserialize))
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
                player.sendMessage(
                    Messages.MESSAGE_DELETE_PROMPT.withPlaceholders(Components.placeholder("name", this.tradeConfig.configName()))
                );
                player.sendMessage(
                    Messages.MESSAGE_CONFIRM.withPlaceholders(Components.placeholder("key", Messages.MESSAGE_CONFIRM_KEY.message()))
                );
                return "";
            })
            .onValidateInput((player, s) -> {
                if (s.equals(Messages.MESSAGE_CONFIRM_KEY.message())) {
                    final Path tcFile = this.plugin.getDataPath().resolve("trades/" + this.tradeConfig.configName() + ".yml");
                    try {
                        Files.delete(tcFile);
                    } catch (Exception e) {
                        Logging.logger().warn("File delete failed", e);
                    }
                    this.plugin.configManager().reload();
                    player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                    new ListTradeConfigsInterface(this.plugin).open(player);
                } else {
                    this.validators.editCancelled(player, s);
                }
                return true;
            })
            .start(context.viewer().player());
    }
}
