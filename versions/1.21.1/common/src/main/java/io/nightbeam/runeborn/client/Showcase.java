package io.nightbeam.runeborn.client;

import com.mojang.datafixers.util.Pair;
import io.nightbeam.runeborn.Runeborn;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

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

    public static boolean enabled() {
        return System.getenv("RUNEBORN_SHOWCASE") != null;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !ClientState.ready()) {
            return;
        }
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
                RbClient.HOST.send(Packets.chooseOrigin("ronin"));
                server.execute(() -> scene(server, id));
                wait = 260;
            }
            case 1 -> {
                server.execute(() -> kindle(server, id));
                wait = 34;
            }
            case 2 -> UiTest.shot("kindled");
            case 3 -> {
                mc.options.hideGui = true;
                wait = 4;
            }
            case 4 -> UiTest.shot("shrine");
            case 5 -> {
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
                UiTest.click(0, 0);
                wait = 6;
            }
            case 8 -> UiTest.shot("levelup");
            case 9 -> menu(() -> UiTest.tab(3));
            case 10 -> UiTest.shot("smithing");
            case 11 -> menu(() -> UiTest.tab(4));
            case 12 -> UiTest.shot("infuse");
            case 13 -> menu(() -> UiTest.tab(6));
            case 14 -> UiTest.shot("merchant");
            case 15 -> menu(() -> RbClient.HOST.open(new CharacterScreen()));
            case 16 -> UiTest.shot("status");
            case 17 -> menu(() -> {
                RbClient.HOST.open(new CodexScreen(null));
                UiTest.codexChapter(1);
            });
            case 18 -> UiTest.shot("codex-shrines");
            case 19 -> menu(() -> UiTest.codexChapter(8));
            case 20 -> UiTest.shot("codex-sovereigns");
            case 21 -> menu(() -> mc.setScreen(new UiTest.TooltipScreen()));
            case 22 -> UiTest.shot("tooltips");
            case 23 -> {
                mc.setScreen(null);
                server.execute(() -> pose(server, id, "morgrath", 9));
                wait = 46;
            }
            case 24 -> {
                server.execute(() -> server.tickRateManager().setFrozen(true));
                wait = 6;
            }
            case 25 -> UiTest.shot("boss-morgrath-hud");
            case 26 -> {
                mc.options.hideGui = true;
                wait = 4;
            }
            case 27 -> UiTest.shot("boss-morgrath");
            case 28 -> {
                mc.options.hideGui = false;
                server.execute(() -> unpose(server));
                server.execute(() -> pose(server, id, "kaelthar", 8));
                wait = 50;
            }
            case 29 -> {
                server.execute(() -> server.tickRateManager().setFrozen(true));
                wait = 6;
            }
            case 30 -> UiTest.shot("boss-kaelthar-hud");
            case 31 -> {
                server.execute(() -> unpose(server));
                server.execute(() -> pose(server, id, "sylvara", 8));
                wait = 50;
            }
            case 32 -> {
                server.execute(() -> server.tickRateManager().setFrozen(true));
                wait = 6;
            }
            case 33 -> {
                mc.options.hideGui = true;
                wait = 4;
            }
            case 34 -> UiTest.shot("boss-sylvara");
            case 35 -> {
                mc.options.hideGui = false;
                server.execute(() -> unpose(server));
                ClientState.banner = new Packets.BannerMsg(Packets.Banner.SOVEREIGN_FELLED, "Sovereign Felled",
                        "Sylvara, Frost Matron");
                ClientState.bannerTime = ClientNet.clock;
                wait = 30;
            }
            case 36 -> UiTest.shot("felled");
            case 37 -> {
                Runeborn.LOG.info("RUNEBORN SHOWCASE DONE");
                mc.stop();
            }
            default -> {
            }
        }
    }

    /** Runs a menu action and waits for the next frames to draw it. */
    private static void menu(Runnable action) {
        action.run();
        wait = 8;
    }

    private static String shrineName() {
        var d = ClientState.data();
        return d == null || d.shrines.isEmpty() ? "Ember Shrine" : d.shrines.get(d.shrines.size() - 1).name;
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
        level.setDayTime(12_450);
        level.setWeatherParameters(24_000, 0, false, false);
        BlockPos from = p.blockPosition();
        Pair<BlockPos, Holder<Biome>> found = null;
        for (var biome : java.util.List.of(Biomes.CHERRY_GROVE, Biomes.MEADOW, Biomes.FLOWER_FOREST, Biomes.PLAINS)) {
            found = level.findClosestBiome3d(h -> h.is(biome), from, 4000, 32, 64);
            if (found != null) {
                break;
            }
        }
        BlockPos at = found == null ? from : found.getFirst();
        level.getChunk(at.getX() >> 4, at.getZ() >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at.getX(), at.getZ());
        p.teleportTo(level, at.getX() + 0.5, y, at.getZ() + 0.5, 210f, 12f);
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
        Vec3 ahead = p.position().add(Vec3.directionFromRotation(0, p.getYRot()).scale(4));
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(ahead.x), Mth.floor(ahead.z));
        shrine = BlockPos.containing(ahead.x, y, ahead.z);
        level.setBlockAndUpdate(shrine, RbItems.shrineBlock.defaultBlockState());
        face(p, Vec3.atCenterOf(shrine), 4);
        Shrines.use(p, shrine);
        RbServer.data(p).runes = 184_250;
    }

    /** Spawns a Sovereign {@code distance} blocks ahead, facing the player. */
    private static void pose(IntegratedServer server, UUID id, String bossId, double distance) {
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p == null) {
            return;
        }
        ServerLevel level = (ServerLevel) p.level();
        Vec3 ahead = p.position().add(Vec3.directionFromRotation(0, p.getYRot()).scale(distance));
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(ahead.x), Mth.floor(ahead.z));
        boss = BossManager.spawn(level, Bosses.get(bossId), new Vec3(ahead.x, y, ahead.z), p);
        if (boss != null) {
            boss.setHealth(boss.getMaxHealth() * 0.74f);
            face(p, boss.getEyePosition(), 0);
        }
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
