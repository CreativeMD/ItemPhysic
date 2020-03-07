package com.creativemd.itemphysic;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.ItemPhysicConfig.Rendering;
import com.creativemd.itemphysic.packet.DropPacket;
import com.creativemd.itemphysic.packet.PickupPacket;
import com.creativemd.itemphysic.physics.ClientPhysic;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDummyContainer extends DummyModContainer {
	
	public static final String modid = "itemphysic";
	public static final String version = "1.4.0";
	
	public static ItemPhysicConfig CONFIG;
	public static ItemPhysicConfig.Rendering CONFIG_RENDERING;
	
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
	
	@Override
	public String getGuiClassName() {
		return "com.creativemd.itemphysic.ItemPhysicSettings";
	}
	
	public static final Logger logger = LogManager.getLogger(modid);
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void modConstruction(FMLConstructionEvent evt) {
		
	}
	
	@Subscribe
	public void init(FMLInitializationEvent evt) {
		if (!ItemTransformer.isLite) {
			MinecraftForge.EVENT_BUS.register(new EventHandler());
			CreativeConfigRegistry.ROOT.registerValue(modid, CONFIG = new ItemPhysicConfig());
			CONFIG_RENDERING = CONFIG.rendering;
			initFull(evt);
		} else {
			MinecraftForge.EVENT_BUS.register(new EventHandlerLite());
			CreativeConfigRegistry.ROOT.registerValue(modid, CONFIG_RENDERING = new Rendering(), ConfigSynchronization.CLIENT, false);
		}
	}
	
	@Method(modid = "creativecore")
	public static void initFull(FMLInitializationEvent evt) {
		
		CreativeCorePacket.registerPacket(DropPacket.class);
		CreativeCorePacket.registerPacket(PickupPacket.class);
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			ItemPhysicClient.init(evt);
	}
	
	@Subscribe
	@SideOnly(Side.CLIENT)
	public void onRender(RenderTickEvent evt) {
		ClientPhysic.tick = System.nanoTime();
	}
	
	@Subscribe
	public void postInit(FMLPostInitializationEvent evt) {
		
	}
	
}
