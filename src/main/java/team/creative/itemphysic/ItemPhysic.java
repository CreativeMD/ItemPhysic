package team.creative.itemphysic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.ICreativeLoader;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.itemphysic.client.ItemPhysicClient;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;
import team.creative.itemphysic.server.ItemPhysicServer;

@Mod(ItemPhysic.MODID)
public class ItemPhysic implements ClientLoader, CommonLoader {
    
    public static final Logger LOGGER = LogManager.getLogger(ItemPhysic.MODID);
    public static final String MODID = "itemphysic";
    public static final CreativeNetwork NETWORK = new CreativeNetwork(1, LOGGER, new ResourceLocation(ItemPhysic.MODID, "main"));
    public static ItemPhysicConfig CONFIG;
    
    public ItemPhysic() {
        ICreativeLoader loader = CreativeCore.loader();
        loader.register(this);
        loader.registerClient(this);
    }
    
    @Override
    public void onInitialize() {
        NETWORK.registerType(DropPacket.class, DropPacket::new);
        NETWORK.registerType(PickupPacket.class, PickupPacket::new);
        
        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new ItemPhysicConfig());
        ItemPhysicServer.init();
    }
    
    @Override
    public void onInitializeClient() {
        ItemPhysicClient.init();
    }
    
}
