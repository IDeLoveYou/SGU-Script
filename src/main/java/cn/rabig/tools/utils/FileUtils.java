package cn.rabig.tools.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author MoNo
 * @since 2022/9/1 12:12
 **/
public class FileUtils {
    /**
     * 创建认证脚本
     *
     * @param username [java.lang.String]
     * @param password [java.lang.String]
     * @param ip       [java.lang.String]
     * @param mode     [java.lang.String]
     * @return byte[]
     * @author MoNo
     * @since 2022/10/12 21:52
     */
    public static byte[] writeSGUScript(String username, String password, String ip, String mode) {
        String scriptTemplate = ResourceUtil.readUtf8Str("script/Openwrt/" + mode + "/sgu_script.sh");
        String script = StrUtil.format(scriptTemplate, username, password, ip);
        return script.getBytes(StandardCharsets.UTF_8);
    }
}
