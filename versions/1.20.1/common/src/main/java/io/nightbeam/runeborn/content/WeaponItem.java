package io.nightbeam.runeborn.content;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.client.Tooltips;
import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.mc.Arts;
import io.nightbeam.runeborn.mc.Stacks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * A Runeborn weapon. Damage scaling, arts and upgrades live in shared code; this is the 1.20.1 shell.
 * It extends {@link SwordItem} only so vanilla weapon enchantments apply; sword mining is switched
 * off to match the plain item used on newer versions (each loader also disables the vanilla sweep).
 */
public class WeaponItem extends SwordItem {
    static final Tier TIER = new Tier() {
        @Override
        public int getUses() {
            return 0;
        }

        @Override
        public float getSpeed() {
            return 1;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public final WeaponSpec spec;
    private Multimap<Attribute, AttributeModifier> modifiers;

    public WeaponItem(WeaponSpec spec, Properties properties) {
        super(TIER, (int) spec.baseAttack() - 1, (float) (spec.cls.speed - 4), properties.stacksTo(1));
        this.spec = spec;
    }

    /** Built on first use: the loader's reach attribute may register after items. */
    private Multimap<Attribute, AttributeModifier> modifiers() {
        if (modifiers == null) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> b = ImmutableMultimap.builder();
            b.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                    spec.baseAttack() - 1, AttributeModifier.Operation.ADDITION));
            b.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                    spec.cls.speed - 4, AttributeModifier.Operation.ADDITION));
            Attribute reach = Platform.entityReach();
            if (spec.cls.reach != 0 && reach != null) {
                b.put(reach, new AttributeModifier(Compat.modifierId("weapon_reach"), Rb.MOD_ID + ":weapon_reach",
                        spec.cls.reach, AttributeModifier.Operation.ADDITION));
            }
            modifiers = b.build();
        }
        return modifiers;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? modifiers() : ImmutableMultimap.of();
    }

    /** Forge reads this to hide the vanilla attribute lines; Fabric applies it through a mixin. */
    public int getDefaultTooltipHideFlags(ItemStack stack) {
        return ItemStack.TooltipPart.MODIFIERS.getMask();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }
        if (player instanceof ServerPlayer sp) {
            return Arts.use(sp, stack) ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = ClientBridge.ready()
                ? Tooltips.weaponName(spec, Stacks.upgrade(stack), Stacks.affinity(stack), ClientBridge.translate)
                : null;
        MutableComponent c = name != null ? Component.literal(name) : Component.translatable(getDescriptionId(stack));
        return c.withStyle(Style.EMPTY.withColor(spec.rarity.color));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        if (!ClientBridge.ready()) {
            return;
        }
        int[] attrs = ClientBridge.attributes.get();
        for (String line : Tooltips.weapon(spec, Stacks.upgrade(stack), Stacks.affinity(stack), attrs,
                ClientBridge.shiftDown.getAsBoolean(), ClientBridge.translate)) {
            lines.add(Component.literal(line));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return spec.rarity == Rarity.LEGENDARY || super.isFoil(stack);
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return false;
    }
}
