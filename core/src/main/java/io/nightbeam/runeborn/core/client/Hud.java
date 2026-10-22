package io.nightbeam.runeborn.core.client;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.config.ClientConfig;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Theme;
import io.nightbeam.runeborn.core.ui.Ui;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;

/**
 * The in-game overlay: health, focus and stamina bars that grow with your stats, a rune counter,
 * flask and art readout, enemy and boss bars, and the big announcement banners.
 */
public final class Hud {
    private static float lastTime;
    /** HUD frames drawn so far, and the frame during which a full-screen menu last drew. */
    private static long frame;
    private static long menuFrame = -10;
    /** Right edge of the stat bars last frame, so centred bars can avoid them. */
    private static int barsRight;

    /** Called by full-screen menus each frame; the HUD stays hidden while one is showing. */
    public static void hideForMenu() {
        menuFrame = frame;
    }

    public static void render(Canvas c, ClientHost host) {
        float now = c.time();
        float dt = Math.max(0, Math.min(5, now - lastTime));
        lastTime = now;
        ClientConfig cfg = ClientConfig.INSTANCE;
        frame++;
        // Menus draw after the HUD, so a menu seen during the previous frame is still open.
        if (!cfg.hudEnabled || !ClientState.ready() || menuFrame >= frame - 1) {
            return;
        }
        PlayerData d = ClientState.data();

        flash(c, now);
        c.push();
        c.translate(cfg.hudOffsetX, cfg.hudOffsetY);
        if (cfg.hudScale != 1f) {
            c.scale(cfg.hudScale);
        }
        bars(c, host, d, dt, cfg);
        c.pop();
        runes(c, host, d, now);
        if (cfg.showEquipment) {
            equipment(c, host, d);
        }
        int top = 6 + host.vanillaBossBars() * 19;
        int centredLeft = c.width() / 2 - bossWidth(c) / 2;
        if (centredLeft < barsRight + 8) {
            top = Math.max(top, 44);
        }
        if (cfg.showBossBars) {
            top = bosses(c, now, dt, top);
        }
        if (cfg.showTargetBar) {
            target(c, now, dt, top);
        }
        if (cfg.showBanners) {
            banner(c, now);
        }
    }

    private static void bars(Canvas c, ClientHost host, PlayerData d, float dt, ClientConfig cfg) {
        float maxHp = Math.max(1, host.maxHealth());
        float hp = Ui.clamp01(host.health() / maxHp);
        float maxFp = (float) d.maxFocus();
        float fp = Ui.clamp01(ClientState.fp / Math.max(1, maxFp));
        float maxSt = (float) d.maxStamina();
        float st = Ui.clamp01(ClientState.stamina / Math.max(1, maxSt));

        ClientState.hpChip = chip(ClientState.hpChip, hp, dt);
        ClientState.fpChip = chip(ClientState.fpChip, fp, dt);
        ClientState.staminaChip = chip(ClientState.staminaChip, st, dt);

        int x = 10;
        int y = 10;
        int wHp = Math.round(Math.min(170, 92 + (maxHp - 20) * 2.0f));
        int wFp = Math.round(Math.min(190, 70 + (maxFp - 80) * 0.3f));
        int wSt = Math.round(Math.min(170, 70 + (maxSt - 98) * 1.3f));

        Ui.bar(c, x, y, wHp, 6, hp, ClientState.hpChip, Theme.HP, Theme.HP_LIGHT);
        barsRight = x + Math.max(wHp, Math.max(wFp, wSt)) + 4;
        int armor = host.armor();
        if (armor > 0) {
            int ax = x + wHp + 5;
            shield(c, ax, y - 1);
            c.text(String.valueOf(armor), ax + 9, y - 1, Theme.TEXT_DIM, true);
            barsRight = Math.max(barsRight, ax + 9 + c.textWidth(String.valueOf(armor)));
        }
        if (host.absorption() > 0) {
            float abs = Ui.clamp01(host.absorption() / maxHp);
            c.fill(x, y, x + Math.round(wHp * abs), y + 2, 0xFFE8C870);
        }
        Ui.bar(c, x, y + 10, wFp, 4, fp, ClientState.fpChip, Theme.FP, Theme.FP_LIGHT);
        if (ClientState.view.staminaEnabled) {
            boolean low = st < 0.2f;
            int fill = low ? Theme.mix(Theme.STAMINA, 0xFFB8A23A, 0.5f + 0.5f * (float) Math.sin(c.time() * 0.6f)) : Theme.STAMINA;
            Ui.bar(c, x, y + 18, wSt, 4, st, ClientState.staminaChip, fill, Theme.STAMINA_LIGHT);
        }
        if (cfg.showBarNumbers) {
            int s = Rb.DISPLAY_SCALE;
            c.text(Math.round(host.health() * s) + " / " + Math.round(maxHp * s), x + wHp + 5, y - 1, Theme.TEXT, true);
            c.text(Math.round(ClientState.fp) + " / " + Math.round(maxFp), x + wFp + 5, y + 8, Theme.BLUE, true);
        }
    }

