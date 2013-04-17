package feCapes.server;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.util.events.ModifyPlayerGroupEvent;
import com.ForgeEssentials.util.events.PermissionPropSetEvent;
import com.ForgeEssentials.util.events.PlayerChangedZone;
import com.ForgeEssentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import feCapes.FeCapes;

public class Updator implements IPlayerTracker, ITickHandler
{
    private void update(EntityPlayer player)
    {
        try
        {
            ServerProxy.packetHandler.update("p:" + player.username);
        }
        catch (Exception e)
        {}
    }

    private void updateMe(EntityPlayer player)
    {
        ServerProxy.packetHandler.updateMe(player);
    }

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        ServerProxy.packetHandler.sendHandshake(player);
        if (FeCapes.conf.mustHaveMod) TaskRegistry.registerSingleTask(new kickTimer(player), 0, 0, FeCapes.conf.timeout, 0);
        delayList.add(player.username);
        updateMe(player);
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        delayList.add(player.username);
        updateMe(player);
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        delayList.add(player.username);
        updateMe(player);
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        ServerProxy.packetHandler.clientsWithMod.remove(player.username);
        ServerProxy.packetHandler.cacheMap.remove(player.username);
    }

    /*
     * Update on capechage
     */

    @ForgeSubscribe
    public void changeCape(PermissionPropSetEvent e)
    {
        if (e.perm.value.equals(FeCapes.CAPEPERM)) delayList.add(e.entity);;
    }

    @ForgeSubscribe
    public void changeZone(PlayerChangedZone e)
    {
        delayList.add(e.entityPlayer.username);
    }

    @ForgeSubscribe
    public void groupChange(ModifyPlayerGroupEvent e)
    {
        delayList.add(e.player);
    }

    ArrayList<String> delayList = new ArrayList<String>();

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        EntityPlayer player = (EntityPlayer) tickData[0];
        if (delayList.contains(player.username))
        {
            update(player);
            delayList.remove(player.username);
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {}

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel()
    {
        return "FeCapes Server delay handler";
    }

    public static class kickTimer extends TimerTask
    {
        public EntityPlayerMP player;

        public kickTimer(EntityPlayer player)
        {
            this.player = (EntityPlayerMP) player;
        }

        @Override
        public void run()
        {
            try
            {
                if (!ServerProxy.packetHandler.clientsWithMod.contains(player.username)) player.playerNetServerHandler.kickPlayerFromServer(FeCapes.conf.kickMessage);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
