package team.creative.itemphysic.common.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.itemphysic.ItemPhysic;

public class DropPacket extends CreativePacket {
    
    public boolean all;
    public int power;
    
    public DropPacket() {}
    
    public DropPacket(boolean all, int power) {
        this.all = all;
        this.power = power;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void executeClient(Player player) {}
    
    public ItemEntity createEntity(ItemStack itemStack, Player player, boolean bl, boolean bl2) {
        if (itemStack.isEmpty()) {
            return null;
        }
        if (player.level().isClientSide) {
            player.swing(InteractionHand.MAIN_HAND);
        }
        double d = player.getEyeY() - 0.3f;
        ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), d, player.getZ(), itemStack);
        itemEntity.setPickUpDelay(40);
        RandomSource random = player.getRandom();
        if (bl2) {
            itemEntity.setThrower(player);
        }
        if (bl) {
            float f = random.nextFloat() * 0.5f;
            float g = random.nextFloat() * ((float) Math.PI * 2);
            itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.2f, Mth.cos(g) * f);
        } else {
            float g = Mth.sin(player.getXRot() * ((float) Math.PI / 180));
            float h = Mth.cos(player.getXRot() * ((float) Math.PI / 180));
            float i = Mth.sin(player.getYRot() * ((float) Math.PI / 180));
            float j = Mth.cos(player.getYRot() * ((float) Math.PI / 180));
            float k = random.nextFloat() * ((float) Math.PI * 2);
            float l = 0.02f * random.nextFloat();
            itemEntity.setDeltaMovement(-i * h * 0.3f + Math.cos(k) * l, -g * 0.3f + 0.1f + (random.nextFloat() - random.nextFloat()) * 0.1f, j * h * 0.3f + Math.sin(k) * l);
        }
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().scale(power * ItemPhysic.CONFIG.throwConfig.multiplierPerStage));
        return itemEntity;
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        ItemStack itemStack = inventory.removeFromSelected(all);
        player.containerMenu.findSlot(inventory, inventory.selected).ifPresent(i -> player.containerMenu.setRemoteSlot(i, inventory.getSelected()));
        var itemEntity = createEntity(itemStack, player, false, true);
        if (itemEntity == null)
            return;
        player.level().addFreshEntity(itemEntity);
        ItemStack itemStack2 = itemEntity.getItem();
        if (!itemStack2.isEmpty())
            player.awardStat(Stats.ITEM_DROPPED.get(itemStack2.getItem()), itemStack.getCount());
        player.awardStat(Stats.DROP);
    }
    
}
