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
	
	public UUID uuid;
	public boolean rightClick;
	
	public PickupPacket()
	{
		
	}
	
	public PickupPacket(UUID uuid, boolean rightClick)
	{
		this.uuid = uuid;
		this.rightClick = rightClick;
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeBoolean(rightClick);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		long least = buf.readLong();
		long most = buf.readLong();
		this.uuid = new UUID(most, least);
		this.rightClick = buf.readBoolean();
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		if(rightClick)
			EventHandler.cancel = true;
		
		Entity item = ((WorldServer) player.world).getEntityFromUuid(uuid);
		if(item != null && item instanceof EntityItem && !item.isDead)
			ServerPhysic.processInitialInteract((EntityItem) item, player, player.getHeldItem(EnumHand.MAIN_HAND), EnumHand.MAIN_HAND);
	}

}
