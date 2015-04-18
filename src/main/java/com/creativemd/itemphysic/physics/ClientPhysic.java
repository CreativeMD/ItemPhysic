package com.creativemd.itemphysic.physics;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.itemphysic.ItemDummyContainer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientPhysic {

	@SideOnly(Side.CLIENT)
	public static RenderBlocks BlockRenderer = new RenderBlocks();
	public static RenderItem ItemRenderer = RenderItem.getInstance();
	public static Random random = new Random();
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static long tick;
	
	public static double rotation;
	
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	

	@SideOnly(Side.CLIENT)
	public static void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9)
    {
		rotation = (double)(System.nanoTime()-tick)/2500000*ItemDummyContainer.rotateSpeed;
		if(!mc.inGameHasFocus)
			rotation = 0;
		EntityItem item = ((EntityItem)par1Entity);
		ItemStack itemstack = item.getEntityItem();

        if (itemstack.getItem() != null)
        {
        	mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(item.getEntityItem().getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            random.setSeed(187L);
            GL11.glPushMatrix();
            byte b0 = 1;

            if (item.getEntityItem().stackSize > 1)
            {
                b0 = 2;
            }

            if (item.getEntityItem().stackSize > 5)
            {
                b0 = 3;
            }

            if (item.getEntityItem().stackSize > 20)
            {
                b0 = 4;
            }

            if (item.getEntityItem().stackSize > 40)
            {
                b0 = 5;
            }

            b0 = getMiniBlockCount(itemstack, b0);

            GL11.glTranslatef((float)x, (float)y, (float)z);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float f6;
            float f7;
            int k;

            if (ForgeHooksClient.renderEntityItem(item, itemstack, 0, 0, random, mc.renderEngine, BlockRenderer, b0))
            {
                ;
            }
            else // Code Style break here to prevent the patch from editing item line
            if (itemstack.getItemSpriteNumber() == 0 && itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
            {
                Block block = Block.getBlockFromItem(itemstack.getItem());

                if (RenderItem.renderInFrame)
                {
                    GL11.glScalef(1.25F, 1.25F, 1.25F);
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }else{
                	GL11.glRotatef(item.rotationYaw, 0.0F, 1.0F, 0.0F);
                	GL11.glRotatef(item.rotationPitch, 1.0F, 0.0F, 0.0F);
                }

                float f9 = 0.25F;
                k = block.getRenderType();

                if (k == 1 || k == 19 || k == 12 || k == 2)
                {
                    f9 = 0.5F;
                }

                if (block.getRenderBlockPass() > 0)
                {
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glScalef(f9, f9, f9);

                for (int l = 0; l < b0; ++l)
                {
                    GL11.glPushMatrix();

                    if (l > 0)
                    {
                        f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        GL11.glTranslatef(f6, f7, f8);
                    }
                    
                    if(item.rotationPitch > 360)
                    	item.rotationPitch = 0;
                    
                    if(item != null && !Double.isNaN(item.posX) && !Double.isNaN(item.posY) && !Double.isNaN(item.posZ) && item.worldObj != null)
                    {
                    	if(item.onGround)
                    	{
                    		/*for (int i = 0; i < 4; i++) {
								double rotation = i*90;
								double range = 5;
								if(item.rotationPitch > rotation-range && item.rotationPitch < rotation+range)
									item.rotationPitch = (float)rotation;
							}
                    		if(item.rotationPitch != 0 && item.rotationPitch != 90 && item.rotationPitch != 180 && item.rotationPitch != 270)
                    		{
                    			double Abstand0 = formPositiv(item.rotationPitch);
                    			double Abstand90 = formPositiv(item.rotationPitch-90);
                    			double Abstand180 = formPositiv(item.rotationPitch-180);
                    			double Abstand270 = formPositiv(item.rotationPitch-270);
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
                    			
                    		}*/
                    		
                    	}else{
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
                    }                    
                    BlockRenderer.renderBlockAsItem(block, itemstack.getItemDamage(), 1.0F);
                    GL11.glPopMatrix();
                }

                if (block.getRenderBlockPass() > 0)
                {
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
            else
            {
                float f5;

                if (/*itemstack.getItemSpriteNumber() == 1 &&*/ itemstack.getItem().requiresMultipleRenderPasses())
                {
                    if (RenderItem.renderInFrame)
                    {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    }
                    else
                    {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    for (int j = 0; j < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++j)
                    {
                        random.setSeed(187L);
                        IIcon iicon1 = itemstack.getItem().getIcon(itemstack, j);

                        if (ItemRenderer.renderWithColor)
                        {
                            k = itemstack.getItem().getColorFromItemStack(itemstack, j);
                            f5 = (float)(k >> 16 & 255) / 255.0F;
                            f6 = (float)(k >> 8 & 255) / 255.0F;
                            f7 = (float)(k & 255) / 255.0F;
                            GL11.glColor4f(f5, f6, f7, 1.0F);
                            renderDroppedItem(item, iicon1, b0, par9, f5, f6, f7, j);
                        }
                        else
                        {
                            renderDroppedItem(item, iicon1, b0, par9, 1.0F, 1.0F, 1.0F,  j);
                        }
                    }
                }
                else
                {
                    if (itemstack != null && itemstack.getItem() instanceof ItemCloth)
                    {
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    if (RenderItem.renderInFrame)
                    {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    }
                    else
                    {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    IIcon iicon = itemstack.getIconIndex();

                    if (ItemRenderer.renderWithColor)
                    {
                        int i = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                        float f4 = (float)(i >> 16 & 255) / 255.0F;
                        f5 = (float)(i >> 8 & 255) / 255.0F;
                        f6 = (float)(i & 255) / 255.0F;
                        renderDroppedItem(item, iicon, b0, par9, f4, f5, f6);
                    }
                    else
                    {
                        renderDroppedItem(item, iicon, b0, par9, 1.0F, 1.0F, 1.0F);
                    }

                    if (itemstack != null && itemstack.getItem() instanceof ItemCloth)
                    {
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(item.getEntityItem().getItemSpriteNumber()));
            TextureUtil.func_147945_b();
        }
    }
	
	public static double formPositiv(float rotationPitch) {
		if(rotationPitch > 0)
			return rotationPitch;
		return -rotationPitch;
	}
	
	@SideOnly(Side.CLIENT)
	public static void renderDroppedItem(EntityItem item, IIcon par2Icon, int par3, float par4, float par5, float par6, float par7)
    {
        renderDroppedItem(item, par2Icon, par3, par4, par5, par6, par7, 0);
    }

	@SideOnly(Side.CLIENT)	
	public static void renderDroppedItem(EntityItem item, IIcon par2Icon, int par3, float par4, float par5, float par6, float par7, int pass)
    {
		Tessellator tessellator = Tessellator.instance;

        if (par2Icon == null)
        {
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourcelocation = texturemanager.getResourceLocation(item.getEntityItem().getItemSpriteNumber());
            par2Icon = ((TextureMap)texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        float f14 = ((IIcon)par2Icon).getMinU();
        float f15 = ((IIcon)par2Icon).getMaxU();
        float f4 = ((IIcon)par2Icon).getMinV();
        float f5 = ((IIcon)par2Icon).getMaxV();
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.25F;
        float f10;

        if (RenderManager.instance.options.fancyGraphics)
        {
            GL11.glPushMatrix();

            if (ItemRenderer.renderInFrame)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
            	GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            	GL11.glRotatef(item.rotationYaw, 0.0F, 0.0F, 1.0F);
            }
            
            if(item != null && !Double.isNaN(item.posX) && !Double.isNaN(item.posY) && !Double.isNaN(item.posZ) && item.worldObj != null && !RenderItem.renderInFrame)
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
            
            GL11.glRotatef(item.rotationPitch, 1.0F, 0.0F, 0.0F);

            float f9 = 0.0625F;
            f10 = 0.021875F;
            ItemStack itemstack = item.getEntityItem();
            int j = itemstack.stackSize;
            byte b0;

            if (j < 2)
            {
                b0 = 1;
            }
            else if (j < 16)
            {
                b0 = 2;
            }
            else if (j < 32)
            {
                b0 = 3;
            }
            else
            {
                b0 = 4;
            }

            b0 = getMiniItemCount(itemstack, b0);

            GL11.glTranslatef(-f7, -f8, -((f9 + f10) * (float)b0 / 2.0F));

            for (int k = 0; k < b0; ++k)
            {
                // Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
                if (k > 0 && ItemRenderer.shouldSpreadItems())
                {
                    //float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    //float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    //float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    GL11.glTranslatef(0, 0, f9 + f10);
                }
                else
                {
                    GL11.glTranslatef(0f, 0f, f9 + f10);
                }

                if (itemstack.getItemSpriteNumber() == 0)
                {
                    mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                }
                else
                {
                	mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(par5, par6, par7, 1.0F);
                mc.entityRenderer.itemRenderer.renderItemIn2D(tessellator, f15, f4, f14, f5, ((IIcon)par2Icon).getIconWidth(), ((IIcon)par2Icon).getIconHeight(), f9);

                if (itemstack.hasEffect(pass))
                {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    mc.renderEngine.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f11 = 0.76F;
                    GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f12 = 0.125F;
                    GL11.glScalef(f12, f12, f12);
                    float f13 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f13, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    mc.entityRenderer.itemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f12, f12, f12);
                    f13 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f13, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    mc.entityRenderer.itemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }
            }

            GL11.glPopMatrix();
        }
        else
        {
            for (int l = 0; l < par3; ++l)
            {
                GL11.glPushMatrix();

                if (l > 0)
                {
                    f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f16 = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f17 = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    GL11.glTranslatef(f10, f16, f17);
                }

                if (!ItemRenderer.renderInFrame)
                {
                    GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
                }

                GL11.glColor4f(par5, par6, par7, 1.0F);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV((double)(0.0F - f7), (double)(0.0F - f8), 0.0D, (double)f14, (double)f5);
                tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0F - f8), 0.0D, (double)f15, (double)f5);
                tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0F - f8), 0.0D, (double)f15, (double)f4);
                tessellator.addVertexWithUV((double)(0.0F - f7), (double)(1.0F - f8), 0.0D, (double)f14, (double)f4);
                tessellator.draw();
                GL11.glPopMatrix();
            }
        }
    }
	
	public static byte getMiniBlockCount(ItemStack stack, byte original)
    {
        return original;
    }

	public static byte getMiniItemCount(ItemStack stack, byte original)
    {
        return original;
    }
}
