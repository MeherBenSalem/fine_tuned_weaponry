package io.nightbeam.runeborn.content;

import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;
import io.nightbeam.runeborn.mc.RbItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

/** Registers every block, item and creative tab through a loader-provided registrar. */
public final class RbContent {
    public interface Registrar {
        Block block(String path, Supplier<Block> factory);

        Item item(String path, Supplier<Item> factory);

        CreativeModeTab tab(String path, Supplier<CreativeModeTab> factory);

        /** Loaders may return a subclass that switches off loader-specific sword behaviour. */
        default Item weapon(WeaponSpec spec, Item.Properties properties) {
            return new WeaponItem(spec, properties);
        }
    }

    public static void blocks(Registrar r) {
        RbItems.shrineBlock = r.block("ember_shrine", () -> new ShrineBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.5f, 1200f)
                .sound(SoundType.DEEPSLATE_BRICKS)
                .lightLevel(state -> 15)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK)));
    }

    public static void items(Registrar r) {
        Item shrine = r.item("ember_shrine", () -> new BlockItem(RbItems.shrineBlock, new Item.Properties()));
        RbItems.shrineItem = shrine;
        RbItems.add("ember_shrine", shrine);
        for (WeaponSpec spec : Weapons.all()) {
            Item.Properties props = new Item.Properties();
            if (spec.rarity == Rarity.LEGENDARY) {
                props.fireResistant();
            }
            RbItems.addWeapon(spec, r.item(spec.id, () -> r.weapon(spec, props)));
        }
        for (MaterialSpec spec : Materials.all()) {
            Item.Properties props = new Item.Properties();
            if (spec.rarity() == Rarity.LEGENDARY) {
                props.fireResistant();
            }
            RbItems.addMaterial(spec, r.item(spec.id(), () -> new MaterialItem(spec, props)));
        }
    }

    public static void tabs(Registrar r) {
        r.tab("armaments", () -> Platform.tabBuilder()
                .title(Component.translatable(Lang.TAB))
                .icon(() -> RbItems.stack("lunar_bloomfang", 1))
                .displayItems((params, out) -> {
                    out.accept(new ItemStack(RbItems.shrineItem));
                    for (WeaponSpec spec : Weapons.all()) {
                        out.accept(RbItems.stack(spec.id, 1));
                    }
                })
                .build());
        r.tab("materials", () -> Platform.tabBuilder()
                .title(Component.translatable(Lang.TAB_MATERIALS))
                .icon(() -> RbItems.stack("ember_flask", 1))
                .displayItems((params, out) -> {
                    for (MaterialSpec spec : Materials.all()) {
                        out.accept(RbItems.stack(spec.id(), 1));
                    }
                })
                .build());
    }

    private RbContent() {
    }
}
