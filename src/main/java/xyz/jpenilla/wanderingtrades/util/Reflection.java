package xyz.jpenilla.wanderingtrades.util;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.RegionAccessor;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

// Class from ChessCraft
@SuppressWarnings("deprecation") // bukkit Consumer
@DefaultQualifier(NonNull.class)
public final class Reflection {
  private static final @Nullable Method OLD_SPAWN;

  static {
    @Nullable Method method = null;
    try {
      method = RegionAccessor.class.getDeclaredMethod("spawn", Location.class, Class.class, org.bukkit.util.Consumer.class);
    } catch (final NoSuchMethodException ignore) {
    }
    OLD_SPAWN = method;
  }

  private Reflection() {
  }

  // On 9/21/23 Bukkit changed the method to take java Consumer instead of Bukkit's; the commodore transform doesn't apply to Paper plugins...
  @SuppressWarnings("unchecked")
  public static <T extends Entity> T spawn(final Location location, final Class<T> clazz, final Consumer<T> function) {
    if (OLD_SPAWN == null) {
      return location.getWorld().spawn(location, clazz, function);
    }
    try {
      return (T) OLD_SPAWN.invoke(location.getWorld(), location, clazz, (org.bukkit.util.Consumer<T>) function::accept);
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
