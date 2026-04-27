package xyz.jpenilla.wanderingtrades.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.entity.Villager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class VillagerReflection {
    private VillagerReflection() {
    }

    private static final @Nullable ReflectionAccess ACCESS = createAccess();

    private static @Nullable ReflectionAccess createAccess() {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final Class<?> craftVillagerClass = Class.forName("org.bukkit.craftbukkit.entity.CraftVillager");
            final MethodHandle craftVillagerGetHandle = lookup.unreflect(
                Objects.requireNonNull(craftVillagerClass.getMethod("getHandle"), "CraftVillager#getHandle")
            );
            final Class<?> livingEntityClass = Class.forName("net.minecraft.world.entity.LivingEntity");
            final Class<?> giveGiftToHeroClass = Class.forName("net.minecraft.world.entity.ai.behavior.GiveGiftToHero");
            final Class<?> brainClass = Class.forName("net.minecraft.world.entity.ai.Brain");
            final Field brainAvailableBehaviorsByPriority = brainClass.getDeclaredField("availableBehaviorsByPriority");
            brainAvailableBehaviorsByPriority.setAccessible(true);
            final MethodHandle livingEntityGetBrain = lookup.unreflect(
                Objects.requireNonNull(livingEntityClass.getMethod("getBrain"), "LivingEntity#getBrain")
            );
            return new ReflectionAccess(
                craftVillagerGetHandle,
                livingEntityGetBrain,
                brainAvailableBehaviorsByPriority,
                giveGiftToHeroClass
            );
        } catch (final ReflectiveOperationException e) {
            return null;
        }
    }

    public static boolean available() {
        return ACCESS != null;
    }

    @SuppressWarnings("unchecked")
    public static boolean removeHeroOfTheVillageGiftBrainBehavior(final @NonNull Villager villager) throws Throwable {
        final @Nullable ReflectionAccess access = ACCESS;
        if (access == null) {
            return false;
        }

        final Object handle = access.craftVillagerGetHandle.bindTo(villager).invoke();
        final Map<Integer, Map<?, Set<?>>> behaviors;
        try {
            behaviors = (Map<Integer, Map<?, Set<?>>>) access.brainAvailableBehaviorsByPriority.get(
                access.livingEntityGetBrain.bindTo(handle).invoke()
            );
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Couldn't access behaviors", e);
        }

        boolean removed = false;
        for (final Map<?, Set<?>> map : behaviors.values()) {
            for (final Set<?> behaviorSet : map.values()) {
                removed |= behaviorSet.removeIf(it -> access.giveGiftToHeroClass.isAssignableFrom(it.getClass()));
            }
        }
        return removed;
    }

    private record ReflectionAccess(
        MethodHandle craftVillagerGetHandle,
        MethodHandle livingEntityGetBrain,
        Field brainAvailableBehaviorsByPriority,
        Class<?> giveGiftToHeroClass
    ) {
    }
}
