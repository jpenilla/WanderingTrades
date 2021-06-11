package xyz.jpenilla.wanderingtrades.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.jmplib.Crafty;

import static io.papermc.lib.PaperLib.getMinecraftVersion;
import static io.papermc.lib.PaperLib.isPaper;

public final class VillagerReflection {
    private VillagerReflection() {
    }

    private static final Class<?> BehaviorController_class;
    private static final Class<?> BehaviorVillageHeroGift_class;
    private static final Class<?> MerchantRecipeList_class = Crafty.needNMSClassOrElse("MerchantRecipeList", "net.minecraft.world.item.trading.MerchantRecipeList");
    private static final Class<?> CraftAbstractVillager_class = Crafty.needCraftClass("entity.CraftAbstractVillager");
    private static final Class<?> CraftVillager_class = Crafty.needCraftClass("entity.CraftVillager");
    private static final Class<?> EntityVillagerAbstract_class = Crafty.needNMSClassOrElse("EntityVillagerAbstract", "net.minecraft.world.entity.npc.EntityVillagerAbstract");
    private static final Class<?> EntityVillager_class = Crafty.needNMSClassOrElse("EntityVillager", "net.minecraft.world.entity.npc.EntityVillager");
    private static final Class<?> EntityLiving_class = Crafty.needNMSClassOrElse("EntityLiving", "net.minecraft.world.entity.EntityLiving");
    private static final Class<?> EntityVillagerTrader_class = Crafty.needNMSClassOrElse("EntityVillagerTrader", "net.minecraft.world.entity.npc.EntityVillagerTrader");
    private static final Class<?> CraftWanderingTrader_class = Crafty.needCraftClass("entity.CraftWanderingTrader");
    private static final MethodHandle CraftAbstractVillager_getHandle = Objects.requireNonNull(Crafty.findMethod(CraftAbstractVillager_class, "getHandle", EntityVillagerAbstract_class), "CraftAbstractVillager#getHandle");
    private static final MethodHandle CraftVillager_getHandle = Objects.requireNonNull(Crafty.findMethod(CraftVillager_class, "getHandle", EntityVillager_class), "CraftVillager#getHandle");
    private static final MethodHandle CraftWanderingTrader_getHandle = Objects.requireNonNull(Crafty.findMethod(CraftWanderingTrader_class, "getHandle", EntityVillagerTrader_class), "CraftWanderingTrader#getHandle");
    private static final MethodHandle EntityLiving_getBehaviorController;
    private static final Method EntityVillagerAbstract_updateTrades;
    private static final Field EntityVillagerAbstract_trades;
    private static final Field BehaviorController_behaviorsMap;
    private static final Field EntityVillagerTrader_despawnTimer;

    static {
        final String updateTradesMethodName;
        switch (getMinecraftVersion()) {
            case 14:
                updateTradesMethodName = "eh";
                break;
            case 15:
                updateTradesMethodName = "eC";
                break;
            case 16:
                updateTradesMethodName = "eW";
                break;
            case 17:
            default:
                updateTradesMethodName = "fE";
                break;
        }
        try {
            EntityVillagerAbstract_updateTrades = EntityVillagerAbstract_class.getDeclaredMethod(updateTradesMethodName);
            EntityVillagerAbstract_updateTrades.setAccessible(true);
            EntityVillagerAbstract_trades = Arrays.stream(MerchantRecipeList_class.getDeclaredFields())
                    .filter(field -> field.getType().isAssignableFrom(MerchantRecipeList_class))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Couldn't find trades field!"));
            EntityVillagerTrader_despawnTimer = Arrays.stream(EntityVillagerTrader_class.getDeclaredFields())
                    .filter(field -> int.class.equals(field.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Couldn't find despawn timer field!"));
            EntityVillagerTrader_despawnTimer.setAccessible(true);

            if (isPaper() && getMinecraftVersion() >= 16) {
                BehaviorVillageHeroGift_class = Crafty.needNMSClassOrElse("BehaviorVillageHeroGift", "net.minecraft.world.entity.ai.behavior.BehaviorVillageHeroGift");
                BehaviorController_class = Crafty.needNMSClassOrElse("BehaviorController", "net.minecraft.world.entity.ai.BehaviorController");
                final String mapName;
                if (getMinecraftVersion() == 16) {
                    mapName = "e";
                } else /* if (ver >=17) */ {
                    mapName = "f";
                }
                BehaviorController_behaviorsMap = BehaviorController_class.getDeclaredField(mapName);
                BehaviorController_behaviorsMap.setAccessible(true);
                EntityLiving_getBehaviorController = Objects.requireNonNull(Crafty.findMethod(EntityLiving_class, "getBehaviorController", BehaviorController_class), "EntityLiving#getBehaviorController");
            } else {
                BehaviorVillageHeroGift_class = null;
                BehaviorController_class = null;
                BehaviorController_behaviorsMap = null;
                EntityLiving_getBehaviorController = null;
            }
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to initialize reflection helper", e);
        }
    }

    public static void resetOffers(final @NonNull AbstractVillager trader) throws Throwable {
        final Object nmsTrader = CraftAbstractVillager_getHandle.bindTo(trader).invoke();
        EntityVillagerAbstract_trades.set(nmsTrader, MerchantRecipeList_class.newInstance());
        EntityVillagerAbstract_updateTrades.invoke(nmsTrader);
    }

    @SuppressWarnings("unchecked")
    public static void removeHeroOfTheVillageGiftBrainBehavior(final @NonNull Villager villager) throws Throwable {
        final Object handle = CraftVillager_getHandle.bindTo(villager).invoke();
        final Map<Integer, Map<?, Set<?>>> behaviors;
        try {
            behaviors = (Map<Integer, Map<?, Set<?>>>) BehaviorController_behaviorsMap.get(
                    EntityLiving_getBehaviorController.bindTo(handle).invoke()
            );
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Couldn't access behaviors", e);
        }

        behaviors.forEach((i, map) ->
                map.forEach((activity, behaviorSet) ->
                        behaviorSet.removeIf(it -> it.getClass().isAssignableFrom(BehaviorVillageHeroGift_class))));
    }

    public static int despawnTimer(final @NonNull WanderingTrader wanderingTrader) throws Throwable {
        return (int) EntityVillagerTrader_despawnTimer.get(getEntityVillagerTrader(wanderingTrader));
    }

    public static void despawnTimer(final @NonNull WanderingTrader wanderingTrader, final int despawnTimer) throws Throwable {
        EntityVillagerTrader_despawnTimer.set(getEntityVillagerTrader(wanderingTrader), despawnTimer);
    }

    private static @NonNull Object getEntityVillagerTrader(final @NonNull WanderingTrader wanderingTrader) throws Throwable {
        return CraftWanderingTrader_getHandle.bindTo(wanderingTrader).invoke();
    }
}
