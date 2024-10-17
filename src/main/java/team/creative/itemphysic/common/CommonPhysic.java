package team.creative.itemphysic.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.util.mc.PlayerUtils;
import team.creative.itemphysic.ItemPhysic;

public class CommonPhysic {
    
    public static float getViscosity(Fluid fluid, Level level) {
        if (fluid == null)
            return 0;
        return CreativeCore.loader().getFluidViscosityMultiplier(fluid, level);
    }
    
    public static Fluid getFluid(ItemEntity item) {
        return getFluid(item, false);
    }
    
    public static Fluid getFluid(ItemEntity item, boolean below) {
        if (item.level() == null)
            return null;
        
        double d0 = item.getY();
        BlockPos pos = item.blockPosition();
        if (below)
            pos = pos.below();
        
        FluidState state = item.level().getFluidState(pos);
        Fluid fluid = state.getType();
        
        if (state.isEmpty() || fluid == null)
            return null;
        
        if (below)
            return fluid;
        
        double filled = state.getHeight(item.level(), pos);
        
        if (d0 - pos.getY() - 0.2 <= filled)
            return fluid;
        return null;
    }
    
    public static double getReachDistance(Player player) {
        if (ItemPhysic.CONFIG.pickup.maximumPickupRange != 5)
            return ItemPhysic.CONFIG.pickup.maximumPickupRange;
        return PlayerUtils.getReach(player);
    }
    
    public static HitResult getEntityItem(Player player, Vec3 position, Vec3 look) {
        Vec3 include = look.subtract(position);
        List list = player.level().getEntities(player, player.getBoundingBox().expandTowards(include.x, include.y, include.z));
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);
            if (entity instanceof ItemEntity) {
                AABB axisalignedbb = entity.getBoundingBox().inflate(ItemPhysic.CONFIG.pickup.hitboxIncrease);
                Optional<Vec3> vec = axisalignedbb.clip(position, look);
                if (vec.isPresent())
                    return new EntityHitResult(entity, vec.get());
                else if (axisalignedbb.contains(position))
                    return new EntityHitResult(entity);
            }
        }
        return null;
    }
    
}
