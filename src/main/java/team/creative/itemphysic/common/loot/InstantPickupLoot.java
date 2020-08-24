package team.creative.itemphysic.common.loot;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import team.creative.itemphysic.ItemPhysic;
import team.creative.itemphysic.common.CommonPhysic;

public class InstantPickupLoot implements IGlobalLootModifier {
	@Override
	public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context) {
		if (ItemPhysic.CONFIG.pickup.pickupMinedImmediately && context.has(LootParameters.BLOCK_STATE) && context.has(LootParameters.THIS_ENTITY) && context.get(LootParameters.THIS_ENTITY) instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) context.get(LootParameters.THIS_ENTITY);
			World world = context.getWorld();
			
			if (ItemPhysic.CONFIG.pickup.respectRangeWhenMined && context.get(LootParameters.field_237457_g_).squareDistanceTo(player.getPositionVec()) > Math.pow(CommonPhysic.getReachDistance(player), 2))
				return generatedLoot;
			
			boolean pickedUp = false;
			for (Iterator<ItemStack> iterator = generatedLoot.iterator(); iterator.hasNext();) {
				ItemStack stack = iterator.next();
				if (player.addItemStackToInventory(stack)) {
					iterator.remove();
					pickedUp = true;
				}
			}
			
			if (pickedUp)
				world.playSound(player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 1.4F + 2.0F, false);
		}
		return generatedLoot;
	}
}
