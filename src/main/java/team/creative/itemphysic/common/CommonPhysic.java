package team.creative.itemphysic.common;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import team.creative.itemphysic.ItemPhysic;

public class CommonPhysic {
	
	public static Fluid getFluid(ItemEntity item) {
		return getFluid(item, false);
	}
	
	public static Fluid getFluid(ItemEntity item, boolean below) {
		if (item.world == null)
			return null;
		
		double d0 = item.getPosY();
		BlockPos pos = item.getPosition();
		if (below)
			pos = pos.down();
		
		IFluidState state = item.world.getFluidState(pos);
		Fluid fluid = state.getFluid();
		
		if (fluid == null || fluid.getFluid().getAttributes().getDensity() == 0)
			return null;
		
		if (below)
			return fluid;
		
		double filled = state.getHeight();
		
		if (d0 - pos.getY() - 0.2 <= filled)
			return fluid;
		return null;
	}
	
	public static double getReachDistance(PlayerEntity player) {
		if (ItemPhysic.CONFIG.pickup.maximumPickupRange != 5)
			return ItemPhysic.CONFIG.pickup.maximumPickupRange;
		float attrib = (float) player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
		return player.isCreative() ? attrib : attrib - 0.5F;
	}
	
	public static RayTraceResult getEntityItem(PlayerEntity player, Vec3d position, Vec3d look, double distance) {
		float f1 = 3.0F;
		Vec3d include = look.subtract(position);
		List list = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getBoundingBox().expand(include.x, include.y, include.z).expand(f1, f1, f1));
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);
			if (entity instanceof ItemEntity) {
				AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(0.2);
				Optional<Vec3d> vec = axisalignedbb.rayTrace(position, look);
				if (vec.isPresent())
					return new EntityRayTraceResult(entity, vec.get());
				else if (axisalignedbb.contains(position))
					return new EntityRayTraceResult(entity);
				
			}
		}
		return null;
	}
	
}
