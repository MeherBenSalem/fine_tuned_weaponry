package io.nightbeam.runeborn.client;

import com.mojang.datafixers.util.Pair;
import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.client.ClientNet;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.screen.CharacterScreen;
import io.nightbeam.runeborn.core.client.screen.CodexScreen;
import io.nightbeam.runeborn.core.client.screen.ShrineScreen;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.mc.BossManager;
import io.nightbeam.runeborn.mc.RbItems;
import io.nightbeam.runeborn.mc.RbServer;
import io.nightbeam.runeborn.mc.Shrines;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Scripted capture of listing screenshots ({@code RUNEBORN_SHOWCASE=1}): finds a scenic biome,
 * kindles a shrine at dusk, walks through the menus and poses Sovereigns, then quits. Run it on a
 * normal (not flat) world at 1920x1080, GUI scale 3.
 */
public final class Showcase {
    private static int step;
    private static int wait;
    private static BlockPos shrine;
    private static LivingEntity boss;
    private static volatile String kindledName = "Ember Shrine";

    public static boolean enabled() {
        return System.getenv("RUNEBORN_SHOWCASE") != null;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !ClientState.ready()) {
            return;
        }
        quiet(mc);
        if (wait > 0) {
            wait--;
            return;
        }
        IntegratedServer server = mc.getSingleplayerServer();
        if (server == null) {
            return;
        }
        UUID id = mc.player.getUUID();
        switch (step++) {
            case 0 -> {
                // Park the cursor in a corner so no menu row or stat shows a hover state.
                GLFW.glfwSetCursorPos(mc.getWindow().getWindow(), 2, 2);
                RbClient.HOST.send(Packets.chooseOrigin("ronin"));
                server.execute(() -> scene(server, id));
                wait = 260;
            }
            case 1 -> {
                server.execute(() -> kindle(server, id));
                wait = 6;
            }
            case 2 -> {
                mc.setScreen(null); // kindling opens the shrine menu; this shot is of the world
                wait = 22;
            }
            case 3 -> UiTest.shot("kindled");
            case 4 -> {
                mc.options.hideGui = true; // also lets the advancement toast run out
                wait = 140;
            }
            case 5 -> {
                UiTest.shot("shrine");
                mc.options.hideGui = false;
                server.execute(() -> UiTest.prepare(server, id));
                wait = 40;
            }
            case 6 -> {
                RbClient.HOST.open(new ShrineScreen(new Packets.Open(Packets.Screen.SHRINE, shrineName(), "minecraft:overworld",
                        shrine.getX(), shrine.getY(), shrine.getZ())));
                wait = 10;
            }
            case 7 -> {
                quiet(mc);
                UiTest.click(0, 0);
                wait = 6;
            }
            case 8 -> {
                UiTest.shot(ras() ? "levelup" : "rest");
                if (ras()) {
                    step = 100;
                }
            }
            case 9 -> menu(() -> UiTest.tab(3));
            case 10 -> UiTest.shot("smithing");
            case 11 -> menu(() -> {
                pickSlot(1); // the claymore: unique armaments cannot be infused
                UiTest.tab(4);
            });
            case 12 -> menu(() -> clickContent(108, 94)); // Fire, so the capture compares two affinities
            case 13 -> UiTest.shot("infuse");
            case 14 -> menu(() -> UiTest.tab(6));
            case 15 -> UiTest.shot("merchant");
            case 16 -> menu(() -> RbClient.HOST.open(new CharacterScreen()));
            case 17 -> UiTest.shot("status");
            case 18 -> menu(() -> {
                RbClient.HOST.open(new CodexScreen(null));
                UiTest.codexChapter(1);
            });
            case 19 -> UiTest.shot("codex-shrines");
            case 20 -> menu(() -> UiTest.codexChapter(8));
            case 21 -> UiTest.shot("codex-sovereigns");
            case 22 -> menu(() -> mc.setScreen(new UiTest.TooltipScreen()));
            case 23 -> UiTest.shot("tooltips");
            case 24 -> {
                mc.setScreen(null);
                server.execute(() -> pose(server, id, "morgrath", 7));
                wait = 90;
            }
            case 25 -> {
                quiet(mc);
                server.execute(() -> aimAndFreeze(server, id));
                wait = 4;
            }
            case 26 -> UiTest.shot("boss-morgrath-hud");
            case 27 -> {
                mc.options.hideGui = true;
                wait = 4;
            }
            case 28 -> UiTest.shot("boss-morgrath");
            case 29 -> {
                mc.options.hideGui = false;
                server.execute(() -> unpose(server));
                server.execute(() -> pose(server, id, "kaelthar", 7));
                wait = 90;
            }
            case 30 -> {
                quiet(mc);
                server.execute(() -> aimAndFreeze(server, id));
                wait = 4;
            }
            case 31 -> UiTest.shot("boss-kaelthar-hud");
            case 32 -> {
                server.execute(() -> unpose(server));
                server.execute(() -> pose(server, id, "sylvara", 7));
                wait = 90;
            }
            case 33 -> {
                quiet(mc);
                server.execute(() -> aimAndFreeze(server, id));
                wait = 4;
            }
            case 34 -> {
                mc.options.hideGui = true;
                wait = 4;
            }
            case 35 -> UiTest.shot("boss-sylvara");
            case 36 -> {
                mc.options.hideGui = false;
                server.execute(() -> unpose(server));
                ClientState.banner = new Packets.BannerMsg(Packets.Banner.SOVEREIGN_FELLED, "Sovereign Felled",
                        "Sylvara, Frost Matron");
                ClientState.bannerTime = ClientNet.clock;
                wait = 30;
            }
            case 37 -> UiTest.shot("felled");
            case 38 -> {
                Runeborn.LOG.info("RUNEBORN SHOWCASE DONE");
                mc.stop();
            }
            // RPG Attribute System pass: its Level Up screen and both HUDs together.
            case 100 -> {
                quiet(mc);
                mc.setScreen(null);
                for (KeyMapping key : mc.options.keyMappings) {
                    if (key.getName().equals("key.rpg_attribute_system.open_stats_menu_keybind")) {
                        KeyMapping.click(key.getDefaultKey());
                    }
                }
                wait = 30;
            }
            case 101 -> UiTest.shot("ras-screen");
            case 102 -> {
                mc.setScreen(null);
                server.execute(() -> pose(server, id, "morgrath", 7));
                wait = 90;
            }
            case 103 -> {
                quiet(mc);
                server.execute(() -> aimAndFreeze(server, id));
                wait = 4;
            }
            case 104 -> UiTest.shot("ras-hud");
            case 105 -> {
                Runeborn.LOG.info("RUNEBORN SHOWCASE DONE");
                mc.stop();
            }
            default -> {
            }
        }
    }

    /** {@code RUNEBORN_SHOWCASE=ras} captures the RPG Attribute System screens instead of the full set. */
    private static boolean ras() {
        return "ras".equals(System.getenv("RUNEBORN_SHOWCASE"));
    }

    /**
     * Runs every tick: clears toasts and chat (vanilla recipe and advancement notices) so they never
     * slide in, and parks the game's cursor in a corner so no row shows a hover state. Opening a
     * screen recentres the cursor, and warping the OS cursor does not reach the game, so this sets
     * the mouse handler's position directly (dev-only script, named mappings).
     */
    private static void quiet(Minecraft mc) {
        mc.getToasts().clear();
        mc.gui.getChat().clearMessages(false);
        try {
            for (String name : new String[]{"xpos", "ypos"}) {
                Field field = MouseHandler.class.getDeclaredField(name);
                field.setAccessible(true);
                field.setDouble(mc.mouseHandler, 2);
            }
        } catch (ReflectiveOperationException e) {
            GLFW.glfwSetCursorPos(mc.getWindow().getWindow(), 2, 2);
        }
    }

    /** Runs a menu action and waits for the next frames to draw it. */
    private static void menu(Runnable action) {
        quiet(Minecraft.getInstance());
        action.run();
        wait = 8;
    }

    /** Picks hotbar slot {@code index} in the shrine's armament row. */
    private static void pickSlot(int index) {
        Minecraft mc = Minecraft.getInstance();
        int contentW = Math.max(220, Math.min(310, mc.getWindow().getGuiScaledWidth() - 112 - 36));
        int gap = Math.max(1, (contentW - 20 - 20 * 9) / 8);
        clickContent(index * (20 + gap) + 10, 55);
    }

    /** Clicks at an offset from the shrine content column's left edge and the panel top. */
    private static void clickContent(int dx, int dy) {
        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int panelH = Math.min(h - 62, 214);
        int top = Math.max(40, h / 2 - panelH / 2 + 14);
        int contentW = Math.max(220, Math.min(310, w - 112 - 36));
        int x = w / 2 - (112 + 8 + contentW) / 2 + 112 + 8 + 10;
        if (mc.screen != null) {
            mc.screen.mouseClicked(x + dx, top + dy, 0);
        }
    }

    private static String shrineName() {
        return kindledName;
    }

    /** Moves the player to a cherry grove (or another scenic biome) at sunset with clear skies. */
    private static void scene(IntegratedServer server, UUID id) {
        UiTest.reset(server, id);
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p == null) {
            return;
        }
        ServerLevel level = server.overworld();
        level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
        level.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
        level.setDayTime(12_700);
        level.setWeatherParameters(24_000, 0, false, false);
        // A sunset-facing clearing in the cherry grove of seed "runeborn"; other seeds search for one.
        BlockPos from = new BlockPos(847, 100, 607);
        Pair<BlockPos, Holder<Biome>> found = null;
        for (var biome : java.util.List.of(Biomes.CHERRY_GROVE, Biomes.MEADOW, Biomes.FLOWER_FOREST, Biomes.PLAINS)) {
            found = level.findClosestBiome3d(h -> h.is(biome), from, 4000, 8, 64);
            if (found != null) {
                break;
            }
        }
        BlockPos at = found == null ? from : found.getFirst();
        level.getChunk(at.getX() >> 4, at.getZ() >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at.getX(), at.getZ());
        p.teleportTo(level, at.getX() + 0.5, y, at.getZ() + 0.5, 90f, 6f); // west, into the sunset
        Runeborn.LOG.info("[showcase] scene at {} {} {} ({})", at.getX(), y, at.getZ(),
                found == null ? "spawn" : found.getSecond().unwrapKey().map(k -> k.location().toString()).orElse("?"));
    }

    /** Places a shrine a few blocks ahead of the player and kindles it. */
    private static void kindle(IntegratedServer server, UUID id) {
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p == null || RbItems.shrineBlock == null) {
            return;
        }
        ServerLevel level = (ServerLevel) p.level();
        Vec3 ahead = p.position().add(Vec3.directionFromRotation(0, p.getYRot() + 28).scale(4));
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(ahead.x), Mth.floor(ahead.z));
        shrine = BlockPos.containing(ahead.x, y, ahead.z);
        level.setBlockAndUpdate(shrine, RbItems.shrineBlock.defaultBlockState());
        // Look west with the shrine right of centre, so the sunset fills the rest of the frame.
        p.teleportTo(level, p.getX(), p.getY(), p.getZ(), 90f + 14f, 10f);
        Shrines.use(p, shrine);
        var d = RbServer.data(p);
        kindledName = d.shrines.get(d.shrines.size() - 1).name;
        d.runes = 184_250;
    }

    /** Spawns a Sovereign {@code distance} blocks ahead, facing the player. */
    private static void pose(IntegratedServer server, UUID id, String bossId, double distance) {
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p == null) {
            return;
        }
        ServerLevel level = (ServerLevel) p.level();
        Vec3 ahead = p.position().add(Vec3.directionFromRotation(0, 90).scale(distance));
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(ahead.x), Mth.floor(ahead.z));
        // Posed, not fighting: no target and no AI, so it holds still while the spawn effects and
        // its name banner fade.
        boss = BossManager.spawn(level, Bosses.get(bossId), new Vec3(ahead.x, y, ahead.z), null);
        if (boss instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setHealth(mob.getMaxHealth() * 0.74f);
            Compat.face(mob, p);
            face(p, mob.position().add(0, mob.getBbHeight() * 0.55, 0), 0);
        }
    }

    /** Turns the player towards the posed boss and freezes the world for the shot. */
    private static void aimAndFreeze(IntegratedServer server, UUID id) {
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p != null && boss != null) {
            face(p, boss.position().add(0, boss.getBbHeight() * 0.55, 0), 0);
        }
        server.tickRateManager().setFrozen(true);
    }

    private static void unpose(IntegratedServer server) {
        server.tickRateManager().setFrozen(false);
        if (boss != null) {
            boss.discard();
            boss = null;
        }
    }

    /** Turns the player towards a point, looking {@code pitchOffset} degrees lower. */
    private static void face(ServerPlayer p, Vec3 target, float pitchOffset) {
        double dx = target.x - p.getX();
        double dz = target.z - p.getZ();
        double dy = target.y - p.getEyeY();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90f;
        float pitch = (float) -(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * Mth.RAD_TO_DEG) + pitchOffset;
        p.teleportTo((ServerLevel) p.level(), p.getX(), p.getY(), p.getZ(), yaw, pitch);
    }

    private Showcase() {
    }
}
