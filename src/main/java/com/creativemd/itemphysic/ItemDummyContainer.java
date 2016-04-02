package com.creativemd.itemphysic;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.creativemd.itemphysic.physics.ServerPhysic;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDummyContainer extends DummyModContainer {
	
	public static final String modid = "itemphysic";
	public static final String version = "1.3.0";
	
	public ItemDummyContainer() {

		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = modid;
		meta.name = "ItemPhysic";
		meta.version = version; //String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
		meta.credits = "CreativeMD";
		meta.authorList = Arrays.asList("CreativeMD");
		meta.description = "";
		meta.url = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
	
	public static final Logger logger = LogManager.getLogger(modid);
	
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
			initFull();
		}else{
			MinecraftForge.EVENT_BUS.register(new EventHandlerLite());
		}
	}
	
	@Method(modid = "creativecore")
	public static void initFull()
	{
		CreativeCorePacket.registerPacket(DropPacket.class, "IPDrop");
		
		CreativeCorePacket.registerPacket(PickupPacket.class, "IPPick");
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
			oldRotation = config.get("Item", "oldRotation", false).getBoolean(false);
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
	public static boolean oldRotation;
	
}
