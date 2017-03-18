package com.creativemd.itemphysic.packet;

import java.util.UUID;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.EventHandler;
import com.creativemd.itemphysic.physics.ServerPhysic;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

public class PickupPacket extends CreativeCorePacket{
	
	/*public Vec3d look;
	public Vec3d pos;*/
	public UUID uuid;
	
	public PickupPacket()
	{
		
	}
	
	public PickupPacket(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	/*public PickupPacket(Vec3d pos, Vec3d look) {
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
	}*/
	
	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
	}

	@Override
	public void readBytes(ByteBuf buf) {
		long least = buf.readLong();
		long most = buf.readLong();
		this.uuid = new UUID(most, least);
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		EventHandler.cancel = true;
		//RayTraceResult result = EventHandler.getEntityItem(player, pos, look);
		Entity item = ((WorldServer) player.world).getEntityFromUuid(uuid);
		if(item != null && item instanceof EntityItem)
		{
			ServerPhysic.processInitialInteract((EntityItem) item, player, player.getHeldItem(EnumHand.MAIN_HAND), EnumHand.MAIN_HAND);
			//item.processInitialInteract(player, player.getHeldItemMainhand(), EnumHand.MAIN_HAND);
		}
	}

}
