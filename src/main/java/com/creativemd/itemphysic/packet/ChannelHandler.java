package com.creativemd.itemphysic.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<DropPacket> {
    public ChannelHandler() {
    	addDiscriminator(0, DropPacket.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, DropPacket packet, ByteBuf data) throws Exception {
    	packet.writeBytes(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, DropPacket packet) {
    	packet.readBytes(data);
        switch (FMLCommonHandler.instance().getEffectiveSide()) {
        case CLIENT:
            executeClient(packet);
            break;
        case SERVER:
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            packet.executeServer(((NetHandlerPlayServer) netHandler).playerEntity);
            break;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void executeClient(DropPacket packet)
    {
    	packet.executeClient(Minecraft.getMinecraft().thePlayer);
    }
}