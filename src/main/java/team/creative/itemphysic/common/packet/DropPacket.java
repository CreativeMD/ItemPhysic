package team.creative.itemphysic.common.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
    @OnlyIn(Dist.CLIENT)
    public void executeClient(Player player) {}
    
    @Override
    public void executeServer(ServerPlayer player) {
        ItemPhysicServer.tempDroppower = power;
    }
    
}
