package com.creativemd.itemphysic.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.EventHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;

public class DropPacket extends CreativeCorePacket{
	
	public int power;
	public boolean control;
	
	public DropPacket()
	{
		power = 0;
		control = false;
	}
	
	public DropPacket(int power, boolean control)
	{
		this.power = power;
		this.control = control;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes)
    {
    	bytes.writeInt(power);
    	bytes.writeBoolean(control);
    }
	
	@Override
	public void readBytes(ByteBuf bytes)
	{
		power = bytes.readInt();
		control = bytes.readBoolean();
	}
    
	@Override
	@SideOnly(Side.CLIENT)
    public void executeClient(EntityPlayer player)
    {
    	
    }
    
	@Override
    public void executeServer(EntityPlayer player)
    {
    	EventHandler.Droppower = power;
    	player.dropOneItem(control);
    	EventHandler.Droppower = 1;
    	
    }
	
}
