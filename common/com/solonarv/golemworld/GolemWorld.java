package com.solonarv.golemworld;

import net.minecraftforge.common.Configuration;

import com.solonarv.golemworld.golem.GolemRegistry;
import com.solonarv.golemworld.item.ModItems;
import com.solonarv.golemworld.lib.Reference;
import com.solonarv.golemworld.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

/**
 * Golem-World
 * 
 * GolemWorld
 * 
 * @author solonarv
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class GolemWorld {

    @Instance(Reference.MOD_ID)
    public static GolemWorld instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy proxy; // Will be fixed later

    public static Configuration config;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
    }

    @Init
    public void load(FMLInitializationEvent event) {
        proxy.registerRenderers();
        ModItems.registerItems();
        GolemRegistry.registerGolems();
    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
        // TODO Stub Method
    }
}
