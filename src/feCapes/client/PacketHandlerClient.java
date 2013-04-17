package feCapes.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import feCapes.FeCapes;

public class PacketHandlerClient implements IPacketHandler
{
    public static ArrayList<String> capes = new ArrayList<String>();

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        try
        {
            if (packet.channel.equals(FeCapes.CHANNEL))
            {
                ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
                DataInputStream stream = new DataInputStream(streambyte);

                int i = stream.read();
                FeCapes.logger.info("Got packet from SERVER " + i);
                switch (i)
                {
                // Server wants handshake
                case 0:
                    FeCapes.conf.mode = stream.readInt();
                    FeCapes.conf.capeserver = stream.readUTF();
                    sendHandShake();
                break;
                // Cape update
                case 1:
                    String username = stream.readUTF();
                    String url = stream.readUTF();

                    ClientProxy.updateMap.put(username, url);
                break;
                // Cape list
                case 2:
                    while (stream.readBoolean())
                        capes.add(stream.readUTF());
                    preLoadCapes();
                break;

                }
                stream.close();
                streambyte.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void preLoadCapes()
    {
        for (String cape : capes)
        {
            cape = FeCapes.conf.capeserver + cape + ".png";
            Minecraft.getMinecraft().renderEngine.obtainImageData(cape, new ImageBufferDownload());
        }
    }
    
    private void sendHandShake()
    {
        try
        {
            ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(streambyte);

            stream.writeByte(0);
            stream.writeUTF(FeCapes.MODID);

            stream.close();
            streambyte.close();

            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(FeCapes.CHANNEL, streambyte.toByteArray()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendUpdate(String cape)
    {
        try
        {
            ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(streambyte);

            stream.writeByte(1);
            stream.writeUTF(cape);

            stream.close();
            streambyte.close();

            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(FeCapes.CHANNEL, streambyte.toByteArray()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
