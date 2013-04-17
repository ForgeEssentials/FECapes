package feCapes.client;

import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import feCapes.CommonProxy;
import feCapes.FeCapes;

public class ClientProxy extends CommonProxy implements ITickHandler
{
    public static HashMap<String, String> updateMap     = new HashMap<String, String>();
    public static PacketHandlerClient     packetHandler = new PacketHandlerClient();

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.instance().registerChannel(packetHandler, FeCapes.CHANNEL, Side.CLIENT);
        TickRegistry.registerTickHandler(this, Side.CLIENT);

        CapeKeyHandler.addKey(CapeKeyHandler.GUIKEY, Keyboard.KEY_C, false);
        KeyBindingRegistry.registerKeyBinding(new CapeKeyHandler());

        LanguageRegistry.instance().addStringLocalization(CapeKeyHandler.GUIKEY, "Capes gui");
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {}

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        EntityPlayer player = (EntityPlayer) tickData[0];
        if (updateMap.containsKey(player.username))
        {
            updateCape(player, updateMap.get(player.username));
            updateMap.remove(player.username);
        }
    }

    public static void updateCape(EntityPlayer player, String capeURL)
    {
        if (capeURL.equals(player.cloakUrl)) return;

        if (!Strings.isNullOrEmpty(player.cloakUrl)) Minecraft.getMinecraft().renderEngine.releaseImageData(player.cloakUrl);
        player.cloakUrl = capeURL;
        if (!Strings.isNullOrEmpty(player.cloakUrl)) Minecraft.getMinecraft().renderEngine.obtainImageData(player.cloakUrl, new ImageBufferDownload());
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
        return "FeCapes Client delay handler";
    }
}
