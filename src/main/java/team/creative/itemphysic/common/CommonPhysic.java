package team.creative.itemphysic.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
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
import team.creative.itemphysic.mixin.ItemEntityAccessor;

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
            if (ItemPhysic.CONFIG.general.vanillaFlowBehavior) {
                if (item.isInWater() && item.getFluidHeight(FluidTags.WATER) > 0.1F)
                    ((ItemEntityAccessor) item).callSetUnderwaterMovement();
                else if (item.isInLava() && item.getFluidHeight(FluidTags.LAVA) > 0.1F)
                    ((ItemEntityAccessor) item).callSetUnderLavaMovement();
            } else {
                float viscosity = CommonPhysic.getViscosity(((ItemEntityExtender) item).getFluid(), item.level());
                item.setDeltaMovement(item.getDeltaMovement().multiply(1 / (1.2 * viscosity), 1, 1 / (1.2 * viscosity)));
            }
        }
    }
    
    public static void updatePre(ItemEntity item, RandomSource rand) {
        var fluid = calculateFluid(item);
        ((ItemEntityExtender) item).setFluid(fluid);
        
        if (fluid == null) {
            if (!item.isNoGravity())
                item.setDeltaMovement(item.getDeltaMovement().add(0.0D, -item.getGravity(), 0.0D));
            return;
        }
        
        double force = -0.02D / Math.max(1, CommonPhysic.getViscosity(fluid, item.level()));
        if (((ItemEntityExtender) item).canSwim() && !fluid.is(FluidTags.LAVA)) {
            double maxSpeed = 0.1;
            if (item.getDeltaMovement().y < maxSpeed)
                force = Math.min(item.getGravity(), maxSpeed - item.getDeltaMovement().y);
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
