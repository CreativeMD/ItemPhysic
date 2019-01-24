package com.creativemd.itemphysic.config;

import com.creativemd.igcm.api.ConfigBranch;
import com.creativemd.igcm.api.segments.BooleanSegment;
import com.creativemd.igcm.api.segments.FloatSegment;
import com.creativemd.igcm.api.segments.IntegerSegment;
import com.creativemd.igcm.api.sorting.ConfigBranchSorting;
import com.creativemd.itemphysic.ItemDummyContainer;
import com.creativemd.itemphysic.physics.ServerPhysic;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class ItemPhysicBranch extends ConfigBranch {
	
	public ItemPhysicBranch(String name) {
		super(name, new ItemStack(Items.FEATHER));
	}
	
	@Override
	public void createChildren() {
		registerElement("despawn", new IntegerSegment("despawn time", 6000));
		registerElement("pickup", new BooleanSegment("custom pickup", false));
		registerElement("pickupWhenSneaking", new BooleanSegment("pickup when sneaking", true));
		registerElement("throw", new BooleanSegment("custom throw", true));
		
		registerElement("igniting", new BooleanSegment("enable igniting", true));
		
		registerElement("maximumPickupRange", new FloatSegment("maximum pickup range", 5F, 0.1F, 100F));
		registerElement("pickupMinedImmediately", new BooleanSegment("pickup mined immediately", false));
		registerElement("respectRangeWhenMined", new BooleanSegment("respect range when mined", false));
		
		registerElement("swiming", new ConfigBranchSorting("Swimming Items", new ItemStack(Items.BOAT), ServerPhysic.swimmingItems));
		registerElement("burning", new ConfigBranchSorting("Burning Items", new ItemStack(Items.FIRE_CHARGE), ServerPhysic.burningItems));
		registerElement("undestroyable", new ConfigBranchSorting("Undestroyable Items", new ItemStack(Blocks.BEDROCK), ServerPhysic.undestroyableItems));
		registerElement("igniting", new ConfigBranchSorting("Igniting Items", new ItemStack(Blocks.TORCH), ServerPhysic.ignitingItems));
	}
	
	@Override
	public boolean requiresSynchronization() {
		return true;
	}
	
	@Override
	public void onRecieveFrom(Side side) {
		ItemDummyContainer.despawnItem = (Integer) getValue("despawn");
		ItemDummyContainer.customPickup = (Boolean) getValue("pickup");
		ItemDummyContainer.pickupWhenSneaking = (Boolean) getValue("pickupWhenSneaking");
		ItemDummyContainer.customThrow = (Boolean) getValue("throw");
		ItemDummyContainer.enableIgniting = (Boolean) getValue("igniting");
		
		ItemDummyContainer.maximumPickupRange = (Float) getValue("maximumPickupRange");
		ItemDummyContainer.pickupMinedImmediately = (Boolean) getValue("pickupMinedImmediately");
		ItemDummyContainer.respectRangeWhenMined = (Boolean) getValue("respectRangeWhenMined");
	}
	
	@Override
	public void loadExtra(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void saveExtra(NBTTagCompound nbt) {
		
	}
	
}
