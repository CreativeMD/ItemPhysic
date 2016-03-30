package com.creativemd.itemphysic;

import com.creativemd.itemphysic.physics.ClientPhysic;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandlerLite {
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTick(RenderTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			ClientPhysic.tick = System.nanoTime();
		}
	}
	
}
