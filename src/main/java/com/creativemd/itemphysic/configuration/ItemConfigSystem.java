package com.creativemd.itemphysic.configuration;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.creativemd.craftingmanager.api.common.utils.entry.BooleanEntry;
import com.creativemd.craftingmanager.api.common.utils.entry.IntegerEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.BooleanPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.api.core.ConfigTab;
import com.creativemd.itemphysic.ItemDummyContainer;

public class ItemConfigSystem extends ConfigSystem{
	
	public ItemConfigSystem() {
		super("ItemPhysic", itemTab);
	}

	public static ConfigTab itemTab = new ConfigTab("ItemPhysic", new ItemStack(Items.feather));
	
	public static void startConfig()
	{
		ConfigRegistry.registerConfig(new ItemConfigSystem());
	}

	@Override
	public void loadSystem() {
		
	}

	@Override
	public void loadConfig(Configuration config) {
		ItemDummyContainer.config.load();
		ItemDummyContainer.despawnItem = ItemDummyContainer.config.get("Item", "despawn", 6000).getInt(6000);
		ItemDummyContainer.customPickup = ItemDummyContainer.config.get("Item", "customPickup", false).getBoolean(false);
		ItemDummyContainer.customThrow = ItemDummyContainer.config.get("Item", "customThrow", true).getBoolean(true);
		ItemDummyContainer.config.save();
	}

	@Override
	public void saveConfig(Configuration config) {
		ItemDummyContainer.config.load();
		ItemDummyContainer.config.get("Item", "despawn", 6000).set(ItemDummyContainer.despawnItem);
		ItemDummyContainer.config.get("Item", "customPickup", false).set(ItemDummyContainer.customPickup);
		ItemDummyContainer.config.get("Item", "customThrow", false).set(ItemDummyContainer.customThrow);
		ItemDummyContainer.config.save();
	}

	@Override
	public ArrayList<ConfigEntry> getEntries() {
		ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		entries.add(new IntegerEntry("Item Despawn", ItemDummyContainer.despawnItem));
		entries.add(new BooleanEntry("Custom Pickup", ItemDummyContainer.customPickup));
		entries.add(new BooleanEntry("Custom Throw", ItemDummyContainer.customThrow));
		return entries;
	}

	@Override
	public void onEntryChange(ConfigEntry entry) {
		if(entry instanceof IntegerEntry)
			if(((IntegerEntry) entry).Title.equals("Item Despawn"))
				ItemDummyContainer.despawnItem = ((IntegerEntry) entry).value;
		if(entry instanceof BooleanEntry)
		{
			if(((BooleanEntry) entry).Title.equals("Custom Pickup"))
				ItemDummyContainer.customPickup = ((BooleanEntry) entry).value;
			else
				ItemDummyContainer.customThrow = ((BooleanEntry) entry).value;
		}
	}

	@Override
	public ArrayList<PacketEntry> getPacketInformation() {
		ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
		entries.add(new IntegerPacketEntry(ItemDummyContainer.despawnItem));
		entries.add(new BooleanPacketEntry(ItemDummyContainer.customPickup));
		entries.add(new BooleanPacketEntry(ItemDummyContainer.customThrow));
		return entries;
	}
	
	public void UpdateInformation(ArrayList<PacketEntry> Packet)
	{
		ItemDummyContainer.despawnItem = ((IntegerPacketEntry)Packet.get(0)).value;
		ItemDummyContainer.customPickup = ((BooleanPacketEntry)Packet.get(1)).value;
		ItemDummyContainer.customThrow = ((BooleanPacketEntry)Packet.get(2)).value;
	}
	
	public String toString(boolean input)
	{
		if(input)
			return "true";
		return "false";
	}
	
	@Override
	public String getRecieveInformation() {
		return "Despawn Item:" + ItemDummyContainer.despawnItem + ";Custom Pickup:" + toString(ItemDummyContainer.customPickup) + ";Custom Throw:" + toString(ItemDummyContainer.customThrow);
	}

	@Override
	public boolean needClientUpdate() {
		return true;
	}
	
}
