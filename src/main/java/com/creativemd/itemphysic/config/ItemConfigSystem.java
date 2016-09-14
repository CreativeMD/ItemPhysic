package com.creativemd.itemphysic.config;

import com.creativemd.igcm.api.core.TabRegistry;
import com.creativemd.igcm.api.tab.ModTab;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemConfigSystem {
	
	public static ModTab tab = new ModTab("ItemPhysic", new ItemStack(Items.FEATHER));
	public static ItemPhysicBranch branch;
	
	public static void loadConfig()
	{
		branch = new ItemPhysicBranch("general");
		tab.addBranch(branch);
		TabRegistry.registerModTab(tab);
	}
}
