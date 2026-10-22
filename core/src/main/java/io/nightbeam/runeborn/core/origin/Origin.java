package io.nightbeam.runeborn.core.origin;

import io.nightbeam.runeborn.core.stat.Leveling;
import io.nightbeam.runeborn.core.weapon.Affinity;

import java.util.List;

/**
 * Starting classes, picked on first join. Attribute order: VIG MND END STR DEX INT FTH ARC.
 * Every class sums to {@code level + 79}.
 */
public enum Origin {
    KNIGHT_ERRANT("Knight-Errant", "A wandering knight sworn to no lord. Sturdy, balanced and steady in a fight.",
            new int[]{15, 10, 12, 14, 12, 9, 9, 7}, "errant_longsword", Affinity.STANDARD),
    WARLORD("Warlord", "A brute of the war-camps. Unmatched strength, little patience for subtlety.",
            new int[]{14, 9, 12, 17, 9, 7, 8, 11}, "warlord_battleaxe", Affinity.STANDARD),
    DUELIST("Duelist", "A blade-dancer of the pits. Dexterous and quick, lives by the dodge.",
            new int[]{11, 12, 11, 10, 17, 10, 7, 9}, "duelist_scimitar", Affinity.STANDARD),
    CUTTHROAT("Cutthroat", "A knife in the dark. Arcane-touched blades that make enemies bleed.",
            new int[]{10, 11, 10, 9, 13, 9, 8, 14}, "cutthroat_kris", Affinity.STANDARD),
    ASTROMANCER("Astromancer", "A scholar of the falling stars, who sees the world as a sky to be read.",
            new int[]{9, 15, 9, 8, 12, 16, 7, 9}, "astral_staff", Affinity.STANDARD),
    ORACLE("Oracle", "A seer whose prayers are answered in flame. Faith is both shield and sword.",
            new int[]{10, 14, 8, 11, 10, 7, 16, 10}, "pilgrim_seal", Affinity.STANDARD),
    RONIN("Ronin", "A masterless swordsman from distant shores. The katana is the whole of the path.",
            new int[]{12, 11, 13, 12, 15, 9, 8, 8}, "ronin_uchigatana", Affinity.STANDARD),
    PENITENT("Penitent", "A sinner of the old order who fights in penance. Strength tempered by faith.",
            new int[]{10, 13, 10, 12, 12, 9, 14, 9}, "penitent_mace", Affinity.STANDARD),
    HEXBLADE("Hexblade", "A spellsword marked by sorcery. Their estoc carries a hex of its own.",
            new int[]{11, 12, 11, 11, 14, 14, 6, 9}, "hexblade_estoc", Affinity.MAGIC),
    ASHBORN("Ashborn", "Risen from ash with nothing. The weakest start, and the freest path.",
            new int[]{10, 10, 10, 10, 10, 10, 10, 10}, "ashborn_club", Affinity.STANDARD);

    public static final Origin[] ALL = values();

    public final String displayName;
    public final String description;
    private final int[] attrs;
    public final String weapon;
    public final Affinity weaponAffinity;

    Origin(String displayName, String description, int[] attrs, String weapon, Affinity weaponAffinity) {
        this.displayName = displayName;
        this.description = description;
        this.attrs = attrs;
        this.weapon = weapon;
        this.weaponAffinity = weaponAffinity;
    }

    public int[] attrs() {
        return attrs.clone();
    }

    public int level() {
        return Leveling.level(attrs);
    }

    public String id() {
        return name().toLowerCase();
    }

    public String nameKey() {
        return "runeborn.origin." + id();
    }

    public String descKey() {
        return "runeborn.origin." + id() + ".desc";
    }

    public record KitItem(String id, int count) {
    }

    /** Items besides the weapon and flasks. */
    public List<KitItem> kit() {
        return switch (this) {
            case ASHBORN -> List.of(new KitItem("faded_rune", 5), new KitItem("tempering_stone_1", 6));
            case ASTROMANCER, ORACLE -> List.of(new KitItem("faded_rune", 2));
            default -> List.of(new KitItem("faded_rune", 2), new KitItem("tempering_stone_1", 2));
        };
    }

    public static Origin byId(String id) {
        for (Origin o : ALL) {
            if (o.id().equals(id)) {
                return o;
            }
        }
        return null;
    }
}
