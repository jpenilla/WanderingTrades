package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import cloud.commandframework.bukkit.parsers.selector.SingleEntitySelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import io.papermc.lib.PaperLib;
import java.lang.reflect.Method;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.MiniMessageUtil;
import xyz.jpenilla.pluginbase.legacy.PaperComponentUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigArgument;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;

@DefaultQualifier(NonNull.class)
public final class SummonCommands extends BaseCommand {
    public SummonCommands(
        final WanderingTrades plugin,
        final Commands commands
    ) {
        super(plugin, commands);

        commands.registerFlag(
            "pitch",
            this.commandManager.flagBuilder("pitch")
                .withArgument(IntegerArgument.newBuilder("pitch").withMin(-180).withMax(180))
        );
        commands.registerFlag(
            "yaw",
            this.commandManager.flagBuilder("yaw")
                .withArgument(IntegerArgument.newBuilder("yaw").withMin(-90).withMax(90))
        );
        commands.registerFlag(
            "world",
            this.commandManager.flagBuilder("world")
                .withArgument(WorldArgument.of("world"))
        );
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = this.commandManager.commandBuilder("wt");

        final Command<CommandSender> summonNatural = wt
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Messages.COMMAND_SUMMONNATURAL_DESCRIPTION.asComponent())
            .literal("summonnatural")
            .argument(LocationArgument.of("location"))
            .flag(this.commands.getFlag("world"))
            .flag(this.commands.getFlag("pitch"))
            .flag(this.commands.getFlag("yaw"))
            .flag(this.commandManager.flagBuilder("noai"))
            .flag(this.commandManager.flagBuilder("protect"))
            .flag(this.commandManager.flagBuilder("refresh"))
            .flag(this.commandManager.flagBuilder("noinvisibility"))
            .permission("wanderingtrades.summonnatural")
            .handler(context -> this.summonNatural(
                resolveLocation(context),
                context.flags().isPresent("refresh"),
                context.flags().isPresent("noai"),
                context.flags().isPresent("protect"),
                context.flags().isPresent("noinvisibility")
            ))
            .build();

