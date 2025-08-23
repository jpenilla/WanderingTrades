package xyz.jpenilla.wanderingtrades.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.entity.Villager;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.pluginbase.legacy.Crafty;

public final class VillagerReflection {
    private VillagerReflection() {
    }

    private static final Class<?> Brain_class;
    private static final Class<?> BehaviorVillageHeroGift_class;
    private static final Class<?> CraftVillager_class = Crafty.needCraftClass("entity.CraftVillager");
    private static final Class<?> EntityVillager_class = Crafty.needNMSClassOrElse("EntityVillager", "net.minecraft.world.entity.npc.EntityVillager");
    private static final Class<?> EntityLiving_class = Crafty.needNMSClassOrElse("EntityLiving", "net.minecraft.world.entity.EntityLiving");
    private static final MethodHandle CraftVillager_getHandle = Objects.requireNonNull(Crafty.findMethod(CraftVillager_class, "getHandle", EntityVillager_class), "CraftVillager#getHandle");
    private static final MethodHandle LivingEntity_getBrain;
    private static final Field Brain_availableBehaviorsByPriority;

    static {
        try {
            BehaviorVillageHeroGift_class = Crafty.needNMSClassOrElse("BehaviorVillageHeroGift", "net.minecraft.world.entity.ai.behavior.BehaviorVillageHeroGift");
            Brain_class = Crafty.needNMSClassOrElse("BehaviorController", "net.minecraft.world.entity.ai.BehaviorController");
            Brain_availableBehaviorsByPriority = Brain_class.getDeclaredField("availableBehaviorsByPriority");
            Brain_availableBehaviorsByPriority.setAccessible(true);
            LivingEntity_getBrain = Objects.requireNonNull(Crafty.findMethod(EntityLiving_class, "getBrain", Brain_class), "LivingEntity#getBrain");
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to initialize reflection helper", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeHeroOfTheVillageGiftBrainBehavior(final @NonNull Villager villager) throws Throwable {
        final Object handle = CraftVillager_getHandle.bindTo(villager).invoke();
        final Map<Integer, Map<?, Set<?>>> behaviors;
        try {
            behaviors = (Map<Integer, Map<?, Set<?>>>) Brain_availableBehaviorsByPriority.get(
                LivingEntity_getBrain.bindTo(handle).invoke()
            );
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Couldn't access behaviors", e);
        }

        behaviors.forEach((i, map) ->
            map.forEach((activity, behaviorSet) ->
                behaviorSet.removeIf(it -> it.getClass().isAssignableFrom(BehaviorVillageHeroGift_class))));
    }
}
