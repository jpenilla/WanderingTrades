package xyz.jpenilla.wanderingtrades.gui;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
public abstract class BaseInterface {
    protected final WanderingTrades plugin;
    protected final Validators validators;
    protected final PartsFactory parts;
    private final Supplier<ChestInterface> builtInterface = Suppliers.memoize(this::buildInterface);

    protected BaseInterface(final WanderingTrades plugin) {
        this.plugin = plugin;
        this.validators = new Validators(this, plugin);
        this.parts = new PartsFactory();
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
