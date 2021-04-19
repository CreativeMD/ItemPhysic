package team.creative.itemphysic.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;

@OnlyIn(value = Dist.CLIENT)
public class ItemPhysicClient {
    
    public static KeyBinding pickup = new KeyBinding("key.pickup.item", InputMappings.UNKNOWN.getValue(), "key.categories.gameplay");
    public static Minecraft mc;
    private static final Field skipPhysicRenderer = ObfuscationReflectionHelper.findField(ItemEntity.class, "skipPhysicRenderer");
    
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
        
        if (event.phase == Phase.END && mc.screen == null)
            renderTickFull();
    }
    
    public static boolean renderItem(ItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, net.minecraft.client.renderer.ItemRenderer itemRenderer, Random random) {
        try {
            if (entityIn.getAge() == 0 || skipPhysicRenderer.getBoolean(entityIn) || ItemPhysic.CONFIG.rendering.vanillaRendering)
                return false;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        matrixStackIn.pushPose();
        ItemStack itemstack = entityIn.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
        random.setSeed(i);
        IBakedModel ibakedmodel = itemRenderer.getModel(itemstack, entityIn.level, (LivingEntity) null);
        boolean flag = ibakedmodel.isGui3d();
        int j = getModelCount(itemstack);
        
        float rotateBy = (System.nanoTime() - lastTickTime) / 200000000F * ItemPhysic.CONFIG.rendering.rotateSpeed;
        if (mc.isPaused())
            rotateBy = 0;
        
        Vector3d motionMultiplier = getMotionMultiplier(entityIn);
        if (motionMultiplier != null && motionMultiplier.lengthSqr() > 0)
            rotateBy *= motionMultiplier.x * 0.2;
        
        matrixStackIn.mulPose(Vector3f.XP.rotation((float) Math.PI / 2));
        matrixStackIn.mulPose(Vector3f.ZP.rotation(entityIn.yRot));
        
        boolean applyEffects = entityIn.getAge() != 0 && (flag || mc.options != null);
        
        //Handle Rotations
        if (applyEffects) {
            if (flag) {
                if (!entityIn.isOnGround()) {
                    rotateBy *= 2;
                    Fluid fluid = CommonPhysic.getFluid(entityIn);
                    if (fluid == null)
                        fluid = CommonPhysic.getFluid(entityIn, true);
                    if (fluid != null)
                        rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
                    
                    entityIn.xRot += rotateBy;
                } else if (ItemPhysic.CONFIG.rendering.oldRotation) {
                    for (int side = 0; side < 4; side++) {
                        double rotation = side * 90;
                        double range = 5;
                        if (entityIn.xRot > rotation - range && entityIn.xRot < rotation + range)
                            entityIn.xRot = (float) rotation;
                    }
                    if (entityIn.xRot != 0 && entityIn.xRot != 90 && entityIn.xRot != 180 && entityIn.xRot != 270) {
                        double Abstand0 = Math.abs(entityIn.xRot);
                        double Abstand90 = Math.abs(entityIn.xRot - 90);
                        double Abstand180 = Math.abs(entityIn.xRot - 180);
                        double Abstand270 = Math.abs(entityIn.xRot - 270);
                        if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270)
                            if (entityIn.xRot < 0)
                                entityIn.xRot += rotateBy;
                            else
                                entityIn.xRot -= rotateBy;
                        if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270)
                            if (entityIn.xRot - 90 < 0)
                                entityIn.xRot += rotateBy;
                            else
                                entityIn.xRot -= rotateBy;
                        if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270)
                            if (entityIn.xRot - 180 < 0)
                                entityIn.xRot += rotateBy;
                            else
                                entityIn.xRot -= rotateBy;
                        if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0)
                            if (entityIn.xRot - 270 < 0)
                                entityIn.xRot += rotateBy;
                            else
                                entityIn.xRot -= rotateBy;
                            
                    }
                }
            } else if (entityIn != null && !Double.isNaN(entityIn.getX()) && !Double.isNaN(entityIn.getY()) && !Double.isNaN(entityIn.getZ()) && entityIn.level != null) {
                if (entityIn.isOnGround()) {
                    if (!flag)
                        entityIn.xRot = 0;
                } else {
                    rotateBy *= 2;
                    Fluid fluid = CommonPhysic.getFluid(entityIn);
                    if (fluid != null)
                        rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
                    
                    entityIn.xRot += rotateBy;
                }
            }
            
            if (flag)
                matrixStackIn.translate(0, -0.2, -0.08);
            else if (entityIn.level.getBlockState(entityIn.blockPosition()).getBlock() == Blocks.SNOW || entityIn.level.getBlockState(entityIn.blockPosition().below())
                    .getBlock() == Blocks.SOUL_SAND)
                matrixStackIn.translate(0, 0.0, -0.14);
            else
                matrixStackIn.translate(0, 0, -0.04);
            
            double height = 0.2;
            if (flag)
                matrixStackIn.translate(0, height, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotation(entityIn.xRot));
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
            matrixStackIn.pushPose();
            if (k > 0) {
                if (flag) {
                    float f11 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrixStackIn.translate(f11, f13, f10);
                }
            }
            
            itemRenderer.render(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
            matrixStackIn.popPose();
            if (!flag)
                matrixStackIn.translate(0.0, 0.0, 0.05375F);
        }
        
        matrixStackIn.popPose();
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
            if (world.isClientSide && entity != null) {
                player.swing(Hand.MAIN_HAND);
                ItemPhysic.NETWORK.sendToServer(new PickupPacket(entity.getOwner(), rightClick));
                return true;
            }
        }
        return false;
    }
    
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        World world = event.getWorld();
        if (event instanceof RightClickEmpty || event instanceof RightClickBlock || event instanceof EntityInteract) {
            if (world.isClientSide && ItemPhysic.CONFIG.pickup.customPickup) {
                if (!ItemPhysicClient.pickup.getKey().equals(InputMappings.UNKNOWN))
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
        float partialTicks = mc.getFrameTime();
        Vector3d position = player.getEyePosition(partialTicks);
        Vector3d vec3d1 = player.getViewVector(partialTicks);
        Vector3d look = position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        
        RayTraceResult result = mc.level.clip(new RayTraceContext(position, look, BlockMode.COLLIDER, FluidMode.NONE, player));
        if (result != null)
            distance = Math.min(distance, result.getLocation().distanceToSqr(position));
        
        AxisAlignedBB axisalignedbb = player.getBoundingBox().expandTowards(vec3d1.scale(distance)).inflate(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = ProjectileHelper.getEntityHitResult(player, position, look, axisalignedbb, (p_215312_0_) -> {
            return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
        }, distance);
        if (entityraytraceresult != null) {
            Vector3d vec3d3 = entityraytraceresult.getLocation();
            double d2 = position.distanceToSqr(vec3d3);
            if (d2 < distance || result == null) {
                result = entityraytraceresult;
                distance = d2;
            }
        }
        
        return CommonPhysic.getEntityItem(player, position, position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance), distance);
        
    }
    
    public static void renderTickFull() {
        if (mc != null && mc.player != null && !mc.isPaused()) {
            if (ItemPhysic.CONFIG.pickup.customPickup) {
                
                RayTraceResult result = getEntityItem(mc.player);
                if (result != null && result.getType() == Type.ENTITY) {
                    if (ItemPhysicClient.pickup.isDown())
                        onPlayerInteractClient(mc.level, mc.player, false);
                    ItemEntity entity = (ItemEntity) ((EntityRayTraceResult) result).getEntity();
                    if (entity != null && ItemPhysic.CONFIG.rendering.showPickupTooltip) {
                        int space = 15;
                        List<ITextComponent> list = new ArrayList<>();
                        try {
                            entity.getItem().getItem().appendHoverText(entity.getItem(), mc.player.level, list, ITooltipFlag.TooltipFlags.NORMAL);
                            list.add(entity.getItem().getDisplayName());
                        } catch (Exception e) {
                            list = new ArrayList();
                            list.add(new StringTextComponent("ERRORED"));
                        }
                        
                        int width = 0;
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            width = Math.max(width, mc.font.width(text) + 10);
                        }
                        
                        RenderSystem.disableBlend();
                        RenderSystem.enableAlphaTest();
                        RenderSystem.enableTexture();
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            mc.font.drawShadow(new MatrixStack(), text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow()
                                    .getGuiScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)), 16579836);
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
                    mc.player.displayClientMessage(new TranslationTextComponent("item.throw", renderPower), true);
                }
            }
        }
    }
    
    public static int throwingPower;
    
    @SubscribeEvent
    public static void gameTick(ClientTickEvent event) {
        if (event.phase == Phase.END) {
            if (mc.player != null && mc.player.getMainHandItem() != null) {
                if (ItemPhysic.CONFIG.general.customThrow) {
                    if (mc.options.keyDrop.isDown())
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
                            mc.player.connection.send(new CPlayerDiggingPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
                            if (mc.player.inventory.removeItem(mc.player.inventory.selected, dropAll && !mc.player.inventory.getSelected().isEmpty() ? mc.player.inventory
                                    .getSelected().getCount() : 1) != ItemStack.EMPTY)
                                mc.player.swing(Hand.MAIN_HAND);
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
    
    private static Field motionMultiplierField = null;
    
    public static Vector3d getMotionMultiplier(Entity entity) {
        if (motionMultiplierField == null)
            motionMultiplierField = ObfuscationReflectionHelper.findField(Entity.class, "field_213328_B");
        try {
            return (Vector3d) motionMultiplierField.get(entity);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }
    
}
