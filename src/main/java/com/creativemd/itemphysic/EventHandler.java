package com.creativemd.itemphysic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.lib.Vector3d;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandler {
	
	public static int Droppower = 1;
	
	@SubscribeEvent
	public void onToos(ItemTossEvent event)
	{
		if(!ItemTransformer.isLite)
		{
			event.entityItem.motionX *= Droppower;
			event.entityItem.motionY *= Droppower;
			event.entityItem.motionZ *= Droppower;
		}
	}
	
	@Method(modid = "creativecore")
	public static EntityItem getEntityItem(EntityPlayer player, Vec3 vec31, Vec3 vec3)
	{
		float f1 = 1.0F;
        double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
        
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
		double d1 = d0;
        
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
	        if (mc.objectMouseOver != null)
	        {
	            d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
	        }
        }
        
        double d2 = d1;
        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = (Entity)list.get(i);
            if(entity instanceof EntityItem)
            {
            	float f2 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3))
                {
                    if (0.0D < d2 || d2 == 0.0D)
                    {
                        return (EntityItem) entity;
                    }
                }
                else if (movingobjectposition != null)
                {
                	return (EntityItem) entity;
                }
            }
        }
        return null;
	}
	
	@Method(modid = "creativecore")
	public static EntityItem getEntityItem(double distance, EntityPlayer player)
	{
		//Minecraft mc = Minecraft.getMinecraft();
		/*Vec3 vec31 = player.getLook(1.0F);
		float f1 = 1.0F;
		double reach = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Entity entity = player.worldObj.findNearestEntityWithinAABB(EntityItem.class, player.boundingBox.addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand((double)f1, (double)f1, (double)f1), player);
		if(entity instanceof EntityItem && player.getDistanceSqToEntity(entity) <= reach)
			return (EntityItem) entity;
		return null;*/
		
		Vec3 vec31 = player.getLook(1.0F);
		Vec3 vec3 = player.getPosition(1.0F);
		EntityItem item = getEntityItem(player, vec31, vec3);
        
		if(item != null && player.getDistanceToEntity(item) < distance)
			return item;
		
		return null;
        
	}
	
	public static boolean cancel = false;
	
	@SubscribeEvent
	@Method(modid = "creativecore")
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(!ItemTransformer.isLite)
		{
			if(event.world.isRemote && ItemDummyContainer.customPickup && (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
			{
				double distance = 100;
				if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
					distance = event.entityPlayer.getDistance(event.x, event.y, event.z);
				EntityItem entity = getEntityItem(distance, event.entityPlayer);
				if(event.entityPlayer.worldObj.isRemote && entity != null)
				{
					PacketHandler.sendPacketToServer(new PickupPacket(event.entityPlayer.getLook(1.0F), event.entityPlayer.getPosition(1.0F)));
				}
			}
			if(!event.entityPlayer.worldObj.isRemote && cancel)
			{
				//entity.interactFirst(event.entityPlayer);	
				cancel = false;
				event.setCanceled(true);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static int power;
	
	@SideOnly(Side.CLIENT)
	public static Minecraft mc;
	@SideOnly(Side.CLIENT)
	public static RenderItem renderer;
	
	@SideOnly(Side.CLIENT)
	@Method(modid = "creativecore")
	public void renderTickFull()
	{
		if(mc == null)
			mc = Minecraft.getMinecraft();
		if(renderer == null)
			renderer = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
		if(mc != null && mc.thePlayer != null && mc.inGameHasFocus)
		{
			if(ItemDummyContainer.customPickup)
			{
				double distance = 100;
				if(mc.objectMouseOver != null)
					if(mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
						distance = mc.thePlayer.getDistance(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
					else if(mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY)
						distance = mc.thePlayer.getDistanceToEntity(mc.objectMouseOver.entityHit);
				EntityItem entity = getEntityItem(distance, mc.thePlayer);
				if(entity != null && mc.inGameHasFocus)
				{
					int space = 15;
					List list = new ArrayList();
					entity.getEntityItem().getItem().addInformation(entity.getEntityItem(), mc.thePlayer, list, true);
					list.add(entity.getEntityItem().getDisplayName());
					
					int width = 0;
					int height = (mc.fontRenderer.FONT_HEIGHT+space+1)*list.size();
					for(int i = 0; i < list.size(); i++)
					{
						String text = (String) list.get(i);
						width = Math.max(width, mc.fontRenderer.getStringWidth(text)+10);
					}
					
					GL11.glEnable(GL11.GL_BLEND);
			        GL11.glDisable(GL11.GL_TEXTURE_2D);
			        GL11.glEnable(GL11.GL_ALPHA_TEST);
			        
					GL11.glPushMatrix();
					GL11.glTranslated(mc.displayWidth/4-width/2, mc.displayHeight/4-height/2-space/2, 0);
					double rgb = (Math.sin(Math.toRadians((double)System.nanoTime()/10000000D))+1)*0.2;
					Vec3 color = Vec3.createVectorHelper(rgb, rgb, rgb);
					//System.out.println(color.xCoord);
					RenderHelper2D.drawRect(0, 0, width, height, color, 0.3);
					color = Vec3.createVectorHelper(0, 0, 0);
					RenderHelper2D.drawRect(1, 1, width-1, height-1, color, 0.1);
					
					GL11.glPopMatrix();
					
					
			        //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			        //Gui.drawRect(0, 0, 100, 100, 100);
			        GL11.glEnable(GL11.GL_TEXTURE_2D);
			        GL11.glDisable(GL11.GL_BLEND);
					for(int i = 0; i < list.size(); i++)
					{
						String text = (String) list.get(i);
						mc.fontRenderer.drawString(text, mc.displayWidth/4-mc.fontRenderer.getStringWidth(text)/2, mc.displayHeight/4+((list.size()/2)*space-space*(i+1)), 16579836);
					}
					//renderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, ((EntityItem)move.entityHit).getEntityItem(), 10, 10);
				}
			}
			if(ItemDummyContainer.customThrow)
			{
				if(mc.thePlayer.getCurrentEquippedItem() != null)
				{
					if(mc.gameSettings.keyBindDrop.getIsKeyPressed())
						power++;
					else
					{
						if(power > 0)
						{
							power /= 30;
							if(power < 1)
								power = 1;
							if(power > 6)
								power = 6;
							PacketHandler.sendPacketToServer(new DropPacket(power, GuiScreen.isCtrlKeyDown()));
						}
						power = 0;
					}
				}
				if(power > 0)
				{
					int renderPower = power;
					renderPower /= 30;
					if(renderPower < 1)
						renderPower = 1;
					if(renderPower > 6)
						renderPower = 6;
					String text = "Power:" + renderPower;
					mc.fontRenderer.drawString(text, mc.displayWidth/4-mc.fontRenderer.getStringWidth(text)/2, mc.displayHeight/4+mc.displayHeight/8, 16579836);
				}
			}else{
				if(mc.gameSettings.keyBindDrop.getIsKeyPressed())
					power++;
				else
					power = 0;
				if(power == 1)
				{
					int i = GuiScreen.isCtrlKeyDown() ? 3 : 4;
				    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(i, 0, 0, 0, 0));
				}
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTick(RenderTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			ClientPhysic.tick = System.nanoTime();
			if(!ItemTransformer.isLite)
			{
				renderTickFull();
			}
		}
	}
	
	private void renderQuad(int par2, int par3, int par4, int par5, int par6)
    {
		Tessellator par1Tessellator = Tessellator.instance;
        par1Tessellator.startDrawingQuads();
        par1Tessellator.setColorOpaque_I(4210752);
        par1Tessellator.addVertex((double)(par2 + 0), (double)(par3 + 0), 0.0D);
        par1Tessellator.addVertex((double)(par2 + 0), (double)(par3 + par5), 0.0D);
        par1Tessellator.addVertex((double)(par2 + par4), (double)(par3 + par5), 0.0D);
        par1Tessellator.addVertex((double)(par2 + par4), (double)(par3 + 0), 0.0D);
        par1Tessellator.draw();
    }
}
