package xyz.jpenilla.wanderingtrades.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Components {
    private Components() {
        throw new IllegalStateException();
    }

    public static TextComponent ofChildren(final ComponentLike... children) {
        if (children.length == 0) return Component.empty();
        return Component.text().append(children).build();
    }
}
