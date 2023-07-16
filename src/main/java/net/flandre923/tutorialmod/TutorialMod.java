package net.flandre923.tutorialmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.flandre923.tutorialmod.block.ModBlocks;
import net.flandre923.tutorialmod.commands.KookCommands;
import net.flandre923.tutorialmod.config.Config;
import net.flandre923.tutorialmod.item.ModItemGroups;
import net.flandre923.tutorialmod.item.ModItems;
import net.flandre923.tutorialmod.listener.KookListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snw.jkook.JKook;
import snw.jkook.config.file.YamlConfiguration;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// shift + f6 更改这个类的名字
public class TutorialMod implements ModInitializer {
	public static final String MOD_ID = "tutorialmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final File kbcSetting = new File(".", "config/McToKook/kbc.yml");
	private static final File configFolder = new File(".", "config/McToKook");
	static KBCClient kbcClient = null;
	public static Config CONFIG;

	public static KBCClient getKbcClient() {
		return kbcClient;
	}


	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {

			if (!configFolder.exists()) {
				configFolder.mkdir();
			}
			//KookBC保存基础配置文件
			saveKBCConfig();
			YamlConfiguration config = new YamlConfiguration();

			CoreImpl core = new CoreImpl();
			JKook.setCore(core);

			//读取配置拿必要的东西
			String bot_token = CONFIG.generic.bot_token;
			String channel_ID = CONFIG.generic.channel_ID;

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

	private static void saveKBCConfig() {
		try (final InputStream stream = TutorialMod.class.getResourceAsStream("/kbc.yml")) {
			if (stream == null) {
				throw new Error("Unable to find kbc.yml");
			}
			if (kbcSetting.exists()) {
				return;
			}
			//noinspection ResultOfMethodCallIgnored
			kbcSetting.createNewFile();

			try (final FileOutputStream out = new FileOutputStream(kbcSetting)) {
				int index;
				byte[] bytes = new byte[1024];
				while ((index = stream.read(bytes)) != -1) {
					out.write(bytes, 0, index);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}