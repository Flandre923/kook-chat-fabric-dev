package net.flandre923.fabrickookchat.listener;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import net.flandre923.fabrickookchat.KookServerMod;
import net.flandre923.fabrickookchat.config.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.TextComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * KookListener 类是用于监听 Kook 消息事件，并将这些消息转发到《我的世界》服务器上的玩家的类
 */
public class KookListener implements Listener {
    public MinecraftServer server;
    private final Config config;

    /**
     * 初始化 KookListener 类的新实例，需要一个 MinecraftServer 对象作为参数。
     * @param server MinecraftServer 类型，表示《我的世界》服务器实例。
     */
    public KookListener(MinecraftServer server) {
        this.server = server;
        this.config = KookServerMod.config;

    }

    /**
     * 当接收到来自 Kook 的消息时，此方法将被调用，以决定是否将消息转发到《我的世界》服务器上的玩家。
     * @param event ChannelMessageEvent 类型，包含消息事件的相关信息
     */
    @EventHandler
    public void onKookMessage(ChannelMessageEvent event) {
        if (!shouldHandleMessage(event)) {
            return;
        }
        sendMessageToMinecraft(event);

    }

    /**
     * 此方法用于确定是否应该处理传入的事件。
     * @param event ChannelMessageEvent 类型，包含消息事件的相关信息。
     * @return 如果应该处理消息，则返回 true，否则返回 false。
     */
    private boolean shouldHandleMessage(ChannelMessageEvent event) {
        return config.to_Minecraft && !event.getMessage().getSender().isBot() && event.getChannel().getId().equals(config.channel_ID);
    }

    /**
     * 此方法用于将 Kook 消息转发到《我的世界》服务器上的所有玩家。
     * @param event 此方法用于将 Kook 消息转发到《我的世界》服务器上的所有玩家。
     */
    private void sendMessageToMinecraft(ChannelMessageEvent event) {
        BaseComponent component = event.getMessage().getComponent();
        if (component instanceof TextComponent) {
            Map<String, String> map = createMessageMap(event);
            server.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(formatMessage(map)));
        }
    }

    /**
     * 此方法用于创建一个包含消息元数据（如发送者的昵称和消息内容）的映射。
     * @param event 此方法用于创建一个包含消息元数据（如发送者的昵称和消息内容）的映射。
     * @return Map<String, String> 类型，包含消息元数据的映射。
     */
    private Map<String, String> createMessageMap(ChannelMessageEvent event) {
        return MapUtil.builder(new HashMap<String,String>())
                .put("nickName", event.getMessage().getSender().getNickName(event.getChannel().getGuild()))
                .put("message", componentToString(event.getMessage().getComponent()))
                .map();
    }

    /**
     * 此方法用于将 BaseComponent 对象转换为字符串表示。
     * @param component: BaseComponent 类型，表示要转换的消息组件。
     * @return String 类型，表示转换后的字符串。
     */
    private String componentToString(BaseComponent component) {
        // Assuming there is a method to convert BaseComponent to String
        return component.toString();
    }

    /**
     * 此方法用于根据提供的元数据映射格式化消息，并将其转换为 Text 对象。
     * @param map: Map<String, String> 类型，包含消息元数据的映射。
     * @return Text 类型，表示格式化后的消息。
     */
    private Text formatMessage(Map<String, String> map) {
        return Text.literal(StrUtil.format(config.to_Minecraft_Message, map));
    }

}
