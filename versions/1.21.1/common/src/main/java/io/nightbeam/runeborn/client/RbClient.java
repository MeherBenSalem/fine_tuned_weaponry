package io.nightbeam.runeborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.content.ClientBridge;
import io.nightbeam.runeborn.core.client.ClientHost;
import io.nightbeam.runeborn.core.client.ClientNet;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.CompanionHud;
import io.nightbeam.runeborn.core.client.Hud;
import io.nightbeam.runeborn.core.client.ItemView;
import io.nightbeam.runeborn.core.client.RbScreen;
import io.nightbeam.runeborn.core.client.Tooltips;
import io.nightbeam.runeborn.core.client.screen.CharacterScreen;
import io.nightbeam.runeborn.core.client.screen.OriginScreen;
import io.nightbeam.runeborn.core.config.ClientConfig;
import io.nightbeam.runeborn.core.config.ConfigIO;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.mc.RasBridge;
import io.nightbeam.runeborn.mc.RbItems;
import io.nightbeam.runeborn.mc.Stacks;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Client glue for 1.21.1, shared by both loaders. Loader code only forwards events here. */
public final class RbClient {
    public static final KeyMapping DODGE = new KeyMapping(Lang.KEY_DODGE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, Lang.KEY_CATEGORY);
    public static final KeyMapping STATUS = new KeyMapping(Lang.KEY_CHARACTER, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, Lang.KEY_CATEGORY);
    public static final KeyMapping ART = new KeyMapping(Lang.KEY_ART, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, Lang.KEY_CATEGORY);
    public static final List<KeyMapping> KEYS = List.of(DODGE, STATUS, ART);

    public static final ClientHost HOST = new Host();
    private static int ticks;

    public static void init() {
        ClientConfig.INSTANCE = ConfigIO.load(Platform.configDir().resolve("runeborn-client.json"), ClientConfig.class,
                ClientConfig::new);
        if (Platform.isModLoaded(RasBridge.MOD_ID)) {
            CompanionHud.rpgAttributeSystem(Platform.configDir());
        }
        ClientBridge.translate = key -> VanillaCanvas.translate(key);
        ClientBridge.attributes = () -> {
            PlayerData d = ClientState.data();
            return d == null ? null : d.effectiveAttrs();
        };
        ClientBridge.shiftDown = Screen::hasShiftDown;
        ClientBridge.emberCharges = () -> ClientState.emberLeft;
        ClientBridge.azureCharges = () -> ClientState.azureLeft;
        ClientBridge.emberMax = () -> ClientState.data() == null ? 1 : ClientState.data().flaskEmberShare;
        ClientBridge.azureMax = () -> ClientState.data() == null ? 1 : ClientState.data().azureShare();
    }

    // ------------------------------------------------------------------ events

    public static void onPacket(byte[] data) {
        ClientNet.handle(data, HOST);
    }

    public static void onDisconnect() {
        ClientState.reset();
    }

    public static void tick() {
        ticks++;
        ClientNet.clock = ticks;
        ClientNet.tick();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        while (STATUS.consumeClick()) {
            if (mc.screen == null) {
                if (ClientState.ready() && ClientState.data().hasOrigin()) {
                    HOST.open(new CharacterScreen());
                } else {
                    HOST.open(new OriginScreen());
                }
            }
        }
        while (ART.consumeClick()) {
            if (mc.screen == null) {
                Platform.sendToServer(Packets.useArt());
                player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            }
        }
        while (DODGE.consumeClick()) {
            dodge(player);
        }
        if (UiTest.enabled()) {
            UiTest.tick();
        } else if (Showcase.enabled()) {
            Showcase.tick();
        }
    }

