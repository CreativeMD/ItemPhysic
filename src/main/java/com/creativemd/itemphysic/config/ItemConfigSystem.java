package com.creativemd.itemphysic.config;

import com.creativemd.igcm.api.ConfigTab;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemConfigSystem {
	
	public static ItemPhysicBranch branch;
	
	public static void loadConfig()
	{
		branch = new ItemPhysicBranch("ItemPhysic");
		ConfigTab.root.registerElement("itemphysic", branch);
	}
}
