package team.creative.itemphysic.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
     * if (this.areEyesInFluid(FluidTags.WATER)) {
     *      this.applyFloatMotion();
     * } else if (!this.hasNoGravity()) {
     *      this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
     * }
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
    
    private static Field eyesFluidLevel = ObfuscationReflectionHelper.findField(Entity.class, "field_233554_M_");
    
    public static boolean handleFluidAcceleration(ItemEntity item, ITag<Fluid> fluidTag, double p_210500_2_) {
        double size = -0.001D;
        if (fluidTag == FluidTags.WATER && ItemPhysic.CONFIG.general.swimmingItems.canPass(item.getItem()))
            size = 0.3;
        
        AxisAlignedBB axisalignedbb = item.getBoundingBox().inflate(size);
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        if (!item.level.hasChunksAt(i, k, i1, j, l, j1)) {
            return false;
        } else {
            double d0 = 0.0D;
            boolean flag = item.isPushedByFluid();
            boolean flag1 = false;
            Vector3d vector3d = Vector3d.ZERO;
            int k1 = 0;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = k; i2 < l; ++i2) {
                    for (int j2 = i1; j2 < j1; ++j2) {
                        blockpos$mutable.set(l1, i2, j2);
                        FluidState fluidstate = item.level.getFluidState(blockpos$mutable);
                        if (fluidstate.is(fluidTag)) {
                            double d1 = i2 + fluidstate.getHeight(item.level, blockpos$mutable);
                            if (d1 >= axisalignedbb.minY) {
                                flag1 = true;
                                d0 = Math.max(d1 - axisalignedbb.minY, d0);
                                if (flag) {
                                    Vector3d vector3d1 = fluidstate.getFlow(item.level, blockpos$mutable);
                                    if (d0 < 0.4D) {
                                        vector3d1 = vector3d1.scale(d0);
                                    }
                                    
                                    vector3d = vector3d.add(vector3d1);
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }
            
            if (vector3d.length() > 0.0D) {
                if (k1 > 0)
                    vector3d = vector3d.scale(1.0D / k1);
                
                Vector3d vector3d2 = item.getDeltaMovement();
                vector3d = vector3d.scale(p_210500_2_ * 1.0D);
                if (Math.abs(vector3d2.x) < 0.003D && Math.abs(vector3d2.z) < 0.003D && vector3d.length() < 0.0045D)
                    vector3d = vector3d.normalize().scale(0.0045D);
                
                item.setDeltaMovement(item.getDeltaMovement().add(vector3d));
            }
            
            try {
                Object2DoubleMap<ITag<Fluid>> map = (Object2DoubleMap<ITag<Fluid>>) eyesFluidLevel.get(item);
                map.put(fluidTag, d0);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            
            return flag1;
        }
    }
    
    /*
     * replace with
     * if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
     *    this.setMotion((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), (double)0.2F, (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
     *    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
     * }
     */
    public static void updateBurn(ItemEntity item) {
        try {
            Random rand = (Random) ItemPhysicServer.rand.get(item);
            if (item.level.getFluidState(item.blockPosition()).is(FluidTags.LAVA) && ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem())) {
                item.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
                item.remove();
                for (int i = 0; i < 100; i++)
                    item.level.addParticle(ParticleTypes.SMOKE, item.getX(), item.getY(), item
                            .getZ(), (rand.nextFloat() * 0.1) - 0.05, 0.2 * rand.nextDouble(), (rand.nextFloat() * 0.1) - 0.05);
            }
            
            if (item.isOnFire() && ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem())) {
                item.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
                item.remove();
                for (int i = 0; i < 100; i++)
                    item.level.addParticle(ParticleTypes.SMOKE, item.getX(), item.getY(), item
                            .getZ(), (rand.nextFloat() * 0.1) - 0.05, 0.2 * rand.nextDouble(), (rand.nextFloat() * 0.1) - 0.05);
            }
            
            if (ItemPhysic.CONFIG.general.enableIgniting && !item.level.isClientSide && item.isOnGround() && Math.random() <= 0.1 && ItemPhysic.CONFIG.general.ignitingItems
                    .canPass(item.getItem())) {
                BlockState state = item.level.getBlockState(item.blockPosition().below());
                if (state.getMaterial().isFlammable() && item.level.getBlockState(item.blockPosition()).getMaterial().isReplaceable())
                    item.level.setBlockAndUpdate(item.blockPosition(), Blocks.FIRE.defaultBlockState());
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
    
    public static void updateFallState(ItemEntity item, double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (onGroundIn && item.fallDistance > 0.0F && ItemPhysic.CONFIG.general.fallSounds)
            item.playSound(SoundEvents.WOOL_FALL, Math.min(1, item.fallDistance / 10), (float) Math.random() * 1F + 1);
    }
    
    public static boolean onCollideWithPlayer(ItemEntity item, PlayerEntity par1EntityPlayer) {
        if (ItemPhysic.CONFIG.pickup.customPickup && (!par1EntityPlayer.isDiscrete()) || !ItemPhysic.CONFIG.pickup.pickupWhenSneaking)
            return true;
        if (item.level.isClientSide || item.hasPickUpDelay())
            return true;
        return false;
    }
    
    public static void onCollideWithPlayer(ItemEntity entity, PlayerEntity player, boolean needsSneak) {
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
                        .getOwner().equals(player.getUUID())) && (hook == 1 || i <= 0 || player.inventory.add(itemstack))) {
                    copy.setCount(copy.getCount() - entity.getItem().getCount());
                    net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerItemPickupEvent(player, entity, copy);
                    player.take(entity, i);
                    if (itemstack.isEmpty()) {
                        player.take(entity, i);
                        entity.remove();
                        itemstack.setCount(i);
                    }
                    
                    player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {}
            
        }
    }
    
    public static boolean processInitialInteract(ItemEntity item, PlayerEntity player, Hand hand) {
        if (ItemPhysic.CONFIG.pickup.customPickup) {
            onCollideWithPlayer(item, player, false);
            return true;
        }
        return false;
    }
    
    public static boolean attackEntityFrom(ItemEntity item, DamageSource source, float amount) {
        if (item.level.isClientSide || item.isAlive())
            return true; //Forge: Fixes MC-53850
            
        if (item.isInvulnerableTo(source))
            return true;
        
        if (!item.getItem().isEmpty() && ItemPhysic.CONFIG.general.undestroyableItems.canPass(item.getItem()))
            return true;
        
        if (!item.getItem().isEmpty() && item.getItem().getItem() == Items.NETHER_STAR && source.isExplosion())
            return true;
        
        if ((source == DamageSource.LAVA | source == DamageSource.ON_FIRE | source == DamageSource.IN_FIRE) && !ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem()))
            return true;
        
        if (source == DamageSource.CACTUS)
            return true;
        return false;
    }
    
    private static Field fire = ObfuscationReflectionHelper.findField(Entity.class, "field_190534_ay");
    private static Field rand = ObfuscationReflectionHelper.findField(Entity.class, "field_70146_Z");
    private static Field age = ObfuscationReflectionHelper.findField(ItemEntity.class, "field_70292_b");
    private static Method getFlag = ObfuscationReflectionHelper.findMethod(Entity.class, "func_70083_f", int.class);
    
    public static boolean isItemBurning(ItemEntity item) {
        boolean flag = item.level != null && item.level.isClientSide;
        try {
            if (!(!item.fireImmune() && (fire.getInt(item) > 0 || flag && (Boolean) getFlag.invoke(item, 0))))
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemPhysic.CONFIG.general.burningItems.canPass(item.getItem());
    }
    
    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload event) {
        toCancel.removeIf((x) -> x.level == event.getWorld());
    }
    
    public static List<PlayerEntity> toCancel = new ArrayList<>();
    
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
