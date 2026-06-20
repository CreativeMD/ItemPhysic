package team.creative.itemphysic.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.ICreativeLoader;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;
import team.creative.itemphysic.mixin.ItemStackRenderStateAccessor;
import team.creative.itemphysic.mixin.LayerRenderStateAccessor;

public class ItemPhysicClient {
    
    public static final KeyMapping PICKUP = new KeyMapping("key.pickup.item", InputConstants.UNKNOWN.getValue(), KeyMapping.Category.GAMEPLAY);
    public static int throwCharge;
    private static final double RANDOM_Y_OFFSET_SCALE = 0.05 / (Math.PI * 2);
    
    public static void init() {
        ICreativeLoader loader = CreativeCore.loader();
        loader.registerKeybind(() -> PICKUP);
        
        loader.registerClientTick(ItemPhysicClient::gameTick);
        loader.registerClientRenderGui(ItemPhysicClient::renderTick);
        CreativeCoreClient.registerClientConfig(ItemPhysic.MODID);
    }
    
    public static int getChargeStage() {
        return Math.min(1 + throwCharge / ItemPhysic.CONFIG.throwConfig.stageChargeTime, ItemPhysic.CONFIG.throwConfig.maxStages);
    }
    
    public static void gameTick() {
        var mc = Minecraft.getInstance();
        if (mc.player != null && ItemPhysic.CONFIG.throwConfig.enabled) {
            if (mc.options.keyDrop.isDown() && !mc.player.getMainHandItem().isEmpty())
                throwCharge++;
            else {
                if (throwCharge > 0 && !mc.player.getMainHandItem().isEmpty()) {
                    boolean dropAll = mc.hasControlDown();
                    
                    ItemPhysic.NETWORK.sendToServer(new DropPacket(mc.hasControlDown(), getChargeStage()));
                    if (mc.player.getInventory().removeItem(mc.player.getInventory().getSelectedSlot(), dropAll && !mc.player.getInventory().getSelectedItem().isEmpty() ? mc.player
                            .getInventory().getSelectedItem().getCount() : 1) != ItemStack.EMPTY)
                        mc.player.swing(InteractionHand.MAIN_HAND);
                }
                throwCharge = 0;
            }
        }
    }
    
    public static void renderTick(Object object) {
        if (Minecraft.getInstance().gui.screen() == null)
            renderTooltip((GuiGraphicsExtractor) object);
    }
    
