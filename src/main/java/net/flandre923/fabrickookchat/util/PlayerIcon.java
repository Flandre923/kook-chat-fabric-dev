package net.flandre923.fabrickookchat.util;

import cn.hutool.core.net.url.UrlBuilder;

public class PlayerIcon {

    /**
     * 获取玩家的头像图标并且上传至KOOK图床并返回url
     *
     * @param playerUUID 玩家uuid
     * @return 上传至KOOK图床后的图片的url
     */
    public static String getPlayerIconUr(String playerUUID) {

        return UrlBuilder.of("https://crafatar.com")
                .addPath("avatars")
                .addPath(playerUUID)
                .addQuery("overlay", "true")
                .build();
    }
}
