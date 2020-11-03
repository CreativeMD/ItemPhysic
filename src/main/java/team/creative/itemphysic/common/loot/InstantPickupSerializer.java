package team.creative.itemphysic.common.loot;

import com.google.gson.JsonObject;

import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;

public class InstantPickupSerializer extends GlobalLootModifierSerializer<InstantPickupLoot> {
	
	@Override
	public InstantPickupLoot read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
		return new InstantPickupLoot();
	}
	
	@Override
	public JsonObject write(InstantPickupLoot instance) {
		return new JsonObject();
	}
	
}
