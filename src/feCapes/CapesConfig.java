package feCapes;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;

public class CapesConfig
{
    public int                                          mode = FREE_MODE;
    public String                                       capeserver;
    public String                                       freeMode_CapeFile;
    public boolean                                      holidays;
    public boolean                                      mojangCapes;
    public boolean                                      mustHaveMod;
    public String                                       kickMessage;
    public int                                          timeout;
    public HashMap<Integer, HashMap<String, String>>    capeservers = new HashMap<Integer, HashMap<String, String>>();
    
    public static final int     FE_MODE     = 0;
    public static final int     SERVER_MODE = 1;
    public static final int     FREE_MODE   = 2;
    public static final String  cat         = "FeCapes";
    
    //@formatter:off
    String  commentMode =  FE_MODE +  ": (default) ForgeEssentials Permission mode. In this mode only players with the right permission can set capes.We support group capes."
            + "\n" + SERVER_MODE +    ": Server mode. This is basicly 'the old system', 1 cape per player, url like this: \"<capeserver>/username.png\""
            + "\n" + FREE_MODE +      ": Free mode. In this mode you allow free choice of cape. (see capes.txt)";
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
            
            String[] temp = config.get(cat, "textFileServers", new String[] {"http://capes.mineuk.com/capes.txt"}, "In order of importance.\nFormat: \"username#url\" or \"username#filename\"\nIf there is no http, it will use a file relative to the text files directory.\nThese servers will override the properties but not the holidays or dev list.").getStringList();
            for (int i = 0; i < temp.length; i ++)
                capeservers.put(i, getAllOverrides(temp[i]));
        }

        config.save();
    }

    private HashMap<String, String> getAllOverrides(String url)
    {
        HashMap<String, String> data = new HashMap<String, String>();
        
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                line = line.trim();
                if (!line.startsWith("#") && line.contains("#"))
                {
                    String[] split = line.split("#", 2);
                    if (split[1].startsWith("http")) data.put(split[0], split[1]);
                    else data.put(split[0], url.substring(0, url.lastIndexOf("/")) + split[1]);
                    
                    System.out.println(split[0] + ": " + data.get(split[0])); //TODO DEBUG CODE
                }
            }
            in.close();
        }
        catch (Exception e)
        {}
        
        return data;
    }
}
