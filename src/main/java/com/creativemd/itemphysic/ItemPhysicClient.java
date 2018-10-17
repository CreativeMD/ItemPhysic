package com.creativemd.itemphysic;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
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
