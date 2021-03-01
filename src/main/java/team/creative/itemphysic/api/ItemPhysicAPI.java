package team.creative.itemphysic.api;

import team.creative.itemphysic.ItemPhysic;

public class ItemPhysicAPI {
    
    public static void addSortingObjects(String list, Object... objects) {
        if (list.equalsIgnoreCase("swimmingItems"))
            ItemPhysic.CONFIG.general.swimmingItems.addSortingObjects(objects);
        else if (list.equalsIgnoreCase("burningItems"))
            ItemPhysic.CONFIG.general.burningItems.addSortingObjects(objects);
        else if (list.equalsIgnoreCase("undestroyableItems"))
            ItemPhysic.CONFIG.general.undestroyableItems.addSortingObjects(objects);
        else if (list.equalsIgnoreCase("ignitingItems"))
            ItemPhysic.CONFIG.general.ignitingItems.addSortingObjects(objects);
    }
    
}
