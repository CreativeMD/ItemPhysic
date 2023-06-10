package team.creative.itemphysic.common.packet;

import java.util.UUID;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.itemphysic.server.ItemPhysicServer;

public class PickupPacket extends CreativePacket {
    
    public UUID uuid;
    public boolean rightClick;
    
    public PickupPacket() {}
    
    public PickupPacket(UUID uuid, boolean rightClick) {
        this.uuid = uuid;
        this.rightClick = rightClick;
    }
    
    @Override
    public void executeClient(Player player) {}
    
    @Override
    public void executeServer(ServerPlayer player) {
        Entity item = ((ServerLevel) player.level()).getEntity(uuid);
        if (item != null && item instanceof ItemEntity && item.isAlive()) {
            ItemPhysicServer.interact((ItemEntity) item, player, InteractionHand.MAIN_HAND);
            player.swing(InteractionHand.MAIN_HAND);
        }
    }
    
}
