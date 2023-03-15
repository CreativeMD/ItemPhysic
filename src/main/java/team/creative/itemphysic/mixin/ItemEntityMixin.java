package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import team.creative.itemphysic.common.ItemEntityPhysic;
import team.creative.itemphysic.server.ItemPhysicServer;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityPhysic {
    
    @Shadow
    public int age;
    
    @Shadow
    public int health;
    
    public boolean skipPhysicRenderer;
    
    private ItemEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    
    @Override
    public boolean skipRendering() {
        return skipPhysicRenderer;
    }
    
    @Override
    public void hurted() {
        markHurt();
    }
    
    @Override
    public int age() {
        return age;
    }
    
    @Override
    public void age(int age) {
        this.age = age;
    }
    
    @Override
    public int health() {
        return health;
    }
    
    @Override
    public void health(int health) {
        this.health = health;
    }
    
    /** @reason behavior will be overwritten
     * @author CreativeMD */
    @Override
    @Overwrite
    public boolean hurt(DamageSource source, float value) {
        return ItemPhysicServer.hurt((ItemEntity) (Object) this, source, value);
    }
    
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return ItemPhysicServer.interact((ItemEntity) (Object) this, player, hand);
    }
    
    @Override
    protected void checkFallDamage(double height, boolean fall, BlockState state, BlockPos pos) {
        ItemPhysicServer.checkFallDamage((ItemEntity) (Object) this, height, fall, state, pos);
        super.checkFallDamage(height, fall, state, pos);
    }
    
    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluid, double p_204033_) {
        return ItemPhysicServer.updateFluidHeightAndDoFluidPushing((ItemEntity) (Object) this, fluid, p_204033_);
    }
    
    @Inject(method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true, require = 1)
    public void playerTouchInject(Player player, CallbackInfo info) {
        if (ItemPhysicServer.playerTouch((ItemEntity) (Object) this, player))
            info.cancel();
    }
    
    @Inject(method = "fireImmune()Z", at = @At("HEAD"), cancellable = true, require = 1)
    public void fireImmuneInject(CallbackInfoReturnable<Boolean> info) {
        if (ItemPhysicServer.fireImmune((ItemEntity) (Object) this))
            info.setReturnValue(true);
    }
    
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;isInWater()Z"), require = 1)
    public void updatePre(CallbackInfo info) {
        ItemPhysicServer.updatePre((ItemEntity) (Object) this, random);
    }
    
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;isInWater()Z"), require = 1)
    public boolean isInWaterRedirect(ItemEntity entity) {
        return false;
    }
    
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;isInLava()Z"), require = 1)
    public boolean isInLavaRedirect(ItemEntity entity) {
        return false;
    }
    
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;isNoGravity()Z"), require = 1)
    public boolean isNoGravityRedirect(ItemEntity entity) {
        return true;
    }
    
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 1),
            require = 1)
    public void update(CallbackInfo info) {
        ItemPhysicServer.update((ItemEntity) (Object) this);
    }
    
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"),
            require = 3)
    public void setDeltaMovementRedirect(ItemEntity entity, Vec3 vec) {}
    
}
