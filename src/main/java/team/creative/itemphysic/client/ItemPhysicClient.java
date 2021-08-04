package team.creative.itemphysic.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;

@OnlyIn(value = Dist.CLIENT)
public class ItemPhysicClient {
    
    public static KeyMapping pickup = new KeyMapping("key.pickup.item", InputConstants.UNKNOWN.getValue(), "key.categories.gameplay");
    public static Minecraft mc;
    private static final Field skipPhysicRenderer = ObfuscationReflectionHelper.findField(ItemEntity.class, "skipPhysicRenderer");
    
    public static void init(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(pickup);
        mc = Minecraft.getInstance();
        
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
    
    public static boolean render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, ItemRenderer itemRenderer, Random rand) {
        try {
            if (entity.getAge() == 0 || skipPhysicRenderer.getBoolean(entity) || ItemPhysic.CONFIG.rendering.vanillaRendering)
                return false;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        pose.pushPose();
        ItemStack itemstack = entity.getItem();
        rand.setSeed(itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue());
        BakedModel bakedmodel = itemRenderer.getModel(itemstack, entity.level, (LivingEntity) null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        int j = getModelCount(itemstack);
        
        float rotateBy = (System.nanoTime() - lastTickTime) / 200000000F * ItemPhysic.CONFIG.rendering.rotateSpeed;
        if (mc.isPaused())
            rotateBy = 0;
        
        Vec3 motionMultiplier = getStuckSpeedMultiplier(entity);
        if (motionMultiplier != null && motionMultiplier.lengthSqr() > 0)
            rotateBy *= motionMultiplier.x * 0.2;
        
        pose.mulPose(Vector3f.XP.rotation((float) Math.PI / 2));
        pose.mulPose(Vector3f.ZP.rotation(entity.getYRot()));
        
        boolean applyEffects = entity.getAge() != 0 && (flag || mc.options != null);
        
        //Handle Rotations
        if (applyEffects) {
            if (flag) {
                if (!entity.isOnGround()) {
                    rotateBy *= 2;
                    Fluid fluid = CommonPhysic.getFluid(entity);
                    if (fluid == null)
                        fluid = CommonPhysic.getFluid(entity, true);
                    if (fluid != null)
                        rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
                    
                    entity.setXRot(entity.getXRot() + rotateBy);
                } else if (ItemPhysic.CONFIG.rendering.oldRotation) {
                    for (int side = 0; side < 4; side++) {
                        double rotation = side * 90;
                        double range = 5;
                        if (entity.getXRot() > rotation - range && entity.getXRot() < rotation + range)
                            entity.setXRot((float) rotation);
                    }
                    if (entity.getXRot() != 0 && entity.getXRot() != 90 && entity.getXRot() != 180 && entity.getXRot() != 270) {
                        double Abstand0 = Math.abs(entity.getXRot());
                        double Abstand90 = Math.abs(entity.getXRot() - 90);
                        double Abstand180 = Math.abs(entity.getXRot() - 180);
                        double Abstand270 = Math.abs(entity.getXRot() - 270);
                        if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270)
                            if (entity.getXRot() < 0)
                                entity.setXRot(entity.getXRot() + rotateBy);
                            else
                                entity.setXRot(entity.getXRot() - rotateBy);
                        if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270)
                            if (entity.getXRot() - 90 < 0)
                                entity.setXRot(entity.getXRot() + rotateBy);
                            else
                                entity.setXRot(entity.getXRot() - rotateBy);
                        if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270)
                            if (entity.getXRot() - 180 < 0)
                                entity.setXRot(entity.getXRot() + rotateBy);
                            else
                                entity.setXRot(entity.getXRot() - rotateBy);
                        if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0)
                            if (entity.getXRot() - 270 < 0)
                                entity.setXRot(entity.getXRot() + rotateBy);
                            else
                                entity.setXRot(entity.getXRot() - rotateBy);
                            
                    }
                }
            } else if (entity != null && !Double.isNaN(entity.getX()) && !Double.isNaN(entity.getY()) && !Double.isNaN(entity.getZ()) && entity.level != null) {
                if (entity.isOnGround()) {
                    if (!flag)
                        entity.setXRot(0);
                } else {
                    rotateBy *= 2;
                    Fluid fluid = CommonPhysic.getFluid(entity);
                    if (fluid != null)
                        rotateBy /= fluid.getAttributes().getDensity() / 1000 * 10;
                    
                    entity.setXRot(entity.getXRot() + rotateBy);
                }
            }
            
            if (flag)
                pose.translate(0, -0.2, -0.08);
            else if (entity.level.getBlockState(entity.blockPosition()).getBlock() == Blocks.SNOW || entity.level.getBlockState(entity.blockPosition().below())
                    .getBlock() == Blocks.SOUL_SAND)
                pose.translate(0, 0.0, -0.14);
            else
                pose.translate(0, 0, -0.04);
            
