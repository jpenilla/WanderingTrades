package xyz.jpenilla.wanderingtrades.config;

import cloud.commandframework.minecraft.extras.RichDescription;
import com.google.common.base.Suppliers;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.util.Logging;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("unused") // Command help messages retrieved by key
public final class Messages {
    private static final List<Consumer<YamlConfiguration>> MIGRATIONS = List.of();
    private static final Map<String, Message> MESSAGES = new HashMap<>();

    @Key(value = "command.about.description", legacyKey = "COMMAND_WT_ABOUT")
    public static SingleMessage COMMAND_ABOUT_DESCRIPTION;
    @Key(value = "command.argument.help-query", legacyKey = "COMMAND_ARGUMENT_HELP_QUERY")
    public static SingleMessage COMMAND_ARGUMENT_HELP_QUERY;
    @Key(value = "command.exception.parse.failure-enum", legacyKey = "COMMAND_ARGUMENT_PARSE_FAILURE_ENUM")
    public static SingleMessage COMMAND_ARGUMENT_PARSE_FAILURE_ENUM;
    @Key(value = "command.exception.parse.failure-location-invalid-format", legacyKey = "COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT")
    public static SingleMessage COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT;
    @Key(value = "command.exception.parse.failure-location-mixed-local-absolute", legacyKey = "COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE")
    public static SingleMessage COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE;
    @Key(value = "command.editconfig.description", legacyKey = "COMMAND_WT_CONFIG")
    public static SingleMessage COMMAND_EDITCONFIG_DESCRIPTION;
    @Key(value = "command.editplayerheads.description", legacyKey = "COMMAND_WT_PH_CONFIG")
    public static SingleMessage COMMAND_EDITPLAYERHEADS_DESCRIPTION;
    @Key(value = "command.edit.description", legacyKey = "COMMAND_WT_EDIT")
    public static SingleMessage COMMAND_EDIT_DESCRIPTION;
    @Key(value = "command.help.description", legacyKey = "COMMAND_WT_HELP")
    public static SingleMessage COMMAND_HELP_DESCRIPTION;
    @Key(value = "command.help.message.arguments", legacyKey = "HELP_ARGUMENTS")
    public static SingleMessage COMMAND_HELP_MESSAGE_ARGUMENTS;
    @Key(value = "command.help.message.available-commands", legacyKey = "HELP_AVAILABLE_COMMANDS")
    public static SingleMessage COMMAND_HELP_MESSAGE_AVAILABLE_COMMANDS;
    @Key(value = "command.help.message.click-for-next-page", legacyKey = "HELP_CLICK_FOR_NEXT_PAGE")
    public static SingleMessage COMMAND_HELP_MESSAGE_CLICK_FOR_NEXT_PAGE;
    @Key(value = "command.help.message.click-for-previous-page", legacyKey = "HELP_CLICK_FOR_PREVIOUS_PAGE")
    public static SingleMessage COMMAND_HELP_MESSAGE_CLICK_FOR_PREVIOUS_PAGE;
    @Key(value = "command.help.message.click-to-show-help", legacyKey = "HELP_CLICK_TO_SHOW_HELP")
    public static SingleMessage COMMAND_HELP_MESSAGE_CLICK_TO_SHOW_HELP;
    @Key(value = "command.help.message.command", legacyKey = "HELP_COMMAND")
    public static SingleMessage COMMAND_HELP_MESSAGE_COMMAND;
    @Key(value = "command.help.message.description", legacyKey = "HELP_DESCRIPTION")
    public static SingleMessage COMMAND_HELP_MESSAGE_DESCRIPTION;
    @Key(value = "command.help.message.help", legacyKey = "HELP_HELP")
    public static SingleMessage COMMAND_HELP_MESSAGE_HELP;
    @Key(value = "command.help.message.no-description", legacyKey = "HELP_NO_DESCRIPTION")
    public static SingleMessage COMMAND_HELP_MESSAGE_NO_DESCRIPTION;
    @Key(value = "command.help.message.no-results-for-query", legacyKey = "HELP_NO_RESULTS_FOR_QUERY")
    public static SingleMessage COMMAND_HELP_MESSAGE_NO_RESULTS_FOR_QUERY;
    @Key(value = "command.help.message.optional", legacyKey = "HELP_OPTIONAL")
    public static SingleMessage COMMAND_HELP_MESSAGE_OPTIONAL;
    @Key(value = "command.help.message.page-out-of-range", legacyKey = "HELP_PAGE_OUT_OF_RANGE")
    public static SingleMessage COMMAND_HELP_MESSAGE_PAGE_OUT_OF_RANGE;
    @Key(value = "command.help.message.showing-results-for-query", legacyKey = "HELP_SHOWING_RESULTS_FOR_QUERY")
    public static SingleMessage COMMAND_HELP_MESSAGE_SHOWING_RESULTS_FOR_QUERY;
    @Key(value = "command.exception.invalid-argument", legacyKey = "COMMAND_INVALID_ARGUMENT")
    public static SingleMessage COMMAND_INVALID_ARGUMENT;
    @Key(value = "command.exception.invalid-sender", legacyKey = "COMMAND_INVALID_SENDER")
    public static SingleMessage COMMAND_INVALID_SENDER;
    @Key(value = "command.exception.invalid-syntax", legacyKey = "COMMAND_INVALID_SYNTAX")
    public static SingleMessage COMMAND_INVALID_SYNTAX;
    @Key(value = "command.list.description", legacyKey = "COMMAND_WT_LIST")
    public static SingleMessage COMMAND_LIST_DESCRIPTION;
    @Key(value = "command.list.message", legacyKey = "COMMAND_LIST_LOADED")
    public static SingleMessage COMMAND_LIST_LOADED;
    @Key(value = "command.exception.parse.no-trade-config", legacyKey = "COMMAND_SUMMON_NO_CONFIG")
    public static SingleMessage COMMAND_PARSE_EXCEPTION_NO_TRADE_CONFIG;
    @Key(value = "command.reload.message", legacyKey = "COMMAND_RELOAD")
    public static SingleMessage COMMAND_RELOAD;
    @Key(value = "command.reload.description", legacyKey = "COMMAND_WT_RELOAD")
    public static SingleMessage COMMAND_RELOAD_DESCRIPTION;
    @Key(value = "command.reload.done-message", legacyKey = "COMMAND_RELOAD_DONE")
    public static SingleMessage COMMAND_RELOAD_DONE;
    @Key(value = "command.summonnatural.description", legacyKey = "COMMAND_SUMMON_NATURAL")
    public static SingleMessage COMMAND_SUMMONNATURAL_DESCRIPTION;
    @Key(value = "command.summonvillager.description", legacyKey = "COMMAND_VSUMMON")
    public static SingleMessage COMMAND_SUMMONVILLAGER_DESCRIPTION;
    @Key(value = "command.summon.description", legacyKey = "COMMAND_SUMMON")
    public static SingleMessage COMMAND_SUMMON_DESCRIPTION;
    @Key(value = "command.summon.malformed-config", legacyKey = "COMMAND_SUMMON_MALFORMED_CONFIG")
    public static SingleMessage COMMAND_SUMMON_MALFORMED_CONFIG;
    @Key(value = "gui.back.name", legacyKey = "GUI_BACK")
    public static SingleMessage GUI_BACK;
    @Key(value = "gui.back.lore", legacyKey = "GUI_BACK_LORE")
    public static SingleMessage GUI_BACK_LORE;
    @Key(value = "gui.close.name", legacyKey = "GUI_CLOSE")
    public static SingleMessage GUI_CLOSE;
    @Key(value = "gui.close.lore", legacyKey = "GUI_CLOSE_LORE")
    public static SingleMessage GUI_CLOSE_LORE;
    @Key(value = "gui.config.allow-multiple-sets", legacyKey = "GUI_CONFIG_ALLOW_MULTIPLE_SETS")
    public static SingleMessage GUI_CONFIG_ALLOW_MULTIPLE_SETS;
    @Key(value = "gui.config.allow-night-invisibility", legacyKey = "GUI_CONFIG_ALLOW_NIGHT_INVISIBILITY")
    public static SingleMessage GUI_CONFIG_ALLOW_NIGHT_INVISIBILITY;
    @Key(value = "gui.tc-edit.config-delete-lore", legacyKey = "GUI_CONFIG_DELETE_LORE")
    public static SingleMessage GUI_CONFIG_DELETE_LORE;
    @Key(value = "gui.config.disabled", legacyKey = "GUI_CONFIG_DISABLED")
    public static SingleMessage GUI_CONFIG_DISABLED;
    @Key(value = "gui.config.disallow-multiple-sets", legacyKey = "GUI_CONFIG_DISALLOW_MULTIPLE_SETS")
    public static SingleMessage GUI_CONFIG_DISALLOW_MULTIPLE_SETS;
    @Key(value = "gui.config.enabled", legacyKey = "GUI_CONFIG_ENABLED")
    public static SingleMessage GUI_CONFIG_ENABLED;
    @Key(value = "gui.config.keep-original", legacyKey = "GUI_CONFIG_KEEP_ORIGINAL")
    public static SingleMessage GUI_CONFIG_KEEP_ORIGINAL;
    @Key(value = "gui.config.no-refresh", legacyKey = "GUI_CONFIG_NO_REFRESH")
    public static SingleMessage GUI_CONFIG_NO_REFRESH;
    @Key(value = "gui.config.prevent-night-invisibility", legacyKey = "GUI_CONFIG_PREVENT_NIGHT_INVISIBILITY")
    public static SingleMessage GUI_CONFIG_PREVENT_NIGHT_INVISIBILITY;
    @Key(value = "gui.config.refresh", legacyKey = "GUI_CONFIG_REFRESH")
    public static SingleMessage GUI_CONFIG_REFRESH;
    @Key(value = "gui.config.refresh-minutes.name", legacyKey = "GUI_CONFIG_REFRESH_MINUTES")
    public static SingleMessage GUI_CONFIG_REFRESH_MINUTES;
    @Key(value = "gui.config.refresh-minutes.lore", legacyKey = "GUI_CONFIG_REFRESH_MINUTES_LORE")
    public static SingleMessage GUI_CONFIG_REFRESH_MINUTES_LORE;
    @Key(value = "gui.config.remove-original", legacyKey = "GUI_CONFIG_REMOVE_ORIGINAL")
    public static SingleMessage GUI_CONFIG_REMOVE_ORIGINAL;
    @Key(value = "gui.config.title", legacyKey = "GUI_CONFIG_TITLE")
    public static SingleMessage GUI_CONFIG_TITLE;
    @Key(value = "gui.config.wg-black", legacyKey = "GUI_CONFIG_WG_BLACK")
    public static SingleMessage GUI_CONFIG_WG_BLACK;
    @Key(value = "gui.config.wg-list.name", legacyKey = "GUI_CONFIG_WG_LIST")
    public static SingleMessage GUI_CONFIG_WG_LIST;
    @Key(value = "gui.config.wg-list.lore", legacyKey = "GUI_CONFIG_WG_LIST_LORE")
    public static SingleMessage GUI_CONFIG_WG_LIST_LORE;
    @Key(value = "gui.config.wg-white", legacyKey = "GUI_CONFIG_WG_WHITE")
    public static SingleMessage GUI_CONFIG_WG_WHITE;
    @Key(value = "gui.edit-lore", legacyKey = "GUI_EDIT_LORE")
    public static SingleMessage GUI_EDIT_LORE;
    @Key(value = "gui.paged.last.name", legacyKey = "GUI_PAGED_LAST")
    public static SingleMessage GUI_PAGED_LAST;
    @Key(value = "gui.paged.last.lore", legacyKey = "GUI_PAGED_LAST_LORE")
    public static SingleMessage GUI_PAGED_LAST_LORE;
    @Key(value = "gui.paged.next.name", legacyKey = "GUI_PAGED_NEXT")
    public static SingleMessage GUI_PAGED_NEXT;
    @Key(value = "gui.paged.next.lore", legacyKey = "GUI_PAGED_NEXT_LORE")
    public static SingleMessage GUI_PAGED_NEXT_LORE;
    @Key(value = "gui.ph-config.amount", legacyKey = "GUI_PH_CONFIG_AMOUNT")
    public static SingleMessage GUI_PH_CONFIG_AMOUNT;
    @Key(value = "gui.ph-config.amount-heads", legacyKey = "GUI_PH_CONFIG_AMOUNT_HEADS")
    public static SingleMessage GUI_PH_CONFIG_AMOUNT_HEADS;
    @Key(value = "gui.ph-config.blacklist", legacyKey = "GUI_PH_CONFIG_BLACKLIST")
    public static SingleMessage GUI_PH_CONFIG_BLACKLIST;
    @Key(value = "gui.ph-config.chance", legacyKey = "GUI_PH_CONFIG_CHANCE")
    public static SingleMessage GUI_PH_CONFIG_CHANCE;
    @Key(value = "gui.ph-config.days", legacyKey = "GUI_PH_CONFIG_DAYS")
    public static SingleMessage GUI_PH_CONFIG_DAYS;
    @Key(value = "gui.ph-config.days-lore", legacyKey = "GUI_PH_CONFIG_DAYS_LORE")
    public static SingleMessage GUI_PH_CONFIG_DAYS_LORE;
    @Key(value = "gui.ph-config.disabled", legacyKey = "GUI_PH_CONFIG_DISABLED")
    public static SingleMessage GUI_PH_CONFIG_DISABLED;
    @Key(value = "gui.ph-config.enabled", legacyKey = "GUI_PH_CONFIG_ENABLED")
    public static SingleMessage GUI_PH_CONFIG_ENABLED;
    @Key(value = "gui.ph-config.pwl-disabled", legacyKey = "GUI_PH_CONFIG_PWL_DISABLED")
    public static SingleMessage GUI_PH_CONFIG_PWL_DISABLED;
    @Key(value = "gui.ph-config.pwl-enabled", legacyKey = "GUI_PH_CONFIG_PWL_ENABLED")
    public static SingleMessage GUI_PH_CONFIG_PWL_ENABLED;
    @Key(value = "gui.ph-config.pwl-lore", legacyKey = "GUI_PH_CONFIG_PWL_LORE")
    public static SingleMessage GUI_PH_CONFIG_PWL_LORE;
    @Key(value = "gui.ph-config.result-lore", legacyKey = "GUI_PH_CONFIG_RESULT_LORE")
    public static SingleMessage GUI_PH_CONFIG_RESULT_LORE;
    @Key(value = "gui.ph-config.save-lore", legacyKey = "GUI_PH_CONFIG_SAVE_LORE")
    public static SingleMessage GUI_PH_CONFIG_SAVE_LORE;
    @Key(value = "gui.ph-config.title", legacyKey = "GUI_PH_CONFIG_TITLE")
    public static SingleMessage GUI_PH_CONFIG_TITLE;
    @Key(value = "gui.tc-edit.chance", legacyKey = "GUI_TC_EDIT_CHANCE")
    public static SingleMessage GUI_TC_EDIT_CHANCE;
    @Key(value = "gui.tc-edit.custom-name", legacyKey = "GUI_TC_EDIT_CUSTOM_NAME")
    public static SingleMessage GUI_TC_EDIT_CUSTOM_NAME;
    @Key(value = "gui.tc-edit.disabled", legacyKey = "GUI_TC_EDIT_DISABLED")
    public static SingleMessage GUI_TC_EDIT_DISABLED;
    @Key(value = "gui.tc-edit.enabled", legacyKey = "GUI_TC_EDIT_ENABLED")
    public static SingleMessage GUI_TC_EDIT_ENABLED;
    @Key(value = "gui.tc-edit.invincible", legacyKey = "GUI_TC_EDIT_INVINCIBLE")
    public static SingleMessage GUI_TC_EDIT_INVINCIBLE;
    @Key(value = "gui.tc-edit.not-invincible", legacyKey = "GUI_TC_EDIT_NOT_INVINCIBLE")
    public static SingleMessage GUI_TC_EDIT_NOT_INVINCIBLE;
    @Key(value = "gui.tc-edit.not-randomized", legacyKey = "GUI_TC_EDIT_NOT_RANDOMIZED")
    public static SingleMessage GUI_TC_EDIT_NOT_RANDOMIZED;
    @Key(value = "gui.tc-edit.randomized", legacyKey = "GUI_TC_EDIT_RANDOMIZED")
    public static SingleMessage GUI_TC_EDIT_RANDOMIZED;
    @Key(value = "gui.tc-edit.random-amount", legacyKey = "GUI_TC_EDIT_RANDOM_AMOUNT")
    public static SingleMessage GUI_TC_EDIT_RANDOM_AMOUNT;
    @Key(value = "gui.tc-edit.title", legacyKey = "GUI_TC_EDIT_TITLE")
    public static SingleMessage GUI_TC_EDIT_TITLE;
    @Key(value = "gui.tc-list.add-config.name", legacyKey = "GUI_TC_LIST_ADD_CONFIG")
    public static SingleMessage GUI_TC_LIST_ADD_CONFIG;
    @Key(value = "gui.tc-list.add-config.lore", legacyKey = "GUI_TC_LIST_ADD_CONFIG_LORE")
    public static SingleMessage GUI_TC_LIST_ADD_CONFIG_LORE;
    @Key(value = "gui.tc-list.and-more", legacyKey = "GUI_TC_LIST_AND_MORE")
    public static SingleMessage GUI_TC_LIST_AND_MORE;
    @Key(value = "gui.tc-list.title", legacyKey = "GUI_TC_LIST_TITLE")
    public static SingleMessage GUI_TC_LIST_TITLE;
    @Key(value = "gui.toggle-lore", legacyKey = "GUI_TOGGLE_LORE")
    public static SingleMessage GUI_TOGGLE_LORE;
    @Key(value = "gui.trade.cancel.name", legacyKey = "GUI_TRADE_CANCEL")
    public static SingleMessage GUI_TRADE_CANCEL;
    @Key(value = "gui.trade.cancel.lore", legacyKey = "GUI_TRADE_CANCEL_LORE")
    public static SingleMessage GUI_TRADE_CANCEL_LORE;
    @Key(value = "gui.trade.create.title", legacyKey = "GUI_TRADE_CREATE_TITLE")
    public static SingleMessage GUI_TRADE_CREATE_TITLE;
    @Key(value = "gui.trade.delete.name", legacyKey = "GUI_TRADE_DELETE")
    public static SingleMessage GUI_TRADE_DELETE;
    @Key(value = "gui.trade.delete.lore", legacyKey = "GUI_TRADE_DELETE_LORE")
    public static SingleMessage GUI_TRADE_DELETE_LORE;
    @Key(value = "gui.trade.edit.title", legacyKey = "GUI_TRADE_EDIT_TITLE")
    public static SingleMessage GUI_TRADE_EDIT_TITLE;
    @Key(value = "gui.trade.exp-reward", legacyKey = "GUI_TRADE_EXP_REWARD")
    public static SingleMessage GUI_TRADE_EXP_REWARD;
    @Key(value = "gui.trade.info.name", legacyKey = "GUI_TRADE_INFO")
    public static SingleMessage GUI_TRADE_INFO;
    @Key(value = "gui.trade.info.lore", legacyKey = "GUI_TRADE_INFO_LORE")
    public static MultiLineMessage GUI_TRADE_INFO_LORE;
    @Key(value = "gui.trade.ingredient-1", legacyKey = "GUI_TRADE_INGREDIENT_1")
    public static SingleMessage GUI_TRADE_INGREDIENT_1;
    @Key(value = "gui.trade.ingredient-2", legacyKey = "GUI_TRADE_INGREDIENT_2")
    public static SingleMessage GUI_TRADE_INGREDIENT_2;
    @Key(value = "gui.trade-list.edit-config.name", legacyKey = "GUI_TRADE_LIST_EDIT_CONFIG")
    public static SingleMessage GUI_TRADE_LIST_EDIT_CONFIG;
    @Key(value = "gui.trade-list.edit-config.lore", legacyKey = "GUI_TRADE_LIST_EDIT_CONFIG_LORE")
    public static SingleMessage GUI_TRADE_LIST_EDIT_CONFIG_LORE;
    @Key(value = "gui.trade-list.new-trade.name", legacyKey = "GUI_TRADE_LIST_NEW_TRADE")
    public static SingleMessage GUI_TRADE_LIST_NEW_TRADE;
    @Key(value = "gui.trade-list.new-trade.lore", legacyKey = "GUI_TRADE_LIST_NEW_TRADE_LORE")
    public static SingleMessage GUI_TRADE_LIST_NEW_TRADE_LORE;
    @Key(value = "gui.trade-list.title", legacyKey = "GUI_TRADE_LIST_TITLE")
    public static SingleMessage GUI_TRADE_LIST_TITLE;
    @Key(value = "gui.trade.max-uses", legacyKey = "GUI_TRADE_MAX_USES")
    public static SingleMessage GUI_TRADE_MAX_USES;
    @Key(value = "gui.trade.no-exp-reward", legacyKey = "GUI_TRADE_NO_EXP_REWARD")
    public static SingleMessage GUI_TRADE_NO_EXP_REWARD;
    @Key(value = "gui.trade.optional-lore", legacyKey = "GUI_TRADE_OPTIONAL_LORE")
    public static MultiLineMessage GUI_TRADE_OPTIONAL_LORE;
    @Key(value = "gui.trade.required-lore", legacyKey = "GUI_TRADE_REQUIRED_LORE")
    public static MultiLineMessage GUI_TRADE_REQUIRED_LORE;
    @Key(value = "gui.trade.result", legacyKey = "GUI_TRADE_RESULT")
    public static SingleMessage GUI_TRADE_RESULT;
    @Key(value = "gui.trade.save.name", legacyKey = "GUI_TRADE_SAVE")
    public static SingleMessage GUI_TRADE_SAVE;
    @Key(value = "gui.trade.save.lore", legacyKey = "GUI_TRADE_SAVE_LORE")
    public static SingleMessage GUI_TRADE_SAVE_LORE;
    @Key(value = "gui.trade.trade-name", legacyKey = "GUI_TRADE_TRADE_NAME")
    public static SingleMessage GUI_TRADE_TRADE_NAME;
    @Key(value = "gui.value-lore", legacyKey = "GUI_VALUE_LORE")
    public static SingleMessage GUI_VALUE_LORE;
    @Key(value = "conversation.add-blacklist-player", legacyKey = "MESSAGE_ADD_BLACKLIST_PLAYER")
    public static SingleMessage MESSAGE_ADD_BLACKLIST_PLAYER;
    @Key(value = "conversation.add-lore-prompt", legacyKey = "MESSAGE_ADD_LORE_PROMPT")
    public static SingleMessage MESSAGE_ADD_LORE_PROMPT;
    @Key(value = "conversation.add-wg-region", legacyKey = "MESSAGE_ADD_WG_REGION")
    public static SingleMessage MESSAGE_ADD_WG_REGION;
    @Key(value = "conversation.confirm", legacyKey = "MESSAGE_CONFIRM")
    public static SingleMessage MESSAGE_CONFIRM;
    @Key(value = "conversation.confirm-key", legacyKey = "MESSAGE_CONFIRM_KEY")
    public static SingleMessage MESSAGE_CONFIRM_KEY;
    @Key(value = "conversation.create-config-cancel", legacyKey = "MESSAGE_CREATE_CONFIG_CANCEL")
    public static SingleMessage MESSAGE_CREATE_CONFIG_CANCEL;
    @Key(value = "conversation.create-config-prompt", legacyKey = "MESSAGE_CREATE_CONFIG_PROMPT")
    public static SingleMessage MESSAGE_CREATE_CONFIG_PROMPT;
    @Key(value = "conversation.create-config-success", legacyKey = "MESSAGE_CREATE_CONFIG_SUCCESS")
    public static SingleMessage MESSAGE_CREATE_CONFIG_SUCCESS;
    @Key(value = "conversation.create-title-or-none-prompt", legacyKey = "MESSAGE_CREATE_TITLE_OR_NONE_PROMPT")
    public static SingleMessage MESSAGE_CREATE_TITLE_OR_NONE_PROMPT;
    @Key(value = "conversation.create-trade-prompt", legacyKey = "MESSAGE_CREATE_TRADE_PROMPT")
    public static SingleMessage MESSAGE_CREATE_TRADE_PROMPT;
    @Key(value = "conversation.create-unique", legacyKey = "MESSAGE_CREATE_UNIQUE")
    public static SingleMessage MESSAGE_CREATE_UNIQUE;
    @Key(value = "conversation.current-value", legacyKey = "MESSAGE_CURRENT_VALUE")
    public static SingleMessage MESSAGE_CURRENT_VALUE;
    @Key(value = "conversation.custom-name-prompt", legacyKey = "MESSAGE_CUSTOM_NAME_PROMPT")
    public static SingleMessage MESSAGE_CUSTOM_NAME_PROMPT;
    @Key(value = "conversation.delete-prompt", legacyKey = "MESSAGE_DELETE_PROMPT")
    public static SingleMessage MESSAGE_DELETE_PROMPT;
    @Key(value = "conversation.edit-cancelled", legacyKey = "MESSAGE_EDIT_CANCELLED")
    public static SingleMessage MESSAGE_EDIT_CANCELLED;
    @Key(value = "conversation.edit-saved", legacyKey = "MESSAGE_EDIT_SAVED")
    public static SingleMessage MESSAGE_EDIT_SAVED;
    @Key(value = "conversation.enter-number", legacyKey = "MESSAGE_ENTER_NUMBER")
    public static SingleMessage MESSAGE_ENTER_NUMBER;
    @Key(value = "conversation.enter-number-or-range", legacyKey = "MESSAGE_ENTER_NUMBER_OR_RANGE")
    public static SingleMessage MESSAGE_ENTER_NUMBER_OR_RANGE;
    @Key(value = "conversation.no-spaces", legacyKey = "MESSAGE_NO_SPACES")
    public static SingleMessage MESSAGE_NO_SPACES;
    @Key(value = "conversation.number-0t1", legacyKey = "MESSAGE_NUMBER_0T1")
    public static SingleMessage MESSAGE_NUMBER_0T1;
    @Key(value = "conversation.number-gte-0", legacyKey = "MESSAGE_NUMBER_GTE_0")
    public static SingleMessage MESSAGE_NUMBER_GTE_0;
    @Key(value = "conversation.number-gte-n1", legacyKey = "MESSAGE_NUMBER_GTE_N1")
    public static SingleMessage MESSAGE_NUMBER_GTE_N1;
    @Key(value = "conversation.number-gt-0", legacyKey = "MESSAGE_NUMBER_GT_0")
    public static SingleMessage MESSAGE_NUMBER_GT_0;
    @Key(value = "conversation.set-chance-prompt", legacyKey = "MESSAGE_SET_CHANCE_PROMPT")
    public static SingleMessage MESSAGE_SET_CHANCE_PROMPT;
    @Key(value = "conversation.set-heads-amount-prompt", legacyKey = "MESSAGE_SET_HEADS_AMOUNT_PROMPT")
    public static SingleMessage MESSAGE_SET_HEADS_AMOUNT_PROMPT;
    @Key(value = "conversation.set-heads-days-prompt", legacyKey = "MESSAGE_SET_HEADS_DAYS_PROMPT")
    public static SingleMessage MESSAGE_SET_HEADS_DAYS_PROMPT;
    @Key(value = "conversation.set-heads-trades-amount-prompt", legacyKey = "MESSAGE_SET_HEADS_TRADES_AMOUNT_PROMPT")
    public static SingleMessage MESSAGE_SET_HEADS_TRADES_AMOUNT_PROMPT;
    @Key(value = "conversation.set-max-uses-prompt", legacyKey = "MESSAGE_SET_MAX_USES_PROMPT")
    public static SingleMessage MESSAGE_SET_MAX_USES_PROMPT;
    @Key(value = "conversation.set-rand-amount-prompt", legacyKey = "MESSAGE_SET_RAND_AMOUNT_PROMPT")
    public static SingleMessage MESSAGE_SET_RAND_AMOUNT_PROMPT;
    @Key(value = "conversation.set-refresh-delay-prompt", legacyKey = "MESSAGE_SET_REFRESH_DELAY_PROMPT")
    public static SingleMessage MESSAGE_SET_REFRESH_DELAY_PROMPT;
    @Key(value = "conversation.yes-no", legacyKey = "MESSAGE_YES_NO")
    public static SingleMessage MESSAGE_YES_NO;
    @Key(value = "conversation.you-entered", legacyKey = "MESSAGE_YOU_ENTERED")
    public static SingleMessage MESSAGE_YOU_ENTERED;

