package team.creative.itemphysic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public interface EntityAccessor {
    
    @Accessor("stuckSpeedMultiplier")
    Vec3 getStuckSpeedMultiplier();
    
    @Invoker
    public BlockPos callGetBlockPosBelowThatAffectsMyMovement();
    
    @Invoker
    public boolean callIsInvulnerableToBase(DamageSource source);
}
