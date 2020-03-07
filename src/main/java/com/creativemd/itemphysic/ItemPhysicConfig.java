package com.creativemd.itemphysic;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.utils.stack.InfoFuel;
import com.creativemd.creativecore.common.utils.stack.InfoName;
import com.creativemd.creativecore.common.utils.type.SortingList;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class ItemPhysicConfig {
	
	@CreativeConfig
	public General general = new General();
	@CreativeConfig
	public Pickup pickup = new Pickup();
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	public Rendering rendering = new Rendering();
	
	public static class General {
		
		@CreativeConfig
		public int despawnItem = 6000;
		
		@CreativeConfig
		public boolean customThrow = true;
		
		@CreativeConfig
		public boolean fallSounds = true;
		
		@CreativeConfig
		public boolean enableIgniting = true;
		
		@CreativeConfig
		public SortingList swimmingItems = new SortingList();
		@CreativeConfig
		public SortingList burningItems = new SortingList();
		@CreativeConfig
		public SortingList undestroyableItems = new SortingList();
		@CreativeConfig
		public SortingList ignitingItems = new SortingList();
		
		public General() {
			swimmingItems.addSortingObjects(Material.WOOD, Material.CLOTH, Material.SPONGE, Material.PACKED_ICE, Material.ICE, Material.LEAVES, Material.PLANTS, Material.CARPET, Material.SNOW, Material.CACTUS, Material.CAKE, Material.VINE, Material.WEB, Blocks.SNOW, new InfoName("wooden"), Items.APPLE, Items.BOW, Items.BOWL, Items.ARROW, Items.APPLE, Items.STRING, Items.FEATHER, Items.WHEAT, Items.BREAD, Items.PAINTING, Items.SIGN, Items.ACACIA_BOAT, Items.ACACIA_DOOR, Items.DARK_OAK_BOAT, Items.DARK_OAK_DOOR, Items.BIRCH_BOAT, Items.BIRCH_DOOR, Items.JUNGLE_BOAT, Items.JUNGLE_DOOR, Items.BOAT, Items.OAK_DOOR, Items.SPRUCE_BOAT, Items.SPRUCE_DOOR, Items.SADDLE, Items.BONE, Items.SUGAR, Items.EGG, Items.FISHING_ROD, Items.DYE, Items.CAKE, Items.BED, Items.MELON, Items.SHEARS, Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BAKED_POTATO, Items.PUMPKIN_PIE, Items.ELYTRA, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SEEDS, Items.BEETROOT_SOUP, Items.SHIELD, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.SNOWBALL);
			
			burningItems.addSortingObjects(Material.WOOD, Material.CLOTH, Material.SPONGE, Material.PACKED_ICE, Material.ICE, Material.LEAVES, Material.PLANTS, Material.CARPET, Material.SNOW, Material.CACTUS, Material.CAKE, Material.VINE, Material.WEB, Material.GRASS, Blocks.SNOW, new InfoName("axe"), new InfoName("wooden"), new InfoName("shovel"), new InfoName("hoe"), new InfoName("sword"), Items.APPLE, Items.BOW, Items.BOWL, Items.ARROW, Items.APPLE, Items.STRING, Items.FEATHER, Items.WHEAT, Items.BREAD, Items.LEATHER, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.LEAD, Items.PAINTING, Items.SIGN, Items.ACACIA_BOAT, Items.ACACIA_DOOR, Items.DARK_OAK_BOAT, Items.DARK_OAK_DOOR, Items.BIRCH_BOAT, Items.BIRCH_DOOR, Items.JUNGLE_BOAT, Items.JUNGLE_DOOR, Items.BOAT, Items.OAK_DOOR, Items.SPRUCE_BOAT, Items.SPRUCE_DOOR, Items.SADDLE, Items.BONE, Items.SUGAR, Items.PAPER, Items.BOOK, Items.EGG, Items.FISHING_ROD, Items.DYE, Items.CAKE, Items.BED, Items.MELON, Items.SHEARS, Items.WRITABLE_BOOK, Items.WRITTEN_BOOK, Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BAKED_POTATO, Items.MAP, Items.PUMPKIN_PIE, Items.NAME_TAG, Items.ENCHANTED_BOOK, Items.ELYTRA, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SEEDS, Items.BEETROOT_SOUP, Items.SHIELD, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, new InfoFuel(), Items.SPIDER_EYE, Items.ROTTEN_FLESH, Items.SNOWBALL);
			
			undestroyableItems.addSortingObjects(Items.NETHER_STAR, Blocks.BEDROCK, Blocks.OBSIDIAN, Material.BARRIER);
			
			ignitingItems.addSortingObjects(Material.LAVA, Blocks.TORCH, Items.LAVA_BUCKET, Items.BLAZE_POWDER);
		}
		
	}
	
	public static class Pickup {
		
		@CreativeConfig
		public boolean customPickup = false;
		@CreativeConfig
		public boolean pickupWhenSneaking = true;
		@CreativeConfig
		public float maximumPickupRange = 5;
		@CreativeConfig
		public boolean pickupMinedImmediately = false;
		@CreativeConfig
		public boolean respectRangeWhenMined = false;
		
	}
	
	public static class Rendering {
		
		@CreativeConfig
		public boolean oldRotation = false;
		@CreativeConfig
		public boolean vanillaRendering = false;
		@CreativeConfig
		@CreativeConfig.DecimalRange(min = 0, max = 10)
		public float rotateSpeed = 1.0F;
		@CreativeConfig
		public boolean showPickupTooltip = true;
		@CreativeConfig
		public boolean disableThrowHUD = false;
		
	}
	
}
