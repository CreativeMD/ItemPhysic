package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

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
    
    @WrapMethod(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", require = 1)
    public void onSyncedDataUpdatedWrapper(EntityDataAccessor<?> accessor, Operation<Void> original) {
        original.call(accessor);
        if (level().isClientSide())
            skipPhysicRenderer = ItemPhysic.CONFIG.rendering.vanillaRendered.canPass(level(), ((ItemEntity) (Entity) this).getItem());
    }
    
}
