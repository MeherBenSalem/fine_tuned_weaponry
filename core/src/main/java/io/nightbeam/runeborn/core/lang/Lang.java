package io.nightbeam.runeborn.core.lang;

import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.art.Names;
import io.nightbeam.runeborn.core.art.Spell;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.mob.EliteAffix;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.DamageType;
import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.WeaponClass;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translation keys with their English text. {@code en_us.json} is generated from this class, so UI
 * code references keys through these constants and English stays in one place.
 */
public final class Lang {
    private static final Map<String, String> EN = new LinkedHashMap<>();

    public static String k(String key, String english) {
        EN.put(key, english);
        return key;
    }

    // ---- General UI
    public static final String MOD_NAME = k("runeborn.name", "Runeborn");
    public static final String TAB = k("itemGroup.runeborn.armaments", "Runeborn: Armaments");
    public static final String TAB_MATERIALS = k("itemGroup.runeborn.materials", "Runeborn: Relics & Materials");
    public static final String LEVEL = k("runeborn.ui.level", "Level");
    public static final String RUNES = k("runeborn.ui.runes", "Runes");
    public static final String RUNES_HELD = k("runeborn.ui.runes_held", "Runes Held");
    public static final String RUNES_NEEDED = k("runeborn.ui.runes_needed", "Runes Needed");
    public static final String CONFIRM = k("runeborn.ui.confirm", "Confirm");
    public static final String CANCEL = k("runeborn.ui.cancel", "Cancel");
    public static final String RESET = k("runeborn.ui.reset", "Reset");
    public static final String BACK = k("runeborn.ui.back", "Back");
    public static final String CLOSE = k("runeborn.ui.close", "Close");
    public static final String ATTRIBUTES = k("runeborn.ui.attributes", "Attributes");
    public static final String BASE_STATS = k("runeborn.ui.base_stats", "Base Stats");
    public static final String ATTACK_POWER = k("runeborn.ui.attack_power", "Attack Power");
    public static final String HEALTH = k("runeborn.ui.health", "HP");
    public static final String FOCUS = k("runeborn.ui.focus", "FP");
    public static final String STAMINA = k("runeborn.ui.stamina", "Stamina");
    public static final String POISE = k("runeborn.ui.poise", "Poise");
    public static final String DISCOVERY = k("runeborn.ui.discovery", "Discovery");
    public static final String WARDING = k("runeborn.ui.warding", "Magic Warding");
    public static final String BLESSING = k("runeborn.ui.blessing", "Healing Bonus");
    public static final String POTENCY = k("runeborn.ui.potency", "Status Potency");
    public static final String ROBUSTNESS = k("runeborn.ui.robustness", "Robustness");
    public static final String NOT_ENOUGH_RUNES = k("runeborn.ui.not_enough_runes", "Not enough runes");
    public static final String MAIN_HAND = k("runeborn.ui.main_hand", "Main Hand");
    public static final String NONE = k("runeborn.ui.none", "None");

    // ---- Origin screen
    public static final String ORIGIN_TITLE = k("runeborn.ui.origin.title", "Choose Your Origin");
    public static final String ORIGIN_SUBTITLE = k("runeborn.ui.origin.subtitle",
            "The crown is sundered. Rise, Runeborn, and choose who you were.");
    public static final String ORIGIN_BEGIN = k("runeborn.ui.origin.begin", "Rise");
    public static final String ORIGIN_WEAPON = k("runeborn.ui.origin.weapon", "Starting Armament");

