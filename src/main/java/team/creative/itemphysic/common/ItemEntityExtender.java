package team.creative.itemphysic.common;

import net.minecraft.world.level.material.Fluid;

public interface ItemEntityExtender {
    
    public Fluid getFluid();
    
    public void setFluid(Fluid fluid);
    
    public boolean canSwim();
    
    public boolean canBurn();
    
}
