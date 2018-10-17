package com.creativemd.itemphysic.config;

import com.creativemd.igcm.api.ConfigTab;

public class ItemConfigSystem {
	
	public static ItemPhysicBranch branch;
	
	public static void loadConfig() {
		branch = new ItemPhysicBranch("ItemPhysic");
		ConfigTab.root.registerElement("itemphysic", branch);
	}
}