    // ---- Shrine screen
    public static final String SHRINE = k("runeborn.ui.shrine", "Ember Shrine");
    public static final String SHRINE_LEVEL_UP = k("runeborn.ui.shrine.level_up", "Level Up");
    public static final String SHRINE_REST = k("runeborn.ui.shrine.rest", "Rest");
    public static final String SHRINE_TRAVEL = k("runeborn.ui.shrine.travel", "Travel");
    public static final String SHRINE_SMITHING = k("runeborn.ui.shrine.smithing", "Reinforce");
    public static final String SHRINE_INFUSE = k("runeborn.ui.shrine.infuse", "Infuse");
    public static final String SHRINE_FLASKS = k("runeborn.ui.shrine.flasks", "Flasks");
    public static final String SHRINE_MERCHANT = k("runeborn.ui.shrine.merchant", "Merchant");
    public static final String SHRINE_RELICS = k("runeborn.ui.shrine.relics", "Relics");
    public static final String SHRINE_SHARDS = k("runeborn.ui.shrine.shards", "Crown Shards");
    public static final String SHRINE_REBIRTH = k("runeborn.ui.shrine.rebirth", "Rebirth");
    public static final String SHRINE_LEAVE = k("runeborn.ui.shrine.leave", "Leave");
    public static final String REST_DESC = k("runeborn.ui.rest.desc",
            "Rest by the flame. Restores health, focus and flasks, and binds your return here.");
    public static final String REST_BUTTON = k("runeborn.ui.rest.button", "Rest at Shrine");
    public static final String REST_BOUND = k("runeborn.ui.rest.bound", "You will return here when you fall.");
    public static final String TRAVEL_EMPTY = k("runeborn.ui.travel.empty", "Kindle more shrines to travel between them.");
    public static final String TRAVEL_DISABLED = k("runeborn.ui.travel.disabled", "Fast travel is disabled on this server.");
    public static final String TRAVEL_HERE = k("runeborn.ui.travel.here", "You are here");
    public static final String SMITH_SELECT = k("runeborn.ui.smith.select", "Select an armament from your hotbar");
    public static final String SMITH_MAXED = k("runeborn.ui.smith.maxed", "Fully reinforced");
    public static final String SMITH_BUTTON = k("runeborn.ui.smith.button", "Reinforce");
    public static final String SMITH_NEEDS = k("runeborn.ui.smith.needs", "Requires");
    public static final String SMITH_NOT_WEAPON = k("runeborn.ui.smith.not_weapon", "This item cannot be reinforced.");
    public static final String INFUSE_LOCKED = k("runeborn.ui.infuse.locked", "Unique armaments cannot be infused.");
    public static final String INFUSE_NEEDS = k("runeborn.ui.infuse.needs", "Requires a whetstone");
    public static final String INFUSE_BUTTON = k("runeborn.ui.infuse.button", "Infuse");
    public static final String FLASK_DESC = k("runeborn.ui.flask.desc",
            "Divide your flask charges between Ember (health) and Azure (focus).");
    public static final String FLASK_POTENCY = k("runeborn.ui.flask.potency", "Potency");
    public static final String FLASK_CHARGES = k("runeborn.ui.flask.charges", "Charges");
    public static final String MERCHANT_BUY = k("runeborn.ui.merchant.buy", "Buy");
    public static final String MERCHANT_LOCKED = k("runeborn.ui.merchant.locked", "Fell more sovereigns to unlock");
    public static final String RELIC_EMPTY = k("runeborn.ui.relic.empty", "Carry a sovereign's relic to exchange it.");
    public static final String RELIC_BUTTON = k("runeborn.ui.relic.button", "Exchange");
    public static final String SHARD_EMPTY = k("runeborn.ui.shard.empty", "Claim crown shards from felled sovereigns.");
    public static final String SHARD_EQUIP = k("runeborn.ui.shard.equip", "Equip");
    public static final String SHARD_EQUIPPED = k("runeborn.ui.shard.equipped", "Equipped");
    public static final String REBIRTH_DESC = k("runeborn.ui.rebirth.desc",
            "Consume a Tear of Rebirth to redistribute every attribute point. Your level stays the same.");
    public static final String REBIRTH_NEEDS = k("runeborn.ui.rebirth.needs", "Requires a Tear of Rebirth");
    public static final String REBIRTH_POINTS = k("runeborn.ui.rebirth.points", "Points to assign");

