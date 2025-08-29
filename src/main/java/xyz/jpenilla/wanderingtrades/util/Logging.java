package xyz.jpenilla.wanderingtrades.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Logging {
    private static final Logger LOGGER = LogManager.getLogger("WanderingTrades");

    private Logging() {
    }

    public static Logger logger() {
        return LOGGER;
    }
}
