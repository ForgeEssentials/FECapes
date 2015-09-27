package feCapes.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.util.FunctionHelper;
import com.google.common.base.Strings;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import feCapes.CapesConfig;
import feCapes.FeCapes;

public class PacketHandlerServer implements IPacketHandler
{
    public ArrayList<String>       clientsWithMod = new ArrayList<String>();
    public HashMap<String, String> cacheMap       = new HashMap<String, String>();

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
                FeCapes.logger.info("Got packet from " + ((EntityPlayer) player).username + " " + i);
                switch (i)
                {
                // Handshake
                case 0:
                    if (stream.readUTF().equals(FeCapes.MODID))
                    {
                        clientsWithMod.add(((EntityPlayer) player).username);
                        sendCapeList(player);
                    }
                    else
                        ((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer("Invalid FeCapes handshake");
                break;
                // GUI chose
                case 1:
                    if (FeCapes.conf.mode == CapesConfig.FREE_MODE)
                    {
                        CapeData.addOverride("p:" + ((EntityPlayer) player).username, stream.readUTF());
                        update("p:" + ((EntityPlayer) player).username);
                    }
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

    public void update(String target)
    {
        if (target.startsWith("p:"))
            updatePlayer(FunctionHelper.getPlayerForName(target.replaceFirst("p:", "")));
        else if (target.startsWith("g:"))
            updateGroup(PermissionsAPI.getGroupForName(target.replaceFirst("g:", "")));
        else
            FeCapes.logger.severe("Something went wrong. Target names do not start with p: or g:...");
    }

    public void updateMe(EntityPlayer player)
    {
        for (Object fakePlayer : player.worldObj.playerEntities)
            try
            {
                PacketDispatcher.sendPacketToPlayer(getUpdatePacket((EntityPlayer) fakePlayer, ServerProxy.capeData.getCapeURL(player)), (Player) player);
            }
            catch (Exception e)
            {}
    }

    private void updateGroup(Group group)
    {
        for (String username : PermissionsAPI.getPlayersInGroup(group.name, group.zoneName))
            updatePlayer(FunctionHelper.getPlayerForName(username));
    }

    private void updatePlayer(EntityPlayer player)
    {
        if (player != null)
        {
            String URL = ServerProxy.capeData.getCapeURL(player);
            if (Strings.isNullOrEmpty(URL) || !cacheMap.containsKey(player.username) || !cacheMap.get(player.username).equals(URL))
            {
                Packet250CustomPayload packet = getUpdatePacket(player, URL);
                for (Object fakeTarget : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
                {
                    EntityPlayerMP target = (EntityPlayerMP) fakeTarget;
                    if (target.dimension == player.dimension && clientsWithMod.contains(target.username)) target.playerNetServerHandler.sendPacketToPlayer(packet);
                }
            }
        }
    }

    public void sendHandshake(EntityPlayer player)
    {
        try
        {
            ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(streambyte);

            stream.write(0);

            stream.writeInt(FeCapes.conf.mode);
            stream.writeUTF(FeCapes.conf.capeserver);

            stream.close();
            streambyte.close();

            PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(FeCapes.CHANNEL, streambyte.toByteArray()), (Player) player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Packet250CustomPayload getUpdatePacket(EntityPlayer player, String URL)
    {
        try
        {
            ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(streambyte);

            stream.write(1);

            stream.writeUTF(player.username);
            stream.writeUTF(URL);

            stream.close();
            streambyte.close();

            return new Packet250CustomPayload(FeCapes.CHANNEL, streambyte.toByteArray());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void sendCapeList(Player player)
    {
        FeCapes.logger.severe("sendCapeList " + player);
        try
        {
            ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(streambyte);

            stream.write(2);

            for (String cape : ServerProxy.capeData.capeList)
            {
                stream.writeBoolean(true);
                stream.writeUTF(cape);
            }
            stream.writeBoolean(false);

            stream.close();
            streambyte.close();

            PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(FeCapes.CHANNEL, streambyte.toByteArray()), player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