    // ---- Character screen
    public static final String CHARACTER = k("runeborn.ui.character", "Status");
    public static final String CHARACTER_HINT = k("runeborn.ui.character.hint", "Level up by resting at an Ember Shrine.");
    public static final String FELLED = k("runeborn.ui.felled", "Sovereigns Felled");
    public static final String KILLS = k("runeborn.ui.kills", "Foes Slain");
    public static final String DEATHS = k("runeborn.ui.deaths", "Deaths");
    public static final String CHAMPIONS = k("runeborn.ui.champions", "Champions Slain");
    public static final String ORIGIN = k("runeborn.ui.origin", "Origin");
    public static final String EQUIPPED_SHARD = k("runeborn.ui.equipped_shard", "Crown Shard");
    public static final String LOST_RUNES = k("runeborn.ui.lost_runes", "Runes lost where you fell");

    // ---- Tooltip
    public static final String TT_ATTACK = k("runeborn.tooltip.attack", "Attack Rating");
    public static final String TT_SPELL_POWER = k("runeborn.tooltip.spell_power", "Spell Power");
    public static final String TT_SCALING = k("runeborn.tooltip.scaling", "Scaling");
    public static final String TT_REQUIRES = k("runeborn.tooltip.requires", "Requires");
    public static final String TT_PASSIVE = k("runeborn.tooltip.passive", "Passive");
    public static final String TT_ART = k("runeborn.tooltip.art", "Art");
    public static final String TT_SPELL = k("runeborn.tooltip.spell", "Spell");
    public static final String TT_FP = k("runeborn.tooltip.fp", "FP");
    public static final String TT_USE = k("runeborn.tooltip.use", "Use to activate");
    public static final String TT_UNMET = k("runeborn.tooltip.unmet", "Requirements not met: damage reduced");
    public static final String TT_SPEED = k("runeborn.tooltip.speed", "Attack Speed");
    public static final String TT_STAMINA = k("runeborn.tooltip.stamina", "Stamina per swing");
    public static final String TT_SHIFT = k("runeborn.tooltip.shift", "Hold Shift for details");
    public static final String TT_IMPLICIT = k("runeborn.tooltip.implicit", "Scales with your attributes");

    // ---- Banners and messages
    public static final String B_KINDLED = k("runeborn.banner.kindled", "Shrine Kindled");
    public static final String B_DIED = k("runeborn.banner.died", "You Died");
    public static final String B_FELLED = k("runeborn.banner.felled", "Sovereign Felled");
    public static final String B_RECOVERED = k("runeborn.banner.recovered", "Runes Recovered");
    public static final String B_RISE = k("runeborn.banner.rise", "Rise, Runeborn");
    public static final String B_CHAMPION = k("runeborn.banner.champion", "Champion Slain");
    public static final String B_LEVEL = k("runeborn.banner.level", "Level Up");
    public static final String B_SHARD = k("runeborn.banner.shard", "Crown Shard Claimed");
    public static final String MSG_NO_FP = k("runeborn.msg.no_fp", "Not enough FP");
    public static final String MSG_NO_STAMINA = k("runeborn.msg.no_stamina", "Too exhausted");
    public static final String MSG_COOLDOWN = k("runeborn.msg.cooldown", "Not ready yet");
    public static final String MSG_REQUIREMENT = k("runeborn.msg.requirement", "You lack the attributes to wield this");
    public static final String MSG_SPELL_REQ = k("runeborn.msg.spell_requirement", "Your mind or faith is too weak for this spell");
    public static final String MSG_FLASK_EMPTY = k("runeborn.msg.flask_empty", "The flask is empty. Rest at a shrine.");
    public static final String MSG_NO_ORIGIN = k("runeborn.msg.no_origin", "Choose your origin first.");
    public static final String MSG_TRAVEL_FAIL = k("runeborn.msg.travel_fail", "That shrine has gone cold.");
    public static final String MSG_TOO_FAR = k("runeborn.msg.too_far", "You are too far from the shrine.");
    public static final String MSG_BOSS_ACTIVE = k("runeborn.msg.boss_active", "A sovereign already walks here.");
    public static final String MSG_SEED = k("runeborn.msg.seed", "Your flasks gain a charge.");
    public static final String MSG_SEED_MAX = k("runeborn.msg.seed_max", "Your flasks can hold no more.");
    public static final String MSG_TEAR = k("runeborn.msg.tear", "Your flasks grow more potent.");
    public static final String MSG_TEAR_MAX = k("runeborn.msg.tear_max", "Your flasks are as potent as they can be.");
    public static final String MSG_SHARD_KNOWN = k("runeborn.msg.shard_known", "You already hold this shard.");
    public static final String MSG_RUNES_LOST = k("runeborn.msg.runes_lost", "Your runes lie where you fell.");
    public static final String NO_REST = k("runeborn.ui.rest.enemies", "You cannot rest while enemies are near.");
    public static final String MSG_NOT_SUMMONABLE = k("runeborn.msg.no_space", "There is no room for a sovereign here.");
    public static final String KEY_CATEGORY = k("key.categories.runeborn", "Runeborn");
    public static final String KEY_DODGE = k("key.runeborn.dodge", "Dodge Roll");
    public static final String KEY_CHARACTER = k("key.runeborn.character", "Status Screen");
    public static final String KEY_ART = k("key.runeborn.art", "Weapon Art");
    public static final String BLOCK_SHRINE = k("block.runeborn.ember_shrine", "Ember Shrine");
    public static final String SHRINE_KINDLE_HINT = k("runeborn.msg.kindle", "Right-click to kindle");

