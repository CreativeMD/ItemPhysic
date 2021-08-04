package team.creative.itemphysic.server;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.hooks.BasicEventHooks;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;

public class ItemPhysicServer {
    
    public static void init(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ItemPhysicServer.class);
    }
    
    public static int tempDroppower = 1;
    
    @SubscribeEvent
    public static void onDespawn(ItemExpireEvent event) {
        if (ItemPhysic.CONFIG.general.despawnItem == -1)
            try {
                age.set(event.getEntityItem(), 1);
                event.setCanceled(true);
                event.setExtraLife(0);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
    }
    
    @SubscribeEvent
    public static void onToos(ItemTossEvent event) {
        event.getEntityItem().setDeltaMovement(event.getEntityItem().getDeltaMovement().scale(tempDroppower));
        tempDroppower = 1;
    }
    
    /*
     * replace with
         if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
            this.setUnderwaterMovement();
         } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
            this.setUnderLavaMovement();
         } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }
     */
    public static void updatePre(ItemEntity item) {
        ItemStack stack = item.getItem();
        fluid.set(CommonPhysic.getFluid(item));
        if (fluid.get() == null) {
            if (!item.isNoGravity())
                item.setDeltaMovement(item.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            return;
        }
        
        double density = fluid.get().getAttributes().getDensity() / 1000D;
        double speed = -1 / density * 0.01;
        if (ItemPhysic.CONFIG.general.swimmingItems.canPass(stack))
            speed = 0.1;
        
        if (item.getDeltaMovement().y > 0 && speed < item.getDeltaMovement().y)
            return;
        double speedreduction = (speed - item.getDeltaMovement().y) / 2;
        double maxSpeedReduction = 0.1;
        if (speedreduction < -maxSpeedReduction)
            speedreduction = -maxSpeedReduction;
        if (speedreduction > maxSpeedReduction)
            speedreduction = maxSpeedReduction;
        item.setDeltaMovement(item.getDeltaMovement().add(0, speedreduction, 0));
    }
    
    private static Field fluidHeightField = ObfuscationReflectionHelper.findField(Entity.class, "f_19799_");
    
    public static boolean updateFluidHeightAndDoFluidPushing(ItemEntity item, Tag<Fluid> fluidTag, double p_210500_2_) {
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
                        FluidState fluidstate = item.level.getFluidState(blockpos$mutableblockpos);
                        if (fluidstate.is(fluidTag)) {
                            double d1 = i2 + fluidstate.getHeight(item.level, blockpos$mutableblockpos);
                            if (d1 >= aabb.minY) {
                                flag1 = true;
                                d0 = Math.max(d1 - aabb.minY, d0);
                                if (flag) {
                                    Vec3 vec31 = fluidstate.getFlow(item.level, blockpos$mutableblockpos);
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
            
            try {
                Object2DoubleMap<Tag<Fluid>> map = (Object2DoubleMap<Tag<Fluid>>) fluidHeightField.get(item);
                map.put(fluidTag, d0);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            
            return flag1;
        }
    }
    
    public static boolean fireImmune(ItemEntity item) {
        return !ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem());
    }
    
    public static final ThreadLocal<Fluid> fluid = new ThreadLocal<>();
    
    //Remove this.setMotion(this.getMotion().mul((double)f, 0.98D, (double)f));
    //Replace with: if (this.onGround) { this.setMotion(this.getMotion().mul(1.0D, -0.5D, 1.0D)); }
    public static void update(ItemEntity item, float f) {
        
        //if (ItemPhysic.CONFIG.general.swimmingItems.canPass(item.getItem()) && fluid.get() != null)
        //item.setMotion(item.getMotion().mul(1 / (fluid.get().getAttributes().getDensity() / 950D * 1.5), 1, 1 / (fluid.get().getAttributes().getDensity() / 950D * 1.5)));
        
        if (fluid.get() == null) {
            item.setDeltaMovement(item.getDeltaMovement().multiply(f, 0.98D, f));
            
            if (item.isOnGround() && item.getDeltaMovement().y < 0.0D)
                item.setDeltaMovement(item.getDeltaMovement().multiply(1.0D, -0.5D, 1.0D));
        } else
            item.setDeltaMovement(item.getDeltaMovement()
                    .multiply(1 / (fluid.get().getAttributes().getDensity() / 900D), 1, 1 / (fluid.get().getAttributes().getDensity() / 900D)));
        
        if (ItemPhysic.CONFIG.general.despawnItem != -1 && item.lifespan == 6000 && item.lifespan != ItemPhysic.CONFIG.general.despawnItem)
            item.lifespan = ItemPhysic.CONFIG.general.despawnItem;
        
    }
    
    public static void checkFallDamage(ItemEntity item, double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (onGroundIn && item.fallDistance > 0.0F && ItemPhysic.CONFIG.general.fallSounds)
            item.playSound(SoundEvents.WOOL_FALL, Math.min(1, item.fallDistance / 10), (float) Math.random() * 1F + 1);
    }
    
    public static boolean playerTouch(ItemEntity item, Player player) {
        if (ItemPhysic.CONFIG.pickup.customPickup && (!player.isDiscrete()) || !ItemPhysic.CONFIG.pickup.pickupWhenSneaking)
            return true;
        if (item.level.isClientSide || item.hasPickUpDelay())
            return true;
        return false;
    }
    
    public static void playerTouch(ItemEntity entity, Player player, boolean needsSneak) {
        if (ItemPhysic.CONFIG.pickup.customPickup && needsSneak && (!player.isCrouching() || !ItemPhysic.CONFIG.pickup.pickupWhenSneaking))
            return;
        if (!entity.level.isClientSide) {
            if (!ItemPhysic.CONFIG.pickup.customPickup && entity.hasPickUpDelay())
                return;
            ItemStack itemstack = entity.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();
            
            int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(entity, player);
            if (hook < 0)
                return;
            
            ItemStack copy = itemstack.copy();
            try {
                if ((!entity.hasPickUpDelay() || ItemPhysic.CONFIG.pickup.customPickup) && (entity.getOwner() == null || entity.lifespan - age.getInt(entity) <= 200 || entity
                        .getOwner().equals(player.getUUID())) && (hook == 1 || i <= 0 || player.getInventory().add(itemstack))) {
                    copy.setCount(copy.getCount() - entity.getItem().getCount());
                    BasicEventHooks.firePlayerItemPickupEvent(player, entity, copy);
                    player.take(entity, i);
                    if (itemstack.isEmpty()) {
                        entity.discard();
                        itemstack.setCount(i);
                    }
                    
                    player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                    player.onItemPickup(entity);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {}
            
        }
    }
    
    public static InteractionResult interact(ItemEntity item, Player player, InteractionHand hand) {
        if (ItemPhysic.CONFIG.pickup.customPickup) {
            playerTouch(item, player, false);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
    
    public static boolean hurt(ItemEntity item, DamageSource source, float amount) {
        if (item.level.isClientSide || item.isRemoved())
            return false; //Forge: Fixes MC-53850
            
        if (item.isInvulnerableTo(source))
            return false;
        
        if (!item.getItem().isEmpty() && ItemPhysic.CONFIG.general.undestroyableItems.canPass(item.getItem()))
            return false;
        
        if (!item.getItem().isEmpty() && item.getItem().getItem() == Items.NETHER_STAR && source.isExplosion())
            return false;
        
        if (!item.getItem().getItem().canBeHurtBy(source))
            return false;
        if (source == DamageSource.LAVA || source == DamageSource.ON_FIRE || source == DamageSource.IN_FIRE)
            if (ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem())) {
                try {
                    Random rand = (Random) randField.get(item);
                    item.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
                    item.kill();
                    for (int i = 0; i < 100; i++)
                        item.level.addParticle(ParticleTypes.SMOKE, item.getX(), item.getY(), item
                                .getZ(), (rand.nextFloat() * 0.1) - 0.05, 0.2 * rand.nextDouble(), (rand.nextFloat() * 0.1) - 0.05);
                } catch (IllegalArgumentException | IllegalAccessException e) {}
                
            } else
                return false;
            
        if (source == DamageSource.CACTUS)
            return false;
        
        try {
            markHurtMethod.invoke(item);
            healthField.setInt(item, (int) (healthField.getInt(item) - amount));
            item.gameEvent(GameEvent.ENTITY_DAMAGED, source.getEntity());
            if (healthField.getInt(item) <= 0) {
                item.getItem().onDestroyed(item);
                item.discard();
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {}
        return true;
    }
    
    private static Field age = ObfuscationReflectionHelper.findField(ItemEntity.class, "f_31985_");
    private static Field healthField = ObfuscationReflectionHelper.findField(ItemEntity.class, "f_31987_");
    private static Field randField = ObfuscationReflectionHelper.findField(Entity.class, "f_19796_");
    private static Method markHurtMethod = ObfuscationReflectionHelper.findMethod(Entity.class, "m_5834_");
    
    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload event) {
        toCancel.removeIf((x) -> x.level == event.getWorld());
    }
    
    public static List<ServerPlayer> toCancel = new ArrayList<>();
    
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event instanceof RightClickEmpty || event instanceof RightClickBlock || event instanceof EntityInteract)
            if (!event.getPlayer().level.isClientSide) {
                if (toCancel.contains(event.getPlayer())) {
                    toCancel.remove(event.getPlayer());
                    event.setCanceled(true);
                }
            }
    }
}
