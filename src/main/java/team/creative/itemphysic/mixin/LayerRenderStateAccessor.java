package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;

@Mixin(LayerRenderState.class)
public interface LayerRenderStateAccessor {
    
    @Accessor
    public ItemTransform getTransform();
    
    @Accessor
    public RenderType getRenderType();
}
