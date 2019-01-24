package com.creativemd.itemphysic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {
	
	public static int Droppower = 1;
	
	@SubscribeEvent
	public void onToos(ItemTossEvent event) {
		if (!ItemTransformer.isLite) {
			event.getEntityItem().motionX *= Droppower;
			event.getEntityItem().motionY *= Droppower;
			event.getEntityItem().motionZ *= Droppower;
			Droppower = 1;
		}
	}
	
	@Method(modid = "creativecore")
	public static RayTraceResult getEntityItem(EntityPlayer player, Vec3d position, Vec3d look, double distance) {
		float f1 = 3.0F;
		Vec3d include = look.subtract(position);
		List list = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(include.x, include.y, include.z).expand((double) f1, (double) f1, (double) f1));
		double d1 = distance;
		
		if (player.getEntityWorld().isRemote) {
			if (mc.objectMouseOver != null) {
				d1 = mc.objectMouseOver.hitVec.distanceTo(position);
			}
		}
		
		double d2 = d1;
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);
			if (entity instanceof EntityItem) {
				
				AxisAlignedBB axisalignedbb = new AxisAlignedBB(entity.getEntityBoundingBox().minX, entity.getEntityBoundingBox().minY, entity.getEntityBoundingBox().minZ, entity.getEntityBoundingBox().maxX, entity.getEntityBoundingBox().maxY, entity.getEntityBoundingBox().maxZ).grow(0.2);
				RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(position, look);
				
				if (movingobjectposition != null) {
					movingobjectposition.typeOfHit = Type.ENTITY;
					movingobjectposition.entityHit = entity;
				}
				
				if (axisalignedbb.contains(position)) {
					if (0.0D < d2 || d2 == 0.0D) {
						return new RayTraceResult(entity);
					}
				} else if (movingobjectposition != null) {
					return movingobjectposition;
				}
			}
		}
		return null;
	}
	
	public static double getReachDistance(EntityPlayer player) {
		if (ItemDummyContainer.maximumPickupRange != 5)
			return ItemDummyContainer.maximumPickupRange;
		return player.capabilities.isCreativeMode ? 5 : 4.5F;
	}
	
	@SideOnly(Side.CLIENT)
	@Method(modid = "creativecore")
	public static RayTraceResult getEntityItem(double distance, EntityPlayer player) {
		
		float partialTicks = mc.getRenderPartialTicks();
		Vec3d position = player.getPositionEyes(partialTicks);
		Vec3d vec3d1 = player.getLook(partialTicks);
		Vec3d look = position.addVector(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
		
		return getEntityItem(player, position, look, distance);
		
	}
	
	public static boolean cancel = false;
	private final Random avRandomizer = new Random();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	@Method(modid = "creativecore")
	public void onDrop(HarvestDropsEvent event) {
		if (ItemDummyContainer.pickupMinedImmediately && event.getHarvester() != null) {
			boolean pickedUp = false;
			for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
				ItemStack stack = iterator.next();
				if (event.getHarvester().addItemStackToInventory(stack)) {
					iterator.remove();
					pickedUp = true;
				}
			}
			
			if (pickedUp)
				event.getWorld().playSound(event.getHarvester().posX, event.getHarvester().posY, event.getHarvester().posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 1.4F + 2.0F, false);
		}
	}
	
	@SubscribeEvent
	@Method(modid = "creativecore")
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event instanceof RightClickEmpty || event instanceof RightClickBlock || event instanceof EntityInteract)
			onPlayerInteract(event, event.getWorld(), event.getEntityPlayer());
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onPlayerInteractClient(World world, EntityPlayer player, boolean rightClick) {
		double distance = getReachDistance(player);
		Vec3d position = mc.getRenderViewEntity().getPositionEyes(mc.getRenderPartialTicks());
		if (mc.objectMouseOver != null)
			if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
				distance = Math.min(distance, position.distanceTo(mc.objectMouseOver.hitVec));
			else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
				distance = Math.min(distance, mc.objectMouseOver.entityHit.getDistance(position.x, position.y, position.z));
			
		RayTraceResult result = getEntityItem(distance, mc.player);
		if (result != null) {
			EntityItem entity = (EntityItem) result.entityHit;
			if (world.isRemote && entity != null && distance > mc.getRenderViewEntity().getDistance(result.hitVec.x, result.hitVec.y, result.hitVec.z)) {
				player.swingArm(EnumHand.MAIN_HAND);
				PacketHandler.sendPacketToServer(new PickupPacket(result.entityHit.getUniqueID(), rightClick));
				return true;
			}
		}
		return false;
	}
	
	@Method(modid = "creativecore")
	public void onPlayerInteract(PlayerInteractEvent event, World world, EntityPlayer player) {
		if (!ItemTransformer.isLite) {
			if (world.isRemote && ItemDummyContainer.customPickup) {
				if (ItemPhysicClient.pickup.getKeyCode() != Keyboard.KEY_NONE)
					return;
				
				if (onPlayerInteractClient(world, player, true)) {
					if (event instanceof RightClickBlock) {
						((RightClickBlock) event).setUseBlock(Result.DENY);
						((RightClickBlock) event).setUseItem(Result.DENY);
						if (event.isCancelable())
							event.setCanceled(true);
					}
				}
			}
			if (!player.world.isRemote) {
				if (cancel) {
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
	
	@SideOnly(Side.CLIENT)
	@Method(modid = "creativecore")
	public void renderTickFull() {
		if (mc == null)
			mc = Minecraft.getMinecraft();
		
		if (mc != null && mc.player != null && mc.inGameHasFocus) {
			if (ItemDummyContainer.customPickup) {
				
				double distance = getReachDistance(mc.player);
				Vec3d position = mc.getRenderViewEntity().getPositionEyes(mc.getRenderPartialTicks());
				if (mc.objectMouseOver != null)
					if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
						distance = Math.min(distance, position.distanceTo(mc.objectMouseOver.hitVec));
					else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
						distance = Math.min(distance, mc.objectMouseOver.entityHit.getDistance(position.x, position.y, position.z));
					
				RayTraceResult result = getEntityItem(distance, mc.player);
				if (result != null) {
					if (ItemPhysicClient.pickup.isKeyDown())
						onPlayerInteractClient(mc.world, mc.player, false);
					EntityItem entity = (EntityItem) result.entityHit;
					if (entity != null && mc.inGameHasFocus && ItemDummyContainer.showTooltip && distance > mc.getRenderViewEntity().getDistance(result.hitVec.x, result.hitVec.y, result.hitVec.z)) {
						int space = 15;
						List<String> list = new ArrayList<>();
						try {
							entity.getItem().getItem().addInformation(entity.getItem(), mc.player.world, list, ITooltipFlag.TooltipFlags.NORMAL);
							list.add(entity.getItem().getDisplayName());
						} catch (Exception e) {
							list = new ArrayList();
							list.add("ERRORED");
						}
						
						int width = 0;
						int height = (mc.fontRenderer.FONT_HEIGHT + space + 1) * list.size();
						for (int i = 0; i < list.size(); i++) {
							String text = (String) list.get(i);
							width = Math.max(width, mc.fontRenderer.getStringWidth(text) + 10);
						}
						
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glEnable(GL11.GL_ALPHA_TEST);
						
						GL11.glPushMatrix();
						GL11.glTranslated(mc.displayWidth / 4 - width / 2, mc.displayHeight / 4 - height / 2 - space / 2, 0);
						double rgb = (Math.sin(Math.toRadians((double) System.nanoTime() / 10000000D)) + 1) * 0.2;
						Vec3d color = new Vec3d(rgb, rgb, rgb);
						color = new Vec3d(0, 0, 0);
						GL11.glPopMatrix();
						
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glDisable(GL11.GL_BLEND);
						for (int i = 0; i < list.size(); i++) {
							String text = (String) list.get(i);
							mc.fontRenderer.drawString(text, mc.displayWidth / 4 - mc.fontRenderer.getStringWidth(text) / 2, mc.displayHeight / 4 + ((list.size() / 2) * space - space * (i + 1)), 16579836);
						}
						
					}
				}
			}
			if (ItemDummyContainer.customThrow && !ItemDummyContainer.disableThrowHUD) {
				if (power > 0) {
					int renderPower = power;
					renderPower /= 6;
					if (renderPower < 1)
						renderPower = 1;
					if (renderPower > 6)
						renderPower = 6;
					String text = "Power: " + renderPower;
					mc.player.sendStatusMessage(new TextComponentString(text), true);
				}
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void gameTick(ClientTickEvent event) {
		if (mc == null)
			mc = Minecraft.getMinecraft();
		if (event.phase == Phase.END) {
			if (mc.player != null && mc.player.getHeldItemMainhand() != null) {
				if (mc.gameSettings.keyBindDrop.isKeyDown())
					power++;
				else {
					if (power > 0) {
						power /= 6;
						if (power < 1)
							power = 1;
						if (power > 6)
							power = 6;
						if (ItemDummyContainer.customThrow)
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
	public void renderTick(RenderTickEvent event) {
		if (event.phase == Phase.END) {
			ClientPhysic.tick = System.nanoTime();
			if (!ItemTransformer.isLite) {
				renderTickFull();
			}
		}
	}
}
