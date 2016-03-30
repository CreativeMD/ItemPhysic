package com.creativemd.itemphysic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Timer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.collection.parallel.ParIterableLike.Min;

public class EventHandler {
	
	public static int Droppower = 1;
	
	private static Timer timer = null;
	
	public static Timer getTimer()
	{
		if(timer == null)
			timer = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer");
		return timer;
	}
	
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
	public static MovingObjectPosition getEntityItem(EntityPlayer player, Vec3 position, Vec3 look)
	{
		float f1 = 3.0F;
		double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().addCoord(position.xCoord, position.yCoord, position.zCoord).expand((double)f1, (double)f1, (double)f1));
        //System.out.println("Found " + list.size() + " items in range!");
        //Vec3d vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
		double d1 = d0;
        
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
	        if (mc.objectMouseOver != null)
	        {
	            d1 = mc.objectMouseOver.hitVec.distanceTo(position);
	        }
        }
        
        double d2 = d1;
        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = (Entity)list.get(i);
            if(entity instanceof EntityItem)
            {
            	
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double)entity.getCollisionBorderSize(), (double)entity.getCollisionBorderSize(), (double)entity.getCollisionBorderSize());
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(position, look);
                
                if(movingobjectposition != null)
                {
                	movingobjectposition.typeOfHit = MovingObjectType.ENTITY;
                	movingobjectposition.entityHit = entity;
                }
                
                if (axisalignedbb.isVecInside(position))
                {
                    if (0.0D < d2 || d2 == 0.0D)
                    {
                        return new MovingObjectPosition(entity);
                    }
                }
                else if (movingobjectposition != null)
                {
                	return movingobjectposition;
                }
            }
        }
        return null;
	}
	
	@Method(modid = "creativecore")
	public static MovingObjectPosition getEntityItem(double distance, EntityPlayer player)
	{
		//Minecraft mc = Minecraft.getMinecraft();
		/*Vec3 vec31 = player.getLook(1.0F);
		float f1 = 1.0F;
		double reach = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Entity entity = player.worldObj.findNearestEntityWithinAABB(EntityItem.class, player.boundingBox.addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand((double)f1, (double)f1, (double)f1), player);
		if(entity instanceof EntityItem && player.getDistanceSqToEntity(entity) <= reach)
			return (EntityItem) entity;
		return null;*/
		
		//Vec3d vec31 = player.getLook(1.0F);
		float partialTicks = getTimer().renderPartialTicks;
		Vec3 position = player.getPositionEyes(partialTicks);
		double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Vec3 vec3d1 = player.getLook(partialTicks);
        Vec3 look = position.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
        
        MovingObjectPosition result = getEntityItem(player, position, look);
        
		if(result != null && player.getDistance(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord) < distance)
			return result;
		
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
				if(mc.objectMouseOver != null)
					if(mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
						distance = mc.getRenderViewEntity().getDistance(mc.objectMouseOver.hitVec.xCoord, mc.objectMouseOver.hitVec.yCoord, mc.objectMouseOver.hitVec.zCoord);
					else if(mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY)
						distance = mc.getRenderViewEntity().getDistanceToEntity(mc.objectMouseOver.entityHit);
				MovingObjectPosition result = getEntityItem(distance, mc.thePlayer);
				if(result != null)
				{
					EntityItem entity = (EntityItem) result.entityHit;
					if(event.entityPlayer.worldObj.isRemote && entity != null && distance > mc.getRenderViewEntity().getDistance(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord))
					{
						float partialTicks = getTimer().renderPartialTicks;
						Vec3 position = event.entityPlayer.getPositionEyes(partialTicks);
						double d0 = event.entityPlayer.capabilities.isCreativeMode ? 5.0F : 4.5F;
						Vec3 vec3d1 = event.entityPlayer.getLook(partialTicks);
				        Vec3 look = position.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
				        
						PacketHandler.sendPacketToServer(new PickupPacket(position, look));
					}
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
	//@SideOnly(Side.CLIENT)
	//public static RenderItem renderer;
	
	@SideOnly(Side.CLIENT)
	@Method(modid = "creativecore")
	public void renderTickFull()
	{
		if(mc == null)
			mc = Minecraft.getMinecraft();
		//if(renderer == null)
			//renderer = (RenderItem) mc.getRenderManager().getEntityClassRenderObject(EntityItem.class);
		if(mc != null && mc.thePlayer != null && mc.inGameHasFocus)
		{
			if(ItemDummyContainer.customPickup)
			{
				double distance = 100;
				if(mc.objectMouseOver != null)
					if(mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
						distance = mc.getRenderViewEntity().getDistance(mc.objectMouseOver.hitVec.xCoord, mc.objectMouseOver.hitVec.yCoord, mc.objectMouseOver.hitVec.zCoord);
					else if(mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY)
						distance = mc.getRenderViewEntity().getDistanceToEntity(mc.objectMouseOver.entityHit);
				MovingObjectPosition result = getEntityItem(distance, mc.thePlayer);
				if(result != null)
				{
					EntityItem entity = (EntityItem) result.entityHit;
					if(entity != null && mc.inGameHasFocus && distance > mc.getRenderViewEntity().getDistance(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord))
					{
						int space = 15;
						List list = new ArrayList();
						try{
							entity.getEntityItem().getItem().addInformation(entity.getEntityItem(), mc.thePlayer, list, true);
							list.add(entity.getEntityItem().getDisplayName());
						}catch(Exception e){
							list = new ArrayList();
							list.add("ERRORED");
						}
						
						int width = 0;
						int height = (mc.fontRendererObj.FONT_HEIGHT+space+1)*list.size();
						for(int i = 0; i < list.size(); i++)
						{
							String text = (String) list.get(i);
							width = Math.max(width, mc.fontRendererObj.getStringWidth(text)+10);
						}
						
						GL11.glEnable(GL11.GL_BLEND);
				        GL11.glDisable(GL11.GL_TEXTURE_2D);
				        GL11.glEnable(GL11.GL_ALPHA_TEST);
				        
						GL11.glPushMatrix();
						GL11.glTranslated(mc.displayWidth/4-width/2, mc.displayHeight/4-height/2-space/2, 0);
						double rgb = (Math.sin(Math.toRadians((double)System.nanoTime()/10000000D))+1)*0.2;
						Vec3 color = new Vec3(rgb, rgb, rgb);
						//System.out.println(color.xCoord);
						//RenderHelper2D.drawRect(0, 0, width, height, color, 0.3); TODO ADD IT AGAIN
						color = new Vec3(0, 0, 0);
						//RenderHelper2D.drawRect(1, 1, width-1, height-1, color, 0.1); TODO ADD IT AGAIN
						
						GL11.glPopMatrix();
						
						
				        //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				        //Gui.drawRect(0, 0, 100, 100, 100);
				        GL11.glEnable(GL11.GL_TEXTURE_2D);
				        GL11.glDisable(GL11.GL_BLEND);
						for(int i = 0; i < list.size(); i++)
						{
							String text = (String) list.get(i);
							mc.fontRendererObj.drawString(text, mc.displayWidth/4-mc.fontRendererObj.getStringWidth(text)/2, mc.displayHeight/4+((list.size()/2)*space-space*(i+1)), 16579836);
						}
						//renderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, ((EntityItem)move.entityHit).getEntityItem(), 10, 10);
					}
				}
			}
			if(ItemDummyContainer.customThrow)
			{
				if(mc.thePlayer.getHeldItem() != null)
				{
					if(mc.gameSettings.keyBindDrop.isKeyDown())
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
					mc.fontRendererObj.drawString(text, mc.displayWidth/4-mc.fontRendererObj.getStringWidth(text)/2, mc.displayHeight/4+mc.displayHeight/8, 16579836);
				}
			}else{
				if(mc.gameSettings.keyBindDrop.isPressed())
				{
					C07PacketPlayerDigging.Action action = GuiScreen.isCtrlKeyDown() ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
				    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
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
}
