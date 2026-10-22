package io.nightbeam.runeborn.core.shop;

import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The shrine merchant's stock. Higher tiers unlock as the player fells Sovereigns,
 * so shopping follows progression instead of skipping it.
 */
public final class Merchant {
    public record Offer(String itemId, long price, int count, int sovereignsRequired, Category category) {
        public long price(double multiplier) {
            return Math.max(1, Math.round(price * multiplier));
        }
    }

    public enum Category {
        MATERIALS, WHETSTONES, ARMAMENTS, CATALYSTS
    }

    private static final List<Offer> OFFERS;

    static {
        List<Offer> list = new ArrayList<>();
        list.add(new Offer("tempering_stone_1", 200, 1, 0, Category.MATERIALS));
        list.add(new Offer("tempering_stone_2", 600, 1, 0, Category.MATERIALS));
        list.add(new Offer("tempering_stone_3", 1_200, 1, 1, Category.MATERIALS));
        list.add(new Offer("tempering_stone_4", 2_000, 1, 2, Category.MATERIALS));
        list.add(new Offer("tempering_stone_5", 3_200, 1, 3, Category.MATERIALS));
        list.add(new Offer("lustrous_stone_1", 2_000, 1, 0, Category.MATERIALS));
        list.add(new Offer("lustrous_stone_2", 4_000, 1, 1, Category.MATERIALS));
        list.add(new Offer("lustrous_stone_3", 6_000, 1, 2, Category.MATERIALS));
        list.add(new Offer("blank_whetstone", 500, 1, 0, Category.MATERIALS));
        list.add(new Offer("heavy_whetstone", 3_000, 1, 0, Category.WHETSTONES));
        list.add(new Offer("keen_whetstone", 3_000, 1, 0, Category.WHETSTONES));
        list.add(new Offer("quality_whetstone", 3_000, 1, 0, Category.WHETSTONES));
        list.add(new Offer("magic_whetstone", 6_000, 1, 1, Category.WHETSTONES));
        list.add(new Offer("fire_whetstone", 6_000, 1, 1, Category.WHETSTONES));
        list.add(new Offer("lightning_whetstone", 6_000, 1, 1, Category.WHETSTONES));
        list.add(new Offer("sacred_whetstone", 6_000, 1, 2, Category.WHETSTONES));
        list.add(new Offer("cold_whetstone", 6_000, 1, 2, Category.WHETSTONES));
        list.add(new Offer("blood_whetstone", 8_000, 1, 3, Category.WHETSTONES));
        list.add(new Offer("poison_whetstone", 8_000, 1, 3, Category.WHETSTONES));
        list.add(new Offer("flame_art_whetstone", 8_000, 1, 3, Category.WHETSTONES));
        list.add(new Offer("occult_whetstone", 12_000, 1, 4, Category.WHETSTONES));
        for (WeaponSpec spec : Weapons.all()) {
            if (spec.rarity != Rarity.COMMON && spec.rarity != Rarity.RARE) {
                continue;
            }
            long price = spec.rarity == Rarity.COMMON ? 1_500 + Math.round(spec.baseAttack() * 250)
                    : 12_000 + Math.round(spec.baseAttack() * 900);
            int gate = spec.rarity == Rarity.COMMON ? 0 : 1;
            list.add(new Offer(spec.id, roundPrice(price), 1, gate,
                    spec.cls.isCatalyst() ? Category.CATALYSTS : Category.ARMAMENTS));
        }
        OFFERS = Collections.unmodifiableList(list);
    }

    private static long roundPrice(long price) {
        return Math.max(100, Math.round(price / 100.0) * 100);
    }

    public static List<Offer> offers() {
        return OFFERS;
    }

    public static Offer get(int index) {
        return index >= 0 && index < OFFERS.size() ? OFFERS.get(index) : null;
    }

    private Merchant() {
    }
}
