package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import team.creative.itemphysic.ItemPhysic;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    
    @Inject(method = "dropItem(Lnet/minecraft/client/player/LocalPlayer;Z)V", at = @At("HEAD"), cancellable = true, require = 1)
    public void drop(LocalPlayer player, boolean all, CallbackInfo info) {
        if (ItemPhysic.CONFIG.throwConfig.enabled)
            info.cancel();
    }
    
}
