package io.nightbeam.runeborn.core.weapon;

import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.art.Spell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.nightbeam.runeborn.core.stat.Attr.ARCANE;
import static io.nightbeam.runeborn.core.stat.Attr.DEXTERITY;
import static io.nightbeam.runeborn.core.stat.Attr.FAITH;
import static io.nightbeam.runeborn.core.stat.Attr.INTELLIGENCE;
import static io.nightbeam.runeborn.core.stat.Attr.STRENGTH;
import static io.nightbeam.runeborn.core.weapon.DamageType.FIRE;
import static io.nightbeam.runeborn.core.weapon.DamageType.HOLY;
import static io.nightbeam.runeborn.core.weapon.DamageType.LIGHTNING;
import static io.nightbeam.runeborn.core.weapon.DamageType.MAGIC;
import static io.nightbeam.runeborn.core.weapon.Rarity.COMMON;
import static io.nightbeam.runeborn.core.weapon.Rarity.EPIC;
import static io.nightbeam.runeborn.core.weapon.Rarity.LEGENDARY;
import static io.nightbeam.runeborn.core.weapon.Rarity.RARE;
import static io.nightbeam.runeborn.core.weapon.StatusType.BLEED;
import static io.nightbeam.runeborn.core.weapon.StatusType.BLIGHT;
import static io.nightbeam.runeborn.core.weapon.StatusType.FROST;
import static io.nightbeam.runeborn.core.weapon.StatusType.POISON;
import static io.nightbeam.runeborn.core.weapon.WeaponClass.*;

/** The weapon catalog. Order here is creative-tab order. */
public final class Weapons {
    // Scaling coefficients by letter at +0.
    static final double E = 0.15;
    static final double D = 0.45;
    static final double C = 0.75;
    static final double B = 1.10;
    static final double A = 1.55;
    static final double S = 1.90;

    private static final Map<String, WeaponSpec> BY_ID = new LinkedHashMap<>();
    private static final List<WeaponSpec> ALL;

