package io.nightbeam.runeborn;

import io.nightbeam.runeborn.core.Rb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Mod-wide constants shared by every loader entrypoint. */
public final class Runeborn {
    public static final String MOD_ID = Rb.MOD_ID;
    public static final Logger LOG = LoggerFactory.getLogger(Rb.NAME);

    private Runeborn() {
    }
}
