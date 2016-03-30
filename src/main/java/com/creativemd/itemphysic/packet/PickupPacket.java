package com.creativemd.itemphysic.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.EventHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class PickupPacket extends CreativeCorePacket{
	
	public Vec3d look;
	public Vec3d pos;
	
	public PickupPacket()
	{
		
	}
	
	public PickupPacket(Vec3d pos, Vec3d look) {
		this.look = look;
		this.pos = pos;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		writeVec3(look, buf);
		writeVec3(pos, buf);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		look = readVec3(buf);
		pos = readVec3(buf);
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		EventHandler.cancel = true;
		RayTraceResult result = EventHandler.getEntityItem(player, pos, look);
		if(result != null)
		{
			result.entityHit.processInitialInteract(player, player.getHeldItemMainhand(), EnumHand.MAIN_HAND);
		}
	}

}
