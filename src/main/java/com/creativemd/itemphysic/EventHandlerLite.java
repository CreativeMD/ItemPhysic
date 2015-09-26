package com.creativemd.itemphysic;

import com.creativemd.itemphysic.physics.ClientPhysic;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