    static {
        // ---- Daggers
        add(w("rusted_dagger", "Rusted Dagger", DAGGER, COMMON).scale(STRENGTH, E).scale(DEXTERITY, C)
                .req(STRENGTH, 5).req(DEXTERITY, 9).sprite("dagger", "rust")
                .lore("Pitted and notched, yet it still finds the gap in armor."));
        add(w("cutthroat_kris", "Cutthroat's Kris", DAGGER, COMMON).phys(3.4).scale(STRENGTH, E).scale(DEXTERITY, C)
                .scale(ARCANE, D).req(DEXTERITY, 10).status(BLEED, 40).sprite("kris", "crimson")
                .lore("A wavy blade favored by those who work in the dark."));
        add(w("starshard_kris", "Starshard Kris", DAGGER, RARE).phys(2.6).dmg(MAGIC, 2.0).scale(DEXTERITY, D)
                .scale(INTELLIGENCE, B).req(DEXTERITY, 10).req(INTELLIGENCE, 16).art(Art.STARBLADE_VOLLEY)
                .sprite("dagger", "glint").lore("Its edge is a sliver of fallen star."));
        add(w("venomfang_stiletto", "Venomfang Stiletto", DAGGER, RARE).phys(3.2).scale(DEXTERITY, C).scale(ARCANE, D)
                .req(DEXTERITY, 12).status(POISON, 55).sprite("stiletto", "venom")
                .lore("The groove along its spine still weeps green."));
        add(w("shadowfang", "Shadowfang", DAGGER, LEGENDARY).phys(4.2).scale(DEXTERITY, B).scale(ARCANE, C)
                .req(DEXTERITY, 20).req(ARCANE, 14).status(BLEED, 35).art(Art.SHADOWSTEP).sprite("kris", "shadow")
                .lore("Taken from the deep where the Warden listens.").lore("It strikes from where you are not."));

        // ---- Straight swords
        add(w("errant_longsword", "Errant Longsword", STRAIGHT_SWORD, COMMON).scale(STRENGTH, C).scale(DEXTERITY, C)
                .req(STRENGTH, 10).req(DEXTERITY, 10).sprite("longsword", "steel")
                .lore("Carried by knights who swore to no lord."));
        add(w("oathsworn_blade", "Oathsworn Blade", STRAIGHT_SWORD, COMMON).phys(5.3).scale(STRENGTH, C).scale(DEXTERITY, D)
                .req(STRENGTH, 12).req(DEXTERITY, 10).sprite("broadsword", "iron")
                .lore("Heavy, honest steel for an honest oath."));
        add(w("ossuary_blade", "Ossuary Blade", STRAIGHT_SWORD, RARE).phys(5.2).scale(STRENGTH, C).scale(DEXTERITY, C)
                .scale(ARCANE, E).req(STRENGTH, 12).req(DEXTERITY, 12).status(BLIGHT, 40).art(Art.SPINNING_REAP)
                .custom("bone_sword", "0=bone_sword").lore("Carved from the femur of something that should not have walked."));
        add(w("emberbrand", "Emberbrand", STRAIGHT_SWORD, RARE).phys(3.8).dmg(FIRE, 2.6).scale(STRENGTH, C).scale(FAITH, C)
                .req(STRENGTH, 12).req(FAITH, 12).art(Art.EMBER_CLEAVE).sprite("longsword", "ember")
                .lore("Never cools, even when sheathed."));
        add(w("rimeguard_sword", "Rimeguard Sword", STRAIGHT_SWORD, RARE).phys(4.0).dmg(MAGIC, 1.8).scale(STRENGTH, D)
                .scale(DEXTERITY, C).scale(INTELLIGENCE, C).req(STRENGTH, 10).req(DEXTERITY, 12).req(INTELLIGENCE, 12)
                .status(FROST, 50).art(Art.RIMEFROST_STOMP).sprite("broadsword", "frost")
                .lore("Frost blooms wherever its point touches."));
        add(w("sunlit_sword", "Sunlit Sword", STRAIGHT_SWORD, EPIC).phys(4.2).dmg(HOLY, 3.0).scale(STRENGTH, D)
                .scale(DEXTERITY, D).scale(FAITH, B).req(STRENGTH, 12).req(FAITH, 18).art(Art.SACRED_EDGE)
                .sprite("longsword", "sun").lore("Forged at dawn and blessed at noon."));

        // ---- Thrusting swords
        add(w("hexblade_estoc", "Hexblade Estoc", THRUSTING_SWORD, COMMON).scale(STRENGTH, D).scale(DEXTERITY, C)
                .req(STRENGTH, 8).req(DEXTERITY, 12).sprite("estoc", "steel")
                .lore("A spellblade's needle. Hex it, then hunt."));
        add(w("noble_rapier", "Noble's Rapier", THRUSTING_SWORD, COMMON).phys(4.2).scale(DEXTERITY, B)
                .req(DEXTERITY, 14).art(Art.QUICKSTEP).sprite("rapier", "gold")
                .lore("An heirloom of a house that no longer stands."));
        add(w("thornspur_rapier", "Thornspur Rapier", THRUSTING_SWORD, RARE).phys(4.0).scale(DEXTERITY, C).scale(ARCANE, D)
                .req(DEXTERITY, 14).status(BLIGHT, 55).sprite("rapier", "blight")
                .lore("Each thrust leaves a festering bloom."));

        // ---- Curved swords
        add(w("duelist_scimitar", "Duelist's Scimitar", CURVED_SWORD, COMMON).scale(STRENGTH, D).scale(DEXTERITY, C)
                .req(DEXTERITY, 11).sprite("scimitar", "steel").lore("Sing it through the air and the crowd will follow."));
        add(w("desert_shamshir", "Desert Shamshir", CURVED_SWORD, COMMON).phys(4.8).scale(STRENGTH, D).scale(DEXTERITY, C)
                .req(STRENGTH, 9).req(DEXTERITY, 13).sprite("scimitar", "brass").lore("Polished by sand and wind."));
        add(w("fangtooth_cleaver", "Fangtooth Cleaver", CURVED_SWORD, RARE).phys(5.0).scale(STRENGTH, C).scale(DEXTERITY, C)
                .scale(ARCANE, E).req(STRENGTH, 13).req(DEXTERITY, 13).status(BLEED, 45).art(Art.FRENZIED_STRIKES)
                .custom("inosukes_sword", "0=inosukes_sword").lore("Its saw-teeth were filed by a beast, not a smith."));
        add(w("magma_falchion", "Magma Falchion", CURVED_SWORD, RARE).phys(3.6).dmg(FIRE, 2.4).scale(STRENGTH, C)
                .scale(DEXTERITY, D).req(STRENGTH, 12).req(DEXTERITY, 10).art(Art.EMBER_CLEAVE).sprite("falchion", "magma")
                .lore("Quenched in a river of molten rock."));

        // ---- Katanas
        add(w("ronin_uchigatana", "Ronin's Uchigatana", KATANA, COMMON).scale(STRENGTH, D).scale(DEXTERITY, C)
                .req(STRENGTH, 11).req(DEXTERITY, 15).status(BLEED, 35).custom("katana", "0=katana_texture")
                .lore("The blade of one who walks without a master."));
        add(w("longfang_nodachi", "Longfang Nodachi", KATANA, RARE).phys(6.0).scale(STRENGTH, D).scale(DEXTERITY, B)
                .req(STRENGTH, 18).req(DEXTERITY, 22).status(BLEED, 45).sprite("nodachi", "steel")
                .lore("Too long to draw quickly. Too deadly to matter."));
        add(w("mistveil_katana", "Mistveil Katana", KATANA, RARE).phys(4.0).dmg(MAGIC, 2.0).scale(DEXTERITY, C)
                .scale(INTELLIGENCE, C).req(DEXTERITY, 14).req(INTELLIGENCE, 14).sprite("katana", "mist")
                .lore("Seen only in the instant before it cuts."));
        add(w("stormfang", "Stormfang", KATANA, EPIC).phys(3.8).dmg(LIGHTNING, 2.8).scale(DEXTERITY, B)
                .req(DEXTERITY, 20).art(Art.THUNDERSTEP).custom("zenitsus_sword", "0=zenitsus_sword")
                .lore("Drawn from the eye of the storm that drowned the old sea-temple."));
        add(w("crimsonfall", "Crimsonfall", KATANA, LEGENDARY).phys(5.2).dmg(FIRE, 1.2).scale(DEXTERITY, C).scale(ARCANE, B)
                .req(STRENGTH, 12).req(DEXTERITY, 18).req(ARCANE, 20).status(BLEED, 55).art(Art.CRIMSON_FLURRY)
                .custom("blood_katana", "0=blood_katana_texture")
                .lore("Ysolde's blade. Its bloom never wilts.").lore("It drinks before you do."));
        add(w("rimeveil", "Rimeveil", KATANA, LEGENDARY).phys(3.8).dmg(MAGIC, 2.6).scale(DEXTERITY, D).scale(INTELLIGENCE, B)
                .req(STRENGTH, 12).req(DEXTERITY, 16).req(INTELLIGENCE, 23).status(FROST, 55).art(Art.RIMEFROST_STOMP)
                .sprite("katana", "rime").lore("Sylvara's veil, folded into steel."));

        // ---- Twinblades
        add(w("twinfang_glaive", "Twinfang Glaive", TWINBLADE, COMMON).scale(STRENGTH, D).scale(DEXTERITY, C)
                .req(STRENGTH, 10).req(DEXTERITY, 14).sprite("twinblade", "steel").lore("Two edges, no rest."));
        add(w("hollow_monk_staff", "Hollow Monk's Staff", TWINBLADE, RARE).phys(4.6).scale(STRENGTH, C).scale(DEXTERITY, C)
                .req(STRENGTH, 13).req(DEXTERITY, 13).custom("pole", "2=classic_pole_texture")
                .lore("The monk who carried it was hollow. The staff was not."));
        add(w("eclipse_twinblade", "Eclipse Twinblade", TWINBLADE, EPIC).phys(3.5).dmg(HOLY, 2.5).scale(DEXTERITY, C)
                .scale(FAITH, C).req(DEXTERITY, 18).req(FAITH, 16).sprite("twinblade", "eclipse")
                .lore("Sun at one end, moon at the other."));

        // ---- Greatswords
        add(w("highland_claymore", "Highland Claymore", GREATSWORD, COMMON).scale(STRENGTH, C).scale(DEXTERITY, C)
                .req(STRENGTH, 16).req(DEXTERITY, 13).sprite("greatsword", "steel").lore("Swung by clansmen in the cold hills."));
        add(w("wavecut_flamberge", "Wavecut Flamberge", GREATSWORD, COMMON).phys(7.6).scale(STRENGTH, D).scale(DEXTERITY, B)
                .req(STRENGTH, 15).req(DEXTERITY, 14).status(BLEED, 30).sprite("flamberge", "steel")
                .lore("The waved edge tears more than it cuts."));
        add(w("banneret_greatsword", "Banneret's Greatsword", GREATSWORD, RARE).phys(8.4).scale(STRENGTH, B).scale(DEXTERITY, D)
                .req(STRENGTH, 22).req(DEXTERITY, 12).sprite("greatsword", "gold").lore("Raised high, it was a banner on its own."));
        add(w("hellbrand", "Hellbrand", GREATSWORD, RARE).phys(5.6).dmg(FIRE, 3.0).scale(STRENGTH, C).scale(FAITH, D)
                .req(STRENGTH, 20).req(FAITH, 10).art(Art.EMBER_CLEAVE).custom("deathbringer", "0=death_bringer_texture")
                .lore("Hammered on the anvils below the Nether's crust."));
        add(w("ashen_greatsword", "Morgrath's Ashen Greatsword", GREATSWORD, LEGENDARY).phys(6.0).dmg(FIRE, 3.6)
                .scale(STRENGTH, B).scale(FAITH, C).req(STRENGTH, 24).req(FAITH, 16).art(Art.EMBER_CLEAVE)
                .sprite("greatsword", "ash").lore("Morgrath's blade. It remembers the burning of the old crown."));
        add(w("lunar_bloomfang", "Lunar Bloomfang", GREATSWORD, LEGENDARY).phys(5.0).dmg(MAGIC, 3.4).scale(STRENGTH, C)
                .scale(DEXTERITY, D).scale(INTELLIGENCE, B).req(STRENGTH, 16).req(DEXTERITY, 10).req(INTELLIGENCE, 28)
                .status(FROST, 45).art(Art.LUNAR_WAVE).custom("moonrayys_sword", "0=lunar_text")
                .lore("Blooms only beneath a full moon.").lore("Taken from the dragon at the world's end."));

        // ---- Colossal swords
        add(w("ruinbreaker", "Ruinbreaker", COLOSSAL_SWORD, COMMON).scale(STRENGTH, B).scale(DEXTERITY, E)
                .req(STRENGTH, 26).req(DEXTERITY, 10).sprite("colossal", "stone").lore("Less a sword than a falling wall."));
        add(w("starfall_slab", "Starfall Slab", COLOSSAL_SWORD, EPIC).phys(8.0).dmg(MAGIC, 4.0).scale(STRENGTH, C)
                .scale(INTELLIGENCE, C).req(STRENGTH, 28).req(INTELLIGENCE, 18).art(Art.GRAVITY_WELL)
                .sprite("colossal", "star").lore("A slab of meteorite with a hilt bolted on."));
        add(w("crownsplitter", "Crownsplitter", COLOSSAL_SWORD, LEGENDARY).phys(12.5).scale(STRENGTH, A)
                .req(STRENGTH, 34).req(DEXTERITY, 12).art(Art.EARTHSHAKER).sprite("colossal", "crown")
                .lore("Varghast grafted it to his own arm.").lore("It split the crown that started it all."));

        // ---- Axes
        add(w("warlord_battleaxe", "Warlord's Battleaxe", AXE, COMMON).scale(STRENGTH, C).scale(DEXTERITY, D)
                .req(STRENGTH, 12).req(DEXTERITY, 8).sprite("axe", "iron").lore("Notched once for every war survived."));
        add(w("jawbone_axe", "Jawbone Axe", AXE, COMMON).phys(6.3).scale(STRENGTH, C).req(STRENGTH, 13)
                .sprite("axe_bone", "bone").lore("The teeth are still in it."));
        add(w("rimebite_axe", "Rimebite Axe", AXE, RARE).phys(5.0).dmg(MAGIC, 1.6).scale(STRENGTH, C).scale(INTELLIGENCE, D)
                .req(STRENGTH, 14).req(INTELLIGENCE, 10).status(FROST, 55).art(Art.RIMEFROST_STOMP).sprite("axe", "frost")
                .lore("Bites, and the cold bites after."));

        // ---- Greataxes
        add(w("executioner_greataxe", "Executioner's Greataxe", GREATAXE, COMMON).scale(STRENGTH, C).scale(DEXTERITY, D)
                .req(STRENGTH, 20).sprite("greataxe", "iron").lore("It has only ever had one job."));
        add(w("ravager_greataxe", "Ravager Greataxe", GREATAXE, RARE).phys(9.8).scale(STRENGTH, B).req(STRENGTH, 24)
                .custom("war_axe", "4=war_axe").lore("Built for chaos, it shatters shields and the arms behind them."));
        add(w("graft_axe", "Varghast's Graft-Axe", GREATAXE, LEGENDARY).phys(11.0).scale(STRENGTH, A).scale(DEXTERITY, D)
                .req(STRENGTH, 30).req(DEXTERITY, 10).art(Art.EARTHSHAKER).sprite("greataxe", "graft")
                .lore("Bone and steel and stolen limbs, fused into one hunger."));

        // ---- Hammers
        add(w("ashborn_club", "Ashborn Club", HAMMER, COMMON).phys(5.6).scale(STRENGTH, C).req(STRENGTH, 8)
                .art(Art.WAR_CRY).sprite("club", "wood").lore("Rise from ash with nothing but this. It is enough."));
        add(w("iron_warhammer", "Iron Warhammer", HAMMER, COMMON).phys(6.2).scale(STRENGTH, C).scale(DEXTERITY, E)
                .req(STRENGTH, 14).custom("hammer", "0=ham_texture").lore("Simple. Solid. Final."));
        add(w("penitent_mace", "Penitent's Mace", HAMMER, COMMON).phys(5.8).scale(STRENGTH, C).scale(DEXTERITY, D)
                .req(STRENGTH, 12).art(Art.SACRED_EDGE).sprite("mace", "iron").lore("Each blow is a prayer for forgiveness."));
        add(w("morning_star", "Morning Star", HAMMER, COMMON).scale(STRENGTH, C).scale(DEXTERITY, D).req(STRENGTH, 12)
                .status(BLEED, 35).sprite("morningstar", "iron").lore("Spiked to make every hit count twice."));
        add(w("bloomwarden_mace", "Bloomwarden's Mace", HAMMER, EPIC).phys(4.4).dmg(HOLY, 2.4).scale(STRENGTH, D)
                .scale(FAITH, B).req(STRENGTH, 12).req(FAITH, 20).art(Art.BLOOMWARD).custom("rosehammer", "0=pink_ham_texture")
                .lore("A gardener's maul. What it plants, heals."));

        // ---- Great hammers
        add(w("titans_gavel", "Titan's Gavel", GREAT_HAMMER, COMMON).phys(11.0).scale(STRENGTH, B).req(STRENGTH, 30)
                .sprite("greathammer", "stone").lore("Judgement, delivered by weight."));
        add(w("stoneheart_maul", "Stoneheart Maul", GREAT_HAMMER, RARE).scale(STRENGTH, B).req(STRENGTH, 26)
                .custom("earthhammer", "0=earth_ham_texture").lore("Forged from the heart of the earth; the ground answers it."));
        add(w("voidstar_hammer", "Voidstar Hammer", GREAT_HAMMER, LEGENDARY).phys(8.0).dmg(FIRE, 3.5).scale(STRENGTH, B)
                .scale(FAITH, C).req(STRENGTH, 28).req(FAITH, 14).art(Art.VOIDSTAR_FALL).custom("obshammer", "0=obs_ham_texture")
                .lore("Dark as the void and unyielding as obsidian.").lore("Morgrath called stars down with it."));

        // ---- Spears
        add(w("militia_spear", "Militia Spear", SPEAR, COMMON).scale(STRENGTH, C).scale(DEXTERITY, C).req(STRENGTH, 10)
                .req(DEXTERITY, 10).sprite("spear", "iron").lore("Handed to farmers when the walls began to fall."));
        add(w("hallowed_lance", "Hallowed Lance", SPEAR, RARE).phys(4.0).dmg(HOLY, 2.0).scale(STRENGTH, D).scale(DEXTERITY, C)
                .scale(FAITH, D).req(DEXTERITY, 12).req(FAITH, 14).sprite("spear", "holy")
                .lore("The undead flinch before its point."));
        add(w("stormcaller_pike", "Stormcaller's Pike", SPEAR, LEGENDARY).phys(4.2).dmg(LIGHTNING, 3.2).scale(STRENGTH, D)
                .scale(DEXTERITY, B).req(STRENGTH, 12).req(DEXTERITY, 20).art(Art.STORMCALL).sprite("pike", "storm")
                .lore("Kaelthar raised it and the sky obeyed."));

        // ---- Halberds
        add(w("sentinel_halberd", "Sentinel Halberd", HALBERD, COMMON).scale(STRENGTH, C).scale(DEXTERITY, C)
                .req(STRENGTH, 14).req(DEXTERITY, 12).sprite("halberd", "steel").lore("It stood guard long after the guard fell."));
        add(w("duskrider_glaive", "Duskrider Glaive", HALBERD, EPIC).phys(8.0).scale(STRENGTH, B).scale(DEXTERITY, C)
                .req(STRENGTH, 22).req(DEXTERITY, 12).sprite("glaive", "dusk")
                .lore("Carried by the rider who only comes at night."));
        add(w("wyrmfang_halberd", "Wyrmfang Halberd", HALBERD, LEGENDARY).phys(6.5).dmg(LIGHTNING, 2.6).scale(STRENGTH, C)
                .scale(DEXTERITY, C).req(STRENGTH, 20).req(DEXTERITY, 16).art(Art.STORMCALL).sprite("halberd", "wyrm")
                .lore("Shaped from the fang of Vaelorn, the wyrm-ravager."));

        // ---- Reapers
        add(w("grave_scythe", "Grave Scythe", REAPER, COMMON).scale(STRENGTH, D).scale(DEXTERITY, C).req(STRENGTH, 12)
                .req(DEXTERITY, 14).status(BLEED, 40).sprite("scythe", "iron").lore("The harvest is always ready."));
        add(w("winters_hook", "Winter's Reaping Hook", REAPER, RARE).phys(5.4).dmg(MAGIC, 1.6).scale(DEXTERITY, C)
                .scale(INTELLIGENCE, D).req(DEXTERITY, 14).req(INTELLIGENCE, 12).status(FROST, 55).sprite("scythe", "frost")
                .lore("It reaps the warmth first, then the rest."));
        add(w("gravewarden_scythe", "Gravewarden's Scythe", REAPER, LEGENDARY).phys(6.0).dmg(MAGIC, 2.5).scale(DEXTERITY, C)
                .scale(INTELLIGENCE, C).scale(ARCANE, C).req(DEXTERITY, 16).req(INTELLIGENCE, 16).req(ARCANE, 14)
                .status(BLEED, 30).art(Art.GRAVE_HARVEST).custom("deathscythe", "2=javion_scythe_texture")
                .lore("Wielded by those who transcended death.").lore("It returns the life it takes to you."));

        // ---- Claws and fists
        add(w("beast_claws", "Beast Claws", CLAW, COMMON).scale(STRENGTH, D).scale(DEXTERITY, C).req(DEXTERITY, 10)
                .status(BLEED, 30).sprite("claw", "bone").lore("Fight like the thing you hunt."));
        add(w("ironbound_knuckles", "Ironbound Knuckles", CLAW, COMMON).phys(3.4).scale(STRENGTH, C).req(STRENGTH, 10)
                .art(Art.WAR_CRY).sprite("knuckles", "iron").lore("No blade to break. No reach to speak of."));

        // ---- Whips
        add(w("thornlash", "Thornlash", WHIP, RARE).phys(3.6).scale(DEXTERITY, C).scale(ARCANE, D).req(DEXTERITY, 14)
                .status(BLEED, 50).sprite("whip", "thorn").lore("A vine that learned to bite."));

        // ---- Staves (sorcery catalysts)
        add(w("astral_staff", "Astral Staff", STAFF, COMMON).scale(INTELLIGENCE, C).req(INTELLIGENCE, 10)
                .spell(Spell.STARSHARD_BOLT).sprite("staff", "astral").lore("A first staff for those who read the stars."));
        add(w("stormglass_rod", "Stormglass Rod", STAFF, RARE).scale(INTELLIGENCE, C).scale(DEXTERITY, D)
                .req(INTELLIGENCE, 16).spell(Spell.STORM_BOLT).sprite("rod", "storm").lore("Glass that caught lightning and kept it."));
        add(w("crystal_spire_staff", "Crystal Spire Staff", STAFF, RARE).scale(INTELLIGENCE, B).req(INTELLIGENCE, 20)
                .spell(Spell.STARSHARD_BARRAGE).sprite("staff_crystal", "crystal").lore("Grown, not carved, in a cave of stars."));
        add(w("gravebound_staff", "Gravebound Staff", STAFF, RARE).scale(INTELLIGENCE, B).req(INTELLIGENCE, 22)
                .spell(Spell.GRAVITY_ORB).sprite("staff_orb", "void").lore("Heavy beyond its weight."));
        add(w("frostbound_staff", "Sylvara's Frostbound Staff", STAFF, LEGENDARY).scale(INTELLIGENCE, A).req(INTELLIGENCE, 28)
                .spell(Spell.FROST_COMET).sprite("staff", "frost").lore("The Frost Matron's scepter. Winter answers it."));
        add(w("abyssal_scepter", "Abyssal Scepter", STAFF, LEGENDARY).scale(INTELLIGENCE, S).req(INTELLIGENCE, 36)
                .spell(Spell.ABYSSAL_RAY).sprite("staff_orb", "abyss").lore("Stare into it and it stares back, then fires."));

        // ---- Seals (incantation catalysts)
        add(w("pilgrim_seal", "Pilgrim's Seal", SEAL, COMMON).scale(FAITH, C).req(FAITH, 10).spell(Spell.BLESSED_FLAME)
                .sprite("seal", "pilgrim").lore("Worn smooth by a thousand prayers on the road."));
        add(w("golden_rite_seal", "Seal of the Golden Rite", SEAL, RARE).scale(FAITH, B).req(FAITH, 20)
                .spell(Spell.MENDING_LIGHT).sprite("seal", "gold").lore("The rite heals; the faithful endure."));
        add(w("flame_prayer_seal", "Flame Prayer Seal", SEAL, RARE).scale(FAITH, B).req(FAITH, 18)
                .spell(Spell.PILLAR_OF_FLAME).sprite("seal", "flame").lore("Prayers that end in fire."));
        add(w("storm_totem", "Kaelthar's Storm Totem", SEAL, LEGENDARY).scale(FAITH, A).req(FAITH, 26)
                .spell(Spell.STORM_SPEAR).sprite("totem", "storm").lore("Kaelthar spoke to the storm through it."));
        add(w("bloodbloom_sigil", "Bloodbloom Sigil", SEAL, LEGENDARY).scale(FAITH, C).scale(ARCANE, A).req(FAITH, 14)
                .req(ARCANE, 24).spell(Spell.BLOOD_SIGIL).sprite("seal", "blood").lore("Ysolde's sigil. Its petals are wet."));
        add(w("dragonsoul_seal", "Dragonsoul Seal", SEAL, LEGENDARY).scale(FAITH, A).req(FAITH, 30)
                .spell(Spell.DRAGONFIRE).sprite("totem", "dragon").lore("A wyrm's heart, still beating, bound in gold."));

        ALL = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }

