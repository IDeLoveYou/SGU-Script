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
     * @return byte[]
     * @author MoNo
     * @since 2022/9/4 23:12
     */
    public static byte[] writeSGUScript(String username, String password, String ip, String mode) {
        String scriptTemplate = ResourceUtil.readUtf8Str("script/Openwrt/" + mode + "/sgu_script.sh");
        String script = StrUtil.format(scriptTemplate, username, password, ip);
        return script.getBytes(StandardCharsets.UTF_8);
    }

}
