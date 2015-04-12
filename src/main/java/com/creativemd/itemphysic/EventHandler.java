package com.creativemd.itemphysic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PacketHandler;

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
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
	
	public EntityItem getEntityItem(EntityPlayer player)
	{
		Vec3 vec31 = player.getLook(1.0F);
		float f1 = 1.0F;
		double reach = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Entity entity = player.worldObj.findNearestEntityWithinAABB(EntityItem.class, player.boundingBox.addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand((double)f1, (double)f1, (double)f1), player);
		if(entity instanceof EntityItem && player.getDistanceSqToEntity(entity) <= reach)
			return (EntityItem) entity;
		return null;
		/*double d0 = (double)this.mc.playerController.getBlockReachDistance();
		Vec3 vec31 = player.getLook(1.0F);
        float f1 = 1.0F;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
        Vec3 vec3 = player.getPosition(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
        
        double d1 = d0;
        player
        if (this.mc.playerController.extendedReach())
        {
            d0 = 6.0D;
            d1 = 6.0D;
        }
        else
        {
            if (d0 > 3.0D)
            {
                d1 = 3.0D;
            }

            d0 = d1;
        }
        
        /*if (this.mc.objectMouseOver != null)
        {
            d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
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
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D)
                    {
                        if (entity == this.mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
                        {
                            if (d2 == 0.0D)
                            {
                                return (EntityItem) entity;
                            }
                        }
                        else
                        {
                            return (EntityItem) entity;
                        }
                    }
                }
            }
        }
        return null;*/
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(ItemDummyContainer.customPickup && (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			EntityItem entity = getEntityItem(event.entityPlayer);
			if(entity != null && !event.entityPlayer.worldObj.isRemote)
			{
				entity.interactFirst(event.entityPlayer);	
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
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTick(RenderTickEvent event)
	{
		if(mc == null)
			mc = Minecraft.getMinecraft();
		if(renderer == null)
			renderer = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
		if(mc != null && mc.thePlayer != null && mc.inGameHasFocus)
		{
			if(ItemDummyContainer.customPickup)
			{
				EntityItem entity = getEntityItem(mc.thePlayer);
				if(entity != null && mc.inGameHasFocus)
				{
					int space = 15;
					List list = new ArrayList();
					entity.getEntityItem().getItem().addInformation(entity.getEntityItem(), mc.thePlayer, list, true);
					list.add(entity.getEntityItem().getDisplayName());
					GL11.glEnable(GL11.GL_BLEND);
			        GL11.glDisable(GL11.GL_TEXTURE_2D);
			        GL11.glEnable(GL11.GL_ALPHA_TEST);
			        //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			        Gui.drawRect(0, 0, 100, 100, 100);
			        GL11.glEnable(GL11.GL_TEXTURE_2D);
			        GL11.glDisable(GL11.GL_BLEND);
					for(int zahl = 0; zahl < list.size(); zahl++)
					{
						String text = (String) list.get(zahl);
						mc.fontRenderer.drawString(text, mc.displayWidth/4-mc.fontRenderer.getStringWidth(text)/2, mc.displayHeight/4+((list.size()/2)*space-space*(zahl+1)), 16579836);
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
							PacketHandler.sendToServer(new DropPacket(power, GuiScreen.isCtrlKeyDown()));
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
