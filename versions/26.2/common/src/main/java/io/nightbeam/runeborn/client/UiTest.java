package io.nightbeam.runeborn.client;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.client.ClientNet;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.screen.CharacterScreen;
import io.nightbeam.runeborn.core.client.screen.CodexScreen;
import io.nightbeam.runeborn.core.client.screen.OriginScreen;
import io.nightbeam.runeborn.core.client.screen.ShrineScreen;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.shrine.ShrineRecord;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.mc.BossManager;
import io.nightbeam.runeborn.mc.Players;
import io.nightbeam.runeborn.mc.RasBridge;
import io.nightbeam.runeborn.mc.RbItems;
import io.nightbeam.runeborn.mc.RbServer;
import io.nightbeam.runeborn.mc.Stacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Scripted UI walkthrough for automated screenshots (set {@code RUNEBORN_UITEST=1}). Joins a
 * world, prepares a character, opens every screen and captures it, then quits.
 */
public final class UiTest {
    private static int step;
    private static int wait;

    public static boolean enabled() {
        return System.getenv("RUNEBORN_UITEST") != null;
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
        switch (step++) {
            case 0 -> {
                if (server != null) {
                    server.execute(() -> reset(server, mc.player.getUUID()));
                }
                wait = 60;
            }
            case 1 -> {
                RbClient.HOST.open(new OriginScreen());
                wait = 20;
            }
            case 2 -> shot("01_origin");
            case 3 -> {
                mc.gui.setScreen(null);
                sendToServer(Packets.chooseOrigin("ronin"));
                wait = 18;
            }
            case 4 -> shot("02_banner_rise");
            case 5 -> {
                if (server != null) {
                    server.execute(() -> prepare(server, mc.player.getUUID()));
                }
                wait = 40;
            }
            case 6 -> {
                RbClient.HOST.open(new ShrineScreen(new Packets.Open(Packets.Screen.SHRINE, "Weeping Birchwood Hollow",
                        "minecraft:overworld", 0, 64, 0)));
                wait = 10;
            }
            case 7 -> {
                click(0.62f, 0.37f);
                wait = 5;
            }
            case 8 -> shot("03_shrine_levelup");
            case 9 -> {
                tab(3);
                wait = 5;
            }
            case 10 -> shot("04_shrine_smithing");
            case 11 -> {
                tab(4);
                wait = 5;
            }
            case 12 -> shot("05_shrine_infuse");
            case 13 -> {
                tab(6);
                wait = 5;
            }
            case 14 -> shot("06_shrine_merchant");
            case 15 -> {
                tab(7);
                wait = 5;
            }
            case 16 -> shot("07_shrine_relics");
            case 17 -> {
                tab(2);
                wait = 5;
            }
            case 18 -> shot("08_shrine_travel");
            case 19 -> {
                tab(5);
                wait = 5;
            }
            case 20 -> shot("09_shrine_flasks");
            case 21 -> {
                RbClient.HOST.open(new CharacterScreen());
                wait = 10;
            }
            case 22 -> shot("10_status");
            case 23 -> {
                mc.gui.setScreen(new TooltipScreen());
                wait = 10;
            }
            case 24 -> shot("11_tooltips");
            case 25 -> {
                mc.gui.setScreen(null);
                if (server != null) {
                    server.execute(() -> boss(server, mc.player.getUUID()));
                }
                wait = 70;
            }
            case 26 -> shot("12_hud_boss");
            case 27 -> {
                ClientState.banner = new Packets.BannerMsg(Packets.Banner.SHRINE_KINDLED, "Shrine Kindled",
                        "Weeping Birchwood Hollow");
                ClientState.bannerTime = ClientNet.clock;
                wait = 25;
            }
            case 28 -> shot("13_banner_kindled");
            case 29 -> {
                ClientState.banner = new Packets.BannerMsg(Packets.Banner.YOU_DIED, "You Died", "");
                ClientState.bannerTime = ClientNet.clock;
                mc.gui.setScreen(new DeathScreen(Component.literal("Player was slain by Varghast the Grafted"), false, mc.player));
                wait = 30;
            }
            case 30 -> shot("14_banner_died");
            case 31 -> {
                mc.gui.setScreen(null);
                ClientState.banner = null;
                RbClient.HOST.open(new CodexScreen(new CharacterScreen()));
                wait = 5;
            }
            case 32 -> {
                codexChapter(1);
                wait = 5;
            }
            case 33 -> shot("15_codex_shrines");
            case 34 -> {
                codexChapter(4);
                wait = 5;
            }
            case 35 -> shot("16_codex_armaments");
            case 36 -> {
                codexChapter(8);
                wait = 5;
            }
            case 37 -> shot("17_codex_sovereigns");
            case 38 -> {
                Runeborn.LOG.info("RUNEBORN UITEST DONE");
                mc.stop();
            }
            default -> {
            }
        }
    }

