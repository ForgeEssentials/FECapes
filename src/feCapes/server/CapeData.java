package feCapes.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PropQueryPlayerSpot;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import feCapes.FeCapes;

public class CapeData
{
    private static final String    DEV_LIST    = "https://raw.github.com/dries007/FEcapes/master/resources/capes/devList.txt";
    private static final String    DEV_CAPE   = "https://raw.github.com/dries007/FEcapes/master/resources/capes/devCape.png";

    public HashMap<String, String> fedevs   = new HashMap<String, String>();
    public ArrayList<String>       capeList = new ArrayList<String>();

    public void init()
    {
        getFeDevs();
        Holiday.init();
        getCapeList();
    }

    /**
     * Returns a complete URL
     * 
     * @param player
     * @return
     */
    public String getCapeURL(EntityPlayer player)
    {
        if (fedevs.containsKey(player.username.toLowerCase())) return fedevs.get(player.username.toLowerCase());

        if (FeCapes.conf.holidays && Holiday.current != null) return Holiday.current.url;

        for (int i = 0; i < FeCapes.conf.capeservers.size(); i++)
            if(FeCapes.conf.capeservers.get(i).containsKey(player.username)) return FeCapes.conf.capeservers.get(i).get(player.username);
        
        PropQueryPlayerSpot prop = new PropQueryPlayerSpot(player, FeCapes.CAPEPERM);
        PermissionsAPI.getPermissionProp(prop);
        if (prop.hasValue()) return prop.getStringValue();

        return FeCapes.conf.mojangCapes ? "http://skins.minecraft.net/MinecraftCloaks/" + StringUtils.stripControlCodes(player.username) + ".png" : "";
    }

    /**
     * Use FE way of determening groups or players
     * 
     * @param target
     * @param url
     */
    public static void addOverride(String target, String url)
    {
        if (!url.contains(".png")) url = url + ".png";
        url = FeCapes.conf.capeserver + url;

        if (target.startsWith("p:"))
            PermissionsAPI.setPlayerPermissionProp(target.replaceFirst("p:", ""), FeCapes.CAPEPERM, url, ZoneManager.getWhichZoneIn(new WorldPoint(FunctionHelper.getPlayerForName(target.replaceFirst("p:", "")))).getZoneName());
        else if (target.startsWith("g:"))
            PermissionsAPI.setGroupPermissionProp(target.replaceFirst("g:", ""), FeCapes.CAPEPERM, url, PermissionsAPI.getGroupForName(target.replaceFirst("g:", "")).zoneName);
        else
            FeCapes.logger.severe("Dafuq? Not p: or g:...");
    }

    private void getFeDevs()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(DEV_LIST).openStream()));
            String str;
            while ((str = in.readLine()) != null)
            {
                str = str.trim();

                if (str.contains("#"))
                {
                    String[] split = str.split("#", 2);
                    fedevs.put(split[0].toLowerCase(), split[1]);
                }
                else
                    fedevs.put(str.toLowerCase(), DEV_CAPE);
            }
            in.close();
        }
        catch (Exception e)
        {}
    }

    private void getCapeList()
    {
        File file = new File(FeCapes.dir, "capes.txt");

        if (file.exists())
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null)
                    if (!(line.isEmpty() || line.startsWith("#"))) capeList.add(line);
                br.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        else
            try
            {
                PrintWriter pw = new PrintWriter(file);
                pw.println("# Cape list");
                pw.println("# If a cape is on this list, if can be choisen in freemode.");
                pw.println("# In other modes TAB completion will only work on these commands, but you can manually add different capes.");

                for (String s : Arrays.asList("hornwood", "blackfyre", "superman", "nightsWatch", "blackFish", "bacon", "royalGuard", "tawney", "targaryen_1", "targaryen", "kingsguard", "diamond", "rainbow", "frey", "baratheonOfKingsLanding", "coffee", "bolton", "goodbrother", "tully", "martell", "starks", "pig", "dayne", "lannister", "snowman", "enderman", "baratheon", "greyjoy", "baratheonOfStormsEnd", "arryn", "coffee_1", "dondarrion", "facelessMen", "slime", "newyear", "tyrell", "king", "christmas", "enderpearl", "muchroom", "batman", "baratheonOfDragonstone", "captainShadows", "templarKnight"))
                    pw.println(s);

                pw.close();

                getCapeList();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }
}
