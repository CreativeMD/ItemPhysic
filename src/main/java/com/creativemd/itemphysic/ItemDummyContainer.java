package com.creativemd.itemphysic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.config.ItemConfigSystem;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.creativemd.itemphysic.physics.ServerPhysic;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDummyContainer extends DummyModContainer {

	public ItemDummyContainer() {

		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "itemphysic";
		meta.name = "ItemPhysic";
		meta.version = "1.1.5"; //String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
		meta.credits = "CreativeMD";
		meta.authorList = Arrays.asList("CreativeMD");
		meta.description = "";
		meta.url = "";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	@Subscribe
	public void modConstruction(FMLConstructionEvent evt){

	}
	
	@Subscribe
	public void init(FMLInitializationEvent evt) {
		
		if(!ItemTransformer.isLite)
		{
			MinecraftForge.EVENT_BUS.register(new EventHandler());
			FMLCommonHandler.instance().bus().register(new EventHandler());
			initFull();
		}else{
			MinecraftForge.EVENT_BUS.register(new EventHandlerLite());
			FMLCommonHandler.instance().bus().register(new EventHandlerLite());
		}
	}
	
	@Method(modid = "creativecore")
	public static void initFull()
	{
		CreativeCorePacket.registerPacket(DropPacket.class, "IPDrop");
		
		CreativeCorePacket.registerPacket(PickupPacket.class, "IPPick");
		
		try{
			if(!ItemTransformer.isLite && Loader.isModLoaded("ingameconfigmanager"))
			{
				ItemConfigSystem.loadConfig();
			}
		}catch(Exception e){}
	}
	
	public static Configuration config;
	
	public static float rotateSpeed = 1.0F;
	
	@Subscribe
	public void preInit(FMLPreInitializationEvent evt) {
		config = new Configuration(evt.getSuggestedConfigurationFile());
		config.load();
		if(!ItemTransformer.isLite)
		{
			despawnItem = config.get("Item", "despawn", 6000).getInt(6000);
			customPickup = config.get("Item", "customPickup", false).getBoolean(false);
			customThrow = config.get("Item", "customThrow", true).getBoolean(true);
		}
		rotateSpeed = config.getFloat("rotateSpeed", "Item", 1.0F, 0, 100, "");
		config.save();
		ServerPhysic.loadItemList();
	}
	
	@Subscribe
	@SideOnly(Side.CLIENT)
	public void onRender(RenderTickEvent evt)
	{
		ClientPhysic.tick = System.nanoTime();
	}
	
	@Subscribe
	public void postInit(FMLPostInitializationEvent evt) {

	}
	
	public static int despawnItem;
	public static boolean customPickup;
	public static boolean customThrow;
	
}
