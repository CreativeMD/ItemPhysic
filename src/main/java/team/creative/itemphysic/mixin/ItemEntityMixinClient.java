package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.client.ItemEntityRendering;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixinClient extends Entity implements ItemEntityRendering {
    
    public boolean skipPhysicRenderer;
    
    public ItemEntityMixinClient(EntityType<?> type, Level level) {
        super(type, level);
    }
    
    @Override
    public boolean skipRendering() {
        return skipPhysicRenderer;
    }
    
    @Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;setEntityRepresentation(Lnet/minecraft/world/entity/Entity;)V"), require = 1)
    private void onSyncedDataUpdated(EntityDataAccessor<?> accessor, CallbackInfo callback) {
        if (level.isClientSide)
            skipPhysicRenderer = ItemPhysic.CONFIG.rendering.vanillaRendered.canPass(((ItemEntity) (Entity) this).getItem());
    }
    
}
