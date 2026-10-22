package io.nightbeam.runeborn.core.weapon;

import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.art.Spell;

/**
 * Weapon archetypes. Values are vanilla units: {@code damage} is attack damage at +0 with no
 * scaling, {@code speed} is attacks per second, {@code reach} is extra interaction range.
 */
public enum WeaponClass {
    DAGGER(3.5, 2.2, 6, -0.5, Art.QUICKSTEP, null),
    STRAIGHT_SWORD(5.0, 1.6, 10, 0.0, Art.WAR_CRY, null),
    THRUSTING_SWORD(4.5, 1.9, 9, 0.5, Art.IMPALING_THRUST, null),
    CURVED_SWORD(4.5, 1.8, 9, 0.0, Art.QUICKSTEP, null),
    KATANA(5.5, 1.6, 11, 0.25, Art.UNSHEATHE, null),
    TWINBLADE(4.5, 1.7, 12, 0.25, Art.WHIRLWIND, null),
    GREATSWORD(8.0, 0.95, 18, 0.5, Art.LEAPING_CLEAVE, null),
    COLOSSAL_SWORD(11.0, 0.7, 26, 0.75, Art.RUINOUS_CHARGE, null),
    AXE(6.0, 1.0, 13, 0.0, Art.WAR_CRY, null),
    GREATAXE(9.5, 0.8, 22, 0.5, Art.FRENZIED_STRIKES, null),
    HAMMER(6.0, 1.1, 13, 0.0, Art.EARTHSHAKER, null),
    GREAT_HAMMER(10.5, 0.75, 24, 0.25, Art.EARTHSHAKER, null),
    SPEAR(5.0, 1.3, 11, 1.5, Art.IMPALING_THRUST, null),
    HALBERD(7.5, 0.9, 17, 1.25, Art.SPINNING_REAP, null),
    REAPER(7.0, 1.0, 16, 1.0, Art.SPINNING_REAP, null),
    CLAW(3.0, 2.6, 5, -0.75, Art.QUICKSTEP, null),
    WHIP(3.5, 1.3, 8, 2.0, Art.SPINNING_REAP, null),
    STAFF(2.5, 1.2, 8, 0.0, Art.SPELLCAST, Spell.School.SORCERY),
    SEAL(2.0, 1.2, 8, -0.5, Art.SPELLCAST, Spell.School.INCANTATION);

    public final double damage;
    public final double speed;
    public final int stamina;
    public final double reach;
    public final Art defaultArt;
    /** Non-null for catalysts. */
    public final Spell.School school;

    WeaponClass(double damage, double speed, int stamina, double reach, Art defaultArt, Spell.School school) {
        this.damage = damage;
        this.speed = speed;
        this.stamina = stamina;
        this.reach = reach;
        this.defaultArt = defaultArt;
        this.school = school;
    }

    public boolean isCatalyst() {
        return school != null;
    }

    /** Swords and polearms sweep like vanilla swords. */
    public boolean sweeps() {
        return switch (this) {
            case STRAIGHT_SWORD, CURVED_SWORD, KATANA, TWINBLADE, GREATSWORD, COLOSSAL_SWORD, HALBERD, REAPER -> true;
            default -> false;
        };
    }

    public String langKey() {
        return "runeborn.class." + name().toLowerCase();
    }
}
