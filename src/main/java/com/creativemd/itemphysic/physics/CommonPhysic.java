package com.creativemd.itemphysic.physics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public class CommonPhysic {
	
	public static Fluid getFluid(EntityItem item)
    {
		return getFluid(item, false);
    }
	
	public static Fluid getFluid(EntityItem item, boolean below)
    {
		if(item.worldObj == null)
        	return null;
		
        double d0 = item.posY + (double)item.getEyeHeight();
        int i = MathHelper.floor_double(item.posX);
        int j = MathHelper.floor_double((float)MathHelper.floor_double(d0));
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
	
}
