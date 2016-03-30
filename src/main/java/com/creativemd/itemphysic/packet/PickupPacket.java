package com.creativemd.itemphysic.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.EventHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PickupPacket extends CreativeCorePacket{
	
	public Vec3 look;
	public Vec3 pos;
	
	public PickupPacket()
	{
		
	}
	
	public PickupPacket(Vec3 pos, Vec3 look) {
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
		MovingObjectPosition result = EventHandler.getEntityItem(player, pos, look);
		if(result != null)
		{
			result.entityHit.interactFirst(player);
		}
	}

}
