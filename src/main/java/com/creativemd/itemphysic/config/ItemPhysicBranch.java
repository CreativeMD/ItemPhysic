package com.creativemd.itemphysic.config;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.igcm.api.common.branch.ConfigBranch;
import com.creativemd.igcm.api.common.branch.ConfigSegmentCollection;
import com.creativemd.igcm.api.common.segment.BooleanSegment;
import com.creativemd.igcm.api.common.segment.IntegerSegment;
import com.creativemd.itemphysic.ItemDummyContainer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPhysicBranch extends ConfigBranch{

	public ItemPhysicBranch(String name) {
		super(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected Avatar getAvatar() {
		return new AvatarItemStack(new ItemStack(Items.FEATHER));
	}

	@Override
	public void loadCore() {
		
	}

	@Override
	public void createConfigSegments() {
		segments.add(new IntegerSegment("despawn", "despawn time", 6000));
		segments.add(new BooleanSegment("pickup", "custom pickup", false));
		segments.add(new BooleanSegment("throw", "custom throw", true));
	}

	@Override
	public boolean needPacket() {
		return true;
	}

	@Override
	public void onRecieveFrom(boolean isServer, ConfigSegmentCollection collection) {
		ItemDummyContainer.despawnItem = (Integer) collection.getSegmentValue("despawn");
		ItemDummyContainer.customPickup = (Boolean) collection.getSegmentValue("pickup");
		ItemDummyContainer.customThrow = (Boolean) collection.getSegmentValue("throw");
	}

}
