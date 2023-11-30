package team.creative.itemphysic.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.CreativeCore;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.mixin.EntityAccessor;
import team.creative.itemphysic.mixin.ItemEntityAccessor;

public class ItemPhysicServer {
    
    public static final ThreadLocal<Fluid> fluid = new ThreadLocal<>();
    public static int tempDroppower = 1;
    
    public static void init() {}
    
    public static void drop(ItemEntity item) {
        if (ItemPhysic.CONFIG.general.customThrow) {
            item.setDeltaMovement(item.getDeltaMovement().scale(ItemPhysicServer.tempDroppower));
            ItemPhysicServer.tempDroppower = 1;
        }
    }
    
    public static void updatePre(ItemEntity item, RandomSource rand) {
        ItemStack stack = item.getItem();
        fluid.set(CommonPhysic.getFluid(item));
        if (fluid.get() == null) {
            if (!item.isNoGravity())
                item.setDeltaMovement(item.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            return;
        }
        
        double force = -0.02D / CommonPhysic.getViscosity(fluid.get(), item.level());
        if (ItemPhysic.CONFIG.general.swimmingItems.canPass(stack) && !fluid.get().is(FluidTags.LAVA)) {
            double maxSpeed = 0.1;
            if (item.getDeltaMovement().y < maxSpeed)
                force = Math.min(0.04, maxSpeed - item.getDeltaMovement().y);
        } else if (item.getDeltaMovement().y < -0.1) {
            force = 0;
            item.setDeltaMovement(item.getDeltaMovement().multiply(1, 0.8, 1));
        }
        item.setDeltaMovement(item.getDeltaMovement().add(0, force, 0));
        
        float f = item.getEyeHeight() - 0.11111111F;
        if ((item.isEyeInFluid(FluidTags.LAVA) || item.getFluidHeight(FluidTags.LAVA) > f || item.isOnFire()) && ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem())) {
            item.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
            for (int i = 0; i < 100; i++)
                item.level().addParticle(ParticleTypes.SMOKE, item.getX(), item.getY(), item.getZ(), (rand.nextFloat() * 0.1) - 0.05, 0.2 * rand.nextDouble(), (rand
                        .nextFloat() * 0.1) - 0.05);
            item.hurt(item.damageSources().onFire(), 3);
        }
        
    }
    
    public static boolean updateFluidHeightAndDoFluidPushing(ItemEntity item, TagKey<Fluid> fluidTag, double p_210500_2_) {
        double size = -0.001D;
        if (fluidTag == FluidTags.WATER && ItemPhysic.CONFIG.general.swimmingItems.canPass(item.getItem()))
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
    
    public static boolean fireImmune(ItemEntity item) {
        return !ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem());
    }
    
    public static void update(ItemEntity item) {
        float f = 0.98F;
        if (item.onGround())
            f = item.level().getBlockState(BlockPos.containing(item.getX(), item.getY() - 1.0D, item.getZ())).getBlock().getFriction() * 0.98F;
        
        if (fluid.get() == null) {
            item.setDeltaMovement(item.getDeltaMovement().multiply(f, 0.98D, f));
            
            if (item.onGround() && item.getDeltaMovement().y < 0.0D)
                item.setDeltaMovement(item.getDeltaMovement().multiply(1.0D, -0.5D, 1.0D));
        } else {
            float viscosity = CommonPhysic.getViscosity(fluid.get(), item.level());
            item.setDeltaMovement(item.getDeltaMovement().multiply(1 / (1.2 * viscosity), 1, 1 / (1.2 * viscosity)));
        }
    }
    
    public static void checkFallDamage(ItemEntity item, double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (onGroundIn && item.fallDistance > 0.0F && ItemPhysic.CONFIG.general.fallSounds)
            item.playSound(SoundEvents.WOOL_FALL, Math.min(1, item.fallDistance / 10), (float) Math.random() * 1F + 1);
    }
    
    public static boolean playerTouch(ItemEntity item, Player player) {
        if (ItemPhysic.CONFIG.pickup.customPickup && (!player
                .isCrouching() || !ItemPhysic.CONFIG.pickup.pickupWhenSneaking) && !ItemPhysic.CONFIG.pickup.pickupNormally && !ItemPhysic.CONFIG.pickup.alwaysPickup.canPass(item
                        .getItem()))
            return true;
        if (item.level().isClientSide || item.hasPickUpDelay())
            return true;
        return false;
    }
    
    public static void playerPickup(ItemEntity entity, Player player) {
        if (!entity.level().isClientSide) {
            if (!ItemPhysic.CONFIG.pickup.customPickup && entity.hasPickUpDelay())
                return;
            ItemStack itemstack = entity.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();
            
            int hook = CreativeCore.utils().onItemPickup(entity, player);
            if (hook < 0)
                return;
            
            ItemStack copy = itemstack.copy();
            ItemEntityAccessor ie = (ItemEntityAccessor) entity;
            if ((!entity.hasPickUpDelay() || ItemPhysic.CONFIG.pickup.customPickup) && (ie.getTarget() == null || ie.getTarget().equals(player
                    .getUUID())) && (hook == 1 || i <= 0 || player.getInventory().add(itemstack))) {
                copy.setCount(copy.getCount() - entity.getItem().getCount());
                CreativeCore.utils().firePlayerItemPickupEvent(player, entity, copy);
                player.take(entity, i);
                if (itemstack.isEmpty()) {
                    entity.discard();
                    itemstack.setCount(i);
                }
                
                player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                player.onItemPickup(entity);
            }
            
        }
    }
    
    public static InteractionResult interact(ItemEntity item, Player player, InteractionHand hand) {
        if (ItemPhysic.CONFIG.pickup.customPickup) {
            playerPickup(item, player);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
    
    public static boolean hurt(ItemEntity item, DamageSource source, float amount) {
        if (item.level().isClientSide || item.isRemoved())
            return false; //Forge: Fixes MC-53850
            
        if (item.isInvulnerableTo(source))
            return false;
        
        if (!item.getItem().isEmpty() && ItemPhysic.CONFIG.general.undestroyableItems.canPass(item.getItem()))
            return false;
        
        if (!item.getItem().isEmpty() && item.getItem().getItem() == Items.NETHER_STAR && source.is(DamageTypeTags.IS_EXPLOSION))
            return false;
        
        if (!item.getItem().getItem().canBeHurtBy(source))
            return false;
        if ((source.is(DamageTypeTags.IS_FIRE) || source == item.damageSources().lava() || source == item.damageSources().onFire() || source == item.damageSources()
                .inFire()) && !ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem()))
            return false;
        
        if (ItemPhysic.CONFIG.general.disableCactusDamage && source == item.damageSources().cactus())
            return false;
        
        return true;
    }
    
}
