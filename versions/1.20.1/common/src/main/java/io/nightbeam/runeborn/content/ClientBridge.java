package io.nightbeam.runeborn.content;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Client-only services used by item classes (tooltips, flask bars). Filled in by the client
 * entrypoint; stays empty on dedicated servers, so common code never touches client classes.
 */
public final class ClientBridge {
    public static Function<String, String> translate;
    public static Supplier<int[]> attributes;
    public static BooleanSupplier shiftDown;
    public static IntSupplier emberCharges;
    public static IntSupplier emberMax;
    public static IntSupplier azureCharges;
    public static IntSupplier azureMax;

    public static boolean ready() {
        return translate != null;
    }

    private ClientBridge() {
    }
}
