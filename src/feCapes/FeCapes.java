package feCapes;

import java.io.File;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;

import com.ForgeEssentials.core.moduleLauncher.FEModule;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = FeCapes.MODID, parentMod = FeCapes.class)
@Mod(modid = FeCapes.MODID, name = "ForgeEssentials Capes", version = FeCapes.VERSION)
public class FeCapes
{
    public static final String DEFSERVER = "http://capes.dries007.net/";
    public static final String MODID     = "FeCapes";
    public static final String VERSION   = "@VERION@";
    public static final String CHANNEL   = MODID;
    public static final String CMDPERM   = MODID + ".command";
    public static final String CAPEPERM  = MODID + ".cape";

    @SidedProxy(clientSide = "feCapes.client.ClientProxy", serverSide = "feCapes.server.ServerProxy")
    public static CommonProxy  proxy;

    @Mod.Instance("FeCapes")
    public static FeCapes      instance;

    public static File         dir;
    public static Side         side;

    public static CapesConfig  conf;
    public static Logger       logger    = Logger.getLogger("FeCapes");

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        logger.info("Thanks for using FeCapes. ~ Dries007");

        conf = new CapesConfig(event.getSuggestedConfigurationFile());

        if (event.getSide().isClient())
            dir = Minecraft.getMinecraftDir();
        else
            dir = new File(".");

        dir = new File(dir, "FeCapes");
        dir.mkdirs();

        proxy.preInit(event);
    }

    @Mod.ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
        proxy.serverStarting(event);
    }
}