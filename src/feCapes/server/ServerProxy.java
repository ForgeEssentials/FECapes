package feCapes.server;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar;
import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import feCapes.CommonProxy;
import feCapes.FeCapes;

@ForgeEssentialsRegistrar(ident = FeCapes.MODID)
public class ServerProxy extends CommonProxy
{

    private static final String       OWNERCAPE     = FeCapes.DEFSERVER + "owner.png";
    private static final String       ADMINCAPE     = FeCapes.DEFSERVER + "admin.png";

    public static PacketHandlerServer packetHandler = new PacketHandlerServer();
    public static Updator             updator       = new Updator();
    public static CapeData            capeData      = new CapeData();

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.instance().registerChannel(packetHandler, FeCapes.CHANNEL, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(updator);
        GameRegistry.registerPlayerTracker(updator);
        TickRegistry.registerTickHandler(updator, Side.SERVER);

        capeData.init();
    }

    @PermRegister()
    public void registerPermissions(IPermRegisterEvent event)
    {
        event.registerPermissionLevel(FeCapes.MODID, RegGroup.OWNERS);
        event.registerPermissionLevel(FeCapes.CMDPERM, RegGroup.OWNERS);

        event.registerGroupPermissionprop(FeCapes.CAPEPERM, OWNERCAPE, RegGroup.OWNERS);
        event.registerGroupPermissionprop(FeCapes.CAPEPERM, ADMINCAPE, RegGroup.ZONE_ADMINS);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CapesCmd());
    }
}
