package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.client.ClientPhysic;
import team.creative.itemphysic.client.ItemEntityRenderStateExtender;
import team.creative.itemphysic.client.ItemEntityRendering;

@Mixin(ItemEntityRenderState.class)
public class ItemEntityRenderStateMixin implements ItemEntityRenderStateExtender {
    
    @Unique
    public float rotX;
    @Unique
    public float rotY;
    @Unique
    public boolean skipRendering;
    @Unique
    public boolean additionalOffset;
    @Unique
    public boolean isBlock;
    
    @Override
    public float getXRot() {
        return rotX;
    }
    
    @Override
    public float getYRot() {
        return rotY;
    }
    
    @Override
    public boolean skipRendering() {
        return skipRendering;
    }
    
    @Override
    public boolean hasAdditionalOffset() {
        return additionalOffset;
    }
    
    @Override
    public boolean isBlock() {
        return isBlock;
    }
    
    @Override
    public void extractPhysic(ItemEntity entity) {
        ItemEntityRenderState state = (ItemEntityRenderState) (Object) this;
        isBlock = state.item.usesBlockLight();
        ClientPhysic.calculateRotation(entity, state);
        skipRendering = ((ItemEntityRendering) entity).skipRendering();
        additionalOffset = ItemPhysic.CONFIG.rendering.blockRequireOffset.is(entity.level().getBlockState(entity
                .blockPosition())) || ItemPhysic.CONFIG.rendering.blockBelowRequireOffset.is(entity.level().getBlockState(entity.blockPosition().below()));
        rotX = entity.getXRot();
        rotY = entity.getYRot();
        
    }
    
}