    /** Tiny shield glyph for the armor readout. */
    private static void shield(Canvas c, int x, int y) {
        int edge = 0xFF9A9384;
        int fill = 0xFF4E4A44;
        c.fill(x, y, x + 7, y + 1, edge);
        c.fill(x, y + 1, x + 1, y + 5, edge);
        c.fill(x + 6, y + 1, x + 7, y + 5, edge);
        c.fill(x + 1, y + 1, x + 6, y + 5, fill);
        c.fill(x + 1, y + 5, x + 6, y + 6, edge);
        c.fill(x + 2, y + 6, x + 5, y + 7, edge);
        c.fill(x + 3, y + 7, x + 4, y + 8, edge);
    }

    private static float chip(float chip, float value, float dt) {
        if (value >= chip) {
            return value;
        }
        return Math.max(value, chip - 0.012f * dt);
    }

    private static void runes(Canvas c, ClientHost host, PlayerData d, float now) {
        String amount = Runes.format(Math.round(ClientState.shownRunes));
        int w = c.textWidth(amount) + 26;
        int x = c.width() - w - 8;
        int y = c.height() - 24;
        Ui.softPanel(c, x, y, w, 16);
        Ui.runeGlyph(c, x + 9, y + 8);
        c.text(amount, x + 18, y + 4, Theme.GOLD_BRIGHT, true);

        float since = now - ClientState.gainTime;
        if (ClientState.pendingGain > 0 && since < 60) {
            float a = since < 40 ? 1 : 1 - (since - 40) / 20f;
            String gain = "+" + Runes.format(ClientState.pendingGain);
            int gy = y - 12 - Math.round(Math.min(since, 10) * 0.4f);
            Ui.right(c, gain, x + w - 4, gy, Theme.alpha(Theme.GOLD, a), true);
        }
        if (d.lost != null && d.lost.dimension.equals(host.dimension())) {
            double dx = d.lost.x - host.x();
            double dz = d.lost.z - host.z();
            int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
            String lost = "◆ " + Runes.compact(d.lost.amount) + " · " + dist + "m";
            Ui.right(c, lost, c.width() - 10, y - 26, 0xFF8FD1A0, true);
        }
    }

    private static void equipment(Canvas c, ClientHost host, PlayerData d) {
        int hotbarLeft = c.width() / 2 - 91;
        int x = hotbarLeft - 29 - 70;
        int y = c.height() - 22;
        if (x < 4) {
            return;
        }
        Ui.softPanel(c, x, y, 64, 20);
        c.item(Rb.id("ember_flask"), x + 2, y + 2);
        c.text(String.valueOf(ClientState.emberLeft), x + 19, y + 11, ClientState.emberLeft > 0 ? Theme.TEXT : Theme.BAD, true);
        c.item(Rb.id("azure_flask"), x + 32, y + 2);
        c.text(String.valueOf(ClientState.azureLeft), x + 49, y + 11, ClientState.azureLeft > 0 ? Theme.TEXT : Theme.BAD, true);

        ItemView held = host.slot(-1);
        WeaponSpec spec = held.weapon();
        if (spec != null) {
            String label;
            int cost;
            if (spec.art == Art.SPELLCAST && spec.spell != null) {
                label = c.tr(spec.spell.langKey());
                cost = spec.spell.fp;
            } else {
                label = c.tr(spec.art.langKey());
                cost = spec.art.fp;
            }
            boolean affordable = ClientState.fp >= cost;
            String line = label + " §7" + cost + " " + c.tr(Lang.FOCUS);
            int lw = c.textWidth(line);
            int lx = x + 64 - lw;
            if (lx < 4) {
                lx = 4;
            }
            c.text(line, lx, y - 11, affordable ? Theme.GOLD : Theme.TEXT_FAINT, true);
        }
    }

