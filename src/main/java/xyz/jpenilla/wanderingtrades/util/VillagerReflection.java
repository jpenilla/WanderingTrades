package xyz.jpenilla.wanderingtrades.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.entity.Villager;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class VillagerReflection {
    private VillagerReflection() {
    }

    private static final Class<?> Brain_class;
    private static final Class<?> GiveGiftToHero_class;
    private static final Class<?> CraftVillager_class;
    private static final Class<?> LivingEntity_class;
    private static final MethodHandle CraftVillager_getHandle;
    private static final MethodHandle LivingEntity_getBrain;
    private static final Field Brain_availableBehaviorsByPriority;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            CraftVillager_class = Class.forName("org.bukkit.craftbukkit.entity.CraftVillager");
            CraftVillager_getHandle = lookup.unreflect(Objects.requireNonNull(CraftVillager_class.getMethod("getHandle"), "CraftVillager#getHandle"));
            LivingEntity_class = Class.forName("net.minecraft.world.entity.LivingEntity");
            GiveGiftToHero_class = Class.forName("net.minecraft.world.entity.ai.behavior.GiveGiftToHero");
            Brain_class = Class.forName("net.minecraft.world.entity.ai.Brain");
            Brain_availableBehaviorsByPriority = Brain_class.getDeclaredField("availableBehaviorsByPriority");
            Brain_availableBehaviorsByPriority.setAccessible(true);
            LivingEntity_getBrain = lookup.unreflect(Objects.requireNonNull(LivingEntity_class.getMethod("getBrain"), "LivingEntity#getBrain"));
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
                behaviorSet.removeIf(it -> it.getClass().isAssignableFrom(GiveGiftToHero_class))));
    }
}
