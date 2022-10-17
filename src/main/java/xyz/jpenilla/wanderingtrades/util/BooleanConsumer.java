package xyz.jpenilla.wanderingtrades.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface BooleanConsumer extends Consumer<Boolean> {
    void accept(boolean t);

    /**
     * {@inheritDoc}
     *
     * @deprecated Please use the corresponding type-specific method instead.
     */
    @Deprecated
    @Override
    default void accept(final Boolean t) {
        this.accept(t.booleanValue());
    }
}