    static {
        for (Attr a : Attr.ALL) {
            k(a.langKey(), Names.title(a.name()));
            k(a.shortKey(), a.code.substring(0, 1).toUpperCase() + a.code.substring(1));
        }
        for (DamageType t : DamageType.ALL) {
            k(t.langKey(), Names.title(t.name()));
        }
        for (StatusType s : StatusType.ALL) {
            k(s.langKey(), switch (s) {
                case BLEED -> "Hemorrhage";
                case FROST -> "Frostbite";
                case POISON -> "Poison";
                case BLIGHT -> "Blight";
            });
        }
        for (Rarity r : Rarity.values()) {
            k(r.langKey(), Names.title(r.name()));
        }
        for (Affinity a : Affinity.ALL) {
            k(a.langKey(), a == Affinity.STANDARD ? "Standard" : Names.title(a.name()));
        }
        for (WeaponClass c : WeaponClass.values()) {
            k(c.langKey(), switch (c) {
                case CLAW -> "Fist";
                case SEAL -> "Sacred Seal";
                case STAFF -> "Glintstone Staff";
                default -> Names.title(c.name());
            });
        }
        for (Art a : Art.values()) {
            k(a.langKey(), a.englishName());
            k(a.descKey(), a.description);
        }
        for (Spell s : Spell.values()) {
            k(s.langKey(), s.englishName());
            k(s.descKey(), s.description);
        }
        for (Origin o : Origin.ALL) {
            k(o.nameKey(), o.displayName);
            k(o.descKey(), o.description);
        }
        for (EliteAffix a : EliteAffix.ALL) {
            k(a.langKey(), a.title);
        }
        for (CrownShard s : CrownShard.values()) {
            k(s.descKey(), s.description);
        }
        for (BossSpec b : Bosses.all()) {
            k(b.nameKey(), b.name());
            if (!b.title().isEmpty()) {
                k(b.titleKey(), b.title());
            }
        }
        for (WeaponSpec w : Weapons.all()) {
            k(w.langKey(), w.name);
            for (int i = 0; i < w.lore.size(); i++) {
                k(w.langKey() + ".lore" + i, w.lore.get(i));
            }
        }
        for (MaterialSpec m : Materials.all()) {
            k(m.langKey(), m.name());
            for (int i = 0; i < m.lore().size(); i++) {
                k(m.langKey() + ".lore" + i, m.lore().get(i));
            }
        }
    }

    public static Map<String, String> english() {
        return Collections.unmodifiableMap(EN);
    }

    public static String en(String key) {
        return EN.getOrDefault(key, key);
    }

    private Lang() {
    }
}
