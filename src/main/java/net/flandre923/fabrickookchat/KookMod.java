package net.flandre923.fabrickookchat;

import net.flandre923.fabrickookchat.commands.KookCommands;
import net.flandre923.fabrickookchat.config.Config;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.flandre923.fabrickookchat.config.ConfigFile;
import net.flandre923.fabrickookchat.listener.KookListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snw.jkook.JKook;
import snw.jkook.config.InvalidConfigurationException;
import snw.jkook.config.file.YamlConfiguration;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// shift + f6 更改这个类的名字
public class KookMod implements ModInitializer {
	public static final String MOD_ID = "fabrickookchat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
	}


}