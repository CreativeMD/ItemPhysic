package team.creative.itemphysic.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;

@OnlyIn(value = Dist.CLIENT)
public class ItemPhysicClient {
	
	public static KeyBinding pickup = new KeyBinding("key.pickup.item", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.gameplay");
	public static Minecraft mc;
	
	public static void init(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(pickup);
		mc = event.getMinecraftSupplier().get();
		
		MinecraftForge.EVENT_BUS.register(ItemPhysicClient.class);
	}
	
	public static long lastTickTime;
	
	@SubscribeEvent
	@OnlyIn(value = Dist.CLIENT)
	public static void renderTick(RenderTickEvent event) {
		if (event.phase == Phase.END)
			lastTickTime = System.nanoTime();
		
		renderTickFull();
	}
	
	public static boolean renderItem(ItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, net.minecraft.client.renderer.ItemRenderer itemRenderer, Random random) {
		if (entityIn.getAge() == 0)
			return false;
		
		matrixStackIn.push();
		ItemStack itemstack = entityIn.getItem();
		int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
		random.setSeed(i);
		IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(itemstack, entityIn.world, (LivingEntity) null);
		boolean flag = ibakedmodel.isGui3d();
		int j = getModelCount(itemstack);
		
		float rotateBy = (System.nanoTime() - lastTickTime) / 200000000F;
		if (mc.isGamePaused())
			rotateBy = 0;
		
		if (entityIn.world.isMaterialInBB(entityIn.getBoundingBox(), Material.WEB))
			rotateBy /= 50;
		
		matrixStackIn.rotate(Vector3f.XP.rotation((float) Math.PI / 2));
		matrixStackIn.rotate(Vector3f.ZP.rotation(entityIn.rotationYaw));
		
		boolean applyEffects = entityIn.getAge() != 0 && (flag || mc.getRenderManager().options != null);
		
		//Handle Rotations
		if (applyEffects) {
			if (flag) {
				if (!entityIn.onGround) {
					rotateBy *= 2;
					Fluid fluid = CommonPhysic.getFluid(entityIn);
					if (fluid == null)
						fluid = CommonPhysic.getFluid(entityIn, true);
					if (fluid != null)
						rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
					
					entityIn.rotationPitch += rotateBy;
				}
			} else if (entityIn != null && !Double.isNaN(entityIn.getPosX()) && !Double.isNaN(entityIn.getPosY()) && !Double.isNaN(entityIn.getPosZ()) && entityIn.world != null) {
				if (entityIn.onGround) {
					if (!flag)
						entityIn.rotationPitch = 0;
				} else {
					rotateBy *= 2;
					Fluid fluid = CommonPhysic.getFluid(entityIn);
					if (fluid != null)
						rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
					
					entityIn.rotationPitch += rotateBy;
				}
			}
			
			if (flag)
				matrixStackIn.translate(0, -0.2, -0.08);
			else if (entityIn.world.getBlockState(entityIn.getPosition()).getBlock() == Blocks.SNOW || entityIn.world.getBlockState(entityIn.getPosition().down()).getBlock() == Blocks.SOUL_SAND)
				matrixStackIn.translate(0, 0.0, -0.14);
			else
				matrixStackIn.translate(0, 0, -0.04);
			
			double height = 0.2;
			if (flag)
				matrixStackIn.translate(0, height, 0);
			matrixStackIn.rotate(Vector3f.YP.rotation(entityIn.rotationPitch));
			if (flag)
				matrixStackIn.translate(0, -height, 0);
		}
		
		if (!flag) {
			float f7 = -0.0F * (j - 1) * 0.5F;
			float f8 = -0.0F * (j - 1) * 0.5F;
			float f9 = -0.09375F * (j - 1) * 0.5F;
			matrixStackIn.translate(f7, f8, f9);
		}
		
		for (int k = 0; k < j; ++k) {
			matrixStackIn.push();
			if (k > 0) {
				if (flag) {
					float f11 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f13 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrixStackIn.translate(f11, f13, f10);
				}
			}
			
			itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
			matrixStackIn.pop();
			if (!flag)
				matrixStackIn.translate(0.0, 0.0, 0.05375F);
		}
		
		matrixStackIn.pop();
		return true;
	}
	
	public static int getModelCount(ItemStack stack) {
		
		if (stack.getCount() > 48)
			return 5;
		if (stack.getCount() > 32)
			return 4;
		if (stack.getCount() > 16)
			return 3;
		if (stack.getCount() > 1)
			return 2;
		
		return 1;
	}
	
	public static boolean onPlayerInteractClient(World world, PlayerEntity player, boolean rightClick) {
		RayTraceResult result = getEntityItem(mc.player);
		if (result != null && result.getType() == Type.ENTITY) {
			ItemEntity entity = (ItemEntity) ((EntityRayTraceResult) result).getEntity();
			if (world.isRemote && entity != null) {
				player.swingArm(Hand.MAIN_HAND);
				ItemPhysic.NETWORK.sendToServer(new PickupPacket(entity.getUniqueID(), rightClick));
				return true;
			}
		}
		return false;
	}
	
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event) {
		World world = event.getWorld();
		if (event instanceof RightClickEmpty || event instanceof RightClickBlock || event instanceof EntityInteract) {
			if (world.isRemote && ItemPhysic.CONFIG.pickup.customPickup) {
				if (!ItemPhysicClient.pickup.getKey().equals(InputMappings.INPUT_INVALID))
					return;
				
				if (onPlayerInteractClient(world, event.getPlayer(), event instanceof RightClickBlock)) {
					if (event instanceof RightClickBlock) {
						((RightClickBlock) event).setUseBlock(Result.DENY);
						((RightClickBlock) event).setUseItem(Result.DENY);
						if (event.isCancelable())
							event.setCanceled(true);
					}
				}
			}
		}
	}
	