    private static WeaponSpec.Builder w(String id, String name, WeaponClass cls, Rarity rarity) {
        return new WeaponSpec.Builder(id, name, cls, rarity);
    }

    private static void add(WeaponSpec.Builder builder) {
        WeaponSpec spec = new WeaponSpec(builder);
        if (spec.model == null) {
            throw new IllegalStateException("Weapon " + spec.id + " has no model");
        }
        if (spec.cls.isCatalyst() && spec.spell == null) {
            throw new IllegalStateException("Catalyst " + spec.id + " has no spell");
        }
        if (BY_ID.put(spec.id, spec) != null) {
            throw new IllegalStateException("Duplicate weapon id " + spec.id);
        }
    }

    public static List<WeaponSpec> all() {
        return ALL;
    }

    public static WeaponSpec get(String id) {
        return BY_ID.get(id);
    }

    /** Implicit profile used when a vanilla or third-party weapon is held. */
    public static WeaponSpec vanilla(WeaponClass cls, double attackDamage) {
        WeaponSpec.Builder b = new WeaponSpec.Builder("vanilla_" + cls.name().toLowerCase(), "", cls, COMMON)
                .phys(attackDamage).scale(STRENGTH, D).scale(DEXTERITY, D).sprite("none", "none");
        if (cls == AXE || cls == GREATAXE || cls == HAMMER) {
            b.scale(STRENGTH, C).scale(DEXTERITY, E);
        } else if (cls == SPEAR) {
            b.scale(STRENGTH, D).scale(DEXTERITY, C);
        }
        return new WeaponSpec(b);
    }

    private Weapons() {
    }
}
