package team.creative.itemphysic.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.item.ItemEntity;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
    
    @Accessor
    public UUID getTarget();
    
    @Invoker
    public void callSetUnderwaterMovement();
    
    @Invoker
    public void callSetUnderLavaMovement();
    
}
