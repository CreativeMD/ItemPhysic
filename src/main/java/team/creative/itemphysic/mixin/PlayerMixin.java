package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.creative.itemphysic.server.ItemPhysicServer;

@Mixin(Player.class)
public class PlayerMixin {
    
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "RETURN", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void drop(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> info, double d0, ItemEntity item) {
        ItemPhysicServer.drop(item);
    }
    
}
