package com.creativemd.itemphysic.physics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.creativemd.itemphysic.ItemDummyContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientPhysic {

	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static long tick;
	
	public static double rotation;
	
	public static Random random = new Random();
	
	public static ResourceLocation getEntityTexture()
	{
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	private static Field renderOutlines = ReflectionHelper.findField(Render.class, "renderOutlines", "field_188301_f");
	
	private static Method getTeamColor = ReflectionHelper.findMethod(Render.class, "getTeamColor", "func_188298_c", Entity.class);
	
	private static Field skipPhysicRenderer = ReflectionHelper.findField(EntityItem.class, "skipPhysicRenderer");
	
	@SideOnly(Side.CLIENT)
	public static void setPositionAndRotationDirect(EntityItem item, double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
		item.setPosition(x, y, z);
    }

	@SideOnly(Side.CLIENT)
	public static boolean doRender(RenderEntityItem renderer, Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		EntityItem item = (EntityItem) entity;
		ItemStack itemstack = item.getItem();
		
		try {
			if(item.getAge() == 0 || ItemDummyContainer.vanillaRendering || skipPhysicRenderer.getBoolean(item))
				return false;
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			e2.printStackTrace();
		}
		
		rotation = (double)(System.nanoTime()-tick)/2500000*ItemDummyContainer.rotateSpeed;
		if(!mc.inGameHasFocus)
			rotation = 0;
		
		IBakedModel ibakedmodel = mc.getRenderItem().getItemModelWithOverrides(itemstack, entity.world, (EntityLivingBase)null);
		
        boolean is3D = ibakedmodel.isGui3d();
		boolean applyEffects = item.getAge() > 0 && (is3D || mc.getRenderManager().options != null);
		
        int i;

        if (itemstack != null && itemstack.getItem() != null)
        {
            i = Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
        }
        else
        {
            i = 187;
        }

        random.setSeed((long)i);
        boolean flag = true;

        renderer.bindTexture(getEntityTexture());
        renderer.getRenderManager().renderEngine.getTexture(getEntityTexture()).setBlurMipmap(false, false);

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        int j = getModelCount(itemstack);
        float f = 0.25F;
        float f1 = 0;//shouldBob() ? MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F : 0;
        float f2 = ibakedmodel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
        
        GlStateManager.translate((float)x, (float)y, (float)z);
        
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
    	GL11.glRotatef(item.rotationYaw, 0.0F, 0.0F, 1.0F);
    	
    	if(is3D)
    		GlStateManager.translate(0, -0.2, -0.08);
    	else
    		if(entity.world.getBlockState(entity.getPosition()).getBlock() == Blocks.SNOW_LAYER)
    			GlStateManager.translate(0, 0.0, -0.14);
    		else
    			GlStateManager.translate(0, 0, -0.04);
    	
    	
    	//Handle Rotations
        if (applyEffects)
        {
            //float f3 = (((float)item.getAge() + partialTicks) / 20.0F + item.hoverStart) * (180F / (float)Math.PI);
            //GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
        	
        	if(is3D)
        	{
        		if(!item.onGround)
        		{
        			double rotation = ClientPhysic.rotation*2;
            		Fluid fluid = CommonPhysic.getFluid(item);
            		if(fluid == null)
            			fluid = CommonPhysic.getFluid(item, true);
            		if(fluid != null)
            		{
            			rotation /= fluid.getDensity()/1000*10;
            		}
                		
            		item.rotationPitch += rotation;
        		}else if(ItemDummyContainer.oldRotation){
        			for (int side = 0; side < 4; side++) {
                        double rotation = side*90;
                        double range = 5;
                        if(item.rotationPitch > rotation-range && item.rotationPitch < rotation+range)
                            item.rotationPitch = (float)rotation;
                    }
                    if(item.rotationPitch != 0 && item.rotationPitch != 90 && item.rotationPitch != 180 && item.rotationPitch != 270)
                    {
                        double Abstand0 = Math.abs(item.rotationPitch);
                        double Abstand90 = Math.abs(item.rotationPitch-90);
                        double Abstand180 = Math.abs(item.rotationPitch-180);
                        double Abstand270 = Math.abs(item.rotationPitch-270);
                        if(Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270)
                            if(item.rotationPitch < 0)item.rotationPitch += rotation;
                            else item.rotationPitch -= rotation;
                        if(Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270)
                            if(item.rotationPitch-90 < 0)item.rotationPitch += rotation;
                            else item.rotationPitch -= rotation;
                        if(Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270)
                            if(item.rotationPitch-180 < 0)item.rotationPitch += rotation;
                            else item.rotationPitch -= rotation;
                        if(Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0)
                            if(item.rotationPitch-270 < 0)item.rotationPitch += rotation;
                            else item.rotationPitch -= rotation;
                       
                    }
        		}
        			
        	}else{
        		
	        	if(item != null && !Double.isNaN(item.posX) && !Double.isNaN(item.posY) && !Double.isNaN(item.posZ) && item.world != null)
	            {
		            if(item.onGround)item.rotationPitch = 0;
		            else {
		            	double rotation = ClientPhysic.rotation*2;
	            		Fluid fluid = CommonPhysic.getFluid(item);
	            		if(fluid != null)
	            		{
	            			rotation /= fluid.getDensity()/1000*10;
	            		}
	                		
	            		item.rotationPitch += rotation;
	            		
		            	//if(item.isInsideOfMaterial(Material.water) | item.worldObj.getBlock((int)item.posX, (int)item.posY-1, (int)item.posZ).getMaterial().equals(Material.water) | item.worldObj.getBlock((int)item.posX, (int)item.posY, (int)item.posZ).getMaterial().equals(Material.water))
		            		//item.rotationPitch += rotation/1600000*ItemDummyContainer.rotateSpeed;	
		            	//else item.rotationPitch += rotation/20000*ItemDummyContainer.rotateSpeed;
		            }
	            }
        	}
        	
        	double height = 0.2;
        	if(is3D)
        		GlStateManager.translate(0, height, 0);
        	GlStateManager.rotate(item.rotationPitch, 1, 0, 0.0F);
        	if(is3D)
        		GlStateManager.translate(0, -height, 0);
        }
        
      
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //int j = renderer.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
        
        boolean renderOutlines = false;
        
        try {
			renderOutlines = ClientPhysic.renderOutlines.getBoolean(renderer);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
        

        if (!is3D)
        {
            float f3 = -0.0F * (float)(j - 1) * 0.5F;
            float f4 = -0.0F * (float)(j - 1) * 0.5F;
            float f5 = -0.09375F * (float)(j - 1) * 0.5F;
            GlStateManager.translate(f3, f4, f5);
        }

        if (renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            try {
				GlStateManager.enableOutlineMode((Integer) getTeamColor.invoke(entity));
			} catch (Exception e){
				e.printStackTrace();
			}
        }
        //float f9 = 0.0625F;
        //float f10 = 0.021875F;
        for (int k = 0; k < j; ++k)
        {
            if (is3D)
            {
                GlStateManager.pushMatrix();

                if (k > 0)
                {
                	
                    float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(renderer.shouldSpreadItems() ? f7 : 0, renderer.shouldSpreadItems() ? f9 : 0, f6);
                	//GlStateManager.translate(0, 0,  0.0625F+0.021875F);
                }

                ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                mc.getRenderItem().renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();

                if (k > 0)
                {
                    //float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    //float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    //GlStateManager.translate(f8, f10, 0.0F);
                }

                ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                mc.getRenderItem().renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.05375F);
            }
        }

        if (renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        renderer.bindTexture(getEntityTexture());

        if (flag)
        {
        	renderer.getRenderManager().renderEngine.getTexture(getEntityTexture()).restoreLastBlurMipmap();
        }
        return true;
    }
	
	public static int getModelCount(ItemStack stack)
    {
        int i = 1;

        if (stack.getCount() > 48)
        {
            i = 5;
        }
        else if (stack.getCount() > 32)
        {
            i = 4;
        }
        else if (stack.getCount() > 16)
        {
            i = 3;
        }
        else if (stack.getCount() > 1)
        {
            i = 2;
        }

        return i;
    }
}
