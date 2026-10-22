package io.nightbeam.runeborn.content;

import io.nightbeam.runeborn.core.client.Tooltips;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.mc.Arts;
import io.nightbeam.runeborn.mc.Stacks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** A Runeborn weapon. Damage scaling, arts and upgrades live in shared code; this is the 26.3 shell. */
public class WeaponItem extends Item {
    public final WeaponSpec spec;

    public WeaponItem(WeaponSpec spec, Properties properties) {
        super(properties.stacksTo(1)
                .attributes(modifiers(spec))
                .enchantable(15)
                .component(DataComponents.WEAPON, new Weapon(0))
                .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)));
        this.spec = spec;
    }

    static ItemAttributeModifiers modifiers(WeaponSpec spec) {
        ItemAttributeModifiers.Builder b = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, spec.baseAttack() - 1,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, spec.cls.speed - 4,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        if (spec.cls.reach != 0) {
            b.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Compat.id("weapon_reach"), spec.cls.reach,
                    AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        return b.build();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer sp) {
            return Arts.use(sp, stack) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = ClientBridge.ready()
                ? Tooltips.weaponName(spec, Stacks.upgrade(stack), Stacks.affinity(stack), ClientBridge.translate)
                : null;
        MutableComponent c = name != null ? Component.literal(name) : Component.translatable(getDescriptionId());
        return c.withStyle(Style.EMPTY.withColor(spec.rarity.color));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines,
                                TooltipFlag flag) {
        if (!ClientBridge.ready()) {
            return;
        }
        int[] attrs = ClientBridge.attributes.get();
        for (String line : Tooltips.weapon(spec, Stacks.upgrade(stack), Stacks.affinity(stack), attrs,
                ClientBridge.shiftDown.getAsBoolean(), ClientBridge.translate)) {
            lines.accept(Component.literal(line));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return spec.rarity == Rarity.LEGENDARY || super.isFoil(stack);
    }
}
