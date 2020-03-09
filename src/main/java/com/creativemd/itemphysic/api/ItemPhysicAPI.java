package com.creativemd.itemphysic.api;

import com.creativemd.itemphysic.ItemDummyContainer;

public class ItemPhysicAPI {
	
	public static void addSortingObjects(String list, Object... objects) {
		if (list.equalsIgnoreCase("swimmingItems"))
			ItemDummyContainer.CONFIG.general.swimmingItems.addSortingObjects(objects);
		else if (list.equalsIgnoreCase("burningItems"))
			ItemDummyContainer.CONFIG.general.burningItems.addSortingObjects(objects);
		else if (list.equalsIgnoreCase("undestroyableItems"))
			ItemDummyContainer.CONFIG.general.undestroyableItems.addSortingObjects(objects);
		else if (list.equalsIgnoreCase("ignitingItems"))
			ItemDummyContainer.CONFIG.general.ignitingItems.addSortingObjects(objects);
	}
	
}
