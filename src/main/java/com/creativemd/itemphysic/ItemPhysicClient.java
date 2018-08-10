package com.creativemd.itemphysic;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemPhysicClient {
	
	public static KeyBinding pickup = new KeyBinding("key.pickup.item", Keyboard.KEY_NONE, "key.categories.gameplay");

	public static void init(FMLInitializationEvent event) {
		ClientRegistry.registerKeyBinding(pickup);
	}

}
