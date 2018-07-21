package com.creativemd.itemphysic.physics;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
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
		if(item.world == null)
        	return null;
		
        double d0 = item.posY + (double)item.getEyeHeight();
        int i = MathHelper.floor(item.posX);
        int j = MathHelper.floor((float)MathHelper.floor(d0));
        if(below)
        	j--;
        int k = MathHelper.floor(item.posZ);
        BlockPos pos = new BlockPos(i, j, k);
        
        IBlockState state = item.world.getBlockState(pos);
        Block block = state.getBlock();
        
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if(fluid == null && block instanceof IFluidBlock)
        	fluid = ((IFluidBlock)block).getFluid();
        else if (block instanceof BlockLiquid)
        	fluid = FluidRegistry.WATER;
        
        if(below)
        	return fluid;
        
        if(fluid == null)
        	return null;
        
        double filled = 1.0f; //If it's not a liquid assume it's a solid block
        if (block instanceof IFluidBlock)
        	filled = ((IFluidBlock)block).getFilledPercentage(item.world, pos);
        else if(block instanceof BlockLiquid)
        	filled = BlockLiquid.getBlockLiquidHeight(state, item.world, pos);
        	
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
