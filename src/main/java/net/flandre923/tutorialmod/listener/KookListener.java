package net.flandre923.tutorialmod.listener;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import net.flandre923.tutorialmod.config.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.TextComponent;

import java.util.HashMap;
import java.util.Map;

public class KookListener implements Listener {

    public MinecraftServer server;

    private static Config CONFIG;
    public KookListener(MinecraftServer server){
        this.server = server;
    }


    @EventHandler
    public void onKookMessage(ChannelMessageEvent event) {

        if (!CONFIG.to_Minecraft) {
            return;
        }

        if (event.getMessage().getSender().isBot()) {
            return;
        }

        if (event.getChannel().getId().equals(CONFIG.channel_ID)) {

            BaseComponent component = event.getMessage().getComponent();

            if (component instanceof TextComponent) {

                Map<String,String> map = MapUtil.builder(new HashMap<String, String>())
                        .put("nickName", event.getMessage().getSender().getNickName(event.getChannel().getGuild()))
                        .put("message", component.toString())
                        .map();

                server.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(
                        Text.literal(StrUtil.format(CONFIG.to_Minecraft_Message,map))));
            }
        }

    }

}
