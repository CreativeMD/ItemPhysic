package com.creativemd.itemphysic.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import com.creativemd.itemphysic.EventHandler;

import net.minecraft.entity.player.EntityPlayer;

public class DropPacket {
	
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
	
	public void writeBytes(ByteBuf bytes)
    {
    	bytes.writeInt(power);
    	bytes.writeBoolean(control);
    }
	
	public void readBytes(ByteBuf bytes)
	{
		power = bytes.readInt();
		control = bytes.readBoolean();
	}
    
    public void executeClient(EntityPlayer player)
    {
    	
    }
    
    public void executeServer(EntityPlayer player)
    {
    	EventHandler.Droppower = power;
    	player.dropOneItem(control);
    	EventHandler.Droppower = 1;
    	
    }
	
}