    private static void dodge(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || !ClientState.ready() || !player.onGround() || player.isPassenger()
                || player.getAbilities().flying || ClientNet.clock < ClientState.dodgeReadyAt) {
            return;
        }
        boolean free = player.isCreative() || !ClientState.view.staminaEnabled;
        if (!free && ClientState.stamina < 18) {
            return;
        }
        float forward = player.input.forwardImpulse;
        float strafe = player.input.leftImpulse;
        if (Math.abs(forward) < 0.1 && Math.abs(strafe) < 0.1) {
            forward = -1;
        }
        double yaw = Math.toRadians(player.getYRot());
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        Vec3 dir = new Vec3(strafe * cos - forward * sin, 0, forward * cos + strafe * sin).normalize();
        player.setDeltaMovement(dir.x * 1.05, 0.18, dir.z * 1.05);
        ClientState.dodgeReadyAt = ClientNet.clock + 14;
        if (!free) {
            ClientState.stamina = Math.max(0, ClientState.stamina - 18);
        }
        for (int i = 0; i < 8; i++) {
            player.level().addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1, player.getZ(),
                    -dir.x * 0.1 + (Math.random() - 0.5) * 0.1, 0.02, -dir.z * 0.1 + (Math.random() - 0.5) * 0.1);
        }
        Platform.sendToServer(Packets.dodge((float) dir.x, (float) dir.z));
    }

    public static void renderHud(GuiGraphics graphics, float partial) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) {
            return;
        }
        Hud.render(new VanillaCanvas(graphics, partial), HOST);
    }

    /** Draws the death banner over the vanilla death screen. */
    public static void renderDeathScreen(GuiGraphics graphics, float partial) {
        Packets.BannerMsg b = ClientState.banner;
        if (b == null || b.kind() != Packets.Banner.YOU_DIED) {
            return;
        }
        // Vanilla's title text sits slightly forward in depth; lift the banner above it so it covers it.
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        VanillaCanvas c = new VanillaCanvas(graphics, partial);
        Hud.drawDeathBanner(c, c.time() - ClientState.bannerTime);
        graphics.pose().popPose();
    }

    /** Extra tooltip line for vanilla and third-party weapons: they scale with attributes too. */
    public static void vanillaTooltip(ItemStack stack, List<Component> lines) {
        if (RbItems.weapon(stack) != null || !ClientState.ready()) {
            return;
        }
        WeaponSpec implicit = Stacks.profile(stack);
        if (implicit == null) {
            return;
        }
        for (String line : Tooltips.implicit(implicit, ClientState.data().effectiveAttrs(), VanillaCanvas::translate)) {
            lines.add(Component.literal(line));
        }
    }

    public static boolean hideVanillaHealth() {
        return ClientConfig.INSTANCE.hudEnabled && ClientConfig.INSTANCE.hideVanillaHearts && ClientState.ready();
    }

    // ------------------------------------------------------------------ host

    private static final class Host implements ClientHost {
        @Override
        public void send(byte[] payload) {
            Platform.sendToServer(payload);
        }

        @Override
        public void open(RbScreen screen) {
            Minecraft.getInstance().setScreen(new HostScreen(screen));
        }

        @Override
        public void close() {
            Minecraft.getInstance().setScreen(null);
        }

        @Override
        public void playClick() {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.25f, 1.2f);
            }
        }

        @Override
        public ItemView slot(int slot) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return ItemView.EMPTY;
            }
            ItemStack stack = slot < 0 ? player.getMainHandItem() : player.getInventory().getItem(slot);
            if (stack.isEmpty()) {
                return ItemView.EMPTY;
            }
            WeaponSpec spec = RbItems.weapon(stack);
            return new ItemView(slot, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), spec == null ? "" : spec.id,
                    Stacks.upgrade(stack), Stacks.affinity(stack), stack.getCount(), stack.getHoverName().getString());
        }

        @Override
        public int selectedSlot() {
            LocalPlayer player = Minecraft.getInstance().player;
            return player == null ? 0 : Compat.selectedSlot(player);
        }

        @Override
        public int count(String runebornItem) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return 0;
            }
            int n = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (RbItems.is(s, runebornItem)) {
                    n += s.getCount();
                }
            }
            return n;
        }

        @Override
        public float health() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 0 : p.getHealth();
        }

        @Override
        public float maxHealth() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 20 : p.getMaxHealth();
        }

        @Override
        public float absorption() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 0 : p.getAbsorptionAmount();
        }

        @Override
        public int armor() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 0 : p.getArmorValue();
        }

        @Override
        public String dimension() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? "" : Compat.dimensionId(p.level());
        }

        @Override
        public double x() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 0 : p.getX();
        }

        @Override
        public double y() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 0 : p.getY();
        }

        @Override
        public double z() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p == null ? 0 : p.getZ();
        }

        @Override
        public boolean creative() {
            LocalPlayer p = Minecraft.getInstance().player;
            return p != null && p.isCreative();
        }

        @Override
        public int vanillaBossBars() {
            return BossBars.count();
        }
    }

    private RbClient() {
    }
}