    public static void renderTooltip(GuiGraphicsExtractor graphics) {
        var mc = Minecraft.getInstance();
        if (mc != null && mc.player != null && !mc.isPaused()) {
            if (ItemPhysic.CONFIG.pickup.canPickup(mc.player)) {
                
                HitResult result = getItemInFocus(mc.player);
                if (result != null && result.getType() == HitResult.Type.ENTITY) {
                    if (ItemPhysicClient.PICKUP.isDown())
                        onPlayerInteractClient(mc.level, mc.player, false);
                    ItemEntity entity = (ItemEntity) ((EntityHitResult) result).getEntity();
                    if (entity != null && ItemPhysic.CONFIG.rendering.showPickupTooltip && (!ItemPhysic.CONFIG.rendering.showPickupTooltipOnlyOnGround || entity.onGround())) {
                        int space = 2;
                        List<Component> list = new ArrayList<>();
                        
                        try {
                            if (ItemPhysic.CONFIG.rendering.showPickupTooltipExtended)
                                list.addAll(entity.getItem().getTooltipLines(TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL));
                            else
                                list.add(entity.getItem().getTooltipLines(TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL).get(0));
                            
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
                        
                        int height = list.size() * (mc.font.lineHeight + space) / 2;
                        //RenderSystem.disableBlend();
                        for (int i = 0; i < list.size(); i++) {
                            String text = list.get(i).getString();
                            graphics.text(mc.font, list.get(i), mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2 + ItemPhysic.CONFIG.rendering.tooltipOffsetX, mc
                                    .getWindow().getGuiScaledHeight() / 2 - height + (mc.font.lineHeight + space) * i + ItemPhysic.CONFIG.rendering.tooltipOffsetY,
                                ColorUtils.WHITE);
                        }
                        
                    }
                }
            }
            
            if (ItemPhysic.CONFIG.throwConfig.enabled && !ItemPhysic.CONFIG.rendering.disableThrowHUD && throwCharge > 0 && !mc.player.getMainHandItem().isEmpty())
                mc.player.sendOverlayMessage(Component.translatable("item.throw", getChargeStage()));
        }
    }
    
    public static boolean submit(ItemEntityRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera, RandomSource rand) {
        if (state.ageInTicks < 1 || ((ItemEntityRenderStateExtender) state).skipRendering() || ItemPhysic.CONFIG.rendering.vanillaRendering)
            return false;
        
        pose.pushPose();
        
        rand.setSeed(state.seed);
        int j = getModelCount(state.count);
        boolean gui3d = ((ItemEntityRenderStateExtender) state).isBlock();
        var transform = ((LayerRenderStateAccessor) ((ItemStackRenderStateAccessor) state.item).callFirstLayer()).getItemTransform();
        
        pose.mulPose(com.mojang.math.Axis.XP.rotation((float) Math.PI / 2));
        pose.mulPose(com.mojang.math.Axis.ZP.rotation(((ItemEntityRenderStateExtender) state).getYRot()));
        
        var mc = Minecraft.getInstance();
        
        if (state.ageInTicks != 0 && (gui3d || mc.options != null)) {
            if (gui3d)
                pose.translate(0, -0.2, -0.08);
            else if (((ItemEntityRenderStateExtender) state).hasAdditionalOffset())
                pose.translate(0, 0.0, -0.14 - state.bobOffset * RANDOM_Y_OFFSET_SCALE);
            else
                pose.translate(0, 0, -0.04 - state.bobOffset * RANDOM_Y_OFFSET_SCALE);
            
            double height = transform.scale().y();
            if (gui3d)
                pose.translate(0, height, 0);
            pose.mulPose(com.mojang.math.Axis.YP.rotation(((ItemEntityRenderStateExtender) state).getXRot()));
            if (gui3d)
                pose.translate(0, -height, 0);
        }
        
        if (!gui3d) {
            float f7 = -0.0F * (j - 1) * 0.5F;
            float f8 = -0.0F * (j - 1) * 0.5F;
            float f9 = -0.09375F * (j - 1) * 0.5F;
            pose.translate(f7, f8, f9);
        }
        
        float f = transform.scale().x();
        float f1 = transform.scale().y();
        float f2 = transform.scale().z();
        
        for (int k = 0; k < j; ++k) {
            pose.pushPose();
            if (k > 0) {
                if (gui3d) {
                    float f11 = (rand.nextFloat() * 2.0F - 1.0F) * f;
                    float f13 = (rand.nextFloat() * 2.0F - 1.0F) * f1;
                    float f10 = (rand.nextFloat() * 2.0F - 1.0F) * f2;
                    pose.translate(f11, f13, f10);
                }
            }
            
            state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            pose.popPose();
            if (!gui3d)
                pose.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
        }
        
        pose.popPose();
        return true;
    }
    
    public static int getModelCount(int count) {
        if (count > 48)
            return 5;
        if (count > 32)
            return 4;
        if (count > 16)
            return 3;
        if (count > 1)
            return 2;
        
        return 1;
    }
    
    public static boolean onPlayerInteractClient(Level level, Player player, boolean rightClick) {
        var mc = Minecraft.getInstance();
        HitResult result = getItemInFocus(mc.player);
        if (result != null && result.getType() == HitResult.Type.ENTITY) {
            ItemEntity entity = (ItemEntity) ((EntityHitResult) result).getEntity();
            if (level.isClientSide() && entity != null) {
                player.swing(InteractionHand.MAIN_HAND);
                ItemPhysic.NETWORK.sendToServer(new PickupPacket(entity.getUUID(), rightClick));
                return true;
            }
        }
        return false;
    }
    
    public static boolean onPlayerInteract(Player player) {
        if (ItemPhysic.CONFIG.pickup.canPickup(player)) {
            if (!ItemPhysicClient.PICKUP.isUnbound())
                return false;
            return onPlayerInteractClient(player.level(), player, true);
        }
        return false;
    }
    
    public static HitResult getItemInFocus(Player player) {
        var mc = Minecraft.getInstance();
        double distance = CommonPhysic.getReachDistance(player);
        float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 position = player.getEyePosition(partialTicks);
        Vec3 view = player.getViewVector(partialTicks);
        double d0 = player.blockInteractionRange();
        double d1 = player.entityInteractionRange();
        var hitResult = pick(player, d0, d1, partialTicks, position, view, position.add(view.x * distance, view.y * distance, view.z * distance));
        if (hitResult != null && hitResult.getType() != Type.MISS)
            distance = Math.min(hitResult.getLocation().distanceTo(position), distance);
        return CommonPhysic.getItemInFocus(player, position, position.add(view.x * distance, view.y * distance, view.z * distance));
        
    }
    
    private static HitResult pick(Entity entity, double blockInteraction, double entityItneraction, float partialTicks, Vec3 position, Vec3 view, Vec3 endPosition) {
        double d0 = Math.max(blockInteraction, entityItneraction);
        double d1 = Mth.square(d0);
        HitResult hitresult = entity.level().clip(new ClipContext(position, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        double d2 = hitresult.getLocation().distanceToSqr(position);
        if (hitresult.getType() != HitResult.Type.MISS) {
            d1 = d2;
            d0 = Math.sqrt(d2);
        }
        
        AABB aabb = entity.getBoundingBox().expandTowards(view.scale(d0)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, position, endPosition, aabb, x -> !x.isSpectator() && x.isPickable(), d1);
        return entityhitresult != null && entityhitresult.getLocation().distanceToSqr(position) < d2 ? filterHitResult(entityhitresult, position,
            entityItneraction) : filterHitResult(hitresult, position, blockInteraction);
    }
    
    private static HitResult filterHitResult(HitResult hit, Vec3 vec, double range) {
        Vec3 hitVec = hit.getLocation();
        if (!hitVec.closerThan(vec, range))
            return BlockHitResult.miss(hitVec, Direction.getApproximateNearest(hitVec.x - vec.x, hitVec.y - vec.y, hitVec.z - vec.z), BlockPos.containing(hitVec));
        return hit;
    }
}
