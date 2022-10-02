package cn.rabig.tools.exception;

import cn.hutool.core.io.FileUtil;
import cn.rabig.tools.utils.CommonUtils;

import java.io.File;

/**
 * @author MoNo
 * @since 2022/9/7 20:59
 **/
public class ManageException {

    /**
     * 统一异常处理
     *
     * @param e [java.lang.Exception]
     * @return void
     * @author MoNo
     * @since 2022/9/7 21:00
     */
    public static void throwException(Exception e) {
        String error = "程序出现错误：" + e.getMessage();
        CommonUtils.error(error);
        FileUtil.writeUtf8String(error, new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "error.log"));
        System.exit(1);//异常退出
    }
}
