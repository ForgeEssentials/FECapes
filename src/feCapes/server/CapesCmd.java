package feCapes.server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import feCapes.FeCapes;

public class CapesCmd extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "cape";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendChatToPlayer("---  Your cape URL ---");
            sender.sendChatToPlayer(ServerProxy.capeData.getCapeURL(sender));
            ServerProxy.packetHandler.update("p:" + sender.username);
            return;
        }

        String target;
        if (PermissionsAPI.getGroupForName(args[0]) != null)
            target = "g:" + PermissionsAPI.getGroupForName(args[0]).name;
        else if (args[0].equalsIgnoreCase("me"))
            target = "p:" + sender.username;
        else
            target = "p:" + FunctionHelper.getPlayerForName(sender, args[0]).username;

        if (args.length == 1)
        {
            OutputHandler.chatConfirmation(sender, "Updated " + target);
            ServerProxy.packetHandler.update(target);
            return;
        }
        else if (args[1].equalsIgnoreCase("update"))
        {
            OutputHandler.chatConfirmation(sender, "Updated " + target);
            ServerProxy.packetHandler.update(target);
            return;
        }
        else if (args[1].equalsIgnoreCase("set"))
        {
            if (args.length == 3)
            {
                OutputHandler.chatConfirmation(sender, "Set to " + args[2]);
                CapeData.addOverride(target, args[2]);
            }
            else
                OutputHandler.chatError(sender, "You must provide a URL.");
        }
        else
            OutputHandler.chatError(sender, "Unknown syntax. Use 'update' or 'set'.");
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatWarning(sender, "You must provide a player or group as 1th argument.");
            return;
        }

        String target;
        if (PermissionsAPI.getGroupForName(args[0]) != null)
            target = "g:" + PermissionsAPI.getGroupForName(args[0]).name;
        else
            target = "p:" + FunctionHelper.getPlayerForName(sender, args[0]).username;

        if (args.length == 1)
        {
            OutputHandler.chatConfirmation(sender, "Updated " + target);
            ServerProxy.packetHandler.update(target);
            return;
        }
        else if (args[1].equalsIgnoreCase("update"))
        {
            OutputHandler.chatConfirmation(sender, "Updated " + target);
            ServerProxy.packetHandler.update(target);
            return;
        }
        else if (args[1].equalsIgnoreCase("set"))
        {
            if (args.length == 3)
            {
                OutputHandler.chatConfirmation(sender, "Set to " + args[2]);
                CapeData.addOverride(target, args[2]);
            }
            else
                OutputHandler.chatError(sender, "You must provide a URL.");
        }
        else
            OutputHandler.chatError(sender, "Unknown syntax. Use 'update' or 'set'.");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            Zone zone = sender instanceof EntityPlayer ? ZoneManager.getWhichZoneIn(new WorldPoint((EntityPlayer) sender)) : ZoneManager.getGLOBAL();
            ArrayList<String> list = new ArrayList<String>();
            for (String s : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames())
                list.add(s);

            while (zone != null)
            {
                for (Group g : PermissionsAPI.getGroupsInZone(zone.getZoneName()))
                    list.add(g.name);
                zone = ZoneManager.getZone(zone.parent);
            }

            return getListOfStringsFromIterableMatchingLastWord(args, list);
        }
        else if (args.length == 2)
            return getListOfStringsMatchingLastWord(args, "update", "set");
        else if (args.length == 3)
            return getListOfStringsFromIterableMatchingLastWord(args, ServerProxy.capeData.capeList);
        else
            return null;
    }

    @Override
    public String getCommandPerm()
    {
        return FeCapes.CMDPERM;
    }
}
