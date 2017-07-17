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
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {
	
	public static int Droppower = 1;
	
	@SubscribeEvent
	public void onToos(ItemTossEvent event)
	{
		//System.out.println("Increase motion " + event.getEntityItem() + " Droppower=" + Droppower);
		if(!ItemTransformer.isLite)
		{
			event.getEntityItem().motionX *= Droppower;
			event.getEntityItem().motionY *= Droppower;
			event.getEntityItem().motionZ *= Droppower;
			Droppower = 1;
		}
	}
	
	@Method(modid = "creativecore")
	public static RayTraceResult getEntityItem(EntityPlayer player, Vec3d position, Vec3d look)
	{
		float f1 = 3.0F;
		double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Vec3d include = look.subtract(position);
        List list = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().addCoord(include.xCoord, include.yCoord, include.zCoord).expand((double)f1, (double)f1, (double)f1));
        //System.out.println("Found " + list.size() + " items in range!");
        //Vec3d vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
		double d1 = d0;
        
        if(player.getEntityWorld().isRemote)
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
            	
                AxisAlignedBB axisalignedbb = new AxisAlignedBB(entity.getEntityBoundingBox().minX, entity.getEntityBoundingBox().minY, entity.getEntityBoundingBox().minZ, entity.getEntityBoundingBox().maxX, entity.getEntityBoundingBox().maxY, entity.getEntityBoundingBox().maxZ).expandXyz((double) 0.2);
                RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(position, look);
                
                if(movingobjectposition != null)
                {
                	movingobjectposition.typeOfHit = Type.ENTITY;
                	movingobjectposition.entityHit = entity;
                }
                
                if (axisalignedbb.isVecInside(position))
                {
                    if (0.0D < d2 || d2 == 0.0D)
                    {
                        return new RayTraceResult(entity);
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
	
	@SideOnly(Side.CLIENT)
	@Method(modid = "creativecore")
	public static RayTraceResult getEntityItem(double distance, EntityPlayer player)
	{
		//Minecraft mc = Minecraft.getMinecraft();
		/*Vec3 vec31 = player.getLook(1.0F);
		float f1 = 1.0F;
		double reach = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Entity entity = player.world.findNearestEntityWithinAABB(EntityItem.class, player.boundingBox.addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand((double)f1, (double)f1, (double)f1), player);
		if(entity instanceof EntityItem && player.getDistanceSqToEntity(entity) <= reach)
			return (EntityItem) entity;
		return null;*/
		
		//Vec3d vec31 = player.getLook(1.0F);
		float partialTicks = mc.getRenderPartialTicks();
		Vec3d position = player.getPositionEyes(partialTicks);
		double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
		Vec3d vec3d1 = player.getLook(partialTicks);
        Vec3d look = position.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
        
        RayTraceResult result = getEntityItem(player, position, look);
        
		if(result != null && position.distanceTo(result.hitVec) < distance)
			return result;
		
		return null;
        
	}
	
	public static boolean cancel = false;
	
	@SubscribeEvent
	@Method(modid = "creativecore")
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event instanceof RightClickEmpty || event instanceof RightClickBlock || event instanceof EntityInteract)
			onPlayerInteract(event, event.getWorld(), event.getEntityPlayer());
	}
	
	@SideOnly(Side.CLIENT)
	public void onPlayerInteractClient(PlayerInteractEvent event, World world, EntityPlayer player)
	{
		double distance = 100;
		Vec3d position = mc.getRenderViewEntity().getPositionEyes(mc.getRenderPartialTicks());
		if(mc.objectMouseOver != null)
			if(mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
				distance = position.distanceTo(mc.objectMouseOver.hitVec);
			else if(mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
				distance = mc.objectMouseOver.entityHit.getDistance(position.xCoord, position.yCoord, position.zCoord);
		RayTraceResult result = getEntityItem(distance, mc.player);
		if(result != null)
		{
			EntityItem entity = (EntityItem) result.entityHit;
			if(world.isRemote && entity != null && distance > mc.getRenderViewEntity().getDistance(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord))
			{
				float partialTicks = mc.getRenderPartialTicks();
				double d0 = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
				Vec3d vec3d1 = player.getLook(partialTicks);
		        Vec3d look = position.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
		        if(event instanceof RightClickBlock)
		        {
		        	((RightClickBlock) event).setUseBlock(Result.DENY);
		        	((RightClickBlock) event).setUseItem(Result.DENY);
		        	if(event.isCancelable())
		        		event.setCanceled(true);
		        }
		        //System.out.println(result.entityHit.getUniqueID());
		        
				PacketHandler.sendPacketToServer(new PickupPacket(result.entityHit.getUniqueID()));
			}
		}
	}
	
	@Method(modid = "creativecore")
	public void onPlayerInteract(PlayerInteractEvent event, World world, EntityPlayer player)
	{
		if(!ItemTransformer.isLite)
		{
			if(world.isRemote && ItemDummyContainer.customPickup)
			{
				onPlayerInteractClient(event, world, player);
			}
			if(!player.world.isRemote)
			{
				//entity.interactFirst(event.entityPlayer);	
				if(cancel)
				{
					cancel = false;
					event.setCanceled(true);
				}
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
		if(mc != null && mc.player != null && mc.inGameHasFocus)
		{
			if(ItemDummyContainer.customPickup)
			{
				double distance = 100;
				Vec3d position = mc.getRenderViewEntity().getPositionEyes(mc.getRenderPartialTicks());
				if(mc.objectMouseOver != null)
					if(mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
						distance = position.distanceTo(mc.objectMouseOver.hitVec);
					else if(mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
						distance = mc.objectMouseOver.entityHit.getDistance(position.xCoord, position.yCoord, position.zCoord);
				RayTraceResult result = getEntityItem(distance, mc.player);
				if(result != null)
				{
					EntityItem entity = (EntityItem) result.entityHit;
					if(entity != null && mc.inGameHasFocus && ItemDummyContainer.showTooltip && distance > mc.getRenderViewEntity().getDistance(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord))
					{
						int space = 15;
						List list = new ArrayList();
						try{
							entity.getEntityItem().getItem().addInformation(entity.getEntityItem(), mc.player, list, true);
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
						Vec3d color = new Vec3d(rgb, rgb, rgb);
						//System.out.println(color.xCoord);
						//RenderHelper2D.drawRect(0, 0, width, height, color, 0.3);
						color = new Vec3d(0, 0, 0);
						//RenderHelper2D.drawRect(1, 1, width-1, height-1, color, 0.1);
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
						//GL11.glEnable(GL11.GL_BLEND);
						
					}
				}
			}
			if(ItemDummyContainer.customThrow)
			{
				if(power > 0)
				{
					int renderPower = power;
					renderPower /= 6;
					if(renderPower < 1)
						renderPower = 1;
					if(renderPower > 6)
						renderPower = 6;
					String text = "Power: " + renderPower;
					mc.player.sendStatusMessage(new TextComponentString(text), true);
					//mc.fontRendererObj.drawString(text, mc.displayWidth/4-mc.fontRendererObj.getStringWidth(text)/2, mc.displayHeight/4+mc.displayHeight/8, 16579836);
				}
			}/*else{
				while(mc.gameSettings.keyBindDrop.isPressed())
				{
					CPacketPlayerDigging.Action action = GuiScreen.isCtrlKeyDown() ? CPacketPlayerDigging.Action.DROP_ALL_ITEMS : CPacketPlayerDigging.Action.DROP_ITEM;
				    mc.player.connection.sendPacket(new CPacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
				    System.out.println("Drop");
				}
			}*/
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void gameTick(ClientTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			if(mc.player != null && mc.player.getHeldItemMainhand() != null)
			{
				if(mc.gameSettings.keyBindDrop.isKeyDown())
					power++;
				else
				{
					if(power > 0)
					{
						power /= 6;
						if(power < 1)
							power = 1;
						if(power > 6)
							power = 6;
						if(ItemDummyContainer.customThrow)
							PacketHandler.sendPacketToServer(new DropPacket(power));
						CPacketPlayerDigging.Action action = GuiScreen.isCtrlKeyDown() ? CPacketPlayerDigging.Action.DROP_ALL_ITEMS : CPacketPlayerDigging.Action.DROP_ITEM;
					    mc.player.connection.sendPacket(new CPacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
					}
					power = 0;
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
