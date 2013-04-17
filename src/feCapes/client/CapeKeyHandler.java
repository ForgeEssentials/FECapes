package feCapes.client;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import feCapes.CapesConfig;
import feCapes.FeCapes;

public class CapeKeyHandler extends KeyHandler
{
    public static final String   GUIKEY      = "FeCapes.guiKey";

    static ArrayList<KeyBinding> keyBindings = new ArrayList<KeyBinding>();
    static ArrayList<Boolean>    repeatings  = new ArrayList<Boolean>();

    public static void addKey(String name, int Key, boolean repeating)
    {
        keyBindings.add(new KeyBinding(name, Key));
        repeatings.add(repeating);
    }

    public static KeyBinding[] getKeyBindArray()
    {
        return keyBindings.toArray(new KeyBinding[keyBindings.size()]);
    }

    public static boolean[] getRepeatingsArray()
    {
        boolean[] array = new boolean[repeatings.size()];

        for (int i = 0; i < array.length; i++)
            array[i] = repeatings.get(i);

        return array;
    }

    public CapeKeyHandler()
    {
        super(getKeyBindArray(), getRepeatingsArray());
    }

    @Override
    public String getLabel()
    {
        return "FeCapes Client keybind handler";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        if (tickEnd && Minecraft.getMinecraft().currentScreen == null && kb.keyDescription.equals(GUIKEY)) if (FeCapes.conf.mode == CapesConfig.FREE_MODE)
            FMLClientHandler.instance().displayGuiScreen(Minecraft.getMinecraft().thePlayer, new CapeGui(Minecraft.getMinecraft().thePlayer));
        else
            Minecraft.getMinecraft().thePlayer.sendChatToPlayer("The server is not in freemode, no capes for you!");
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

}
