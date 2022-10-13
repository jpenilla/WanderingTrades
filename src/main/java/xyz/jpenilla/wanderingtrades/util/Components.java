package xyz.jpenilla.wanderingtrades.util;

import com.google.gson.JsonElement;
import io.papermc.lib.PaperLib;
import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Components {
    private static final @Nullable Object NATIVE_GSON;
    private static final @Nullable Method NATIVE_DESERIALIZE;

    static {
        if (!PaperLib.isPaper()) {
            NATIVE_GSON = null;
            NATIVE_DESERIALIZE = null;
        } else {
            try {
                final Class<?> gsonClass = Class.forName(String.join(".", "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializer"));
                NATIVE_DESERIALIZE = gsonClass.getMethod("deserializeFromTree", JsonElement.class);
                NATIVE_GSON = gsonClass.getDeclaredMethod("gson").invoke(null);
            } catch (final ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Components() {
    }

    public static Object toNative(final Component component) {
        if (NATIVE_GSON == null || NATIVE_DESERIALIZE == null) {
            throw new IllegalStateException();
        }
        try {
            return NATIVE_DESERIALIZE.invoke(NATIVE_GSON, GsonComponentSerializer.gson().serializeToTree(component));
        } catch (final ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> nativeAdventureComponentClass() {
        try {
            return Class.forName(String.join(".", "net", "kyori", "adventure", "text", "Component"));
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
