package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Description;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import cloud.commandframework.bukkit.parsers.selector.SingleEntitySelectorArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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

import static xyz.jpenilla.wanderingtrades.command.CommandManager.metaWithDescription;

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
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NATURAL)))
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
                            final WanderingTrader wt = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
                            PersistentDataContainer persistentDataContainer = wt.getPersistentDataContainer();
                            if (context.flags().isPresent("refresh")) {
                                persistentDataContainer.set(Constants.REFRESH_NATURAL, PersistentDataType.STRING, "true");
                            }
                            if (context.flags().isPresent("noai")) {
                                wt.setAI(false);
                            }
                            if (context.flags().isPresent("protect")) {
                                persistentDataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
                            }
                        }).execute())
        );

        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_SUMMON)))
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
        );

        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_VSUMMON)))
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
        );

        /* Entity Rename Command */
        mgr.command(
                mgr.commandBuilder("nameentity", metaWithDescription("Sets the name of an entity."))
                        .argument(SingleEntitySelectorArgument.of("entity"))
                        .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY),
                                Description.of("The MiniMessage string to use as a name."))
                        .permission("wanderingtrades.name")
                        .senderType(Player.class)
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            Entity entity = context.<SingleEntitySelector>get("entity").getEntity();
                            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                                setCustomName(entity, context.get("name"));
                                entity.setCustomNameVisible(true);
                                chat.send(context.getSender(), "Named entity<gray>:</gray> " + context.get("name"));
                            } else {
                                chat.send(context.getSender(), "<red>Cannot name player or non-living entity.");
                            }
                        }).execute())
        );
    }

    private Location resolveLocation(CommandContext<CommandSender> ctx) {
        final Location loc = ctx.get("location");
        final World world = ctx.flags().getValue("world", null);
        if (world != null) {
            loc.setWorld(world);
        }
        final Integer yaw = ctx.flags().getValue("yaw", null);
        if (yaw != null) {
            loc.setYaw(yaw);
        }
        final Integer pitch = ctx.flags().getValue("pitch", null);
        if (pitch != null) {
            loc.setPitch(pitch);
        }
        return loc;
    }

    private void summonTrader(CommandSender sender, String tradeConfig, Location loc, boolean disableAI) {
        try {
            List<MerchantRecipe> recipes = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            final WanderingTrader wt = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
            wanderingTrades.getListeners().getTraderSpawnListener().getTraderBlacklistCache().add(wt.getUniqueId());
            wt.setRecipes(recipes);
            wt.setAI(!disableAI);

            PersistentDataContainer p = wt.getPersistentDataContainer();

            TradeConfig t = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig);
            if (t.getCustomName() != null && !t.getCustomName().equalsIgnoreCase("NONE")) {
                setCustomName(wt, t.getCustomName());
                wt.setCustomNameVisible(true);
            }
            if (t.isInvincible()) {
                wt.setInvulnerable(true);
                wt.setRemoveWhenFarAway(false);
                wt.setPersistent(true);
                p.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }

            p.set(Constants.CONFIG, PersistentDataType.STRING, tradeConfig);
        } catch (NullPointerException | IllegalStateException ex) {
            if (ex instanceof NullPointerException) {
                chat.sendParsed(sender, "<red><italic>" + wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG));
            } else {
                chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            }
        }
    }

    private void summonVillagerTrader(CommandSender sender, String tradeConfig, Location loc, Villager.Type type, Villager.Profession profession, boolean disableAI) {
        try {
            List<MerchantRecipe> recipes = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            final Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
            v.setVillagerType(type);
            v.setProfession(profession);
            v.setVillagerLevel(5);
            v.setRecipes(recipes);
            v.setAI(!disableAI);

            PersistentDataContainer p = v.getPersistentDataContainer();

            TradeConfig t = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig);
            if (t.getCustomName() != null && !t.getCustomName().equalsIgnoreCase("NONE")) {
                setCustomName(v, t.getCustomName());
                v.setCustomNameVisible(true);
            }
            if (t.isInvincible()) {
                v.setInvulnerable(true);
                v.setRemoveWhenFarAway(false);
                v.setPersistent(true);
                p.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }

            p.set(Constants.CONFIG, PersistentDataType.STRING, tradeConfig);
        } catch (NullPointerException | IllegalStateException ex) {
            if (ex instanceof NullPointerException) {
                chat.sendParsed(sender, "<red><italic>" + wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG));
            } else {
                chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            }
        }
    }

    private static final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
    //private static final BungeeCordComponentSerializer bungeeSerializer = BungeeCordComponentSerializer.get();

    /**
     * Set the custom name of an entity from a MiniMessage string using reflection. Falls back to Bukkit api using legacy text.
     *
     * @param entity      The Bukkit entity
     * @param miniMessage The MiniMessage string. Clears the name if empty or null
     */
    private void setCustomName(final @NonNull Entity entity,
                               final @Nullable String miniMessage) {
        if (miniMessage == null || miniMessage.equals("")) {
            entity.setCustomName(null);
            return;
        }
        // TODO: prefer paper api to set name with components once the api exists https://github.com/PaperMC/Paper/pull/4357
        //if (wanderingTrades.isPaperServer() && wanderingTrades.getMajorMinecraftVersion() > 15) {
        //    entity.setCustomNameComponent(bungeeSerializer.serialize(wanderingTrades.getMiniMessage().parse(miniMessage)));
        //    return;
        //}
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
            wanderingTrades.getLog().debug("Failed to set entity name with reflection: " + throwable.getMessage());
            entity.setCustomName(MiniMessageUtil.miniMessageToLegacy(miniMessage));
        }
    }

}