    private Messages() {
    }

    public static Message get(final String key) {
        return Objects.requireNonNull(MESSAGES.get(key), () -> "Message with key '" + key + "' was not present!");
    }

    public static void load(final File file) {
        MESSAGES.clear();
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        MIGRATIONS.forEach(consumer -> consumer.accept(config));
        for (final Field field : Messages.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            final @Nullable Key key = field.getAnnotation(Key.class);
            if (key == null) {
                continue;
            }
            try {
                if (field.getType() == SingleMessage.class) {
                    handleSingleMessage(config, field, key);
                } else if (field.getType() == MultiLineMessage.class) {
                    handleMultiLineMessage(config, field, key);
                } else {
                    continue;
                }
                final Message message = (Message) field.get(null);
                MESSAGES.put(key.value(), message);
            } catch (final ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            config.save(file);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void handleSingleMessage(
        final YamlConfiguration config,
        final Field field,
        final Key key
    ) throws IllegalAccessException {
        @Nullable String value = config.getString(key.value());
        if (value != null) {
            field.set(null, Message.create(value));
            return;
        }
        if (!key.legacyKey().isEmpty()) {
            value = config.getString(key.legacyKey());
            if (value != null) {
                config.set(key.legacyKey(), null);
                config.set(key.value(), value);
                field.set(null, Message.create(value));
                return;
            }
        }
        field.set(null, Message.create("<red>Message missing"));
        Logging.logger().warn("No message found for key '{}'", key.value());
    }

    private static void handleMultiLineMessage(
        final YamlConfiguration config,
        final Field field,
        final Key key
    ) throws IllegalAccessException {
        final List<String> value = config.getStringList(key.value());
        if (!value.isEmpty()) {
            field.set(null, Message.multiLine(value));
            return;
        }
        if (!key.legacyKey().isEmpty()) {
            final @Nullable String legacyValue = config.getString(key.legacyKey());
            if (legacyValue != null) {
                config.set(key.legacyKey(), null);
                final List<String> newValue = Arrays.asList(legacyValue.split("\\|"));
                config.set(key.value(), newValue);
                field.set(null, Message.multiLine(newValue));
                return;
            }
        }
        field.set(null, Message.multiLine(List.of("<red>Message missing")));
        Logging.logger().warn("No message found for key '{}'", key.value());
    }

    private static Consumer<YamlConfiguration> migrate(
        final String from,
        final String to
    ) {
        return config -> {
            @Nullable Object o = config.get(from);
            if (o != null) {
                config.set(from, null);
                config.set(to, o);
            }
        };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    private @interface Key {
        String value();

        String legacyKey() default "";
    }

    public interface Message extends ComponentLike {
        static MultiLineMessage multiLine(final List<String> messages) {
            return new MultiLineMessage(messages);
        }

        static SingleMessage create(final String message) {
            return new SingleMessage(message);
        }

        default RichDescription asDescription() {
            return RichDescription.of(this.asComponent());
        }
    }

    public static final class SingleMessage implements Message {
        private final String message;
        private final Supplier<Component> asComponent;

        private SingleMessage(final String message) {
            this.message = message;
            this.asComponent = Suppliers.memoize(
                () -> WanderingTrades.instance().miniMessage().deserialize(message)
            );
        }

        @Override
        public Component asComponent() {
            return this.asComponent.get();
        }

        public Component withPlaceholders(final TagResolver... placeholders) {
            return WanderingTrades.instance().miniMessage().deserialize(this.message, placeholders);
        }

        public String message() {
            return this.message;
        }
    }

    public static final class MultiLineMessage implements Message {
        private final List<String> messages;
        private final Supplier<List<Component>> asComponents;

        private MultiLineMessage(final List<String> messages) {
            this.messages = messages;
            this.asComponents = Suppliers.memoize(
                () -> this.messages.stream()
                    .map(WanderingTrades.instance().miniMessage()::deserialize)
                    .toList()
            );
        }

        @Override
        public Component asComponent() {
            return Component.join(JoinConfiguration.newlines(), this.asComponents.get());
        }

        public List<Component> asComponents() {
            return this.asComponents.get();
        }

        public List<Component> withPlaceholders(final TagResolver... placeholders) {
            return this.messages.stream().map(message -> WanderingTrades.instance().miniMessage().deserialize(message, placeholders)).toList();
        }

        public List<String> messages() {
            return this.messages;
        }
    }
}