    private static void sendToServer(byte[] payload) {
        RbClient.HOST.send(payload);
    }

    private static void tab(int index) {
        Minecraft mc = Minecraft.getInstance();
        int h = mc.getWindow().getGuiScaledHeight();
        int panelH = Math.min(h - 62, 214);
        int top = Math.max(40, h / 2 - panelH / 2 + 14);
        int rowH = Math.min(18, (panelH - 8) / 11);
        int w = mc.getWindow().getGuiScaledWidth();
        int contentW = Math.max(220, Math.min(310, w - 112 - 36));
        int left = w / 2 - (112 + 8 + contentW) / 2;
        press(left + 20, top + 4 + index * rowH + rowH / 2.0);
    }

    private static void codexChapter(int index) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui.screen() == null) {
            return;
        }
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int panelH = Math.max(120, Math.min(h - 80, 250));
        int top = Math.max(40, h / 2 - panelH / 2 + 6);
        int pageW = Math.max(220, Math.min(360, w - 112 - 48));
        int left = w / 2 - (112 + 8 + pageW) / 2;
        press(left + 30, top + 6 + index * 15 + 3);
    }

    private static void press(double x, double y) {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen != null) {
            screen.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)), false);
        }
    }

    /** Clicks at a fraction of the screen, used to press a "+" stepper in the level-up tab. */
    private static void click(float fx, float fy) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui.screen() == null) {
            return;
        }
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int panelH = Math.min(h - 62, 214);
        int top = Math.max(40, h / 2 - panelH / 2 + 14);
        int contentW = Math.max(220, Math.min(310, w - 112 - 36));
        int left = w / 2 - (112 + 8 + contentW) / 2;
        int x = left + 112 + 8 + 10;
        int colW = (contentW - 20 - 14) / 2;
        int ay = top + 8 + 24 + 40 + 12;
        for (int attr : new int[]{0, 0, 3, 4, 4}) {
            press(x + colW - 6, ay + attr * 13 + 3);
        }
    }

    /** Removes bosses left over from earlier runs and makes the test player unkillable. */
    private static void reset(IntegratedServer server, java.util.UUID id) {
        for (ServerLevel level : server.getAllLevels()) {
            java.util.List<Entity> stale = new java.util.ArrayList<>();
            for (Entity e : level.getAllEntities()) {
                if (e.entityTags().stream().anyMatch(t -> t.startsWith(BossManager.TAG))) {
                    stale.add(e);
                }
            }
            stale.forEach(Entity::discard);
        }
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p != null) {
            p.getAbilities().invulnerable = true;
            p.onUpdateAbilities();
            p.setHealth(p.getMaxHealth());
        }
    }

    private static void prepare(IntegratedServer server, java.util.UUID id) {
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p == null) {
            return;
        }
        PlayerData d = RbServer.data(p);
        d.runes = 184_250;
        d.attrs = new int[]{24, 14, 20, 18, 30, 12, 9, 10};
        d.shards.add("crown_shard_varghast");
        d.equippedShard = "crown_shard_varghast";
        d.felled.add("varghast");
        d.felled.add("ender_dragon");
        d.kills = 412;
        d.champions = 17;
        d.deaths = 23;
        d.flaskCharges = 7;
        d.flaskEmberShare = 5;
        d.flaskPotency = 4;
        d.refillFlasks();
        d.shrines.add(new ShrineRecord("Weeping Birchwood Hollow", "minecraft:overworld", 0, 64, 0));
        d.shrines.add(new ShrineRecord("Shattered Dune Cairn", "minecraft:overworld", -1240, 71, 388));
        d.shrines.add(new ShrineRecord("Shrine of the Crimson", "minecraft:the_nether", 112, 48, -60));
        d.lost = new PlayerData.LostRunes(12_400, "minecraft:overworld", p.getX() + 30, p.getY(), p.getZ() + 10);
        p.getInventory().clearContent();
        ItemStack bloomfang = RbItems.stack("lunar_bloomfang", 1);
        Stacks.setUpgrade(bloomfang, 6);
        p.getInventory().setItem(0, bloomfang);
        ItemStack claymore = RbItems.stack("highland_claymore", 1);
        Stacks.setUpgrade(claymore, 9);
        Stacks.setAffinity(claymore, Affinity.HEAVY);
        p.getInventory().setItem(1, claymore);
        p.getInventory().setItem(2, RbItems.stack("ronin_uchigatana", 1));
        p.getInventory().setItem(3, RbItems.stack("astral_staff", 1));
        p.getInventory().setItem(4, RbItems.stack("ember_flask", 1));
        p.getInventory().setItem(5, RbItems.stack("azure_flask", 1));
        p.getInventory().setItem(9, RbItems.stack("tempering_stone_3", 12));
        p.getInventory().setItem(10, RbItems.stack("lustrous_stone_7", 3));
        p.getInventory().setItem(11, RbItems.stack("heavy_whetstone", 1));
        p.getInventory().setItem(12, RbItems.stack("fire_whetstone", 1));
        p.getInventory().setItem(13, RbItems.stack("relic_varghast", 1));
        p.getInventory().setItem(14, RbItems.stack("relic_ender_dragon", 1));
        p.getInventory().setSelectedSlot(1);
        p.connection.send(new ClientboundSetHeldSlotPacket(1)); // the client is not told about server-side slot changes
        Players.applyAttributes(p);
        if (RasBridge.installed()) {
            // Integration probe: buying rune levels must raise the RPG Attribute System level too.
            int before = rasLevel(p);
            RasBridge.onRuneLevels(p, 3);
            Runeborn.LOG.info("[uitest] RPG Attribute System level {} -> {}", before, rasLevel(p));
        }
        Players.sync(p);
    }

    /** RPG Attribute System level through its public API, or -1 when it is absent. */
    private static int rasLevel(ServerPlayer p) {
        try {
            Class<?> api = Class.forName("tn.nightbeam.ras.api.RasApi");
            return (int) api.getMethod("getLevel", net.minecraft.world.entity.player.Player.class).invoke(null, p);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    private static void boss(IntegratedServer server, java.util.UUID id) {
        ServerPlayer p = server.getPlayerList().getPlayer(id);
        if (p == null) {
            return;
        }
        ServerLevel level = (ServerLevel) p.level();
        Vec3 at = p.position().add(p.getLookAngle().multiply(6, 0, 6));
        var boss = BossManager.spawn(level, Bosses.get("varghast"), at, p);
        if (boss != null) {
            boss.setHealth(boss.getMaxHealth() * 0.62f);
            io.nightbeam.runeborn.mc.Combat.hit(p, boss, 12, io.nightbeam.runeborn.mc.Combat.Kind.PHYSICAL);
            p.setHealth(p.getHealth() - 6);
            RbServer.data(p).setFocus(RbServer.data(p).maxFocus() * 0.55);
        }
    }

    private static void shot(String name) {
        Minecraft mc = Minecraft.getInstance();
        Screenshot.grab(mc.gameDirectory, "runeborn_" + name + ".png", mc.gameRenderer.mainRenderTarget(), 1,
                msg -> Runeborn.LOG.info("[uitest] {}", msg.getString()));
    }

    /** Shows three weapon tooltips side by side. */
    private static final class TooltipScreen extends Screen {
        TooltipScreen() {
            super(Component.literal("tooltips"));
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partial) {
            Minecraft mc = Minecraft.getInstance();
            g.nextStratum();
            tooltip(g, mc, mc.player.getInventory().getItem(0), 4, 20);
            tooltip(g, mc, mc.player.getInventory().getItem(1), width / 3 + 4, 20);
            tooltip(g, mc, mc.player.getInventory().getItem(3), width * 2 / 3 + 4, 20);
        }

        /** Draws an item tooltip right away; the deferred vanilla tooltip only allows one per frame. */
        private void tooltip(GuiGraphicsExtractor g, Minecraft mc, ItemStack stack, int x, int y) {
            List<ClientTooltipComponent> lines = getTooltipFromItem(mc, stack).stream()
                    .map(line -> ClientTooltipComponent.create(line.getVisualOrderText()))
                    .toList();
            g.tooltip(font, lines, x, y, DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE));
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    private UiTest() {
    }
}