    private static int bosses(Canvas c, float now, float dt, int top) {
        if (ClientState.bosses.isEmpty() || now - ClientState.bossesTime > 40) {
            return top;
        }
        int w = bossWidth(c);
        int x = c.width() / 2 - w / 2;
        int y = top + 10;
        for (Packets.BossBar bar : ClientState.bosses) {
            float ratio = bar.maxHealth() <= 0 ? 0 : Ui.clamp01(bar.health() / bar.maxHealth());
            float chip = ClientState.bossChips.getOrDefault(bar.entityId(), ratio);
            chip = chip(chip, ratio, dt * 0.6f);
            ClientState.bossChips.put(bar.entityId(), chip);
            c.title(bar.name(), x, y - 10, Theme.TEXT, 1.0f);
            if (bar.phase() > 1) {
                Ui.right(c, "§c✦", x + w, y - 10, Theme.BAD, true);
            }
            Ui.bar(c, x, y, w, 5, ratio, chip, Theme.HP, Theme.HP_LIGHT);
            y += 26;
        }
        return y - 4;
    }

    private static int bossWidth(Canvas c) {
        return Math.min(340, Math.round(c.width() * 0.5f));
    }

    private static void target(Canvas c, float now, float dt, int top) {
        Packets.Target t = ClientState.target;
        if (t == null) {
            return;
        }
        if (now - ClientState.bossesTime < 40) {
            for (Packets.BossBar bar : ClientState.bosses) {
                if (bar.entityId() == t.entityId()) {
                    return;
                }
            }
        }
        float since = now - ClientState.targetTime;
        if (since > 90) {
            return;
        }
        float a = since < 70 ? 1 : 1 - (since - 70) / 20f;
        float ratio = t.maxHealth() <= 0 ? 0 : Ui.clamp01(t.health() / t.maxHealth());
        ClientState.targetChip = chip(ClientState.targetChip, ratio, dt);
        int w = 150;
        int x = c.width() / 2 - w / 2;
        int y = top + 12;
        String name = (t.elite() ? "§6" : "") + t.name();
        String lvl = "Lv " + t.level();
        c.text(name, x, y - 10, Theme.alpha(t.elite() ? Theme.GOLD : Theme.TEXT, a), true);
        Ui.right(c, lvl, x + w, y - 10, Theme.alpha(Theme.TEXT_DIM, a), true);
        Ui.bar(c, x, y, w, 3, ratio, ClientState.targetChip, Theme.HP, Theme.HP_LIGHT);
        if (t.lastDamage() > 0 && since < 40) {
            String dmg = String.valueOf(Math.round(t.lastDamage() * Rb.DISPLAY_SCALE));
            c.text(dmg, x + w + 6, y - 3, Theme.alpha(Theme.GOLD_BRIGHT, a), true);
        }
        int sy = y + 5;
        for (StatusType s : StatusType.ALL) {
            float v = t.status()[s.ordinal()];
            if (v > 0.01f) {
                c.fill(x, sy, x + Math.round(w * Ui.clamp01(v)), sy + 1, Theme.alpha(Theme.opaque(s.color), a));
                sy += 2;
            }
        }
    }

