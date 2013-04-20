package feCapes;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;

public class CapesConfig
{
    public int                  mode = FREE_MODE;
    public String               capeserver;
    public String               freeMode_CapeFile;
    public boolean              holidays;
    public boolean              mojangCapes;
    public boolean              mustHaveMod;
    public String               kickMessage;
    public int                  timeout;
    
    public static final int     FE_MODE     = 0;
    public static final int     SERVER_MODE = 1;
    public static final int     FREE_MODE   = 2;
    public static final String  cat         = "FeCapes";
    
    //@formatter:off
    String  commentMode =  FE_MODE +  ": (default) ForgeEssentials Permission mode. In this mode only players with the right permission can set capes.We support group capes."
            + "\n" + SERVER_MODE +    ": Server mode. This is basicly 'the old system', 1 cape per player, url like this: \"<capeserver>/username.png\""
            + "\n" + FREE_MODE +      ": Free mode. In this mode you allow free choise of cape. (see capes.txt)";
    //@formatter:on

    public CapesConfig(File file)
    {
        Configuration config = new Configuration(file);

        config.addCustomCategoryComment(cat, "The client takes all settings from the server!");

        if (FMLCommonHandler.instance().getSide().isServer())
        {
            mode = config.get(cat, "mode", FE_MODE, commentMode).getInt();
            capeserver = config.get(cat, "capeServer", FeCapes.DEFSERVER, "This is the root adress for your capes.").getString();

            mustHaveMod = config.get(cat, "mustHaveMod", false, "If true, the user gets denied acess to the server without the mod.").getBoolean(false);
            kickMessage = config.get(cat, "kickMessage", "Please install FeCapes! jenkins.dries007.net", "Message for 'mustHaveMod'").getString();
            timeout = config.get(cat, "timeout", 5, "Time in seconds the player has before getting kicked.").getInt();

            holidays = config.get(cat, "holidays", true, "Give special capes on holidays. (see holidays.txt)").getBoolean(true);
            mojangCapes = config.get(cat, "mojangCapes", true, "If false, no mojang capes will be applied").getBoolean(true);
        }

        config.save();
    }
}