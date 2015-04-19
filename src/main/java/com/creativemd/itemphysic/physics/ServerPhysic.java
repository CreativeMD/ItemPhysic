package com.creativemd.itemphysic.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.creativemd.itemphysic.ItemDummyContainer;
import com.creativemd.itemphysic.ItemTransformer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;

public class ServerPhysic {
	
	public static Random random = new Random();
	
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
		swimItem.add(Items.wooden_door);
		swimItem.add(Items.saddle);
		swimItem.add(Items.boat);
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
		burnItem.add(Items.wooden_door);
		burnItem.add(Items.saddle);
		burnItem.add(Items.boat);
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
	}
	
	public static void update(EntityItem item)
	{
		ItemStack stack = item.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null)
        {
            if (stack.getItem().onEntityItemUpdate(item))
            {
                return;
            }
        }
        
        if (item.getEntityItem() == null)
        {
            item.setDead();
        }
        else
        {
            item.onEntityUpdate();
            
            if (item.delayBeforeCanPickup > 0)
            {
                --item.delayBeforeCanPickup;
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
            
            item.noClip = func_145771_j(item, item.posX, (item.boundingBox.minY + item.boundingBox.maxY) / 2.0D, item.posZ);
            item.moveEntity(item.motionX, item.motionY, item.motionZ);
            boolean flag = (int)item.prevPosX != (int)item.posX || (int)item.prevPosY != (int)item.posY || (int)item.prevPosZ != (int)item.posZ;
            
            if (flag || item.ticksExisted % 25 == 0)
            {
                if (item.worldObj.getBlock(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.posY), MathHelper.floor_double(item.posZ)).getMaterial() == Material.lava && canItemBurn(stack))
                {
                	item.playSound("random.fizz", 0.4F, 2.0F + random.nextFloat() * 0.4F);
                    for(int zahl = 0; zahl < 100; zahl++)
                    	item.worldObj.spawnParticle("smoke", item.posX, item.posY, item.posZ, (random.nextFloat()*0.1)-0.05, 0.2*random.nextDouble(), (random.nextFloat()*0.1)-0.05);
                }

                if (!item.worldObj.isRemote)
                {
                    searchForOtherItemsNearby(item);
                }
            }
            
            if (item.onGround)
            {
                f = item.worldObj.getBlock(MathHelper.floor_double(item.posX), MathHelper.floor_double(item.boundingBox.minY) - 1, MathHelper.floor_double(item.posZ)).slipperiness * 0.98F;
            }
            
            item.motionX *= (double)f;
            item.motionZ *= (double)f;
            
            if(fluid == null)
            {           
	            
	            item.motionY *= 0.98D;
	            
	            
	            if (item.onGround)
	            {
	            	item.motionY *= -0.5D;
	            }
            }
            
            if(item.age < 1 && item.lifespan == 6000)
            	item.lifespan = ItemDummyContainer.despawnItem;
            
            ++item.age;
            
            if (!item.worldObj.isRemote && item.age >= item.lifespan)
            {
                if (stack != null)
                {   
                    ItemExpireEvent event = new ItemExpireEvent(item, (stack.getItem() == null ? 6000 : stack.getItem().getEntityLifespan(stack, item.worldObj)));
                    if (MinecraftForge.EVENT_BUS.post(event))
                    {
                        item.lifespan += event.extraLife;
                    }
                    else
                    {
                        item.setDead();
                    }
                }
                else
                {
                    item.setDead();
                }
            }
            
            if (stack != null && stack.stackSize <= 0)
            {
                item.setDead();
            }
        }
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
        Block block = item.worldObj.getBlock(i, j, k);
        
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
            filled = ((IFluidBlock)block).getFilledPercentage(item.worldObj, i, j, k);
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
	
	public static double lastPosY;
	
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
    }
	
	private static void searchForOtherItemsNearby(EntityItem item)
    {
        Iterator iterator = item.worldObj.getEntitiesWithinAABB(EntityItem.class, item.boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

        while (iterator.hasNext())
        {
            EntityItem entityitem = (EntityItem)iterator.next();
            item.combineItems(entityitem);
        }
    }
	
	public static boolean func_145771_j(EntityItem item, double p_145771_1_, double p_145771_3_, double p_145771_5_)
    {
        int i = MathHelper.floor_double(p_145771_1_);
        int j = MathHelper.floor_double(p_145771_3_);
        int k = MathHelper.floor_double(p_145771_5_);
        double d3 = p_145771_1_ - (double)i;
        double d4 = p_145771_3_ - (double)j;
        double d5 = p_145771_5_ - (double)k;
        List list = item.worldObj.func_147461_a(item.boundingBox);

        if (list.isEmpty() && !item.worldObj.func_147469_q(i, j, k))
        {
            return false;
        }
        else
        {
            boolean flag = !item.worldObj.func_147469_q(i - 1, j, k);
            boolean flag1 = !item.worldObj.func_147469_q(i + 1, j, k);
            boolean flag2 = !item.worldObj.func_147469_q(i, j - 1, k);
            boolean flag3 = !item.worldObj.func_147469_q(i, j + 1, k);
            boolean flag4 = !item.worldObj.func_147469_q(i, j, k - 1);
            boolean flag5 = !item.worldObj.func_147469_q(i, j, k + 1);
            byte b0 = 3;
            double d6 = 9999.0D;

            if (flag && d3 < d6)
            {
                d6 = d3;
                b0 = 0;
            }

            if (flag1 && 1.0D - d3 < d6)
            {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if (flag3 && 1.0D - d4 < d6)
            {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if (flag4 && d5 < d6)
            {
                d6 = d5;
                b0 = 4;
            }

            if (flag5 && 1.0D - d5 < d6)
            {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            float f = random.nextFloat() * 0.2F + 0.1F;

            if (b0 == 0)
            {
                item.motionX = (double)(-f);
            }

            if (b0 == 1)
            {
                item.motionX = (double)f;
            }

            if (b0 == 2)
            {
                item.motionY = (double)(-f);
            }

            if (b0 == 3)
            {
                item.motionY = (double)f;
            }

            if (b0 == 4)
            {
                item.motionZ = (double)(-f);
            }

            if (b0 == 5)
            {
                item.motionZ = (double)f;
            }

            return true;
        }
    }
	
	public static void onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer)
    {
		onCollideWithPlayer(item, par1EntityPlayer, true);
    }
	
	public static void onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer, boolean needsSneak)
    {
		if(ItemDummyContainer.customPickup && needsSneak && !par1EntityPlayer.isSneaking())
			return;
        if (!item.worldObj.isRemote)
        {
            if (!ItemDummyContainer.customPickup && item.delayBeforeCanPickup > 0)
            {
                return;
            }

            EntityItemPickupEvent event = new EntityItemPickupEvent(par1EntityPlayer, item);

            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return;
            }

            ItemStack itemstack = item.getEntityItem();
            int i = itemstack.stackSize;

            if ((ItemDummyContainer.customPickup | item.delayBeforeCanPickup <= 0) && (item.func_145798_i() == null || item.lifespan - item.age <= 200 || item.func_145798_i().equals(par1EntityPlayer.getCommandSenderName())) && (event.getResult() == Result.ALLOW || i <= 0 || par1EntityPlayer.inventory.addItemStackToInventory(itemstack)))
            {
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.log))
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                }

                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.log2))
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                }

                if (itemstack.getItem() == Items.leather)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                }

                if (itemstack.getItem() == Items.diamond)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                }

                if (itemstack.getItem() == Items.blaze_rod)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                }

                if (itemstack.getItem() == Items.diamond && item.func_145800_j() != null)
                {
                    EntityPlayer entityplayer1 = item.worldObj.getPlayerEntityByName(item.func_145800_j());

                    if (entityplayer1 != null && entityplayer1 != par1EntityPlayer)
                    {
                        entityplayer1.triggerAchievement(AchievementList.field_150966_x);
                    }
                }

                FMLCommonHandler.instance().firePlayerItemPickupEvent(par1EntityPlayer, item);

                item.worldObj.playSoundAtEntity(par1EntityPlayer, "random.pop", 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(item, i);

                if (itemstack.stackSize <= 0)
                {
                	item.setDead();
                }
            }
        }
    }
	
	public static boolean interactFirst(EntityItem item, EntityPlayer par1EntityPlayer)
    {
		if(ItemDummyContainer.customPickup)
		{
			onCollideWithPlayer(item, par1EntityPlayer, false);
			return true;
		}
        return false;
    }
	
	public static boolean attackEntityFrom(EntityItem item, DamageSource par1DamageSource, float par2)
    {
		if (item.isEntityInvulnerable())
        {
            return false;
        }
        else if (item.getEntityItem() != null && item.getEntityItem().getItem() == Items.nether_star && par1DamageSource.isExplosion() && canItemBurn(item.getEntityItem()))
        {
            return false;
        }
        else
        {
        	if((par1DamageSource == DamageSource.lava | par1DamageSource == DamageSource.onFire | par1DamageSource == DamageSource.inFire) && !canItemBurn(item.getEntityItem()))return false;
        	if(par1DamageSource == DamageSource.cactus)return false;
        	
        	setHealth(item, (int) (getHealth(item) - 1));
        	
            if (getHealth(item)  <= 0)
            {
                item.setDead();
            }

            return false;
        }
    }
	
	public static int getHealth(EntityItem item)
	{
		boolean obfuscated = false;
		try{
			obfuscated = EntityItem.class.getField("health") == null;
		} catch(Exception e){
			obfuscated = true;	
		}
		String name = ItemTransformer.patchT("health", obfuscated);
		try {
			return EntityItem.class.getField(name).getInt(item);
		} catch (Exception e){
			try {
				return EntityItem.class.getField(name).getInt(item);
			} catch (Exception e1){
				System.out.println("Field not found health (" + name + ")");
				return 0;
			}
		}
	}
	
	public static void setHealth(EntityItem item, int health)
	{
		boolean obfuscated = false;
		try{
			obfuscated = EntityItem.class.getField("health") == null;
		} catch(Exception e){
			obfuscated = true;	
		}
		String name = ItemTransformer.patchT("health", obfuscated);
		try {
			EntityItem.class.getField(name).setInt(item, health);
		} catch (Exception e){
			try {
				EntityItem.class.getField(name).setInt(item, health);
			} catch (Exception e1){
				System.out.println("Field not found health (" + name + ")");
			}
		}
	}
	
	public static boolean isItemBurning(EntityItem item)
	{
		boolean flag = item.worldObj != null && item.worldObj.isRemote;
        if(!(!item.isImmuneToFire() && (flag && (item.getDataWatcher().getWatchableObjectByte(0) & 1 << 0) != 0)))
        	return false;
        return canItemBurn(item.getEntityItem());
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
