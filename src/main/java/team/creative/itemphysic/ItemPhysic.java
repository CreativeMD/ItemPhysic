package team.creative.itemphysic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.itemphysic.client.ItemPhysicClient;
import team.creative.itemphysic.common.packet.DropPacket;
import team.creative.itemphysic.common.packet.PickupPacket;
import team.creative.itemphysic.server.ItemPhysicServer;

@Mod(ItemPhysic.MODID)
public class ItemPhysic {
    
    public static final Logger LOGGER = LogManager.getLogger(ItemPhysic.MODID);
    public static final String MODID = "itemphysic";
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(ItemPhysic.MODID, "main"));
    public static ItemPhysicConfig CONFIG;
    
    public ItemPhysic() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        ItemPhysicClient.init(event);
        CreativeCoreClient.registerClientConfig(MODID);
    }
    
    private void init(final FMLCommonSetupEvent event) {
        NETWORK.registerType(DropPacket.class, DropPacket::new);
        NETWORK.registerType(PickupPacket.class, PickupPacket::new);
        
        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new ItemPhysicConfig());
        
        ItemPhysicServer.init(event);
    }
    
}
