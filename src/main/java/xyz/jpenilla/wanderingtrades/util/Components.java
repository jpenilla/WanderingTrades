package xyz.jpenilla.wanderingtrades.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Components {
    private Components() {
    }

    public static Component disableItalics(final Component component) {
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static TagResolver.Single valuePlaceholder(final ComponentLike value) {
        return placeholder("value", value);
    }

    public static TagResolver.Single valuePlaceholder(final Object value, final TextColor color) {
        return placeholder("value", Component.text(value.toString(), color));
    }

    public static TagResolver.Single valuePlaceholder(final Object value) {
        return valuePlaceholder(value, NamedTextColor.YELLOW);
    }

    public static TagResolver.Single placeholder(final String name, final ComponentLike value) {
        return Placeholder.component(name, value);
    }

    public static TagResolver.Single placeholder(final String name, final Object value) {
        return Placeholder.unparsed(name, value.toString());
    }
}
