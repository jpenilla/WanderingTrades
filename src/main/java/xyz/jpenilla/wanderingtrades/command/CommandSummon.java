package xyz.jpenilla.wanderingtrades.command;

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
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import org.bukkit.Location;
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
import xyz.jpenilla.pluginbase.legacy.Chat;
import xyz.jpenilla.pluginbase.legacy.Crafty;
import xyz.jpenilla.pluginbase.legacy.MiniMessageUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigArgument;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;

import static io.papermc.lib.PaperLib.isPaper;

@DefaultQualifier(NonNull.class)
public final class CommandSummon implements WTCommand {
    private final WanderingTrades wanderingTrades;
    private final CommandManager mgr;
    private final Chat chat;

    public CommandSummon(
        final WanderingTrades wanderingTrades,
        final CommandManager mgr
    ) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.chat = wanderingTrades.chat();

        mgr.registerFlag(
            "pitch",
            mgr.flagBuilder("pitch")
                .withArgument(IntegerArgument.newBuilder("pitch").withMin(-180).withMax(180))
        );
        mgr.registerFlag(
            "yaw",
            mgr.flagBuilder("yaw")
                .withArgument(IntegerArgument.newBuilder("yaw").withMin(-90).withMax(90))
        );
        mgr.registerFlag(
            "world",
            mgr.flagBuilder("world")
                .withArgument(WorldArgument.of("world"))
        );
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = this.mgr.commandBuilder("wt");

        final Command<CommandSender> summonNatural = wt
            .meta(CommandMeta.DESCRIPTION, this.wanderingTrades.langConfig().get(Lang.COMMAND_SUMMON_NATURAL))
            .literal("summonnatural")
            .argument(LocationArgument.of("location"))
            .flag(this.mgr.getFlag("world"))
            .flag(this.mgr.getFlag("pitch"))
            .flag(this.mgr.getFlag("yaw"))
            .flag(this.mgr.flagBuilder("noai"))
            .flag(this.mgr.flagBuilder("protect"))
            .flag(this.mgr.flagBuilder("refresh"))
            .flag(this.mgr.flagBuilder("noinvisibility"))
            .permission("wanderingtrades.summonnatural")
            .handler(c -> this.mgr.taskRecipe().begin(c).synchronous(context -> {
                this.summonNatural(
                    resolveLocation(context),
                    context.flags().isPresent("refresh"),
                    context.flags().isPresent("noai"),
                    context.flags().isPresent("protect"),
                    context.flags().isPresent("noinvisibility")
                );
            }).execute())
            .build();

        final Command<CommandSender> summon = wt
            .meta(CommandMeta.DESCRIPTION, this.wanderingTrades.langConfig().get(Lang.COMMAND_SUMMON))
            .literal("summon")
            .argument(TradeConfigArgument.of("trade_config"))
            .argument(LocationArgument.of("location"))
            .flag(this.mgr.getFlag("world"))
            .flag(this.mgr.getFlag("pitch"))
            .flag(this.mgr.getFlag("yaw"))
            .flag(this.mgr.flagBuilder("noai"))
            .permission("wanderingtrades.summon")
            .handler(c -> this.mgr.taskRecipe().begin(c).synchronous(context -> {
                this.summonTrader(
                    context.getSender(),
                    context.get("trade_config"),
                    resolveLocation(context),
                    context.flags().isPresent("noai")
                );
            }).execute())
            .build();

        final Command<CommandSender> summonVillager = wt
            .meta(CommandMeta.DESCRIPTION, this.wanderingTrades.langConfig().get(Lang.COMMAND_VSUMMON))
            .literal("summonvillager")
            .argument(TradeConfigArgument.of("trade_config"))
            .argument(EnumArgument.of(Villager.Type.class, "type"))
            .argument(EnumArgument.of(Villager.Profession.class, "profession"))
            .argument(LocationArgument.of("location"))
            .flag(this.mgr.getFlag("world"))
            .flag(this.mgr.getFlag("pitch"))
            .flag(this.mgr.getFlag("yaw"))
            .flag(this.mgr.flagBuilder("noai"))
            .permission("wanderingtrades.villager")
            .handler(c -> this.mgr.taskRecipe().begin(c).synchronous(context -> {
                this.summonVillagerTrader(
                    context.getSender(),
                    context.get("trade_config"),
                    resolveLocation(context),
                    context.get("type"),
                    context.get("profession"),
                    context.flags().isPresent("noai")
                );
            }).execute())
            .build();

