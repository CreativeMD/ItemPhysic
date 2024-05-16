package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.player.LocalPlayer;
import team.creative.itemphysic.ItemPhysic;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    
    @Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true, require = 1)
    public void drop(boolean all, CallbackInfoReturnable<Boolean> info) {
        if (ItemPhysic.CONFIG.general.customThrow)
            info.setReturnValue(true);
    }
    
}
