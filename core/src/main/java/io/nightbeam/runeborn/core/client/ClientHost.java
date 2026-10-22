package io.nightbeam.runeborn.core.client;

/** Client services implemented once per Minecraft version for screens and the HUD. */
public interface ClientHost {
    void send(byte[] payload);

    void open(RbScreen screen);

    void close();

    void playClick();

    /** Slots 0-8 are the hotbar, 40 the offhand, -1 the selected main hand. */
    ItemView slot(int slot);

    int selectedSlot();

    /** Count of a Runeborn item (by path, e.g. {@code tempering_stone_2}) across the inventory. */
    int count(String runebornItem);

    float health();

    float maxHealth();

    float absorption();

    String dimension();

    double x();

    double y();

    double z();

    boolean creative();

    /** Number of vanilla boss bars currently shown (so the target bar can sit below them). */
    int vanillaBossBars();
}
