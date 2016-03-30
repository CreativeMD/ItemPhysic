package com.creativemd.itemphysic.physics;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.creativemd.itemphysic.ItemDummyContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
		return TextureMap.locationBlocksTexture;
	}
	
	@SideOnly(Side.CLIENT)
    public static void setPositionAndRotation2(EntityItem item, double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
		item.setPosition(x, y, z);		
		
		//item.setRotation(yaw, pitch);
    }

	@SideOnly(Side.CLIENT)
	public static void doRender(RenderEntityItem renderer, Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
		rotation = (double)(System.nanoTime()-tick)/2500000*ItemDummyContainer.rotateSpeed;
		if(!mc.inGameHasFocus)
			rotation = 0;
		EntityItem item = ((EntityItem)entity);
		
		ItemStack itemstack = item.getEntityItem();
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
        IBakedModel ibakedmodel = mc.getRenderItem().getItemModelWithOverrides(itemstack, entity.worldObj, (EntityLivingBase)null);
        /*int j = 1;
        try {
			j = (int) ReflectionHelper.findMethod(RenderEntityItem.class, renderer, new String[]{"transformModelCount", "func_177077_a"}, EntityItem.class, double.class, double.class, double.class, float.class, IBakedModel.class).invoke(renderer, entity, x, y, z, partialTicks, ibakedmodel);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
        boolean flag1 = ibakedmodel.isGui3d();
        boolean is3D = ibakedmodel.isGui3d();
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
    		GlStateManager.translate(0, 0, -0.04);
    	
    	
    	//Handle Rotations
        if (is3D || mc.getRenderManager().options != null)
        {
            //float f3 = (((float)item.getAge() + partialTicks) / 20.0F + item.hoverStart) * (180F / (float)Math.PI);
            //GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
        	
        	if(is3D)
        	{
        		if(!item.onGround)
        		{
        			double rotation = ClientPhysic.rotation*2;
            		Fluid fluid = ServerPhysic.getFluid(item);
            		if(fluid == null)
            			fluid = ServerPhysic.getFluid(item, true);
            		if(fluid != null)
            		{
            			rotation /= fluid.getDensity()/1000*10;
            		}
                		
            		item.rotationPitch += rotation;
        		}
        	}else{
        		
	        	if(item != null && !Double.isNaN(item.posX) && !Double.isNaN(item.posY) && !Double.isNaN(item.posZ) && item.worldObj != null)
	            {
		            if(item.onGround)item.rotationPitch = 0;
		            else {
		            	double rotation = ClientPhysic.rotation*2;
	            		Fluid fluid = ServerPhysic.getFluid(item);
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
        
        boolean renderOutlines = ReflectionHelper.getPrivateValue(Render.class, renderer, "renderOutlines", "field_188301_f"/*, "field_178639_r"*/);
        

        if (!flag1)
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
				GlStateManager.enableOutlineMode((Integer) ReflectionHelper.findMethod(RenderEntityItem.class, renderer, new String[]{"getTeamColor", "func_188298_c"}, Entity.class).invoke(entity));
			} catch (Exception e){
				e.printStackTrace();
			}
        }
        //float f9 = 0.0625F;
        //float f10 = 0.021875F;
        for (int k = 0; k < j; ++k)
        {
            if (flag1)
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
    }
	
	public static int getModelCount(ItemStack stack)
    {
        int i = 1;

        if (stack.stackSize > 48)
        {
            i = 5;
        }
        else if (stack.stackSize > 32)
        {
            i = 4;
        }
        else if (stack.stackSize > 16)
        {
            i = 3;
        }
        else if (stack.stackSize > 1)
        {
            i = 2;
        }

        return i;
    }
}
