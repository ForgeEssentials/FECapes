package feCapes;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public abstract class CommonProxy
{
    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void serverStarting(FMLServerStartingEvent event);
}