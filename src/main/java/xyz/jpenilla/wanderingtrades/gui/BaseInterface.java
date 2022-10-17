package xyz.jpenilla.wanderingtrades.gui;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.LangConfig;

@DefaultQualifier(NonNull.class)
public abstract class BaseInterface {
    protected final WanderingTrades plugin;
    protected final LangConfig lang;
    protected final Validators validators;
    protected final PartsFactory parts;
    private final Supplier<ChestInterface> builtInterface = Suppliers.memoize(this::buildInterface);

    protected BaseInterface(final WanderingTrades plugin) {
        this.plugin = plugin;
        this.lang = plugin.langConfig();
        this.validators = new Validators(this, plugin);
        this.parts = new PartsFactory(plugin);
    }

    protected abstract ChestInterface buildInterface();

    public final void open(final Player player) {
        this.builtInterface.get().open(PlayerViewer.of(player));
    }

    public final void openAsChild(
        final ClickContext<?, ?, PlayerViewer> context
    ) {
        this.builtInterface.get().open(context.view(), HashMapInterfaceArguments.empty());
    }

    public final void replaceActiveScreen(
        final ClickContext<?, ?, PlayerViewer> context
    ) {
        final Player player = context.viewer().player();
        player.closeInventory();
        this.open(player);
    }
}
