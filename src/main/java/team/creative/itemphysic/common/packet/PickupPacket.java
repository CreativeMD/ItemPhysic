package team.creative.itemphysic.common.packet;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.itemphysic.server.ItemPhysicServer;

public class PickupPacket extends CreativePacket {
    
    public UUID uuid;
    public boolean rightClick;
    
    public PickupPacket() {
        
    }
    
    public PickupPacket(UUID uuid, boolean rightClick) {
        this.uuid = uuid;
        this.rightClick = rightClick;
    }
    
    @Override
    public void executeClient(PlayerEntity player) {
        
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        if (rightClick)
            ItemPhysicServer.toCancel.add(player);
        
        Entity item = ((ServerWorld) player.level).getEntity(uuid);
        if (item != null && item instanceof ItemEntity && item.isAlive()) {
            ItemPhysicServer.processInitialInteract((ItemEntity) item, player, Hand.MAIN_HAND);
            player.swing(Hand.MAIN_HAND);
        }
    }
    
}
