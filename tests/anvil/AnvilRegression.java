import com.google.gson.Gson;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import com.naizo.finetuned.init.FineTunedWeaponryModMenus;
import com.naizo.finetuned.procedures.InsertGemsProcedure;
import com.naizo.finetuned.procedures.RemoveGemProcedure;
import com.naizo.finetuned.registry.RegistryHolder;
import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.nbt.CompoundTag;
import com.naizo.finetuned.world.inventory.WeaponsAnvilGUIMenu;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import sun.misc.Unsafe;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Headless common-code regression using real Minecraft items, slots, transfers and NBT.
 * No client, loader lifecycle, networking, or world simulation is exercised.
 */
public class AnvilRegression {
    static int assertions;
    static void check(boolean value, String message) {
        assertions++;
        if (!value) throw new AssertionError(message);
    }
    static Item item(String id) { return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id)); }
    static net.minecraft.world.level.Level testLevel;
    public static com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity testBlockEntity;
    static class TestPlayer extends Player {
        private TestPlayer() { super(null, net.minecraft.core.BlockPos.ZERO, 0, null); }
        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
        @Override public void displayClientMessage(Component message, boolean overlay) { }
        @Override public net.minecraft.world.level.Level level() { return testLevel; }
    }
    static Player player() throws Exception {
        var field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        testLevel = (ServerLevel) unsafe.allocateInstance(ServerLevel.class);
        return (Player) unsafe.allocateInstance(TestPlayer.class);
    }
    static void setName(ItemStack stack) throws Exception {
        try {
            Class<?> components = Class.forName("net.minecraft.core.component.DataComponents");
            Class<?> componentType = Class.forName("net.minecraft.core.component.DataComponentType");
            ItemStack.class.getMethod("set", componentType, Object.class).invoke(stack,
                    components.getField("CUSTOM_NAME").get(null), Component.literal("Keep my name"));
        } catch (ClassNotFoundException legacy) {
            ItemStack.class.getMethod("setHoverName", Component.class).invoke(stack, Component.literal("Keep my name"));
        }
    }
    static CompoundTag getTag(ItemStack stack) throws Exception {
        try {
            return (CompoundTag) Class.forName("com.naizo.finetuned.util.ItemStackDataHelper")
                    .getMethod("getTag", ItemStack.class).invoke(null, stack);
        } catch (ClassNotFoundException legacy) {
            return (CompoundTag) ItemStack.class.getMethod("getTag").invoke(stack);
        }
    }
    static void updateTag(ItemStack stack, java.util.function.Consumer<CompoundTag> mutator) throws Exception {
        try {
            Class.forName("com.naizo.finetuned.util.ItemStackDataHelper")
                    .getMethod("updateTag", ItemStack.class, java.util.function.Consumer.class).invoke(null, stack, mutator);
        } catch (ClassNotFoundException legacy) {
            mutator.accept((CompoundTag) ItemStack.class.getMethod("getOrCreateTag").invoke(stack));
        }
    }
    static String defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return "null";
        if (type == boolean.class) return "false";
        if (type == char.class) return "'\\0'";
        if (type == long.class) return "0L";
        if (type == float.class) return "0f";
        if (type == double.class) return "0d";
        return "0";
    }
    static void bindTestLevel() throws Exception {
        // Compile a minimal Level adapter against this exact cached game version.
        // Only getBlockEntity/getBlockState are exercised; there are no chunks or ticks.
        Class<?> level = net.minecraft.world.level.Level.class;
        var constructor = Arrays.stream(level.getDeclaredConstructors())
                .min(Comparator.comparingInt(java.lang.reflect.Constructor::getParameterCount)).orElseThrow();
        StringBuilder code = new StringBuilder("public final class AnvilFixtureLevel extends net.minecraft.world.level.Level { public AnvilFixtureLevel() { super(");
        code.append(String.join(",", Arrays.stream(constructor.getParameterTypes()).map(AnvilRegression::defaultValue).toList()));
        code.append("); } public net.minecraft.world.level.block.entity.BlockEntity getBlockEntity(net.minecraft.core.BlockPos pos) { return AnvilRegression.testBlockEntity; }");
        code.append(" public net.minecraft.world.level.block.state.BlockState getBlockState(net.minecraft.core.BlockPos pos) { return AnvilRegression.testBlockEntity.getBlockState(); }");
        Map<String, java.lang.reflect.Method> methods = new HashMap<>();
        for (var method : level.getMethods()) if (java.lang.reflect.Modifier.isAbstract(method.getModifiers())) {
            methods.put(method.getName() + Arrays.toString(method.getParameterTypes()), method);
        }
        for (Class<?> parent = level; parent != null; parent = parent.getSuperclass()) {
            for (var method : parent.getDeclaredMethods()) if (java.lang.reflect.Modifier.isAbstract(method.getModifiers())) {
                methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
            }
        }
        for (var method : methods.values()) {
            code.append(" public ").append(method.getReturnType().getCanonicalName()).append(' ').append(method.getName()).append('(');
            List<String> parameters = new ArrayList<>();
            for (int i = 0; i < method.getParameterCount(); i++) parameters.add(method.getParameterTypes()[i].getCanonicalName() + " p" + i);
            code.append(String.join(",", parameters)).append(") {");
            if (method.getReturnType() != void.class) code.append(" return ").append(defaultValue(method.getReturnType())).append(';');
            code.append(" }");
        }
        code.append(" }");
        Path source = Path.of("AnvilFixtureLevel.java").toAbsolutePath();
        Files.writeString(source, code);
        int result = javax.tools.ToolProvider.getSystemJavaCompiler().run(null, null, null, "-proc:none", "-cp",
                System.getProperty("java.class.path"), "-d", source.getParent().toString(), source.toString());
        check(result == 0, "compile version-specific bound-world adapter");
        var field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        testLevel = (net.minecraft.world.level.Level) ((Unsafe) field.get(null)).allocateInstance(Class.forName("AnvilFixtureLevel"));
    }
    static Object registries() throws Exception {
        return net.minecraft.core.RegistryAccess.class.getMethod("fromRegistryOfRegistries", Registry.class)
                .invoke(null, BuiltInRegistries.REGISTRY);
    }
    static CompoundTag diskRoundTrip(com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity anvil, boolean legacy) throws Exception {
        Class<?> blockEntity = net.minecraft.world.level.block.entity.BlockEntity.class;
        CompoundTag tag = legacy ? (CompoundTag) blockEntity.getMethod("saveWithFullMetadata").invoke(anvil)
                : (CompoundTag) blockEntity.getMethod("saveWithFullMetadata", Class.forName("net.minecraft.core.HolderLookup$Provider")).invoke(anvil, registries());
        check(tag.getString("id").equals("fine_tuned_weaponry:weapons_anvil"), "persist actual block-entity registry id");
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        net.minecraft.nbt.NbtIo.class.getMethod("writeCompressed", CompoundTag.class, java.io.OutputStream.class).invoke(null, tag, bytes);
        java.io.InputStream input = new java.io.ByteArrayInputStream(bytes.toByteArray());
        CompoundTag decoded;
        if (legacy) decoded = (CompoundTag) net.minecraft.nbt.NbtIo.class.getMethod("readCompressed", java.io.InputStream.class).invoke(null, input);
        else {
            Class<?> accounter = Class.forName("net.minecraft.nbt.NbtAccounter");
            decoded = (CompoundTag) net.minecraft.nbt.NbtIo.class.getMethod("readCompressed", java.io.InputStream.class, accounter)
                    .invoke(null, input, accounter.getMethod("unlimitedHeap").invoke(null));
        }
        check(decoded.equals(tag), "native compressed NBT round trip");
        return decoded;
    }
    static com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity restore(CompoundTag tag, boolean legacy) throws Exception {
        Class<?> blockEntity = net.minecraft.world.level.block.entity.BlockEntity.class;
        var pos = net.minecraft.core.BlockPos.ZERO;
        var state = net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState();
        Object loaded = legacy ? blockEntity.getMethod("loadStatic", net.minecraft.core.BlockPos.class,
                net.minecraft.world.level.block.state.BlockState.class, CompoundTag.class).invoke(null, pos, state, tag)
                : blockEntity.getMethod("loadStatic", net.minecraft.core.BlockPos.class,
                net.minecraft.world.level.block.state.BlockState.class, CompoundTag.class, Class.forName("net.minecraft.core.HolderLookup$Provider"))
                .invoke(null, pos, state, tag, registries());
        check(loaded instanceof com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity, "restore native anvil through registry factory");
        return (com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity) loaded;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void boundPersistence(Player player, Inventory inventory, boolean legacy) throws Exception {
        var registryFrozen = net.minecraft.core.MappedRegistry.class.getDeclaredField("frozen");
        registryFrozen.setAccessible(true);
        registryFrozen.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        var intrusive = net.minecraft.core.MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        intrusive.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        // Vanilla's supplier interface is private before loader access transforms.
        // Reflect the native builder without adding a loader or a test runtime dependency.
        var factory = Arrays.stream(net.minecraft.world.level.block.entity.BlockEntityType.Builder.class.getDeclaredMethods())
                .filter(method -> method.getName().equals("of")).findFirst().orElseThrow();
        Class<?> supplierType = factory.getParameterTypes()[0];
        Object supplier = java.lang.reflect.Proxy.newProxyInstance(supplierType.getClassLoader(), new Class<?>[]{supplierType},
                (proxy, method, arguments) -> new com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity(
                        (net.minecraft.core.BlockPos) arguments[0], (net.minecraft.world.level.block.state.BlockState) arguments[1]));
        var builder = (net.minecraft.world.level.block.entity.BlockEntityType.Builder) factory.invoke(null, supplier,
                new net.minecraft.world.level.block.Block[]{net.minecraft.world.level.block.Blocks.CHEST});
        var type = builder.build(null);
        if (!((Map<?, ?>) intrusive.get(BuiltInRegistries.BLOCK_ENTITY_TYPE)).containsKey(type)) {
            BuiltInRegistries.BLOCK_ENTITY_TYPE.createIntrusiveHolder(type);
        }
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.tryParse("fine_tuned_weaponry:weapons_anvil"), type);
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        com.naizo.finetuned.init.FineTunedWeaponryModBlockEntities.WEAPONS_ANVIL.bind(type);
        bindTestLevel();
        for (boolean oldSocket : new boolean[]{false, true}) {
            testBlockEntity = new com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity(net.minecraft.core.BlockPos.ZERO,
                    net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState());
            WeaponsAnvilGUIMenu bound = (WeaponsAnvilGUIMenu) testBlockEntity.createMenu(2, inventory);
            player.containerMenu = bound;
            check(bound.get().get(0).container == testBlockEntity, "menu uses bound anvil container");
            ItemStack weapon = new ItemStack(item("minecraft:diamond_sword"));
            setName(weapon);
            weapon.setDamageValue(42);
            updateTag(weapon, tag -> { tag.putString("third_party_data", "keep me"); tag.putInt("counter", 7); });
            check(bound.get().get(0).safeInsert(weapon).isEmpty(), "insert into bound anvil");
            testBlockEntity.setItem(8, new ItemStack(item("minecraft:dirt"), 3));
            if (oldSocket) {
                updateTag(bound.get().get(0).getItem(), tag -> {
                    tag.putString("ft_slot1.0", "fine_tuned_weaponry:inferno_core");
                    tag.putString("ft_slot3.0", "fine_tuned_weaponry:blazing_amplifier");
                    tag.putBoolean("fine_tuned_weaponry:inferno_core", true);
                    tag.putBoolean("fine_tuned_weaponry:blazing_amplifier", true);
                    tag.putBoolean("finetunned", true);
                });
            } else {
                bound.get().get(1).set(new ItemStack(item("fine_tuned_weaponry:inferno_core"), 2));
                bound.get().get(3).set(new ItemStack(item("fine_tuned_weaponry:blazing_amplifier"), 2));
                InsertGemsProcedure.execute(null, 0, 0, 0, player);
            }
            CompoundTag saved = diskRoundTrip(testBlockEntity, legacy);
            bound.removed(player);
            player.containerMenu = null;
            testBlockEntity = restore(saved, legacy);
            WeaponsAnvilGUIMenu reopened = (WeaponsAnvilGUIMenu) testBlockEntity.createMenu(3, inventory);
            player.containerMenu = reopened;
            check(reopened.get().get(0).container == testBlockEntity, "reopened menu binds restored anvil");
            ItemStack loaded = reopened.get().get(0).getItem();
            String key = legacy || oldSocket ? "ft_slot1.0" : "ft_slot1";
            check(getTag(loaded).getString(key).equals("fine_tuned_weaponry:inferno_core"), "socket id survives save/load");
            check(GemNbtKeys.hasGem(loaded, "fine_tuned_weaponry:inferno_core") && GemNbtKeys.isModified(loaded), "socket flags survive save/load");
            check(loaded.getDamageValue() == 42 && loaded.getHoverName().getString().equals("Keep my name"), "weapon components survive save/load");
            check(getTag(loaded).getString("third_party_data").equals("keep me") && getTag(loaded).getInt("counter") == 7, "foreign NBT survives save/load");
            check(testBlockEntity.getItem(8).getCount() == 3 && testBlockEntity.getItem(8).is(item("minecraft:dirt")), "non-menu inventory slots survive save/load");
            if (!oldSocket) check(reopened.get().get(1).getItem().getCount() == 1 && reopened.get().get(3).getItem().getCount() == 1, "unused upgrades survive save/load");
            reopened.get().get(1).set(new ItemStack(item("fine_tuned_weaponry:storm_shard"), 2));
            InsertGemsProcedure.execute(null, 0, 0, 0, player);
            check(reopened.get().get(1).getItem().getCount() == 2, "persisted occupied socket refuses replacement");
            reopened.get().get(1).set(ItemStack.EMPTY);
            reopened.get().get(3).set(ItemStack.EMPTY);
            RemoveGemProcedure.execute(player);
            check(reopened.get().get(1).getItem().is(item("fine_tuned_weaponry:inferno_core")), "remove persisted gem");
            check(reopened.get().get(3).getItem().is(item("fine_tuned_weaponry:blazing_amplifier")), "remove persisted amplifier");
            CompoundTag removed = diskRoundTrip(testBlockEntity, legacy);
            testBlockEntity = restore(removed, legacy);
            check(!GemNbtKeys.hasGem(testBlockEntity.getItem(0), "fine_tuned_weaponry:inferno_core") && !GemNbtKeys.isModified(testBlockEntity.getItem(0)), "cleared flags survive second save/load");
            check(getTag(testBlockEntity.getItem(0)).getString("ft_slot1").isEmpty() && getTag(testBlockEntity.getItem(0)).getString("ft_slot1.0").isEmpty(), "cleared socket keys survive second save/load");
            check(testBlockEntity.getItem(1).is(item("fine_tuned_weaponry:inferno_core")) && testBlockEntity.getItem(3).is(item("fine_tuned_weaponry:blazing_amplifier")), "returned upgrades survive second save/load");
            check(getTag(testBlockEntity.getItem(0)).getString("third_party_data").equals("keep me"), "foreign data survives second save/load");
        }
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void main(String[] args) throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        // Standalone vanilla bootstrap freezes registries. Reopen only the test JVM's
        // item registry to register native mod items without launching either loader.
        var frozen = net.minecraft.core.MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.set(BuiltInRegistries.ITEM, false);
        var intrusive = net.minecraft.core.MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        intrusive.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        for (var field : FineTunedWeaponryModItems.class.getFields()) {
            if (field.get(null) instanceof RegistryHolder<?> holder && holder.get() instanceof Item value) {
                Registry.register(BuiltInRegistries.ITEM, holder.id(), value);
            }
        }
        for (String id : new String[]{"third_party_weapon", "legacy_weapon", "explicit_weapon"}) {
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.tryParse("anvil_test:" + id), new Item(new Item.Properties().stacksTo(1)));
        }
        BuiltInRegistries.ITEM.freeze();
        FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI.bind((MenuType) MenuType.GENERIC_9x1);
        Map<String, List<String>> fixture = new Gson().fromJson(Files.readString(Path.of(args[0])), Map.class);
        Map<TagKey<Item>, List<Holder<Item>>> tags = new HashMap<>();
        fixture.forEach((id, values) -> {
            List<Holder<Item>> holders = new ArrayList<>();
            for (String value : values) BuiltInRegistries.ITEM.getHolder(net.minecraft.resources.ResourceKey.create(
                    Registries.ITEM, ResourceLocation.tryParse(value))).ifPresent(holders::add);
            tags.put(TagKey.create(Registries.ITEM, ResourceLocation.tryParse(id)), holders);
        });
        BuiltInRegistries.ITEM.bindTags(tags);
        Player player = player();
        Inventory inventory = new Inventory(player);
        WeaponsAnvilGUIMenu menu = new WeaponsAnvilGUIMenu(1, inventory, null);
        player.containerMenu = menu;
        boolean baseline = args.length > 1 && args[1].equals("baseline");
        String socketKey = args.length > 1 && args[1].equals("1.20.1") ? "ft_slot1.0" : "ft_slot1";
        String[] weapons = {"minecraft:diamond_sword", "fine_tuned_weaponry:classic_katana", "anvil_test:third_party_weapon"};
        for (String id : weapons) {
            ItemStack stack = new ItemStack(item(id));
            check(menu.get().get(0).mayPlace(stack) != baseline, "direct insertion " + id);
            ItemStack remainder = menu.get().get(0).safeInsert(stack.copy());
            check(remainder.isEmpty() != baseline, "direct safeInsert " + id);
            check(menu.get().get(0).hasItem() != baseline, "direct destination " + id);
            menu.get().get(0).set(ItemStack.EMPTY);
            inventory.setItem(9, stack);
            ItemStack moved = menu.quickMoveStack(player, 7);
            check(moved.isEmpty() == baseline, "shift-click " + id);
            check(menu.get().get(0).hasItem() != baseline, "weapon destination " + id);
            check(inventory.getItem(9).isEmpty() != baseline, "weapon source " + id);
            menu.get().get(0).set(ItemStack.EMPTY);
            inventory.setItem(9, ItemStack.EMPTY);
        }
        for (String id : new String[]{"minecraft:dirt", "minecraft:stick", "minecraft:diamond", "minecraft:diamond_chestplate"}) {
            ItemStack stack = new ItemStack(item(id));
            check(!menu.get().get(0).mayPlace(stack), "reject unrelated " + id);
            inventory.setItem(9, stack);
            check(menu.quickMoveStack(player, 7).isEmpty(), "reject unrelated shift-click " + id);
            check(inventory.getItem(9) == stack, "rejected stack preserved " + id);
        }
        if (baseline) {
            check(!menu.get().get(1).mayPlace(new ItemStack(item("fine_tuned_weaponry:inferno_core"))), "released gem tag missing");
            check(!menu.get().get(3).mayPlace(new ItemStack(item("fine_tuned_weaponry:blazing_amplifier"))), "released amplifier tag missing");
            System.out.println("REPRODUCED current release: direct and shift-click rejected all three weapon fixtures; gem/amp tags missing. Assertions=" + assertions);
            return;
        }
        for (String id : new String[]{"minecraft:diamond_axe", "minecraft:diamond_pickaxe", "minecraft:diamond_shovel", "minecraft:diamond_hoe", "minecraft:bow", "minecraft:crossbow", "minecraft:shield", "minecraft:trident", "minecraft:fishing_rod", "anvil_test:legacy_weapon", "anvil_test:explicit_weapon"}) {
            check(menu.get().get(0).mayPlace(new ItemStack(item(id))), "allowed tool category " + id);
        }
        if (args.length < 2 || !args[1].equals("1.20.1")) {
            for (String id : new String[]{"minecraft:shears", "minecraft:flint_and_steel", "minecraft:brush", "minecraft:mace"}) {
                check(menu.get().get(0).mayPlace(new ItemStack(item(id))), "modern allowed category " + id);
            }
        } else {
            for (String id : new String[]{"minecraft:shears", "minecraft:flint_and_steel", "minecraft:brush"}) {
                check(!menu.get().get(0).mayPlace(new ItemStack(item(id))), "preserve legacy excluded category " + id);
            }
        }
        for (var field : FineTunedWeaponryModItems.class.getFields()) {
            if (field.get(null) instanceof RegistryHolder<?> holder && holder.get() instanceof net.minecraft.world.item.TieredItem) {
                check(menu.get().get(0).mayPlace(new ItemStack((Item) holder.get())), "native weapon " + holder.id());
            }
        }
        inventory.setItem(9, ItemStack.EMPTY);
        for (int slot : new int[]{1, 3}) {
            String prefix = slot == 1 ? "gem" : "amp";
            for (String id : fixture.get("fine_tuned_weaponry:" + prefix)) {
                ItemStack input = new ItemStack(item(id), 2);
                inventory.setItem(9, input);
                check(!menu.quickMoveStack(player, 7).isEmpty(), "shift-click " + prefix + " " + id);
                check(menu.get().get(slot).getItem().is(item(id)) && menu.get().get(slot).getItem().getCount() == 2, "route " + prefix);
                check(inventory.getItem(9).isEmpty(), "clear " + prefix + " source");
                menu.get().get(slot).set(ItemStack.EMPTY);
            }
        }
        ItemStack weapon = new ItemStack(item("minecraft:diamond_sword"));
        setName(weapon);
        weapon.setDamageValue(42);
        updateTag(weapon, tag -> { tag.putString("third_party_data", "keep me"); tag.putInt("counter", 7); });
        menu.get().get(0).set(weapon);
        String[] gems = {"inferno_core", "frost_rune", "storm_shard"};
        for (String gem : gems) {
            ItemStack input = new ItemStack(item("fine_tuned_weaponry:" + gem), 2);
            check(menu.get().get(1).mayPlace(input), "gem placement " + gem);
            menu.get().get(1).set(input);
            ItemStack amp = new ItemStack(item("fine_tuned_weaponry:blazing_amplifier"), 2);
            check(menu.get().get(3).mayPlace(amp), "amplifier placement");
            menu.get().get(3).set(amp);
            InsertGemsProcedure.execute(null, 0, 0, 0, player);
            check(input.getCount() == 1 && amp.getCount() == 1, "consume one gem and amp");
            check(GemNbtKeys.hasGem(weapon, "fine_tuned_weaponry:" + gem) && GemNbtKeys.isModified(weapon), "gem applied");
            check(getTag(weapon).getString(socketKey).equals("fine_tuned_weaponry:" + gem), "socket key");
            menu.get().get(1).set(new ItemStack(item("fine_tuned_weaponry:storm_shard"), 2));
            InsertGemsProcedure.execute(null, 0, 0, 0, player);
            check(menu.get().get(1).getItem().getCount() == 2, "occupied socket does not consume replacement");
            check(getTag(weapon).getString(socketKey).equals("fine_tuned_weaponry:" + gem), "occupied socket does not overwrite");
            menu.get().get(1).set(ItemStack.EMPTY);
            menu.get().get(3).set(ItemStack.EMPTY);
            RemoveGemProcedure.execute(player);
            check(menu.get().get(1).getItem().is(item("fine_tuned_weaponry:" + gem)), "gem returned");
            check(menu.get().get(3).getItem().is(item("fine_tuned_weaponry:blazing_amplifier")), "amp returned");
            check(!GemNbtKeys.hasGem(weapon, "fine_tuned_weaponry:" + gem) && !GemNbtKeys.isModified(weapon), "gem removed");
            check(getTag(weapon).getString("third_party_data").equals("keep me") && getTag(weapon).getInt("counter") == 7, "foreign NBT preserved");
            check(weapon.getDamageValue() == 42 && weapon.getHoverName().getString().equals("Keep my name"), "components preserved");
            menu.get().get(1).set(ItemStack.EMPTY);
            menu.get().get(3).set(ItemStack.EMPTY);
        }
        // Preserve sockets from the reported 2.1.0 release as well as current data.
        updateTag(weapon, tag -> {
            tag.putString("ft_slot1.0", "fine_tuned_weaponry:inferno_core");
            tag.putBoolean("fine_tuned_weaponry:inferno_core", true);
            tag.putBoolean("finetunned", true);
        });
        menu.get().get(1).set(new ItemStack(item("fine_tuned_weaponry:storm_shard"), 2));
        InsertGemsProcedure.execute(null, 0, 0, 0, player);
        check(menu.get().get(1).getItem().getCount() == 2, "legacy socket does not consume replacement");
        check(getTag(weapon).getString("ft_slot1.0").equals("fine_tuned_weaponry:inferno_core"), "legacy socket preserved while occupied");
        menu.get().get(1).set(ItemStack.EMPTY);
        RemoveGemProcedure.execute(player);
        check(menu.get().get(1).getItem().is(item("fine_tuned_weaponry:inferno_core")), "legacy gem returned");
        check(getTag(weapon).getString("ft_slot1.0").isEmpty(), "legacy socket cleared");
        check(!GemNbtKeys.hasGem(weapon, "fine_tuned_weaponry:inferno_core") && !GemNbtKeys.isModified(weapon), "legacy flags cleared");
        check(getTag(weapon).getString("third_party_data").equals("keep me"), "legacy foreign NBT preserved");
        boundPersistence(player, inventory, args.length > 1 && args[1].equals("1.20.1"));
        System.out.println("PASS headless native menu, transfers, gem/amp round trips and data preservation. Assertions=" + assertions);
    }
}
