package xyz.jpenilla.wanderingtrades.command.commands;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.registry.RegistryKey;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
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
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.data.SingleEntitySelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.parser.RegistryEntryParser;
import org.incendo.cloud.parser.ParserDescriptor;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;
import xyz.jpenilla.wanderingtrades.util.Reflection;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static org.incendo.cloud.bukkit.parser.WorldParser.worldParser;
import static org.incendo.cloud.bukkit.parser.location.LocationParser.locationParser;
import static org.incendo.cloud.bukkit.parser.selector.SingleEntitySelectorParser.singleEntitySelectorParser;
import static org.incendo.cloud.paper.parser.RegistryEntryParser.registryEntryParser;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
import static xyz.jpenilla.wanderingtrades.command.argument.TradeConfigParser.tradeConfigParser;

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
                .withComponent(integerParser(-180, 180))
        );
        commands.registerFlag(
            "yaw",
            this.commandManager.flagBuilder("yaw")
                .withComponent(integerParser(-90, 90))
        );
        commands.registerFlag(
            "world",
            this.commandManager.flagBuilder("world")
                .withComponent(worldParser())
        );
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = this.commandManager.commandBuilder("wt");

        final Command<CommandSender> summonNatural = wt
            .commandDescription(Messages.COMMAND_SUMMONNATURAL_DESCRIPTION.asDescription())
            .literal("summonnatural")
            .required("location", locationParser())
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
            .commandDescription(Messages.COMMAND_SUMMON_DESCRIPTION.asDescription())
            .literal("summon")
            .required("trade_config", tradeConfigParser())
            .required("location", locationParser())
            .flag(this.commands.getFlag("world"))
            .flag(this.commands.getFlag("pitch"))
            .flag(this.commands.getFlag("yaw"))
            .flag(this.commandManager.flagBuilder("noai"))
            .permission("wanderingtrades.summon")
            .handler(context -> this.summonTrader(
                context.sender(),
                context.get("trade_config"),
                resolveLocation(context),
                context.flags().isPresent("noai")
            ))
            .build();

        @SuppressWarnings("RedundantCast") final Command<CommandSender> summonVillager = wt
            .commandDescription(Messages.COMMAND_SUMMONVILLAGER_DESCRIPTION.asDescription())
            .literal("summonvillager")
            .required("trade_config", tradeConfigParser())
            .required("type", registryValueOrEnumParser(() -> (Object) RegistryKey.VILLAGER_TYPE, TypeToken.get(Villager.Type.class)))
            .required("profession", registryValueOrEnumParser(() -> (Object) RegistryKey.VILLAGER_PROFESSION, TypeToken.get(Villager.Profession.class)))
            .required("location", locationParser())
            .flag(this.commands.getFlag("world"))
            .flag(this.commands.getFlag("pitch"))
            .flag(this.commands.getFlag("yaw"))
            .flag(this.commandManager.flagBuilder("noai"))
            .permission("wanderingtrades.villager")
            .handler(context -> this.summonVillagerTrader(
                context.sender(),
                context.get("trade_config"),
                resolveLocation(context),
                context.get("type"),
                context.get("profession"),
                context.flags().isPresent("noai")
            ))
            .build();

        /* Entity Rename Command */
        final Command<Player> nameEntity = this.commandManager.commandBuilder("nameentity")
            .commandDescription(Description.of("Sets the name of an entity."))
            .required("entity", singleEntitySelectorParser())
            .required("name", greedyStringParser(), Description.of("The MiniMessage string to use as a name."))
            .permission("wanderingtrades.name")
            .senderType(Player.class)
            .handler(context -> {
                final @Nullable Entity entity = context.<SingleEntitySelector>get("entity").single();
                if (entity != null && !(entity instanceof Player)) {
                    this.setCustomName(entity, context.get("name"));
                    entity.setCustomNameVisible(true);
                    context.sender().sendRichMessage("Named entity<gray>:</gray> " + context.get("name"));
                } else {
                    context.sender().sendRichMessage("<red>Cannot name player or non-living entity.");
                }
            })
            .build();

        this.commands.register(List.of(summonNatural, summon, summonVillager, nameEntity));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <C, E> ParserDescriptor<C, E> registryValueOrEnumParser(
        final Supplier<Object> registryKey,
        final TypeToken<E> elementType
    ) {
        if (GenericTypeReflector.isSuperType(Enum.class, elementType.getType())) {
            return enumParser((Class<? extends Enum>) GenericTypeReflector.erase(elementType.getType()));
        }
        return registryEntryParser((RegistryKey) registryKey.get(), (TypeToken) elementType)
            .mapSuccess(elementType, (ctx, entry) -> CompletableFuture.completedFuture(((RegistryEntryParser.RegistryEntry) entry).value()));
    }

    private void summonNatural(
        final Location location,
        final boolean refresh,
        final boolean noAI,
        final boolean protect,
        final boolean noInvisibility
    ) {
        Reflection.spawn(location, WanderingTrader.class, wanderingTrader -> {
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
                wanderingTrader.setCanDrinkPotion(false);
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
        Reflection.spawn(loc, WanderingTrader.class, wanderingTrader -> {
            wanderingTrader.setRecipes(recipes);
            if (disableAI) {
                wanderingTrader.setAI(false);
            }

            final PersistentDataContainer dataContainer = wanderingTrader.getPersistentDataContainer();
            dataContainer.set(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);

            this.applyConfig(tradeConfig, wanderingTrader);

            if (this.plugin.config().preventNightInvisibility()) {
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
        final Villager v = Reflection.spawn(loc, Villager.class, villager -> {
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

    private void setCustomName(
        final Nameable entity,
        final @Nullable String miniMessage
    ) {
        if (miniMessage == null || miniMessage.isBlank()) {
            entity.customName(null);
            return;
        }
        entity.customName(miniMessage().deserialize(miniMessage));
    }

    private static Location resolveLocation(final CommandContext<CommandSender> ctx) {
        final Location loc = ctx.get("location");
        ctx.flags().<World>getValue("world").ifPresent(loc::setWorld);
        ctx.flags().<Integer>getValue("yaw").ifPresent(loc::setYaw);
        ctx.flags().<Integer>getValue("pitch").ifPresent(loc::setPitch);
        return loc;
    }
}