    private static void banner(Canvas c, float now) {
        Packets.BannerMsg b = ClientState.banner;
        if (b == null || b.kind() == Packets.Banner.YOU_DIED) {
            return; // drawn by the death screen instead
        }
        float t = now - ClientState.bannerTime;
        float total = b.kind() == Packets.Banner.YOU_DIED ? 110 : 80;
        if (t > total) {
            return;
        }
        drawBanner(c, b, t, total);
    }

    /** Also used on the death screen. */
    public static void drawBanner(Canvas c, Packets.BannerMsg b, float t, float total) {
        float a;
        if (t < 10) {
            a = Ui.ease(t / 10f);
        } else if (t > total - 20) {
            a = Ui.ease((total - t) / 20f);
        } else {
            a = 1;
        }
        boolean death = b.kind() == Packets.Banner.YOU_DIED;
        boolean small = b.kind() == Packets.Banner.RUNES_RECOVERED || b.kind() == Packets.Banner.LEVEL_UP
                || b.kind() == Packets.Banner.CHAMPION_SLAIN;
        int cy = Math.round(c.height() * (death ? 0.40f : 0.36f));
        int band = small ? 30 : 46;
        int edge = 0x00000000;
        int mid = Theme.alpha(0xD0000000, a);
        Ui.hgradient(c, 0, cy - band / 2, c.width() / 2, cy + band / 2, edge, mid);
        Ui.hgradient(c, c.width() / 2, cy - band / 2, c.width(), cy + band / 2, mid, edge);

        int color = death ? 0xFFB8261E : switch (b.kind()) {
            case RUNES_RECOVERED -> 0xFF9FD8A8;
            case BOSS_APPEARS -> 0xFFE06A5A;
            default -> Theme.GOLD_BRIGHT;
        };
        float zoom = 1 + 0.06f * Ui.clamp01(t / total);
        float scale = (small ? 1.7f : death ? 3.4f : 2.8f) * zoom;
        String title = b.title().toUpperCase();
        float th = 9 * scale;
        Ui.titleCentered(c, title, c.width() / 2, cy - th / 2 - (b.subtitle().isEmpty() ? 0 : 4), Theme.alpha(color, a), scale);
        if (!b.subtitle().isEmpty()) {
            Ui.centered(c, b.subtitle(), c.width() / 2, Math.round(cy + th / 2 - 2), Theme.alpha(Theme.TEXT, a * 0.9f), true);
        }
        if (!small) {
            int rw = Math.min(320, c.width() / 2);
            Ui.rule(c, c.width() / 2 - rw / 2, cy + band / 2 - 3, rw, Theme.alpha(color, a * 0.6f));
        }
    }

    /**
     * Death-screen banner: an opaque band over the vanilla "You Died!" title (drawn at y 60-78 at
     * 2x scale), so the cause of death and the buttons stay readable below it.
     */
    public static void drawDeathBanner(Canvas c, float t) {
        float a = Ui.ease(Math.min(1, t / 14f));
        // Covers vanilla's own title (y 60-78) and leaves the cause of death below (y 85) readable.
        int cy = 66;
        int band = 34;
        int mid = Theme.alpha(0xF2000000, a);
        Ui.hgradient(c, 0, cy - band / 2, c.width() / 2, cy + band / 2, 0x00000000, mid);
        Ui.hgradient(c, c.width() / 2, cy - band / 2, c.width(), cy + band / 2, mid, 0x00000000);
        c.fill(c.width() / 2 - 150, cy - band / 2, c.width() / 2 + 150, cy + band / 2, Theme.alpha(0xF2000000, a));
        float scale = 3.2f * (1 + 0.04f * Ui.clamp01(t / 80f));
        Ui.titleCentered(c, "YOU DIED", c.width() / 2, cy - 9 * scale / 2 + 1, Theme.alpha(0xFFB8261E, a), scale);
    }

    private static void flash(Canvas c, float now) {
        float t = now - ClientState.flashTime;
        if (t < 0 || t > 12) {
            return;
        }
        float a = 0.35f * (1 - t / 12f);
        c.fill(0, 0, c.width(), c.height(), Theme.alpha(0xFF000000 | ClientState.flashColor, a));
    }

    private Hud() {
    }
}
