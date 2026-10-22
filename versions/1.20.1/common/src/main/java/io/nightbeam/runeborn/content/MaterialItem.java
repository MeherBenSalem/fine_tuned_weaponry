package io.nightbeam.runeborn.content;

import io.nightbeam.runeborn.core.client.Tooltips;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.mc.ItemUse;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** Stones, whetstones, runes, flasks, sigils, relics and crown shards. */
public class MaterialItem extends Item {
    public final MaterialSpec spec;

    public MaterialItem(MaterialSpec spec, Properties properties) {
        super(properties.stacksTo(spec.stack()));
        this.spec = spec;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!spec.usable()) {
            return InteractionResultHolder.pass(stack);
        }
        if (player instanceof ServerPlayer sp) {
            return ItemUse.use(sp, stack) ? InteractionResultHolder.consume(stack) : InteractionResultHolder.fail(stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(stack)).withStyle(Style.EMPTY.withColor(spec.rarity().color));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        if (!ClientBridge.ready()) {
            return;
        }
        for (String line : Tooltips.material(spec, ClientBridge.translate)) {
            lines.add(Component.literal(line));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return spec.rarity() == Rarity.LEGENDARY || super.isFoil(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ClientBridge.ready() && (spec.kind() == MaterialSpec.Kind.EMBER_FLASK || spec.kind() == MaterialSpec.Kind.AZURE_FLASK);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!ClientBridge.ready()) {
            return 0;
        }
        boolean ember = spec.kind() == MaterialSpec.Kind.EMBER_FLASK;
        int left = ember ? ClientBridge.emberCharges.getAsInt() : ClientBridge.azureCharges.getAsInt();
        int max = ember ? ClientBridge.emberMax.getAsInt() : ClientBridge.azureMax.getAsInt();
        return max <= 0 ? 0 : Math.round(13f * left / max);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return spec.kind() == MaterialSpec.Kind.EMBER_FLASK ? 0xE8743C : 0x4F7FE0;
    }
}
