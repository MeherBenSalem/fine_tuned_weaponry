package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.net.Buf;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.PlayerStore;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * In-game smoke test, run on a dedicated server when the {@code runeborn.selftest} system
 * property is set. Exercises registration, mob scaling, bosses, combat, status, item data and
 * persistence against the real game, then stops the server.
 */
public final class SelfTest {
    private static final List<String> FAILURES = new ArrayList<>();
    private static int passed;

    public static void maybeRun(MinecraftServer server) {
        if (!Boolean.getBoolean("runeborn.selftest") && System.getenv("RUNEBORN_SELFTEST") == null) {
            return;
        }
        RbServer.later(40, () -> run(server));
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            Runeborn.LOG.info("[selftest] PASS {}", name);
        } else {
            FAILURES.add(name);
            Runeborn.LOG.error("[selftest] FAIL {}", name);
        }
    }

    private static void run(MinecraftServer server) {
        try {
            runChecks(server);
        } catch (Throwable t) {
            Runeborn.LOG.error("[selftest] crashed", t);
            FAILURES.add("crash: " + t);
        }
        if (FAILURES.isEmpty()) {
            Runeborn.LOG.info("RUNEBORN SELFTEST PASSED ({} checks)", passed);
        } else {
            Runeborn.LOG.error("RUNEBORN SELFTEST FAILED: {} of {} failed: {}", FAILURES.size(), passed + FAILURES.size(), FAILURES);
        }
        server.halt(false);
    }

    private static void runChecks(MinecraftServer server) {
        ServerLevel level = Compat.level(server, "minecraft:overworld");
        check("overworld available", level != null);

        // Registration
        int missing = 0;
        for (WeaponSpec w : Weapons.all()) {
            if (RbItems.get(w.id) == null) {
                missing++;
            }
        }
        for (MaterialSpec m : Materials.all()) {
            if (RbItems.get(m.id()) == null) {
                missing++;
            }
        }
        check("all items registered (" + RbItems.all().size() + ")", missing == 0);
        check("shrine block registered", RbItems.shrineBlock != null && RbItems.shrineItem != null);

        // Item data round trip
        ItemStack sword = RbItems.stack("highland_claymore", 1);
        Stacks.setUpgrade(sword, 7);
        Stacks.setAffinity(sword, Affinity.HEAVY);
        check("weapon upgrade stored", Stacks.upgrade(sword) == 7);
        check("weapon affinity stored", Stacks.affinity(sword) == Affinity.HEAVY);
        check("weapon profile resolves", Stacks.profile(sword) == Weapons.get("highland_claymore"));
        check("vanilla sword gets implicit profile", Stacks.profile(Compat.vanillaSword()) != null);

        BlockPos spawn = Compat.spawnPos(level);
        int y = Compat.surface(level, spawn.getX(), spawn.getZ());
        Vec3 at = new Vec3(spawn.getX() + 0.5, y, spawn.getZ() + 0.5);

        // Mob levels and champions
        Entity zombieEntity = Compat.create(level, "minecraft:zombie");
        check("zombie created", zombieEntity instanceof Mob);
        Mob zombie = (Mob) zombieEntity;
        zombie.setPos(at.x + 3, at.y, at.z);
        level.addFreshEntity(zombie);
        Mobs.onLoad(zombie, level);
        check("zombie got a zone level", Mobs.level(zombie) >= 1 && Compat.tags(zombie).contains(Mobs.SEEN));
        Mobs.makeElite(zombie, 40);
        check("champion has affixes", Mobs.isElite(zombie) && !Mobs.affixes(zombie).isEmpty());
        check("champion is tougher", zombie.getMaxHealth() > 20);

        // Boss
        BossSpec spec = Bosses.get("varghast");
        LivingEntity boss = BossManager.spawn(level, spec, at.add(-4, 0, 0), null);
        check("boss spawned", boss != null && boss.isAlive());
        check("boss health set", boss != null && Math.abs(boss.getMaxHealth() - spec.health()) < 1);
        check("boss recognised", boss != null && BossManager.isBoss(boss));

        // Combat and status
        float before = zombie.getHealth();
        boolean landed = boss != null && Combat.hit(boss, zombie, 5, Combat.Kind.PHYSICAL);
        check("scripted hit lands", landed && zombie.getHealth() < before);
        float beforeBleed = zombie.getHealth();
        Status.add(zombie, StatusType.BLEED, 10_000, boss);
        check("bleed procs", zombie.getHealth() < beforeBleed || !zombie.isAlive());

        // Effects and sounds do not throw
        for (Fx.P p : Fx.P.values()) {
            Compat.particles(level, p, at.x, at.y + 1, at.z, 1, 0, 0, 0, 0);
        }
        for (Fx.S s : Fx.S.values()) {
            Compat.sound(level, at.x, at.y, at.z, s, 0.1f, 1f);
        }
        Compat.dust(level, 0xFF0000, 1f, at.x, at.y, at.z, 1, 0);
        check("all particles and sounds resolve", true);

        // Persistence
        PlayerStore store = new PlayerStore(server.getWorldPath(LevelResource.ROOT));
        UUID id = UUID.randomUUID();
        PlayerData d = new PlayerData();
        d.origin = "ronin";
        d.runes = 12345;
        store.write(id, d);
        store.unload(id);
        PlayerData loaded = new PlayerStore(server.getWorldPath(LevelResource.ROOT)).get(id);
        check("player data persists", "ronin".equals(loaded.origin) && loaded.runes == 12345);

        // Packets
        Buf b = Buf.reader(Packets.sync(d, ServerConfig.INSTANCE));
        b.readVarInt();
        Packets.SyncView view = Packets.readSync(b);
        check("sync packet round trip", view.data.runes == 12345 && "ronin".equals(view.data.origin));

        zombie.discard();
        if (boss != null) {
            boss.discard();
        }
    }

    private SelfTest() {
    }
}
