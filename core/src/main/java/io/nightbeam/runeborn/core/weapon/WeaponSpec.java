package io.nightbeam.runeborn.core.weapon;

import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.art.Spell;
import io.nightbeam.runeborn.core.stat.Attr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Immutable definition of a weapon. Build instances through {@link Weapons}. */
public final class WeaponSpec {
    public enum Path {
        /** +0 to +25 with tempering stones; accepts affinities. */
        STANDARD(25),
        /** +0 to +10 with lustrous stones; fixed affinity. */
        LUSTROUS(10);

        public final int max;

        Path(int max) {
            this.max = max;
        }
    }

    public final String id;
    public final String name;
    public final WeaponClass cls;
    public final Rarity rarity;
    public final Path path;
    private final double[] damage;
    private final double[] scaling;
    private final int[] requirements;
    private final int[] status;
    public final Art art;
    public final Spell spell;
    /** Either {@code sprite:<template>:<palette>} or {@code custom:<model>:<texture key>=<texture>,...}. */
    public final String model;
    public final List<String> lore;

    WeaponSpec(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.cls = b.cls;
        this.rarity = b.rarity;
        this.path = b.path;
        this.damage = b.damage.clone();
        this.scaling = b.scaling.clone();
        this.requirements = b.requirements.clone();
        this.status = b.status.clone();
        this.art = b.art != null ? b.art : b.cls.defaultArt;
        this.spell = b.spell;
        this.model = b.model;
        this.lore = Collections.unmodifiableList(new ArrayList<>(b.lore));
    }

    public double damage(DamageType type) {
        return damage[type.ordinal()];
    }

    public double scaling(Attr attr) {
        return scaling[attr.ordinal()];
    }

    public int requirement(Attr attr) {
        return requirements[attr.ordinal()];
    }

    public int status(StatusType type) {
        return status[type.ordinal()];
    }

    public double[] damageArray() {
        return damage.clone();
    }

    public double[] scalingArray() {
        return scaling.clone();
    }

    public int[] statusArray() {
        return status.clone();
    }

    public boolean infusable() {
        return path == Path.STANDARD && !cls.isCatalyst();
    }

    /** Vanilla attack damage attribute value at +0 (physical share only). */
    public double baseAttack() {
        double sum = 0;
        for (double d : damage) {
            sum += d;
        }
        return sum;
    }

    public String langKey() {
        return "item.runeborn." + id;
    }

    public static final class Builder {
        private final String id;
        private final String name;
        private final WeaponClass cls;
        private final Rarity rarity;
        private Path path = Path.STANDARD;
        private final double[] damage = new double[DamageType.ALL.length];
        private final double[] scaling = new double[Attr.COUNT];
        private final int[] requirements = new int[Attr.COUNT];
        private final int[] status = new int[StatusType.ALL.length];
        private Art art;
        private Spell spell;
        private String model;
        private final List<String> lore = new ArrayList<>();

        Builder(String id, String name, WeaponClass cls, Rarity rarity) {
            this.id = id;
            this.name = name;
            this.cls = cls;
            this.rarity = rarity;
            this.damage[DamageType.PHYSICAL.ordinal()] = cls.damage;
            if (rarity == Rarity.EPIC || rarity == Rarity.LEGENDARY) {
                this.path = Path.LUSTROUS;
            }
        }

        public Builder phys(double value) {
            damage[DamageType.PHYSICAL.ordinal()] = value;
            return this;
        }

        public Builder dmg(DamageType type, double value) {
            damage[type.ordinal()] = value;
            return this;
        }

        public Builder scale(Attr attr, double coefficient) {
            scaling[attr.ordinal()] = coefficient;
            return this;
        }

        public Builder req(Attr attr, int value) {
            requirements[attr.ordinal()] = value;
            return this;
        }

        public Builder status(StatusType type, int buildup) {
            status[type.ordinal()] = buildup;
            return this;
        }

        public Builder art(Art art) {
            this.art = art;
            return this;
        }

        public Builder spell(Spell spell) {
            this.spell = spell;
            return this;
        }

        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        public Builder sprite(String template, String palette) {
            this.model = "sprite:" + template + ":" + palette;
            return this;
        }

        /** Reuses a hand-built block-bench model; {@code textures} is {@code key=texture,...}. */
        public Builder custom(String modelName, String textures) {
            this.model = "custom:" + modelName + ":" + textures;
            return this;
        }

        public Builder lore(String line) {
            lore.add(line);
            return this;
        }
    }
}
