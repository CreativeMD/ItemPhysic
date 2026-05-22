package team.creative.itemphysic.server;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import team.creative.creativecore.CreativeCore;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.ItemEntityExtender;
import team.creative.itemphysic.mixin.EntityAccessor;
import team.creative.itemphysic.mixin.ItemEntityAccessor;

public class ItemPhysicServer {
    
    public static final ThreadLocal<Fluid> fluid = new ThreadLocal<>();
    
    public static void init() {}
    
    public static boolean playerTouch(ItemEntity item, Player player) {
        if (ItemPhysic.CONFIG.pickup.walkOverRange < 1) {
            var range = ItemPhysic.CONFIG.pickup.walkOverRange;
            if (!item.getBoundingBox().intersects(player.getBoundingBox().inflate(range, Math.max(0.5, range), range)))
                return true;
        }
        
        if (ItemPhysic.CONFIG.pickup.canPickup(player) && (!player
                .isCrouching() || !ItemPhysic.CONFIG.pickup.pickupWhenSneaking) && !ItemPhysic.CONFIG.pickup.pickupNormally && !ItemPhysic.CONFIG.pickup.alwaysPickup.canPass(item
                        .level(), item.getItem()))
            return true;
        if (item.level().isClientSide() || item.hasPickUpDelay())
            return true;
        return false;
    }
    
    public static void playerPickup(ItemEntity entity, Player player) {
        if (!entity.level().isClientSide()) {
            if (!ItemPhysic.CONFIG.pickup.canPickup(player) && entity.hasPickUpDelay())
                return;
            ItemStack itemstack = entity.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();
            
            int hook = CreativeCore.utils().fireItemPickupPre(entity, player);
            if (hook == 1)
                return;
            
            ItemStack copy = itemstack.copy();
            ItemEntityAccessor ie = (ItemEntityAccessor) entity;
            int sizeBefore = itemstack.getCount();
            if ((hook == 0 || !entity.hasPickUpDelay() || ItemPhysic.CONFIG.pickup.customPickup) && (ie.getTarget() == null || ie.getTarget().equals(player.getUUID())) && player
                    .getInventory().add(itemstack)) {
                
                i = copy.getCount() - itemstack.getCount();
                CreativeCore.utils().fireItemPickupPost(entity, player, copy);
                
                player.take(entity, i);
                if (itemstack.isEmpty()) {
                    entity.discard();
                    itemstack.setCount(i);
                } else
                    entity.setItem(itemstack.copy());
                
                player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                player.onItemPickup(entity);
            } else if (sizeBefore > itemstack.getCount()) {
                entity.setItem(itemstack.copy());
                player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                player.onItemPickup(entity);
                player.level().playSound(null, entity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (entity.getRandom().nextFloat() - entity.getRandom()
                        .nextFloat()) * 1.4F + 2.0F);
            }
            
        }
    }
    
    public static InteractionResult interact(ItemEntity item, Player player, InteractionHand hand) {
        if (ItemPhysic.CONFIG.pickup.canPickup(player)) {
            playerPickup(item, player);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
    
    public static boolean hurt(ItemEntity item, DamageSource source, float amount) {
        if (item.level().isClientSide() || item.isRemoved())
            return false; //Forge: Fixes MC-53850
            
        if (((EntityAccessor) item).callIsInvulnerableToBase(source))
            return false;
        
        if (!item.getItem().isEmpty() && ItemPhysic.CONFIG.general.undestroyableItems.canPass(item.level(), item.getItem()))
            return false;
        
        if (!item.getItem().isEmpty() && item.getItem().getItem() == Items.NETHER_STAR && source.is(DamageTypeTags.IS_EXPLOSION))
            return false;
        
        if (!item.getItem().canBeHurtBy(source))
            return false;
        if ((source.is(DamageTypeTags.IS_FIRE) || source == item.damageSources().lava() || source == item.damageSources().onFire() || source == item.damageSources()
                .inFire()) && !((ItemEntityExtender) item).canBurn())
            return false;
        
        if (ItemPhysic.CONFIG.general.disableCactusDamage && source == item.damageSources().cactus())
            return false;
        
        return true;
    }
    
}
