package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import team.creative.itemphysic.client.ItemPhysicClient;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    
    @Inject(method = "startUseItem()V", at = @At("TAIL"), require = 1)
    public void rightClickEmpty(CallbackInfo info) {
        HitResult result = ((Minecraft) (Object) this).hitResult;
        if (result == null || result.getType() == HitResult.Type.MISS)
            ItemPhysicClient.onPlayerInteractClient(((Minecraft) (Object) this).level, ((Minecraft) (Object) this).player, true);
    }
    
    @Inject(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"),
            cancellable = true, require = 1)
    public void rightClickEarly(CallbackInfo info) {
        if (ItemPhysicClient.onPlayerInteract(((Minecraft) (Object) this).player))
            info.cancel();
    }
    
}
