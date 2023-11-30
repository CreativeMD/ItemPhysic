package team.creative.itemphysic.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.ICreativeLoader;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;
import team.creative.itemphysic.mixin.EntityAccessor;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ItemPhysicClient {
    
    public static final KeyMapping PICKUP = new KeyMapping("key.pickup.item", InputConstants.UNKNOWN.getValue(), "key.categories.gameplay");
    public static final Minecraft mc = Minecraft.getInstance();;
    public static int throwingPower;
    public static long lastTickTime;
    
    public static void init() {
        ICreativeLoader loader = CreativeCore.loader();
        loader.registerKeybind(() -> PICKUP);
        
        loader.registerClientTick(ItemPhysicClient::gameTick);
        loader.registerClientRenderGui(ItemPhysicClient::renderTick);
        CreativeCoreClient.registerClientConfig(ItemPhysic.MODID);
    }
    
    public static void gameTick() {
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
    
    public static void renderTick(Object object) {
        lastTickTime = System.nanoTime();
        
        if (mc.screen == null)
            renderTooltip((GuiGraphics) object);
    }
    
    public static void renderTooltip(GuiGraphics graphics) {
        if (mc != null && mc.player != null && !mc.isPaused()) {
            if (ItemPhysic.CONFIG.pickup.customPickup) {
                
                HitResult result = getEntityItem(mc.player);
                if (result != null && result.getType() == HitResult.Type.ENTITY) {
                    if (ItemPhysicClient.PICKUP.isDown())
                        onPlayerInteractClient(mc.level, mc.player, false);
                    ItemEntity entity = (ItemEntity) ((EntityHitResult) result).getEntity();
                    if (entity != null && ItemPhysic.CONFIG.rendering.showPickupTooltip) {
                        int space = 15;
                        List<Component> list = new ArrayList<>();
                        
                        try {
                            if (ItemPhysic.CONFIG.rendering.showPickupTooltipExtended)
                                entity.getItem().getItem().appendHoverText(entity.getItem(), mc.player.level(), list, TooltipFlag.Default.NORMAL);
                            list.add(entity.getItem().getHoverName());
                        } catch (Exception e) {
                            list = new ArrayList();
                            list.add(Component.literal("ERRORED"));
                        }
                        
                        if (ItemPhysic.CONFIG.rendering.showPickupTooltipKeybind)
                            list.add(Component.translatable("item.tooltip.pickup.keybind", ItemPhysicClient.PICKUP.isUnbound() ? mc.options.keyUse
                                    .getTranslatedKeyMessage() : ItemPhysicClient.PICKUP.getTranslatedKeyMessage()));
                        
                        int width = 0;
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            width = Math.max(width, mc.font.width(text) + 10);
                        }
                        
                        RenderSystem.disableBlend();
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            graphics.drawString(mc.font, text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow().getGuiScaledHeight() / 2 + ((list
                                    .size() / 2) * space - space * (i + 1)), 16579836);
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
                    mc.player.displayClientMessage(Component.translatable("item.throw", renderPower), true);
                }
            }
        }
    }
    
    public static boolean render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, ItemRenderer itemRenderer, RandomSource rand) {
        if (entity.getAge() == 0 || ((ItemEntityRendering) entity).skipRendering() || ItemPhysic.CONFIG.rendering.vanillaRendering)
            return false;
        
        pose.pushPose();
        ItemStack itemstack = entity.getItem();
        rand.setSeed(itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue());
        BakedModel bakedmodel = itemRenderer.getModel(itemstack, entity.level(), (LivingEntity) null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        int j = getModelCount(itemstack);
        
        float rotateBy = (System.nanoTime() - lastTickTime) / 200000000F * ItemPhysic.CONFIG.rendering.rotateSpeed;
        if (mc.isPaused())
            rotateBy = 0;
        
        Vec3 motionMultiplier = ((EntityAccessor) entity).getStuckSpeedMultiplier();
        if (motionMultiplier != null && motionMultiplier.lengthSqr() > 0)
            rotateBy *= motionMultiplier.x * 0.2;
        
        pose.mulPose(com.mojang.math.Axis.XP.rotation((float) Math.PI / 2));
        pose.mulPose(com.mojang.math.Axis.ZP.rotation(entity.getYRot()));
        
        boolean applyEffects = entity.getAge() != 0 && (flag || mc.options != null);
        
        //Handle Rotations
        if (applyEffects) {
            if (flag) {
                if (!entity.onGround()) {
                    rotateBy *= 2;
                    Fluid fluid = CommonPhysic.getFluid(entity);
                    if (fluid == null)
                        fluid = CommonPhysic.getFluid(entity, true);
                    if (fluid != null)
                        rotateBy /= (1 + CommonPhysic.getViscosity(fluid, entity.level()));
                    
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
            } else if (entity != null && !Double.isNaN(entity.getX()) && !Double.isNaN(entity.getY()) && !Double.isNaN(entity.getZ()) && entity.level() != null) {
                if (entity.onGround()) {
                    if (!flag)
                        entity.setXRot(0);
                } else {
                    rotateBy *= 2;
                    Fluid fluid = CommonPhysic.getFluid(entity);
                    if (fluid != null)
                        rotateBy /= (1 + CommonPhysic.getViscosity(fluid, entity.level()));
                    
                    entity.setXRot(entity.getXRot() + rotateBy);
                }
            }
            
            if (flag)
                pose.translate(0, -0.2, -0.08);
            else if (entity.level().getBlockState(entity.blockPosition()).getBlock() == Blocks.SNOW || entity.level().getBlockState(entity.blockPosition().below())
                    .getBlock() == Blocks.SOUL_SAND)
                pose.translate(0, 0.0, -0.14);
            else
                pose.translate(0, 0, -0.04);
            
            double height = 0.2;
            if (flag)
                pose.translate(0, height, 0);
            pose.mulPose(com.mojang.math.Axis.YP.rotation(entity.getXRot()));
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
            
            itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
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
    
    public static boolean onPlayerInteract(Player player) {
        if (ItemPhysic.CONFIG.pickup.customPickup) {
            if (!ItemPhysicClient.PICKUP.isUnbound())
                return false;
            
            return onPlayerInteractClient(player.level(), player, true);
        }
        return false;
    }
    
    public static HitResult getEntityItem(Player player) {
        double distance = CommonPhysic.getReachDistance(player);
        float partialTicks = mc.getDeltaFrameTime();
        Vec3 position = player.getEyePosition(partialTicks);
        Vec3 view = player.getViewVector(partialTicks);
        if (mc.hitResult != null && mc.hitResult.getType() != Type.MISS)
            distance = Math.min(mc.hitResult.getLocation().distanceTo(position), distance);
        return CommonPhysic.getEntityItem(player, position, position.add(view.x * distance, view.y * distance, view.z * distance));
        
    }
    
}