        /* Entity Rename Command */
        final Command<CommandSender> nameEntity = this.mgr.commandBuilder("nameentity")
            .meta(CommandMeta.DESCRIPTION, "Sets the name of an entity.")
            .argument(SingleEntitySelectorArgument.of("entity"))
            .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY),
                ArgumentDescription.of("The MiniMessage string to use as a name."))
            .permission("wanderingtrades.name")
            .senderType(Player.class)
            .handler(c -> this.mgr.taskRecipe().begin(c).synchronous(context -> {
                final @Nullable Entity entity = context.<SingleEntitySelector>get("entity").getEntity();
                if (entity != null && !(entity instanceof Player)) {
                    setCustomName(entity, context.get("name"));
                    entity.setCustomNameVisible(true);
                    this.chat.send(context.getSender(), "Named entity<gray>:</gray> " + context.get("name"));
                } else {
                    this.chat.send(context.getSender(), "<red>Cannot name player or non-living entity.");
                }
            }).execute())
            .build();

        this.mgr.register(List.of(summonNatural, summon, summonVillager, nameEntity));
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
                if (isPaper()) {
                    wanderingTrader.setCanDrinkPotion(false);
                }
                persistentDataContainer.set(Constants.PREVENT_INVISIBILITY, PersistentDataType.STRING, "true");
            }
        });
    }

    private void summonTrader(CommandSender sender, TradeConfig tradeConfig, Location loc, boolean noAI) {
        final @Nullable List<MerchantRecipe> recipes = this.tryGetTrades(sender, tradeConfig);
        if (recipes == null) {
            return;
        }
        loc.getWorld().spawn(loc, WanderingTrader.class, wanderingTrader -> {
            wanderingTrader.setRecipes(recipes);
            if (noAI) {
                wanderingTrader.setAI(false);
            }

            final PersistentDataContainer dataContainer = wanderingTrader.getPersistentDataContainer();
            dataContainer.set(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);

            this.applyConfig(tradeConfig, wanderingTrader);

            if (this.wanderingTrades.config().preventNightInvisibility() && isPaper()) {
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
        final @Nullable List<MerchantRecipe> recipes = this.tryGetTrades(sender, tradeConfig);
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

    private @Nullable List<MerchantRecipe> tryGetTrades(
        final CommandSender sender,
        final TradeConfig tradeConfig
    ) {
        try {
            return tradeConfig.getTrades(true);
        } catch (final IllegalStateException ex) {
            this.chat.sendParsed(sender, this.wanderingTrades.langConfig().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            return null;
        }
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

    /**
     * Set the custom name of an entity from a MiniMessage string using reflection. Falls back to Bukkit api using legacy text.
     *
     * @param entity      The Bukkit entity
     * @param miniMessage The MiniMessage string. Clears the name if empty or null
     */
    private void setCustomName(
        final Entity entity,
        final @Nullable String miniMessage
    ) {
        if (miniMessage == null || miniMessage.isEmpty()) {
            entity.setCustomName(null);
            return;
        }
        try {
            Class<?> _CraftEntity = Crafty.needCraftClass("entity.CraftEntity");
            Class<?> _Entity = Crafty.needNMSClassOrElse("Entity", "net.minecraft.world.entity.Entity");
            Class<?> _IChatBaseComponent = Crafty.needNMSClassOrElse("IChatBaseComponent", "net.minecraft.network.chat.IChatBaseComponent");
            MethodHandle _getHandle = Crafty.findMethod(_CraftEntity, "getHandle", _Entity);
            Method _setCustomName = _Entity.getDeclaredMethod("setCustomName", _IChatBaseComponent);

            Object nmsEntity = Objects.requireNonNull(_getHandle).bindTo(entity).invoke();
            final Object nmsComponent = MinecraftComponentSerializer.get().serialize(this.wanderingTrades.miniMessage().deserialize(miniMessage));

            _setCustomName.invoke(nmsEntity, nmsComponent);
        } catch (Throwable throwable) {
            if (this.wanderingTrades.config().debug()) {
                this.wanderingTrades.getLogger().log(
                    Level.WARNING,
                    "Failed to set entity name using reflection, falling back onto legacy text serialization",
                    throwable
                );
            }
            entity.setCustomName(MiniMessageUtil.miniMessageToLegacy(miniMessage));
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
