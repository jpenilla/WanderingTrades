package xyz.jpenilla.wanderingtrades.util;

import com.google.common.base.Preconditions;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
public class InputConversation {
    private final Plugin plugin;

    private @Nullable BiConsumer<Player, String> acceptedListener;
    private @Nullable BiConsumer<Player, String> deniedListener;
    private @Nullable BiPredicate<Player, String> inputValidator;
    private @Nullable Function<Player, String> promptHandler;
    private @Nullable BiFunction<Player, String, String> confirmText;
    private @Nullable ConversationAbandonedListener abandonListener;
    private boolean localEcho = false;

    private InputConversation(final Plugin plugin) {
        this.plugin = plugin;
    }

    public InputConversation onAccepted(BiConsumer<Player, String> acceptedListener) {
        this.acceptedListener = acceptedListener;
        return this;
    }

    public InputConversation onDenied(BiConsumer<Player, String> deniedListener) {
        this.deniedListener = deniedListener;
        return this;
    }

    public InputConversation onValidateInput(BiPredicate<Player, String> inputValidator) {
        this.inputValidator = inputValidator;
        return this;
    }

    public InputConversation onPromptText(Function<Player, String> promptHandler) {
        this.promptHandler = promptHandler;
        return this;
    }

    public InputConversation onConfirmText(BiFunction<Player, String, String> confirmTextHandler) {
        this.confirmText = confirmTextHandler;
        return this;
    }

    public InputConversation localEcho(final boolean localEcho) {
        this.localEcho = localEcho;
        return this;
    }

    public InputConversation abandonListener(final ConversationAbandonedListener conversationAbandonedListener) {
        this.abandonListener = conversationAbandonedListener;
        return this;
    }

    public void start(final Player player) {
        Preconditions.checkArgument(this.promptHandler != null, "Must set onPromptText");
        Preconditions.checkArgument(this.inputValidator != null, "Must set onValidateInput");
        if (this.acceptedListener != null || this.deniedListener != null) {
            Preconditions.checkArgument(this.confirmText != null, "Must set onConfirmText if onAccepted/onDenied is set");
        }

        final ConversationFactory factory = new ConversationFactory(this.plugin)
                .withFirstPrompt(this.createFirstPrompt())
                .withLocalEcho(this.localEcho);
        if (this.abandonListener != null) {
            factory.addConversationAbandonedListener(this.abandonListener);
        }
        factory.buildConversation(player)
                .begin();
    }

    private Prompt createFirstPrompt() {
        return new StringPrompt() {
            @Override
            public String getPromptText(final ConversationContext conversationContext) {
                return InputConversation.this.promptHandler.apply((Player) conversationContext.getForWhom());
            }

            @Override
            public Prompt acceptInput(final ConversationContext conversationContext, final @Nullable String s) {
                if (!InputConversation.this.inputValidator.test((Player) conversationContext.getForWhom(), s)) {
                    return this;
                }
                if (InputConversation.this.acceptedListener != null || InputConversation.this.deniedListener != null) {
                    return InputConversation.this.createSecondPrompt(s);
                }
                return END_OF_CONVERSATION;
            }
        };
    }

    private BooleanPrompt createSecondPrompt(final String s) {
        return new BooleanPrompt() {
            @Override
            protected @Nullable Prompt acceptValidatedInput(final ConversationContext conversationContext, final boolean b) {
                if (b && InputConversation.this.acceptedListener != null) {
                    InputConversation.this.acceptedListener.accept((Player) conversationContext.getForWhom(), s);
                } else if (InputConversation.this.deniedListener != null) {
                    InputConversation.this.deniedListener.accept((Player) conversationContext.getForWhom(), s);
                }
                return END_OF_CONVERSATION;
            }

            @Override
            public String getPromptText(final ConversationContext conversationContext) {
                return InputConversation.this.confirmText.apply((Player) conversationContext.getForWhom(), s);
            }
        };
    }

    public static InputConversation create() {
        return new InputConversation(WanderingTrades.instance());
    }
}
