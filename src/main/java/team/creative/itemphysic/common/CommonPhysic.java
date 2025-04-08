package team.creative.itemphysic.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import team.creative.itemphysic.mixin.EntityAccessor;

public class CommonPhysic {
    
    public static float getViscosity(Fluid fluid, Level level) {
        if (fluid == null)
            return 0;
        return CreativeCore.loader().getFluidViscosityMultiplier(fluid, level);
    }
    
    public static boolean fireImmune(ItemEntity item) {
        return !((ItemEntityExtender) item).canBurn();
    }
    
    public static void update(ItemEntity item) {
        float f = 0.98F;
        if (item.onGround())
            f = CreativeCore.loader().getFriction(item.level(), ((EntityAccessor) item).callGetBlockPosBelowThatAffectsMyMovement(), item) * 0.98F;
        
        if (((ItemEntityExtender) item).getFluid() == null) {
            item.setDeltaMovement(item.getDeltaMovement().multiply(f, 0.98D, f));
            
            if (item.onGround() && item.getDeltaMovement().y < 0.0D)
                item.setDeltaMovement(item.getDeltaMovement().multiply(1.0D, -0.5D, 1.0D));
        } else {
            float viscosity = CommonPhysic.getViscosity(((ItemEntityExtender) item).getFluid(), item.level());
            item.setDeltaMovement(item.getDeltaMovement().multiply(1 / (1.2 * viscosity), 1, 1 / (1.2 * viscosity)));
        }
    }
    
    public static void updatePre(ItemEntity item, RandomSource rand) {
        var fluid = calculateFluid(item);
        ((ItemEntityExtender) item).setFluid(fluid);
        
        if (fluid == null) {
            if (!item.isNoGravity())
                item.setDeltaMovement(item.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            return;
        }
        
        double force = -0.02D / Math.max(1, CommonPhysic.getViscosity(fluid, item.level()));
        if (((ItemEntityExtender) item).canSwim() && !fluid.is(FluidTags.LAVA)) {
            double maxSpeed = 0.1;
            if (item.getDeltaMovement().y < maxSpeed)
                force = Math.min(0.04, maxSpeed - item.getDeltaMovement().y);
        } else if (item.getDeltaMovement().y < -0.1) {
            force = 0;
            item.setDeltaMovement(item.getDeltaMovement().multiply(1, 0.8, 1));
        }
        item.setDeltaMovement(item.getDeltaMovement().add(0, force, 0));
    }
    
    public static Fluid calculateFluid(ItemEntity item) {
        return calculateFluid(item, false);
    }
    
    public static Fluid calculateFluid(ItemEntity item, boolean below) {
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
    
    public static boolean updateFluidHeightAndDoFluidPushing(ItemEntity item, TagKey<Fluid> fluidTag, double p_210500_2_) {
        double size = -0.001D;
        if (fluidTag == FluidTags.WATER && ((ItemEntityExtender) item).canSwim())
            size = 0.3;
        
        if (item.touchingUnloadedChunk()) {
            return false;
        } else {
            AABB aabb = item.getBoundingBox().inflate(size);
            int i = Mth.floor(aabb.minX);
            int j = Mth.ceil(aabb.maxX);
            int k = Mth.floor(aabb.minY);
            int l = Mth.ceil(aabb.maxY);
            int i1 = Mth.floor(aabb.minZ);
            int j1 = Mth.ceil(aabb.maxZ);
            double d0 = 0.0D;
            boolean flag = item.isPushedByFluid();
            boolean flag1 = false;
            Vec3 vec3 = Vec3.ZERO;
            int k1 = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = k; i2 < l; ++i2) {
                    for (int j2 = i1; j2 < j1; ++j2) {
                        blockpos$mutableblockpos.set(l1, i2, j2);
                        FluidState fluidstate = item.level().getFluidState(blockpos$mutableblockpos);
                        if (fluidstate.is(fluidTag)) {
                            double d1 = i2 + fluidstate.getHeight(item.level(), blockpos$mutableblockpos);
                            if (d1 >= aabb.minY) {
                                flag1 = true;
                                d0 = Math.max(d1 - aabb.minY, d0);
                                if (flag) {
                                    Vec3 vec31 = fluidstate.getFlow(item.level(), blockpos$mutableblockpos);
                                    if (d0 < 0.4D) {
                                        vec31 = vec31.scale(d0);
                                    }
                                    
                                    vec3 = vec3.add(vec31);
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }
            
            if (vec3.length() > 0.0D) {
                if (k1 > 0)
                    vec3 = vec3.scale(1.0D / k1);
                
                vec3 = vec3.normalize();
                
                Vec3 vec32 = item.getDeltaMovement();
                vec3 = vec3.scale(p_210500_2_ * 1.0D);
                if (Math.abs(vec32.x) < 0.003D && Math.abs(vec32.z) < 0.003D && vec3.length() < 0.0045D)
                    vec3 = vec3.normalize().scale(0.0045D);
                
                item.setDeltaMovement(item.getDeltaMovement().add(vec3));
            }
            
            ((EntityAccessor) item).getFluidOnEyes().add(fluidTag);
            
            return flag1;
        }
    }
    
    public static double getReachDistance(Player player) {
        if (ItemPhysic.CONFIG.pickup.maximumPickupRange != 5)
            return ItemPhysic.CONFIG.pickup.maximumPickupRange;
        return PlayerUtils.getReach(player);
    }
    
    public static HitResult getItemInFocus(Player player, Vec3 position, Vec3 look) {
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
