package team.creative.itemphysic.common.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.itemphysic.server.ItemPhysicServer;

public class DropPacket extends CreativePacket {
    
    public int power;
    
    public DropPacket() {
        power = 0;
    }
    
    public DropPacket(int power) {
        this.power = power;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void executeClient(PlayerEntity player) {
        
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        ItemPhysicServer.tempDroppower = power;
    }
    
}