            double height = 0.2;
            if (flag)
                pose.translate(0, height, 0);
            pose.mulPose(Vector3f.YP.rotation(entity.getXRot()));
            if (flag)
                pose.translate(0, -height, 0);
        }
        
        if (!flag) {
            float f7 = -0.0F * (j - 1) * 0.5F;
            float f8 = -0.0F * (j - 1) * 0.5F;
            float f9 = -0.09375F * (j - 1) * 0.5F;
            pose.translate(f7, f8, f9);
        }
        
        for (int k = 0; k < j; ++k) {
            pose.pushPose();
            if (k > 0) {
                if (flag) {
                    float f11 = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    pose.translate(f11, f13, f10);
                }
            }
            
            itemRenderer.render(itemstack, ItemTransforms.TransformType.GROUND, false, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
            pose.popPose();
            if (!flag)
                pose.translate(0.0, 0.0, 0.09375F); // pose.translate(0.0, 0.0, 0.05375F);
                
        }
        
        pose.popPose();
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
    
    public static boolean onPlayerInteractClient(Level level, Player player, boolean rightClick) {
        HitResult result = getEntityItem(mc.player);
        if (result != null && result.getType() == HitResult.Type.ENTITY) {
            ItemEntity entity = (ItemEntity) ((EntityHitResult) result).getEntity();
            if (level.isClientSide && entity != null) {
                player.swing(InteractionHand.MAIN_HAND);
                ItemPhysic.NETWORK.sendToServer(new PickupPacket(entity.getUUID(), rightClick));
                return true;
            }
        }
        return false;
    }
    
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Level world = event.getWorld();
        if (event instanceof RightClickEmpty || event instanceof RightClickBlock || event instanceof EntityInteract) {
            if (world.isClientSide && ItemPhysic.CONFIG.pickup.customPickup) {
                if (!ItemPhysicClient.pickup.getKey().equals(InputConstants.UNKNOWN))
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
    
    public static HitResult getEntityItem(Player player) {
        double distance = CommonPhysic.getReachDistance(player);
        float partialTicks = mc.getFrameTime();
        Vec3 position = player.getEyePosition(partialTicks);
        Vec3 vec3d1 = player.getViewVector(partialTicks);
        Vec3 look = position.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        
        HitResult result = mc.level.clip(new ClipContext(position, look, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (result != null)
            distance = Math.min(distance, result.getLocation().distanceToSqr(position));
        
        AABB axisalignedbb = player.getBoundingBox().expandTowards(vec3d1.scale(distance)).inflate(1.0D, 1.0D, 1.0D);
        EntityHitResult entityraytraceresult = ProjectileUtil.getEntityHitResult(player, position, look, axisalignedbb, (p_215312_0_) -> {
            return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
        }, distance);
        if (entityraytraceresult != null) {
            Vec3 vec3d3 = entityraytraceresult.getLocation();
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
                
                HitResult result = getEntityItem(mc.player);
                if (result != null && result.getType() == HitResult.Type.ENTITY) {
                    if (ItemPhysicClient.pickup.isDown())
                        onPlayerInteractClient(mc.level, mc.player, false);
                    ItemEntity entity = (ItemEntity) ((EntityHitResult) result).getEntity();
                    if (entity != null && ItemPhysic.CONFIG.rendering.showPickupTooltip) {
                        int space = 15;
                        List<Component> list = new ArrayList<>();
                        try {
                            entity.getItem().getItem().appendHoverText(entity.getItem(), mc.player.level, list, TooltipFlag.Default.NORMAL);
                            list.add(entity.getItem().getDisplayName());
                        } catch (Exception e) {
                            list = new ArrayList();
                            list.add(new TextComponent("ERRORED"));
                        }
                        
                        int width = 0;
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            width = Math.max(width, mc.font.width(text) + 10);
                        }
                        
                        RenderSystem.disableBlend();
                        //RenderSystem.enableAlphaTest();
                        RenderSystem.enableTexture();
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            mc.font.drawShadow(new PoseStack(), text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow()
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
                    mc.player.displayClientMessage(new TranslatableComponent("item.throw", renderPower), true);
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
                            ServerboundPlayerActionPacket.Action cplayerdiggingpacket$action = dropAll ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
                            mc.player.connection.send(new ServerboundPlayerActionPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
                            if (mc.player.getInventory().removeItem(mc.player.getInventory().selected, dropAll && !mc.player.getInventory().getSelected().isEmpty() ? mc.player
                                    .getInventory().getSelected().getCount() : 1) != ItemStack.EMPTY)
                                mc.player.swing(InteractionHand.MAIN_HAND);
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
    
    private static Field stuckSpeedMultiplierField = null;
    
    public static Vec3 getStuckSpeedMultiplier(Entity entity) {
        if (stuckSpeedMultiplierField == null)
            stuckSpeedMultiplierField = ObfuscationReflectionHelper.findField(Entity.class, "f_19865_");
        try {
            return (Vec3) stuckSpeedMultiplierField.get(entity);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }
    
}
