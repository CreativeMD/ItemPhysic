package com.creativemd.itemphysic.physics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.creativemd.itemphysic.ItemDummyContainer;
import com.creativemd.itemphysic.ItemTransformer;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ServerPhysic {
	
	public static Random rand = new Random();
	
	public static ArrayList swimItem = new ArrayList(); //Can be Material, Block, Item, Stack, String(Contains)
	public static ArrayList burnItem = new ArrayList(); //Can be Material, Block, Item, Stack, String(Contains)
	
	public static void loadItemList()
	{
		swimItem.add(Material.WOOD);
		swimItem.add(Material.CLOTH);
		swimItem.add(Material.SPONGE);
		swimItem.add(Material.PACKED_ICE);
		swimItem.add(Material.ICE);
		swimItem.add(Material.LEAVES);
		swimItem.add(Material.PLANTS);
		swimItem.add(Material.CARPET);
		swimItem.add(Material.SNOW);
		swimItem.add(Material.CACTUS);
		swimItem.add(Material.CAKE);
		swimItem.add(Material.VINE);
		swimItem.add(Material.WEB);
		swimItem.add(Material.GRASS);
		swimItem.add(Blocks.SNOW);
		swimItem.add("axe");
		swimItem.add("shovel");
		swimItem.add("hoe");
		swimItem.add("sword");
		swimItem.add(Items.APPLE);
		swimItem.add(Items.BOW);
		swimItem.add(Items.BOWL);
		swimItem.add(Items.ARROW);
		swimItem.add(Items.APPLE);
		swimItem.add(Items.STRING);
		swimItem.add(Items.FEATHER);
		swimItem.add(Items.WHEAT);
		swimItem.add(Items.BREAD);
		swimItem.add(Items.LEATHER);
		swimItem.add(Items.LEATHER_BOOTS);
		swimItem.add(Items.LEATHER_CHESTPLATE);
		swimItem.add(Items.LEATHER_HELMET);
		swimItem.add(Items.LEATHER_LEGGINGS);
		swimItem.add(Items.LEAD);
		swimItem.add(Items.PAINTING);
		swimItem.add(Items.SIGN);
		swimItem.add(Items.ACACIA_BOAT);
		swimItem.add(Items.ACACIA_DOOR);
		swimItem.add(Items.DARK_OAK_BOAT);
		swimItem.add(Items.DARK_OAK_DOOR);
		swimItem.add(Items.BIRCH_BOAT);
		swimItem.add(Items.BIRCH_DOOR);
		swimItem.add(Items.JUNGLE_BOAT);
		swimItem.add(Items.JUNGLE_DOOR);
		swimItem.add(Items.BOAT);
		swimItem.add(Items.OAK_DOOR);
		swimItem.add(Items.SPRUCE_BOAT);
		swimItem.add(Items.SPRUCE_DOOR);
		swimItem.add(Items.SADDLE);
		swimItem.add(Items.BONE);
		swimItem.add(Items.SUGAR);
		swimItem.add(Items.PAPER);
		swimItem.add(Items.BOOK);
		swimItem.add(Items.EGG);
		swimItem.add(Items.FISHING_ROD);
		swimItem.add(Items.DYE);
		swimItem.add(Items.CAKE);
		swimItem.add(Items.BED);
		swimItem.add(Items.BREAD);
		swimItem.add(Items.MELON);
		swimItem.add(Items.SHEARS);
		swimItem.add(Items.WRITABLE_BOOK);
		swimItem.add(Items.WRITTEN_BOOK);
		swimItem.add(Items.CARROT);
		swimItem.add(Items.POTATO);
		swimItem.add(Items.POISONOUS_POTATO);
		swimItem.add(Items.BAKED_POTATO);
		swimItem.add(Items.MAP);
		swimItem.add(Items.PUMPKIN_PIE);
		swimItem.add(Items.NAME_TAG);
		swimItem.add(Items.ENCHANTED_BOOK);
		swimItem.add(Items.ELYTRA);
		swimItem.add(Items.MUTTON);
		swimItem.add(Items.COOKED_MUTTON);
		swimItem.add(Items.RABBIT);
		swimItem.add(Items.COOKED_RABBIT);
		swimItem.add(Items.RABBIT_STEW);
		swimItem.add(Items.BEETROOT);
		swimItem.add(Items.BEETROOT_SEEDS);
		swimItem.add(Items.BEETROOT_SOUP);
		swimItem.add(Items.SHIELD);
		swimItem.add(Items.WHEAT_SEEDS);
		swimItem.add(Items.PUMPKIN_SEEDS);
		swimItem.add(Items.MELON_SEEDS);
		
		burnItem.add(Material.WOOD);
		burnItem.add(Material.CLOTH);
		burnItem.add(Material.SPONGE);
		burnItem.add(Material.PACKED_ICE);
		burnItem.add(Material.ICE);
		burnItem.add(Material.LEAVES);
		burnItem.add(Material.PLANTS);
		burnItem.add(Material.CARPET);
		burnItem.add(Material.SNOW);
		burnItem.add(Material.CACTUS);
		burnItem.add(Material.CAKE);
		burnItem.add(Material.VINE);
		burnItem.add(Material.WEB);
		burnItem.add(Material.GRASS);
		burnItem.add(Blocks.SNOW);
		burnItem.add("axe");
		burnItem.add("shovel");
		burnItem.add("hoe");
		burnItem.add("sword");
		burnItem.add(Items.APPLE);
		burnItem.add(Items.BOW);
		burnItem.add(Items.BOWL);
		burnItem.add(Items.ARROW);
		burnItem.add(Items.APPLE);
		burnItem.add(Items.STRING);
		burnItem.add(Items.FEATHER);
		burnItem.add(Items.WHEAT);
		burnItem.add(Items.BREAD);
		burnItem.add(Items.LEATHER);
		burnItem.add(Items.LEATHER_BOOTS);
		burnItem.add(Items.LEATHER_CHESTPLATE);
		burnItem.add(Items.LEATHER_HELMET);
		burnItem.add(Items.LEATHER_LEGGINGS);
		burnItem.add(Items.LEAD);
		burnItem.add(Items.PAINTING);
		burnItem.add(Items.SIGN);
		burnItem.add(Items.ACACIA_BOAT);
		burnItem.add(Items.ACACIA_DOOR);
		burnItem.add(Items.DARK_OAK_BOAT);
		burnItem.add(Items.DARK_OAK_DOOR);
		burnItem.add(Items.BIRCH_BOAT);
		burnItem.add(Items.BIRCH_DOOR);
		burnItem.add(Items.JUNGLE_BOAT);
		burnItem.add(Items.JUNGLE_DOOR);
		burnItem.add(Items.BOAT);
		burnItem.add(Items.OAK_DOOR);
		burnItem.add(Items.SPRUCE_BOAT);
		burnItem.add(Items.SPRUCE_DOOR);
		burnItem.add(Items.SADDLE);
		burnItem.add(Items.BONE);
		burnItem.add(Items.SUGAR);
		burnItem.add(Items.PAPER);
		burnItem.add(Items.BOOK);
		burnItem.add(Items.EGG);
		burnItem.add(Items.FISHING_ROD);
		burnItem.add(Items.DYE);
		burnItem.add(Items.CAKE);
		burnItem.add(Items.BED);
		burnItem.add(Items.BREAD);
		burnItem.add(Items.MELON);
		burnItem.add(Items.SHEARS);
		burnItem.add(Items.WRITABLE_BOOK);
		burnItem.add(Items.WRITTEN_BOOK);
		burnItem.add(Items.CARROT);
		burnItem.add(Items.POTATO);
		burnItem.add(Items.POISONOUS_POTATO);
		burnItem.add(Items.BAKED_POTATO);
		burnItem.add(Items.MAP);
		burnItem.add(Items.PUMPKIN_PIE);
		burnItem.add(Items.NAME_TAG);
		burnItem.add(Items.ENCHANTED_BOOK);
		burnItem.add(Items.ELYTRA);
		burnItem.add(Items.MUTTON);
		burnItem.add(Items.COOKED_MUTTON);
		burnItem.add(Items.RABBIT);
		burnItem.add(Items.COOKED_RABBIT);
		burnItem.add(Items.RABBIT_STEW);
		burnItem.add(Items.BEETROOT);
		burnItem.add(Items.BEETROOT_SEEDS);
		burnItem.add(Items.BEETROOT_SOUP);
		burnItem.add(Items.SHIELD);
		burnItem.add(Items.WHEAT_SEEDS);
		burnItem.add(Items.PUMPKIN_SEEDS);
		burnItem.add(Items.MELON_SEEDS);
	}
	
	public static DataParameter<Optional<ItemStack>> ITEM = null;
	
	
	//replace with if (!this.func_189652_ae()) { this.motionY -= 0.03999999910593033D; } 
	public static void updatePre(EntityItem item)
	{
		ItemStack stack = item.getEntityItem();
		float f = 0.98F;
        fluid = getFluid(item);
        if(fluid == null)
        {
        	item.motionY -= 0.04D;
        }else{
        	double density = (double)fluid.getDensity()/1000D;
        	double speed = - 1/density * 0.01;
        	if(canItemSwim(stack))
            	speed = 0.05;
        	
        	double speedreduction = (speed-item.motionY)/2;
        	double maxSpeedReduction = 0.05;
        	if(speedreduction < -maxSpeedReduction)
        		speedreduction = -maxSpeedReduction;
        	if(speedreduction > maxSpeedReduction)
        		speedreduction = maxSpeedReduction;
        	item.motionY += speedreduction;
        	f = (float) (1D/density/1.2);
        }
	}
	
	//replace with: if (this.worldObj.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) { this.motionY = 0.20000000298023224D; this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F); this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F); this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F); }
	public static void updateBurn(EntityItem item)
	{
		if (item.worldObj.getBlockState(new BlockPos(item)).getMaterial() == Material.LAVA && canItemBurn(item.getEntityItem()))
        {
    		item.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
            for(int zahl = 0; zahl < 100; zahl++)
            	item.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, item.posX, item.posY, item.posZ, (rand.nextFloat()*0.1)-0.05, 0.2*rand.nextDouble(), (rand.nextFloat()*0.1)-0.05);
        }
	}
	
	public static Fluid fluid;
	
	//Remove this.motionY *= 0.9800000190734863D;
	//Replace with: if (this.onGround){ this.motionY *= -0.5D; }
	public static void updatePost(EntityItem item)
	{
		if(fluid == null)
        {
            item.motionY *= 0.98D;
            
            if (item.onGround)
            {
            	item.motionY *= -0.5D;
            }
        }
	}
	
	public static int getAge(EntityItem item)
	{
		return (Integer) ReflectionHelper.getPrivateValue(EntityItem.class, item, "age", "field_70292_b");
	}
	
	public static void onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer)
    {
		onCollideWithPlayer(item, par1EntityPlayer, true);
    }
	
	public static void onCollideWithPlayer(EntityItem item, EntityPlayer player, boolean needsSneak)
    {
		if(ItemDummyContainer.customPickup && needsSneak && !player.isSneaking())
			return;
        if (!item.worldObj.isRemote)
        {
            if (!ItemDummyContainer.customPickup && item.cannotPickup())
            	return;
            ItemStack itemstack = item.getEntityItem();
            int i = itemstack.stackSize;

            int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(item, player, itemstack);
            if (hook < 0) return;

            if ((!item.cannotPickup() || ItemDummyContainer.customPickup) && (item.getOwner() == null || item.lifespan - getAge(item) <= 200 || item.getOwner().equals(player.getName())) && (hook == 1 || i <= 0 || player.inventory.addItemStackToInventory(itemstack)))
            {
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.LOG))
                {
                	player.addStat(AchievementList.MINE_WOOD);
                }

                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.LOG2))
                {
                	player.addStat(AchievementList.MINE_WOOD);
                }

                if (itemstack.getItem() == Items.LEATHER)
                {
                	player.addStat(AchievementList.KILL_COW);
                }

                if (itemstack.getItem() == Items.DIAMOND)
                {
                	player.addStat(AchievementList.DIAMONDS);
                }

                if (itemstack.getItem() == Items.BLAZE_ROD)
                {
                	player.addStat(AchievementList.BLAZE_ROD);
                }

                if (itemstack.getItem() == Items.DIAMOND && item.getThrower() != null)
                {
                    EntityPlayer entityplayer = item.worldObj.getPlayerEntityByName(item.getThrower());

                    if (entityplayer != null && entityplayer != player)
                    {
                        entityplayer.addStat(AchievementList.DIAMONDS_TO_YOU);
                    }
                }

                net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player, item);
                if (!item.isSilent())
                {
                	item.worldObj.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }

                player.onItemPickup(item, i);

                if (itemstack.stackSize <= 0)
                {
                    item.setDead();
                }

                player.addStat(StatList.getObjectsPickedUpStats(itemstack.getItem()), i);
            }
        }
    }
	
	public static boolean processInitialInteract(EntityItem item, EntityPlayer player, ItemStack stack, EnumHand hand)
    {
		if(ItemDummyContainer.customPickup)
		{
			onCollideWithPlayer(item, player, false);
			return true;
		}
        return false;
    }
	
	public static boolean attackEntityFrom(EntityItem item, DamageSource source, float amount)
    {
		if (item.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (item.getEntityItem() != null && item.getEntityItem().getItem() == Items.NETHER_STAR && source.isExplosion() && canItemBurn(item.getEntityItem()))
        {
            return false;
        }
        else
        {
        	if((source == DamageSource.lava | source == DamageSource.onFire | source == DamageSource.inFire) && !canItemBurn(item.getEntityItem()))return false;
        	if(source == DamageSource.cactus)return false;
        	
        	try {
				ReflectionHelper.findMethod(Entity.class, item, new String[]{"setBeenAttacked", "func_70018_K"}).invoke(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	//item.setBeenAttacked();
        	try {
	        	Field health = ReflectionHelper.findField(EntityItem.class, "health", "field_70291_e");
	        	health.setInt(item, (int)((float)health.getInt(item) - amount));
	        	
	            if (health.getInt(item) <= 0)
	            {
	            	item.setDead();
	            }
	        } catch (Exception e) {
				e.printStackTrace();
			}

            return false;
        }
    }
	
	public static boolean isItemBurning(EntityItem item)
	{
		boolean flag = item.worldObj != null && item.worldObj.isRemote;
		try{
	        if(!(!item.isImmuneToFire() && ((Integer) ReflectionHelper.getPrivateValue(Entity.class, item, "fire", "field_70151_c") > 0 || flag && (Boolean)ReflectionHelper.findMethod(Entity.class, item, new String[]{"getFlag", "func_70083_f"}, int.class).invoke(item, 0))))
	        	return false;
		}catch(Exception e){
			e.printStackTrace();
		}
        return canItemBurn(item.getEntityItem());
	}
	
	public static Fluid getFluid(EntityItem item)
    {
		return getFluid(item, false);
    }
	
	public static Fluid getFluid(EntityItem item, boolean below)
    {
        double d0 = item.posY + (double)item.getEyeHeight();
        int i = MathHelper.floor_double(item.posX);
        int j = MathHelper.floor_float((float)MathHelper.floor_double(d0));
        if(below)
        	j--;
        int k = MathHelper.floor_double(item.posZ);
        BlockPos pos = new BlockPos(i, j, k);
        Block block = item.worldObj.getBlockState(pos).getBlock();
        
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if(fluid == null && block instanceof IFluidBlock)
        	fluid = ((IFluidBlock)block).getFluid();
        else if (block instanceof BlockLiquid)
        	fluid = FluidRegistry.WATER;
        
        if(below)
        	return fluid;
        
        double filled = 1.0f; //If it's not a liquid assume it's a solid block
        if (block instanceof IFluidBlock)
        {
            filled = ((IFluidBlock)block).getFilledPercentage(item.worldObj, pos);
        }

        if (filled < 0)
        {
            filled *= -1;
            //filled -= 0.11111111F; //Why this is needed.. not sure...
            if(d0 > (double)(j + (1 - filled)))
            	return fluid;
        }
        else
        {
            if(d0 < (double)(j + filled))
            	return fluid;
        }
        return null;
    }
	
	public static boolean canItemSwim(ItemStack stack)
	{
		return contains(swimItem, stack);
	}
	
	public static boolean canItemBurn(ItemStack stack)
	{
		if(TileEntityFurnace.isItemFuel(stack))
			return true;
		return contains(burnItem, stack);
	}
	
	public static boolean contains(ArrayList list, ItemStack stack)
	{
		if(stack == null || stack.getItem() == null)
			return false;
			
		Object object = stack.getItem();
		
		Material material = null;
		
		if(object instanceof ItemBlock)
		{
			object = Block.getBlockFromItem((Item) object);
			material = ((Block)object).getMaterial(null);
		}
		
		int[] ores = OreDictionary.getOreIDs(stack);
		
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i) instanceof ItemStack && ItemStack.areItemStacksEqual(stack, (ItemStack) list.get(i)))
				return true;
			
			if(list.get(i) == object)
				return true;
			
			if(list.get(i) == material)
				return true;
			
			if(list.get(i) instanceof String)
			for (int j = 0; j < ores.length; j++) {
				if(OreDictionary.getOreName(ores[j]).contains((CharSequence) list.get(i)))
					return true;
			}
		}
		return false;
	}
}
