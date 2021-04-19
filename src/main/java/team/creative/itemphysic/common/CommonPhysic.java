package team.creative.itemphysic.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import team.creative.itemphysic.ItemPhysic;

public class CommonPhysic {
    
    public static Fluid getFluid(ItemEntity item) {
        return getFluid(item, false);
    }
    
    public static Fluid getFluid(ItemEntity item, boolean below) {
        if (item.level == null)
            return null;
        
        double d0 = item.getY();
        BlockPos pos = item.blockPosition();
        if (below)
            pos = pos.below();
        
        FluidState state = item.level.getFluidState(pos);
        Fluid fluid = state.getType();
        
        if (fluid == null || fluid.getFluid().getAttributes().getDensity() == 0)
            return null;
        
        if (below)
            return fluid;
        
        double filled = state.getHeight(item.level, pos);
        
        if (d0 - pos.getY() - 0.2 <= filled)
            return fluid;
        return null;
    }
    
    public static double getReachDistance(PlayerEntity player) {
        if (ItemPhysic.CONFIG.pickup.maximumPickupRange != 5)
            return ItemPhysic.CONFIG.pickup.maximumPickupRange;
        float attrib = (float) player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
        return player.isCreative() ? attrib : attrib - 0.5F;
    }
    
    public static RayTraceResult getEntityItem(PlayerEntity player, Vector3d position, Vector3d look, double distance) {
        float f1 = 3.0F;
        Vector3d include = look.subtract(position);
        List list = player.level.getEntities(player, player.getBoundingBox().expandTowards(include.x, include.y, include.z).expandTowards(f1, f1, f1));
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);
            if (entity instanceof ItemEntity) {
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate(0.2);
                Optional<Vector3d> vec = axisalignedbb.clip(position, look);
                if (vec.isPresent())
                    return new EntityRayTraceResult(entity, vec.get());
                else if (axisalignedbb.contains(position))
                    return new EntityRayTraceResult(entity);
                
            }
        }
        return null;
    }
    
}
