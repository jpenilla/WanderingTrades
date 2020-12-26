package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Command;
import cloud.commandframework.Description;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import cloud.commandframework.bukkit.parsers.selector.SingleEntitySelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.jmplib.Crafty;
import xyz.jpenilla.jmplib.MiniMessageUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class CommandSummon implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final CommandManager mgr;
    private final Chat chat;

    public CommandSummon(WanderingTrades wanderingTrades, CommandManager mgr) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.chat = wanderingTrades.getChat();

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
    public void registerCommands() {
        final Command.Builder<CommandSender> wt = mgr.commandBuilder("wt");

        final Command<CommandSender> summonNatural = wt
                .meta(CommandMeta.DESCRIPTION, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NATURAL))
                .literal("summonnatural")
                .argument(LocationArgument.of("location"))
                .flag(mgr.getFlag("world"))
                .flag(mgr.getFlag("pitch"))
                .flag(mgr.getFlag("yaw"))
                .flag(mgr.flagBuilder("noai"))
                .flag(mgr.flagBuilder("protect"))
                .flag(mgr.flagBuilder("refresh"))
                .permission("wanderingtrades.summonnatural")
                .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                    final Location loc = resolveLocation(context);
                    final WanderingTrader wanderingTrader = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
                    PersistentDataContainer persistentDataContainer = wanderingTrader.getPersistentDataContainer();
                    if (context.flags().isPresent("refresh")) {
                        persistentDataContainer.set(Constants.REFRESH_NATURAL, PersistentDataType.STRING, "true");
                    }
                    if (context.flags().isPresent("noai")) {
                        wanderingTrader.setAI(false);
                    }
                    if (context.flags().isPresent("protect")) {
                        persistentDataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
                    }
                }).execute())
                .build();

        final Command<CommandSender> summon = wt
                .meta(CommandMeta.DESCRIPTION, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON))
                .literal("summon")
                .argument(mgr.getArgument("trade_config"))
                .argument(LocationArgument.of("location"))
                .flag(mgr.getFlag("world"))
                .flag(mgr.getFlag("pitch"))
                .flag(mgr.getFlag("yaw"))
                .flag(mgr.flagBuilder("noai"))
                .permission("wanderingtrades.summon")
                .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                    final Location loc = resolveLocation(context);
                    this.summonTrader(context.getSender(), context.get("trade_config"), loc, context.flags().isPresent("noai"));
                }).execute())
                .build();

        final Command<CommandSender> summonVillager = wt
                .meta(CommandMeta.DESCRIPTION, wanderingTrades.getLang().get(Lang.COMMAND_VSUMMON))
                .literal("summonvillager")
                .argument(mgr.getArgument("trade_config"))
                .argument(EnumArgument.of(Villager.Type.class, "type"))
                .argument(EnumArgument.of(Villager.Profession.class, "profession"))
                .argument(LocationArgument.of("location"))
                .flag(mgr.getFlag("world"))
                .flag(mgr.getFlag("pitch"))
                .flag(mgr.getFlag("yaw"))
                .flag(mgr.flagBuilder("noai"))
                .permission("wanderingtrades.villager")
                .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                    final Location loc = resolveLocation(context);
                    this.summonVillagerTrader(context.getSender(), context.get("trade_config"), loc, context.get("type"), context.get("profession"), context.flags().isPresent("noai"));
                }).execute())
                .build();

        /* Entity Rename Command */
        final Command<CommandSender> nameEntity = mgr.commandBuilder("nameentity")
                .meta(CommandMeta.DESCRIPTION, "Sets the name of an entity.")
                .argument(SingleEntitySelectorArgument.of("entity"))
                .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY),
                        Description.of("The MiniMessage string to use as a name."))
                .permission("wanderingtrades.name")
                .senderType(Player.class)
                .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                    Entity entity = context.<SingleEntitySelector>get("entity").getEntity();
                    if (entity != null && !(entity instanceof Player)) {
                        setCustomName(entity, context.get("name"));
                        entity.setCustomNameVisible(true);
                        chat.send(context.getSender(), "Named entity<gray>:</gray> " + context.get("name"));
                    } else {
                        chat.send(context.getSender(), "<red>Cannot name player or non-living entity.");
                    }
                }).execute())
                .build();

        mgr.register(ImmutableList.of(summonNatural, summon, summonVillager, nameEntity));
    }

    private Location resolveLocation(CommandContext<CommandSender> ctx) {
        final Location loc = ctx.get("location");
        ctx.flags().<World>getValue("world").ifPresent(loc::setWorld);
        ctx.flags().<Integer>getValue("yaw").ifPresent(loc::setYaw);
        ctx.flags().<Integer>getValue("pitch").ifPresent(loc::setPitch);
        return loc;
    }

    private void summonTrader(CommandSender sender, TradeConfig tradeConfig, Location loc, boolean disableAI) {
        final List<MerchantRecipe> recipes;
        try {
            recipes = tradeConfig.getTrades(true);
        } catch (IllegalStateException ex) {
            chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            return;
        }
        loc.getWorld().spawn(loc, WanderingTrader.class, wanderingTrader -> {
            wanderingTrades.getListeners().getTraderSpawnListener().getTraderBlacklistCache().add(wanderingTrader.getUniqueId());
            wanderingTrader.setRecipes(recipes);
            wanderingTrader.setAI(!disableAI);

            final PersistentDataContainer dataContainer = wanderingTrader.getPersistentDataContainer();
            dataContainer.set(Constants.CONFIG_NAME, PersistentDataType.STRING, tradeConfig.getConfigName());

            final String customName = tradeConfig.getCustomName();
            if (customName != null && !customName.isEmpty() && !customName.equalsIgnoreCase("NONE")) {
                setCustomName(wanderingTrader, customName);
                wanderingTrader.setCustomNameVisible(true);
            }
            if (tradeConfig.isInvincible()) {
                wanderingTrader.setInvulnerable(true);
                wanderingTrader.setRemoveWhenFarAway(false);
                wanderingTrader.setPersistent(true);
                dataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }
        });
    }

    private void summonVillagerTrader(CommandSender sender, TradeConfig tradeConfig, Location loc, Villager.Type type, Villager.Profession profession, boolean disableAI) {
        final List<MerchantRecipe> recipes;
        try {
            recipes = tradeConfig.getTrades(true);
        } catch (IllegalStateException ex) {
            chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            return;
        }
        loc.getWorld().spawn(loc, Villager.class, villager -> {
            villager.setRecipes(recipes);
            villager.setAI(!disableAI);
            villager.setVillagerType(type);
            villager.setProfession(profession);
            villager.setVillagerLevel(5);

            final PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
            dataContainer.set(Constants.CONFIG_NAME, PersistentDataType.STRING, tradeConfig.getConfigName());

            final String customName = tradeConfig.getCustomName();
            if (customName != null && !customName.isEmpty() && !customName.equalsIgnoreCase("NONE")) {
                setCustomName(villager, customName);
                villager.setCustomNameVisible(true);
            }
            if (tradeConfig.isInvincible()) {
                villager.setInvulnerable(true);
                villager.setRemoveWhenFarAway(false);
                villager.setPersistent(true);
                dataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }
        });
    }

    private static final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();

    /**
     * Set the custom name of an entity from a MiniMessage string using reflection. Falls back to Bukkit api using legacy text.
     *
     * @param entity      The Bukkit entity
     * @param miniMessage The MiniMessage string. Clears the name if empty or null
     */
    private void setCustomName(
            final @NonNull Entity entity,
            final @Nullable String miniMessage
    ) {
        if (miniMessage == null || miniMessage.isEmpty()) {
            entity.setCustomName(null);
            return;
        }
        try {
            Class<?> _CraftEntity = Crafty.needCraftClass("entity.CraftEntity");
            Class<?> _Entity = Crafty.needNmsClass("Entity");
            Class<?> _IChatBaseComponent = Crafty.needNmsClass("IChatBaseComponent");
            Class<?> _ChatSerializer = Crafty.needNmsClass("IChatBaseComponent$ChatSerializer");
            MethodHandle _getHandle = Crafty.findMethod(_CraftEntity, "getHandle", _Entity);
            Method _jsonToComponent = _ChatSerializer.getMethod("a", String.class);
            Method _setCustomName = _Entity.getDeclaredMethod("setCustomName", _IChatBaseComponent);

            Object nmsEntity = Objects.requireNonNull(_getHandle).bindTo(entity).invoke();
            Object customName = Objects.requireNonNull(_jsonToComponent).invoke(null, gsonComponentSerializer.serialize(wanderingTrades.getMiniMessage().parse(miniMessage)));

            _setCustomName.invoke(nmsEntity, customName);
        } catch (Throwable throwable) {
            if (wanderingTrades.getCfg().isDebug()) {
                wanderingTrades.getLogger().log(
                        Level.WARNING,
                        "Failed to set entity name using reflection, falling back onto legacy text serialization",
                        throwable
                );
            }
            entity.setCustomName(MiniMessageUtil.miniMessageToLegacy(miniMessage));
        }
    }

}
