package com.creativemd.itemphysic.config;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.igcm.api.ConfigBranch;
import com.creativemd.igcm.api.segments.BooleanSegment;
import com.creativemd.igcm.api.segments.IntegerSegment;
import com.creativemd.itemphysic.ItemDummyContainer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPhysicBranch extends ConfigBranch{

	public ItemPhysicBranch(String name) {
		super(name, new ItemStack(Items.FEATHER));
	}

	@Override
	public void createChildren() {
		registerElement("despawn", new IntegerSegment("despawn time", 6000));
		registerElement("pickup", new BooleanSegment("custom pickup", false));
		registerElement("throw", new BooleanSegment("custom throw", true));
	}

	@Override
	public boolean requiresSynchronization() {
		return true;
	}

	@Override
	public void onRecieveFrom(Side side) {
		ItemDummyContainer.despawnItem = (Integer) getValue("despawn");
		ItemDummyContainer.customPickup = (Boolean) getValue("pickup");
		ItemDummyContainer.customThrow = (Boolean) getValue("throw");
	}

	@Override
	public void loadExtra(NBTTagCompound nbt) {
		
	}

	@Override
	public void saveExtra(NBTTagCompound nbt) {
		
	}

}
