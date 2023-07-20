package net.flandre923.fabrickookchat.commands;

import net.minecraft.server.MinecraftServer;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.DividerModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;

public class KookCommands {
    public MinecraftServer server;
    public KookCommands(MinecraftServer server){
        this.server = server;
    }


    public JKookCommand list = new JKookCommand("list","/")
            .setDescription("用法: /list ; 作用: 返回当前服务器内的玩家列表")
            .setHelpContent("用法: /list ; 作用: 返回当前服务器内的玩家列表")
            .executesUser(((sender, arguments, message) -> {

                StringBuilder players = new StringBuilder();

                server.getPlayerManager().getPlayerList()
                        .forEach(player -> players.append(player.getGameProfile().getName()).append(" "));

                MultipleCardComponent playerListCard = new CardBuilder()
                        .setTheme(Theme.SUCCESS)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("在线玩家列表: ")))
                        .addModule(DividerModule.INSTANCE)
                        .addModule(new SectionModule(new MarkdownElement(players.toString()),null, Accessory.Mode.LEFT))
                        .build();


                message.reply(playerListCard);

            }));
}
