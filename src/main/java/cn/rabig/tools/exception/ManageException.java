package cn.rabig.tools.exception;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.rabig.tools.utils.CommonUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author MoNo
 * @since 2022/9/7 20:59
 **/
public class ManageException {
    /**
     * 统一异常处理
     *
     * @param exception [java.lang.Exception]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:56
     */
    public static void throwException(Exception exception) {
        //提取错误堆栈信息
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        //格式化错误信息
        String error = "[" + DateUtil.now() + "] [ERROR] " + stringWriter + "\n";
        //打印错误信息
        CommonUtils.error(error);
        //追加写出错误信息
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + System.getProperty("file.separator") + "error.log");
        writer.write(error,true);
        //异常退出
        System.exit(1);
    }
}
