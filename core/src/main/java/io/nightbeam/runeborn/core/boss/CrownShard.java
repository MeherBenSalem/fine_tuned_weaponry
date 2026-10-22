package io.nightbeam.runeborn.core.boss;

/** Permanent blessings from Crown Shards. Only one may be equipped at a time, at a shrine. */
public enum CrownShard {
    GRAFTED("+5 to every attribute."),
    FROST("+15% max health. Immune to frostbite."),
    STORM("+20% lightning damage. +30% stamina recovery."),
    BLOOM("Kills restore 3% max health. Bleed procs restore 2%."),
    ASH("+15% physical damage. Immune to fire."),
    WYRM("Spells cost 20% less FP. Focus recovers twice as fast."),
    END("+10% to all damage dealt.");

    public final String description;

    CrownShard(String description) {
        this.description = description;
    }

    public String descKey() {
        return "runeborn.shard." + name().toLowerCase() + ".desc";
    }
}