        final Command<CommandSender> summon = wt
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Messages.COMMAND_SUMMON_DESCRIPTION.asComponent())
            .literal("summon")
            .argument(TradeConfigArgument.of("trade_config"))
            .argument(LocationArgument.of("location"))
            .flag(this.commands.getFlag("world"))
            .flag(this.commands.getFlag("pitch"))
            .flag(this.commands.getFlag("yaw"))
            .flag(this.commandManager.flagBuilder("noai"))
            .permission("wanderingtrades.summon")
            .handler(context -> this.summonTrader(
                context.getSender(),
                context.get("trade_config"),
                resolveLocation(context),
                context.flags().isPresent("noai")
            ))
            .build();

        final Command<CommandSender> summonVillager = wt
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Messages.COMMAND_SUMMONVILLAGER_DESCRIPTION.asComponent())
            .literal("summonvillager")
            .argument(TradeConfigArgument.of("trade_config"))
            .argument(EnumArgument.of(Villager.Type.class, "type"))
            .argument(EnumArgument.of(Villager.Profession.class, "profession"))
            .argument(LocationArgument.of("location"))
            .flag(this.commands.getFlag("world"))
            .flag(this.commands.getFlag("pitch"))
            .flag(this.commands.getFlag("yaw"))
            .flag(this.commandManager.flagBuilder("noai"))
            .permission("wanderingtrades.villager")
            .handler(context -> this.summonVillagerTrader(
                context.getSender(),
                context.get("trade_config"),
                resolveLocation(context),
                context.get("type"),
                context.get("profession"),
                context.flags().isPresent("noai")
            ))
            .build();

        /* Entity Rename Command */
        final Command<CommandSender> nameEntity = this.commandManager.commandBuilder("nameentity")
            .meta(CommandMeta.DESCRIPTION, "Sets the name of an entity.")
            .argument(SingleEntitySelectorArgument.of("entity"))
            .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY),
                ArgumentDescription.of("The MiniMessage string to use as a name."))
            .permission("wanderingtrades.name")
            .senderType(Player.class)
            .handler(context -> {
                final @Nullable Entity entity = context.<SingleEntitySelector>get("entity").getEntity();
                if (entity != null && !(entity instanceof Player)) {
                    this.setCustomName(entity, context.get("name"));
                    entity.setCustomNameVisible(true);
                    this.chat.send(context.getSender(), "Named entity<gray>:</gray> " + context.get("name"));
                } else {
                    this.chat.send(context.getSender(), "<red>Cannot name player or non-living entity.");
                }
            })
            .build();

        this.commands.register(List.of(summonNatural, summon, summonVillager, nameEntity));
    }

    private void summonNatural(
        final Location location,
        final boolean refresh,
        final boolean noAI,
        final boolean protect,
        final boolean noInvisibility
    ) {
        location.getWorld().spawn(location, WanderingTrader.class, wanderingTrader -> {
            final PersistentDataContainer persistentDataContainer = wanderingTrader.getPersistentDataContainer();
            if (refresh) {
                persistentDataContainer.set(Constants.REFRESH_NATURAL, PersistentDataType.STRING, "true");
            }
            if (noAI) {
                wanderingTrader.setAI(false);
            }
            if (protect) {
                persistentDataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }
            if (noInvisibility) {
                if (PaperLib.isPaper()) {
                    wanderingTrader.setCanDrinkPotion(false);
                }
                persistentDataContainer.set(Constants.PREVENT_INVISIBILITY, PersistentDataType.STRING, "true");
            }
        });
    }

    private void summonTrader(
        final CommandSender sender,
        final TradeConfig tradeConfig,
        final Location loc,
        final boolean disableAI
    ) {
        final @Nullable List<MerchantRecipe> recipes = tradeConfig.tryGetTrades(sender);
        if (recipes == null) {
            return;
        }
        loc.getWorld().spawn(loc, WanderingTrader.class, wanderingTrader -> {
            wanderingTrader.setRecipes(recipes);
            if (disableAI) {
                wanderingTrader.setAI(false);
            }

            final PersistentDataContainer dataContainer = wanderingTrader.getPersistentDataContainer();
            dataContainer.set(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);

            this.applyConfig(tradeConfig, wanderingTrader);

            if (this.plugin.config().preventNightInvisibility() && PaperLib.isPaper()) {
                wanderingTrader.setCanDrinkPotion(false);
            }
        });
    }

    private void summonVillagerTrader(
        final CommandSender sender,
        final TradeConfig tradeConfig,
        final Location loc,
        final Villager.Type type,
        final Villager.Profession profession,
        final boolean disableAI
    ) {
        final @Nullable List<MerchantRecipe> recipes = tradeConfig.tryGetTrades(sender);
        if (recipes == null) {
            return;
        }
        final Villager v = loc.getWorld().spawn(loc, Villager.class, villager -> {
            villager.setAI(!disableAI);
            villager.setVillagerType(type);
            villager.setProfession(profession);
            villager.setVillagerLevel(5);

            this.applyConfig(tradeConfig, villager);
        });
        v.setRecipes(recipes);
    }

    private void applyConfig(
        final TradeConfig config,
        final AbstractVillager trader
    ) {
        final PersistentDataContainer dataContainer = trader.getPersistentDataContainer();
        dataContainer.set(Constants.CONFIG_NAME, PersistentDataType.STRING, config.configName());
        final String customName = config.customName();
        if (customName != null && !customName.isEmpty() && !customName.equalsIgnoreCase("NONE")) {
            this.setCustomName(trader, customName);
            trader.setCustomNameVisible(true);
        }
        if (config.invincible()) {
            trader.setInvulnerable(true);
            trader.setRemoveWhenFarAway(false);
            trader.setPersistent(true);
            dataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
        }
    }

    @SuppressWarnings("deprecation")
    private void setCustomName(
        final Nameable entity,
        final @Nullable String miniMessage
    ) {
        if (!PaperLib.isPaper()) {
            if (miniMessage == null || miniMessage.isEmpty()) {
                entity.setCustomName(null);
                return;
            }
            entity.setCustomName(MiniMessageUtil.miniMessageToLegacy(miniMessage));
            return;
        }
        try {
            final Method paperMethod = Nameable.class.getMethod("customName", PaperComponentUtil.nativeAdventureComponentClass());

            if (miniMessage == null || miniMessage.isEmpty()) {
                paperMethod.invoke(entity, (Object) null);
                return;
            }

            paperMethod.invoke(entity, PaperComponentUtil.toNative(this.plugin.miniMessage().deserialize(miniMessage)));
        } catch (final ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Location resolveLocation(final CommandContext<CommandSender> ctx) {
        final Location loc = ctx.get("location");
        ctx.flags().<World>getValue("world").ifPresent(loc::setWorld);
        ctx.flags().<Integer>getValue("yaw").ifPresent(loc::setYaw);
        ctx.flags().<Integer>getValue("pitch").ifPresent(loc::setPitch);
        return loc;
    }
}
