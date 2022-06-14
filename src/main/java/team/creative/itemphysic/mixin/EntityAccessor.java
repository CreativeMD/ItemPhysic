package team.creative.itemphysic.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public interface EntityAccessor {
    
    @Accessor("stuckSpeedMultiplier")
    Vec3 getStuckSpeedMultiplier();
    
    @Accessor("fluidOnEyes")
    public Set<TagKey<Fluid>> getFluidOnEyes();
}
