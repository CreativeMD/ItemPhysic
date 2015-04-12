package com.creativemd.itemphysic.packet;

import com.creativemd.itemphysic.ItemDummyContainer;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
	
	public static void sendToServer(DropPacket packet)
	{
		ItemDummyContainer.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		ItemDummyContainer.channels.get(Side.CLIENT).writeOutbound(packet);
	}
	
	public static void sendToPlayer(DropPacket packet, EntityPlayer player)
	{
		ItemDummyContainer.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		ItemDummyContainer.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		ItemDummyContainer.channels.get(Side.SERVER).writeOutbound(packet);
	}
	
	public static void sendToAllPlayers(DropPacket packet)
	{
		ItemDummyContainer.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		ItemDummyContainer.channels.get(Side.SERVER).writeOutbound(packet);
	}
	
}
