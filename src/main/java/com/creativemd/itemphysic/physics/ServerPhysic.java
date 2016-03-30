package com.creativemd.itemphysic.physics;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import com.creativemd.itemphysic.ItemDummyContainer;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ServerPhysic {
	
	public static Random rand = new Random();
	
	public static ArrayList swimItem = new ArrayList(); //Can be Material, Block, Item, Stack, String(Contains)
	public static ArrayList burnItem = new ArrayList(); //Can be Material, Block, Item, Stack, String(Contains)
	
	public static void loadItemList()
	{
		swimItem.add(Material.wood);
		swimItem.add(Material.cloth);
		swimItem.add(Material.sponge);
		swimItem.add(Material.packedIce);
		swimItem.add(Material.ice);
		swimItem.add(Material.leaves);
		swimItem.add(Material.plants);
		swimItem.add(Material.carpet);
		swimItem.add(Material.snow);
		swimItem.add(Material.cactus);
		swimItem.add(Material.cake);
		swimItem.add(Material.vine);
		swimItem.add(Material.web);
		swimItem.add(Material.grass);
		swimItem.add("axe");
		swimItem.add("shovel");
		swimItem.add("hoe");
		swimItem.add("sword");
		swimItem.add(Items.apple);
		swimItem.add(Items.bow);
		swimItem.add(Items.bowl);
		swimItem.add(Items.arrow);
		swimItem.add(Items.apple);
		swimItem.add(Items.string);
		swimItem.add(Items.feather);
		swimItem.add(Items.wheat);
		swimItem.add(Items.bread);
		swimItem.add(Items.leather);
		swimItem.add(Items.leather_boots);
		swimItem.add(Items.leather_chestplate);
		swimItem.add(Items.leather_helmet);
		swimItem.add(Items.leather_leggings);
		swimItem.add(Items.lead);
		swimItem.add(Items.painting);
		swimItem.add(Items.sign);
		swimItem.add(Items.acacia_door);
		swimItem.add(Items.dark_oak_door);
		swimItem.add(Items.birch_door);
		swimItem.add(Items.jungle_door);
		swimItem.add(Items.boat);
		swimItem.add(Items.oak_door);
		swimItem.add(Items.spruce_door);
		swimItem.add(Items.saddle);
		swimItem.add(Items.bone);
		swimItem.add(Items.sugar);
		swimItem.add(Items.paper);
		swimItem.add(Items.book);
		swimItem.add(Items.egg);
		swimItem.add(Items.fishing_rod);
		swimItem.add(Items.dye);
		swimItem.add(Items.cake);
		swimItem.add(Items.bed);
		swimItem.add(Items.bread);
		swimItem.add(Items.melon);
		swimItem.add(Items.shears);
		swimItem.add(Items.writable_book);
		swimItem.add(Items.written_book);
		swimItem.add(Items.carrot);
		swimItem.add(Items.potato);
		swimItem.add(Items.poisonous_potato);
		swimItem.add(Items.baked_potato);
		swimItem.add(Items.map);
		swimItem.add(Items.pumpkin_pie);
		swimItem.add(Items.name_tag);
		swimItem.add(Items.enchanted_book);
		swimItem.add(Items.mutton);
		swimItem.add(Items.cooked_mutton);
		swimItem.add(Items.rabbit);
		swimItem.add(Items.cooked_rabbit);
		swimItem.add(Items.rabbit_stew);
		
		burnItem.add(Material.wood);
		burnItem.add(Material.cloth);
		burnItem.add(Material.sponge);
		burnItem.add(Material.packedIce);
		burnItem.add(Material.ice);
		burnItem.add(Material.leaves);
		burnItem.add(Material.plants);
		burnItem.add(Material.carpet);
		burnItem.add(Material.snow);
		burnItem.add(Material.cactus);
		burnItem.add(Material.cake);
		burnItem.add(Material.vine);
		burnItem.add(Material.web);
		burnItem.add(Material.grass);
		burnItem.add("axe");
		burnItem.add("shovel");
		burnItem.add("hoe");
		burnItem.add("sword");
		burnItem.add(Items.apple);
		burnItem.add(Items.bow);
		burnItem.add(Items.bowl);
		burnItem.add(Items.arrow);
		burnItem.add(Items.apple);
		burnItem.add(Items.string);
		burnItem.add(Items.feather);
		burnItem.add(Items.wheat);
		burnItem.add(Items.bread);
		burnItem.add(Items.leather);
		burnItem.add(Items.leather_boots);
		burnItem.add(Items.leather_chestplate);
		burnItem.add(Items.leather_helmet);
		burnItem.add(Items.leather_leggings);
		burnItem.add(Items.lead);
		burnItem.add(Items.painting);
		burnItem.add(Items.sign);
		burnItem.add(Items.acacia_door);
		burnItem.add(Items.dark_oak_door);
		burnItem.add(Items.birch_door);
		burnItem.add(Items.jungle_door);
		burnItem.add(Items.boat);
		burnItem.add(Items.oak_door);
		burnItem.add(Items.spruce_door);
		burnItem.add(Items.saddle);
		burnItem.add(Items.bone);
		burnItem.add(Items.sugar);
		burnItem.add(Items.paper);
		burnItem.add(Items.book);
		burnItem.add(Items.egg);
		burnItem.add(Items.fishing_rod);
		burnItem.add(Items.dye);
		burnItem.add(Items.cake);
		burnItem.add(Items.bed);
		burnItem.add(Items.bread);
		burnItem.add(Items.melon);
		burnItem.add(Items.shears);
		burnItem.add(Items.writable_book);
		burnItem.add(Items.written_book);
		burnItem.add(Items.carrot);
		burnItem.add(Items.potato);
		burnItem.add(Items.poisonous_potato);
		burnItem.add(Items.baked_potato);
		burnItem.add(Items.map);
		burnItem.add(Items.pumpkin_pie);
		burnItem.add(Items.name_tag);
		burnItem.add(Items.enchanted_book);
		burnItem.add(Items.mutton);
		burnItem.add(Items.cooked_mutton);
		burnItem.add(Items.rabbit);
		burnItem.add(Items.cooked_rabbit);
		burnItem.add(Items.rabbit_stew);
	}
	
	public static DataParameter<Optional<ItemStack>> ITEM = null;
	
	public static void update(EntityItem item)
	{
		if(ITEM == null)
			ITEM = ReflectionHelper.getPrivateValue(EntityItem.class, null, "ITEM", "field_184525_c", "field_184533_c");
		ItemStack stack = item.getDataManager().get(ITEM).orNull();
        if (stack != null && stack.getItem() != null && stack.getItem().onEntityItemUpdate(item)) return;
        if (item.getEntityItem() == null)
        {
            item.setDead();
        }
        else
        {
        	
        	if (!item.worldObj.isRemote)
            {
        		try{
        			ReflectionHelper.findMethod(Entity.class, item, new String[]{"setFlag", "func_70052_a"}, int.class, boolean.class).invoke(item, 6, item.isGlowing());
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        		//item.setFlag(6, item.isGlowing());
            }
            item.onEntityUpdate();
            
            int delay = (Integer)ReflectionHelper.getPrivateValue(EntityItem.class, item, "delayBeforeCanPickup", "delayBeforeCanPickup");
            
            if (delay > 0 && delay != 32767)
            {
            	item.setPickupDelay(delay-1);
               // --item.delayBeforeCanPickup;
            }

            item.prevPosX = item.posX;
            item.prevPosY = item.posY;
            item.prevPosZ = item.posZ;
            
            float f = 0.98F;
            Fluid fluid = getFluid(item);
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
            	
            	/*double range = 0.005;
            	double amount = 50;
            	double speed = -0.05/(density*5);
            	if(item.motionY > range+speed)
            		item.motionY -= density/amount;
            	else if(item.motionY < range-speed)
            		item.motionY += density/amount;
            	else
            		item.motionY = -speed;
            	
            	if(canItemSwim(stack))
            		item.motionY += 0.024*density;*/
            	
            	f = (float) (1D/density/1.2);

            	/*double amount = 0.03*((double)fluid.getDensity()/1000D);
            	if(canItemSwim(stack))
            		amount += 0.05;
            	item.motionY += amount;*/
            }
            
            //item.motionY -= 0.03999999910593033D;
            try{
            	item.noClip = (Boolean) ReflectionHelper.findMethod(Entity.class, item, new String[]{"pushOutOfBlocks", "func_145771_j"}, double.class, double.class, double.class).invoke(item, item.posX, (item.getEntityBoundingBox().minY + item.getEntityBoundingBox().maxY) / 2.0D, item.posZ);
            }catch(Exception e){
            	e.printStackTrace();
            }
            item.moveEntity(item.motionX, item.motionY, item.motionZ);
            boolean flag = (int)item.prevPosX != (int)item.posX || (int)item.prevPosY != (int)item.posY || (int)item.prevPosZ != (int)item.posZ;

            if (flag || item.ticksExisted % 25 == 0)
            {
            	if (item.worldObj.getBlockState(new BlockPos(item)).getMaterial() == Material.lava && canItemBurn(stack))
                {
            		item.playSound(SoundEvents.entity_generic_burn, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
                    for(int zahl = 0; zahl < 100; zahl++)
                    	item.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, item.posX, item.posY, item.posZ, (rand.nextFloat()*0.1)-0.05, 0.2*rand.nextDouble(), (rand.nextFloat()*0.1)-0.05);
                }
            	
                /*if (item.worldObj.getBlockState(new BlockPos(item)).getMaterial() == Material.lava)
                {
                    item.motionY = 0.20000000298023224D;
                    item.motionX = (double)((rand.nextFloat() - rand.nextFloat()) * 0.2F);
                    item.motionZ = (double)((rand.nextFloat() - rand.nextFloat()) * 0.2F);
                    item.playSound(SoundEvents.entity_generic_burn, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
                }*/

                if (!item.worldObj.isRemote)
                {
                	try{
                		ReflectionHelper.findMethod(EntityItem.class, item, new String[]{"searchForOtherItemsNearby", "func_85054_d"}).invoke(item);
                	}catch(Exception e){
                		e.printStackTrace();
                	}
                    //item.searchForOtherItemsNearby();
                }
            }

            //float f = 0.98F;

            if (item.onGround)
            {
                f = item.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(item.posZ))).getBlock().slipperiness * 0.98F;
            }

            item.motionX *= (double)f;
            //item.motionY *= 0.9800000190734863D;
            item.motionZ *= (double)f;

            /*if (item.onGround)
            {
                item.motionY *= -0.5D;
            }*/
            
            if(fluid == null)
            {           
	            
	            item.motionY *= 0.98D;
	            
	            
	            if (item.onGround)
	            {
	            	item.motionY *= -0.5D;
	            }
            }
            
            if(item.getAge() < 1 && item.lifespan == 6000)
            	item.lifespan = ItemDummyContainer.despawnItem;
            
            if (item.getAge() != -32768)
            {
            	try{
            		ReflectionHelper.findField(EntityItem.class, "age", "field_70292_b").set(item, item.getAge()+1);
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }

            item.handleWaterMovement();

            if (!item.worldObj.isRemote && item.getAge() >= item.lifespan)
            {
                int hook = net.minecraftforge.event.ForgeEventFactory.onItemExpire(item, stack);
                if (hook < 0) item.setDead();
                else          item.lifespan += hook;
            }
            if (stack != null && stack.stackSize <= 0)
            {
                item.setDead();
            }
        }
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

            if (!item.cannotPickup() && (item.getOwner() == null || item.lifespan - item.getAge() <= 200 || item.getOwner().equals(player.getName())) && (hook == 1 || i <= 0 || player.inventory.addItemStackToInventory(itemstack)))
            {
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.log))
                {
                	player.addStat(AchievementList.mineWood);
                }

                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.log2))
                {
                	player.addStat(AchievementList.mineWood);
                }

                if (itemstack.getItem() == Items.leather)
                {
                	player.addStat(AchievementList.killCow);
                }

                if (itemstack.getItem() == Items.diamond)
                {
                	player.addStat(AchievementList.diamonds);
                }

                if (itemstack.getItem() == Items.blaze_rod)
                {
                	player.addStat(AchievementList.blazeRod);
                }

                if (itemstack.getItem() == Items.diamond && item.getThrower() != null)
                {
                    EntityPlayer entityplayer = item.worldObj.getPlayerEntityByName(item.getThrower());

                    if (entityplayer != null && entityplayer != player)
                    {
                        entityplayer.addStat(AchievementList.diamondsToYou);
                    }
                }

                net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player, item);
                if (!item.isSilent())
                {
                	item.worldObj.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.entity_item_pickup, SoundCategory.PLAYERS, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }

                player.onItemPickup(item, i);

                if (itemstack.stackSize <= 0)
                {
                    item.setDead();
                }

                player.addStat(StatList.func_188056_d(itemstack.getItem()), i);
            }
        }
    }
	
	public boolean processInitialInteract(EntityItem item, EntityPlayer player, ItemStack stack, EnumHand hand)
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
        else if (item.getEntityItem() != null && item.getEntityItem().getItem() == Items.nether_star && source.isExplosion() && canItemBurn(item.getEntityItem()))
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
    
    /*public static double lastPosY;
	TODO Check if it's necessary!
	public static void updatePositionBefore(EntityItem item)
    {
		lastPosY = item.posY;
    }
	
    public static void updatePosition(EntityItem item, double posY)
    {
		double diff = Math.sqrt(Math.pow(lastPosY - posY, 2));
		if(diff < 0.5D && diff > 0)
		{
			item.setPosition(item.posX, lastPosY, item.posZ);
		}
    }*/
	
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
			material = ((Block)object).getMaterial();
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
