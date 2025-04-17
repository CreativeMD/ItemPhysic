package team.creative.itemphysic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.mixin.EntityAccessor;

public class ClientPhysic {
    
    private static final float baseMultiplier = 0.25F;
    private static final Minecraft MC = Minecraft.getInstance();
    
    public static void calculateRotation(ItemEntity entity, ItemEntityRenderState state) {
        float rotateBy = MC.getDeltaTracker().getRealtimeDeltaTicks() * baseMultiplier * ItemPhysic.CONFIG.rendering.rotateSpeed;
        if (MC.isPaused())
            rotateBy = 0;
        
        Vec3 motionMultiplier = ((EntityAccessor) entity).getStuckSpeedMultiplier();
        if (motionMultiplier != null && motionMultiplier.lengthSqr() > 0)
            rotateBy *= motionMultiplier.x * 0.2;
        
        boolean gui3d = ((ItemEntityRenderStateExtender) state).isBlock();
        
        if (gui3d) {
            if (!entity.onGround()) {
                rotateBy *= 2;
                Fluid fluid = CommonPhysic.calculateFluid(entity);
                if (fluid == null)
                    fluid = CommonPhysic.calculateFluid(entity, true);
                if (fluid != null)
                    rotateBy /= (1 + CommonPhysic.getViscosity(fluid, entity.level()));
                
                entity.setXRot(entity.getXRot() + rotateBy);
            } else if (ItemPhysic.CONFIG.rendering.oldRotation) {
                for (int side = 0; side < 4; side++) {
                    double rotation = side * 90;
                    double range = 5;
                    if (entity.getXRot() > rotation - range && entity.getXRot() < rotation + range)
                        entity.setXRot((float) rotation);
                }
                if (entity.getXRot() != 0 && entity.getXRot() != 90 && entity.getXRot() != 180 && entity.getXRot() != 270) {
                    double Abstand0 = Math.abs(entity.getXRot());
                    double Abstand90 = Math.abs(entity.getXRot() - 90);
                    double Abstand180 = Math.abs(entity.getXRot() - 180);
                    double Abstand270 = Math.abs(entity.getXRot() - 270);
                    if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270)
                        if (entity.getXRot() < 0)
                            entity.setXRot(entity.getXRot() + rotateBy);
                        else
                            entity.setXRot(entity.getXRot() - rotateBy);
                    if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270)
                        if (entity.getXRot() - 90 < 0)
                            entity.setXRot(entity.getXRot() + rotateBy);
                        else
                            entity.setXRot(entity.getXRot() - rotateBy);
                    if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270)
                        if (entity.getXRot() - 180 < 0)
                            entity.setXRot(entity.getXRot() + rotateBy);
                        else
                            entity.setXRot(entity.getXRot() - rotateBy);
                    if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0)
                        if (entity.getXRot() - 270 < 0)
                            entity.setXRot(entity.getXRot() + rotateBy);
                        else
                            entity.setXRot(entity.getXRot() - rotateBy);
                        
                }
            }
        } else if (entity != null && !Double.isNaN(entity.getX()) && !Double.isNaN(entity.getY()) && !Double.isNaN(entity.getZ()) && entity.level() != null) {
            if (entity.onGround()) {
                if (!gui3d)
                    entity.setXRot(0);
            } else {
                rotateBy *= 2;
                Fluid fluid = CommonPhysic.calculateFluid(entity);
                if (fluid != null)
                    rotateBy /= (1 + CommonPhysic.getViscosity(fluid, entity.level()));
                
                entity.setXRot(entity.getXRot() + rotateBy);
            }
        }
    }
    
}
