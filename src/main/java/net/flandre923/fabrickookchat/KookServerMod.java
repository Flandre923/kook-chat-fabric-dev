package net.flandre923.fabrickookchat;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.flandre923.fabrickookchat.commands.KookCommands;
import net.flandre923.fabrickookchat.config.Config;
import net.flandre923.fabrickookchat.config.ConfigFile;
import net.flandre923.fabrickookchat.listener.KookListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snw.jkook.JKook;
import snw.jkook.config.InvalidConfigurationException;
import snw.jkook.config.file.YamlConfiguration;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;

import java.io.*;
import java.nio.charset.StandardCharsets;

// shift + f6 更改这个类的名字
public class KookServerMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "fabrickookchat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	static KBCClient kbcClient = null;
	public static Config config = ConfigFile.DEFAULT_CONFIG;
	public static ConfigFile configFile = new ConfigFile("kook.json");
	public static KBCClient getKbcClient() {
		return kbcClient;
	}


	@Override
	public void onInitializeServer() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			/*
			 * 读写本模组的配置文件
			 */
			if(configFile.exists()){
				if(readConfig()){
					LOGGER.warn("Config file read successful");
				}else{
					LOGGER.warn("Config file is malformed,Aborting");
				}
			}else{
				LOGGER.warn("Config file doesn't exist,writing default");
				writeConfig();
			}

			//KookBC保存基础配置文件
			YamlConfiguration config = new YamlConfiguration();
			saveKBCConfig(config);

			CoreImpl core = new CoreImpl();
			JKook.setCore(core);

			//读取配置拿必要的东西
			String bot_token = KookServerMod.config.bot_token;
			String channel_ID = KookServerMod.config.channel_ID;

			if (bot_token.equals("No token provided")) {
				LOGGER.info("你没有提供bot-token或者bot-token不正确");
				LOGGER.info("McToKook-Mod将会停用");
				throw new Error("你没有提供bot-token或者bot-token不正确,McToKook-Mod将会停用,服务端即将崩溃");
			} else {
				if (channel_ID.equals("No channel ID provided")) {
					LOGGER.info("你没有提供channel ID或channel ID不正确");
					LOGGER.info("你所提供的channel_ID: " + channel_ID);
					throw new Error("你没有提供channel ID或channel ID不正确,McToKook-Mod将会停用,服务端即将崩溃");
				}
			}
			LOGGER.info("info token :" + KookServerMod.config.bot_token + " -- channel id" + KookServerMod.config.channel_ID);
			// pluginsFolder 传入 null,不会尝试从任何地方加载插件（或者后续需要使用 KookBC 的 plugin?）
			kbcClient = new KBCClient(core, config, null, bot_token);

			kbcClient.start();
			LOGGER.info("机器人启动！");

			//注册KOOK消息监听器
			//夏夜说: 不要用InternalPlugin,但是我摆了！
			kbcClient.getCore().getEventManager().registerHandlers(kbcClient.getInternalPlugin(), new KookListener(server));
			//注册KOOK指令
			kbcClient.getCore().getCommandManager().registerCommand(kbcClient.getInternalPlugin(), new KookCommands(server).list);
		});
	}

	/**
	 * 传入一个 config,使用 stream 读取
	 * @param config
	 * @author RealSeek
	 */
	private static void saveKBCConfig(YamlConfiguration config) {
		try (final InputStream stream = KookServerMod.class.getResourceAsStream("/kbc.yml")) {
			if (stream == null) {
				throw new Error("Unable to find kbc.yml");
			}

			StringBuilder yamlContentBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					yamlContentBuilder.append(line).append(System.lineSeparator());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			String yamlContent = yamlContentBuilder.toString();

			try {
				config.loadFromString(yamlContent);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 *
	 */
	public static boolean readConfig(){
		try {
			config = configFile.read();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 *
	 */
	public static boolean writeConfig(){
		try {
			configFile.write(config);
			return true;
		}catch (IOException e){
			return false;
		}
	}


}