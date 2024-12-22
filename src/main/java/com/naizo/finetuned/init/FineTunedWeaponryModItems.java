
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.item.ItemProperties;

import com.naizo.finetuned.procedures.BarkbowPropertyValueProviderProcedure;
import com.naizo.finetuned.item.UpgradeTemplateT2Item;
import com.naizo.finetuned.item.UpgradeTemplateT1Item;
import com.naizo.finetuned.item.T1RefinedCrystalItem;
import com.naizo.finetuned.item.T1BarkArrowItem;
import com.naizo.finetuned.item.SantashelmetItem;
import com.naizo.finetuned.item.MineFavorGemItem;
import com.naizo.finetuned.item.IroncladFury3Item;
import com.naizo.finetuned.item.IroncladFury2Item;
import com.naizo.finetuned.item.IroncladFury1Item;
import com.naizo.finetuned.item.GrandmasterlightpoleItem;
import com.naizo.finetuned.item.GingerBreadMaceItem;
import com.naizo.finetuned.item.GingerBreadItem;
import com.naizo.finetuned.item.CandyCaneSwordItem;
import com.naizo.finetuned.item.CanadyCaneItem;
import com.naizo.finetuned.item.BarkshieldT3Item;
import com.naizo.finetuned.item.BarkshieldT2Item;
import com.naizo.finetuned.item.BarkshieldItem;
import com.naizo.finetuned.item.BarkbowItem;
import com.naizo.finetuned.FineTunedWeaponryMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FineTunedWeaponryModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Item> T_1_REFINED_CRYSTAL = REGISTRY.register("t_1_refined_crystal", () -> new T1RefinedCrystalItem());
	public static final RegistryObject<Item> BARKSHIELD = REGISTRY.register("barkshield", () -> new BarkshieldItem());
	public static final RegistryObject<Item> BARKSHIELD_T_2 = REGISTRY.register("barkshield_t_2", () -> new BarkshieldT2Item());
	public static final RegistryObject<Item> BARKSHIELD_T_3 = REGISTRY.register("barkshield_t_3", () -> new BarkshieldT3Item());
	public static final RegistryObject<Item> BARKBOW = REGISTRY.register("barkbow", () -> new BarkbowItem());
	public static final RegistryObject<Item> T_1_BARK_ARROW = REGISTRY.register("t_1_bark_arrow", () -> new T1BarkArrowItem());
	public static final RegistryObject<Item> MINE_FAVOR_GEM = REGISTRY.register("mine_favor_gem", () -> new MineFavorGemItem());
	public static final RegistryObject<Item> GEMORE = block(FineTunedWeaponryModBlocks.GEMORE);
	public static final RegistryObject<Item> IRONCLAD_FURY_1 = REGISTRY.register("ironclad_fury_1", () -> new IroncladFury1Item());
	public static final RegistryObject<Item> IRONCLAD_FURY_2 = REGISTRY.register("ironclad_fury_2", () -> new IroncladFury2Item());
	public static final RegistryObject<Item> IRONCLAD_FURY_3 = REGISTRY.register("ironclad_fury_3", () -> new IroncladFury3Item());
	public static final RegistryObject<Item> CANDY_CANE_SWORD = REGISTRY.register("candy_cane_sword", () -> new CandyCaneSwordItem());
	public static final RegistryObject<Item> CANADY_CANE = REGISTRY.register("canady_cane", () -> new CanadyCaneItem());
	public static final RegistryObject<Item> GINGER_BREAD = REGISTRY.register("ginger_bread", () -> new GingerBreadItem());
	public static final RegistryObject<Item> GINGER_BREAD_MACE = REGISTRY.register("ginger_bread_mace", () -> new GingerBreadMaceItem());
	public static final RegistryObject<Item> SANTASHELMET_HELMET = REGISTRY.register("santashelmet_helmet", () -> new SantashelmetItem.Helmet());
	public static final RegistryObject<Item> SANTASHELMET_CHESTPLATE = REGISTRY.register("santashelmet_chestplate", () -> new SantashelmetItem.Chestplate());
	public static final RegistryObject<Item> SANTASHELMET_LEGGINGS = REGISTRY.register("santashelmet_leggings", () -> new SantashelmetItem.Leggings());
	public static final RegistryObject<Item> SANTASHELMET_BOOTS = REGISTRY.register("santashelmet_boots", () -> new SantashelmetItem.Boots());
	public static final RegistryObject<Item> GRANDMASTERLIGHTPOLE = REGISTRY.register("grandmasterlightpole", () -> new GrandmasterlightpoleItem());
	public static final RegistryObject<Item> UPGRADE_TEMPLATE_T_1 = REGISTRY.register("upgrade_template_t_1", () -> new UpgradeTemplateT1Item());
	public static final RegistryObject<Item> UPGRADE_TEMPLATE_T_2 = REGISTRY.register("upgrade_template_t_2", () -> new UpgradeTemplateT2Item());

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(BARKSHIELD.get(), new ResourceLocation("blocking"), ItemProperties.getProperty(Items.SHIELD, new ResourceLocation("blocking")));
			ItemProperties.register(BARKSHIELD_T_2.get(), new ResourceLocation("blocking"), ItemProperties.getProperty(Items.SHIELD, new ResourceLocation("blocking")));
			ItemProperties.register(BARKSHIELD_T_3.get(), new ResourceLocation("blocking"), ItemProperties.getProperty(Items.SHIELD, new ResourceLocation("blocking")));
			ItemProperties.register(BARKBOW.get(), new ResourceLocation("fine_tuned_weaponry:barkbow_pulling"), (itemStackToRender, clientWorld, entity, itemEntityId) -> (float) BarkbowPropertyValueProviderProcedure.execute(itemStackToRender));
		});
	}
}