	public static RayTraceResult getEntityItem(PlayerEntity player) {
		double distance = CommonPhysic.getReachDistance(player);
		float partialTicks = mc.getRenderPartialTicks();
		Vec3d position = player.getEyePosition(partialTicks);
		Vec3d vec3d1 = player.getLook(partialTicks);
		Vec3d look = position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
		
		RayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(position, look, BlockMode.COLLIDER, FluidMode.NONE, player));
		if (result != null)
			distance = Math.min(distance, result.getHitVec().squareDistanceTo(position));
		
		AxisAlignedBB axisalignedbb = player.getBoundingBox().expand(vec3d1.scale(distance)).grow(1.0D, 1.0D, 1.0D);
		EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(player, position, look, axisalignedbb, (p_215312_0_) -> {
			return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
		}, distance);
		if (entityraytraceresult != null) {
			Vec3d vec3d3 = entityraytraceresult.getHitVec();
			double d2 = position.squareDistanceTo(vec3d3);
			if (d2 < distance || result == null) {
				result = entityraytraceresult;
				distance = d2;
			}
		}
		
		return CommonPhysic.getEntityItem(player, position, position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance), distance);
		
	}
	
	public static void renderTickFull() {
		if (mc != null && mc.player != null && !mc.isGamePaused()) {
			if (ItemPhysic.CONFIG.pickup.customPickup) {
				
				RayTraceResult result = getEntityItem(mc.player);
				if (result != null && result.getType() == Type.ENTITY) {
					if (ItemPhysicClient.pickup.isKeyDown())
						onPlayerInteractClient(mc.world, mc.player, false);
					ItemEntity entity = (ItemEntity) ((EntityRayTraceResult) result).getEntity();
					if (entity != null && ItemPhysic.CONFIG.rendering.showPickupTooltip) {
						int space = 15;
						List<ITextComponent> list = new ArrayList<>();
						try {
							entity.getItem().getItem().addInformation(entity.getItem(), mc.player.world, list, ITooltipFlag.TooltipFlags.NORMAL);
							list.add(entity.getItem().getDisplayName());
						} catch (Exception e) {
							list = new ArrayList();
							list.add(new StringTextComponent("ERRORED"));
						}
						
						int width = 0;
						for (int i = 0; i < list.size(); i++) {
							String text = list.get(i).getString();
							width = Math.max(width, mc.fontRenderer.getStringWidth(text) + 10);
						}
						
						RenderSystem.disableBlend();
						RenderSystem.enableAlphaTest();
						RenderSystem.enableTexture();
						for (int i = 0; i < list.size(); i++) {
							String text = list.get(i).getString();
							mc.fontRenderer.drawString(text, mc.getMainWindow().getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(text) / 2, mc.getMainWindow().getScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)), 16579836);
						}
						
					}
				}
			}
			
			if (ItemPhysic.CONFIG.general.customThrow && !ItemPhysic.CONFIG.rendering.disableThrowHUD) {
				if (throwingPower > 0) {
					int renderPower = throwingPower;
					renderPower /= 6;
					if (renderPower < 1)
						renderPower = 1;
					if (renderPower > 6)
						renderPower = 6;
					mc.player.sendStatusMessage(new TranslationTextComponent("item.throw", renderPower), true);
				}
			}
		}
	}
	
	public static int throwingPower;
	
	@SubscribeEvent
	public static void gameTick(ClientTickEvent event) {
		if (event.phase == Phase.END) {
			if (mc.player != null && mc.player.getHeldItemMainhand() != null) {
				if (ItemPhysic.CONFIG.general.customThrow) {
					if (mc.gameSettings.keyBindDrop.isKeyDown())
						throwingPower++;
					else {
						if (throwingPower > 0) {
							throwingPower /= 6;
							if (throwingPower < 1)
								throwingPower = 1;
							if (throwingPower > 6)
								throwingPower = 6;
							
							boolean dropAll = Screen.hasControlDown();
							
							ItemPhysic.NETWORK.sendToServer(new DropPacket(throwingPower));
							CPlayerDiggingPacket.Action cplayerdiggingpacket$action = dropAll ? CPlayerDiggingPacket.Action.DROP_ALL_ITEMS : CPlayerDiggingPacket.Action.DROP_ITEM;
							mc.player.connection.sendPacket(new CPlayerDiggingPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
							if (mc.player.inventory.decrStackSize(mc.player.inventory.currentItem, dropAll && !mc.player.inventory.getCurrentItem().isEmpty() ? mc.player.inventory.getCurrentItem().getCount() : 1) != ItemStack.EMPTY)
								mc.player.swingArm(Hand.MAIN_HAND);
						}
						throwingPower = 0;
					}
				}
			}
		}
	}
	
	public static boolean dropItem(boolean dropAll) {
		return ItemPhysic.CONFIG.general.customThrow;
	}
	
}
