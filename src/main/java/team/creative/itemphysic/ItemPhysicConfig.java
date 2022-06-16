package team.creative.itemphysic;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientFuel;
import team.creative.creativecore.common.util.type.list.SortingList;

public class ItemPhysicConfig {
    
    @CreativeConfig
    public General general = new General();
    @CreativeConfig
    public Pickup pickup = new Pickup();
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    public Rendering rendering = new Rendering();
    
    public static class General {
        
        @CreativeConfig
        public boolean customThrow = true;
        
        @CreativeConfig
        public boolean fallSounds = true;
        
        @CreativeConfig
        public SortingList swimmingItems = new SortingList();
        @CreativeConfig
        public SortingList burningItems = new SortingList();
        @CreativeConfig
        public SortingList undestroyableItems = new SortingList();
        
        public General() {
            swimmingItems
                    .addSortingObjects(Material.WOOD, Material.WOOL, Material.SPONGE, Material.ICE_SOLID, Material.ICE, Material.LEAVES, Material.PLANT, Material.CLOTH_DECORATION, Material.SNOW, Material.CACTUS, Material.CAKE, Material.REPLACEABLE_PLANT, Material.WEB, Blocks.SNOW, Items.APPLE, Items.BOW, Items.BOWL, Items.ARROW, Items.APPLE, Items.STRING, Items.FEATHER, Items.WHEAT, Items.BREAD, Items.PAINTING, Items.ACACIA_BOAT, Items.ACACIA_DOOR, Items.DARK_OAK_BOAT, Items.DARK_OAK_DOOR, Items.BIRCH_BOAT, Items.BIRCH_DOOR, Items.JUNGLE_BOAT, Items.JUNGLE_DOOR, Items.OAK_BOAT, Items.OAK_DOOR, Items.SPRUCE_BOAT, Items.SPRUCE_DOOR, Items.SADDLE, Items.BONE, Items.SUGAR, Items.EGG, Items.FISHING_ROD, Items.CAKE, Items.MELON, Items.SHEARS, Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BAKED_POTATO, Items.PUMPKIN_PIE, Items.ELYTRA, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SEEDS, Items.BEETROOT_SOUP, Items.SHIELD, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.SNOWBALL);
            
            burningItems
                    .addSortingObjects(Material.WOOD, Material.WOOL, Material.SPONGE, Material.ICE_SOLID, Material.ICE, Material.LEAVES, Material.PLANT, Material.CLOTH_DECORATION, Material.SNOW, Material.CACTUS, Material.CAKE, Material.REPLACEABLE_PLANT, Material.WEB, Material.DIRT, Blocks.SNOW, Items.APPLE, Items.BOW, Items.BOWL, Items.ARROW, Items.APPLE, Items.STRING, Items.FEATHER, Items.WHEAT, Items.BREAD, Items.LEATHER, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.LEAD, Items.PAINTING, Items.ACACIA_BOAT, Items.ACACIA_DOOR, Items.DARK_OAK_BOAT, Items.DARK_OAK_DOOR, Items.BIRCH_BOAT, Items.BIRCH_DOOR, Items.JUNGLE_BOAT, Items.JUNGLE_DOOR, Items.OAK_BOAT, Items.OAK_DOOR, Items.SPRUCE_BOAT, Items.SPRUCE_DOOR, Items.SADDLE, Items.BONE, Items.SUGAR, Items.PAPER, Items.BOOK, Items.EGG, Items.FISHING_ROD, Items.CAKE, Items.MELON, Items.SHEARS, Items.WRITABLE_BOOK, Items.WRITTEN_BOOK, Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BAKED_POTATO, Items.MAP, Items.PUMPKIN_PIE, Items.NAME_TAG, Items.ENCHANTED_BOOK, Items.ELYTRA, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SEEDS, Items.BEETROOT_SOUP, Items.SHIELD, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, new CreativeIngredientFuel(), Items.SPIDER_EYE, Items.ROTTEN_FLESH, Items.SNOWBALL);
            
            undestroyableItems.addSortingObjects(Items.NETHER_STAR, Blocks.BEDROCK, Blocks.OBSIDIAN, Material.BARRIER);
        }
        
    }
    
    public static class Pickup {
        
        @CreativeConfig
        public boolean customPickup = false;
        @CreativeConfig
        public boolean pickupWhenSneaking = true;
        @CreativeConfig
        public float maximumPickupRange = 5;
        
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
