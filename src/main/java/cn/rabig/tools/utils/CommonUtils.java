package cn.rabig.tools.utils;

import cn.hutool.core.lang.Console;
import cn.rabig.tools.exception.ManageException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author MoNo
 * @since 2022/9/4 18:53
 **/
public class CommonUtils {
    /**
     * 打印日志
     *
     * @param str [java.lang.String]
     * @return void
     * @since 2022/10/12 22:11
     * @author MoNo
     */
    public static void log(String str) {
        Console.log(str);
    }

    /**
     * 打印错误日志
     *
     * @param str [java.lang.String]
     * @return void
     * @since 2022/10/12 22:11
     * @author MoNo
     */
    public static void error(String str) {
        Console.error(str);
    }

    /**
     * 统一处理线程睡眠异常
     *
     * @param time [int]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:50
     */
    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            ManageException.throwException(e);
        }
    }

    /**
     * 退出脚本
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:50
     */
    public static void exitCli() {
        CommonUtils.printProgressBar(10, "脚本10s后退出");
        System.exit(0);//正常退出
    }

    /**
     * 打印进度条
     *
     * @param time [int]
     * @param info [java.lang.String]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:50
     */
    public static void printProgressBar(int time, String info) {
        ProgressBarBuilder pbb = new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII)
                .setTaskName("[Exit] " + info)
                .setUnit("s", 1);
        ProgressBar.wrap(IntStream.rangeClosed(1, time).boxed().collect(Collectors.toList()), pbb).forEach(i -> sleep(1000));
    }

    /**
     * 加载fxml文件
     *
     * @param fxml [java.lang.String]
     * @return javafx.scene.Parent
     * @author MoNo
     * @since 2022/10/12 21:51
     */
    public static Parent loadFXML(String fxml) {
        FXMLLoader fxmlLoader;
        Parent load = null;
        try {
            fxmlLoader = new FXMLLoader(CommonUtils.class.getResource("/view/" + fxml + ".fxml"));
            load = fxmlLoader.load();
        } catch (Exception e) {
            ManageException.throwException(e);
        }
        return load;
    }

    /**
     * 统一处理输入
     *
     * @return java.lang.String
     * @author MoNo
     * @since 2022/10/12 21:51
     */
    public static String scanner() {
        String input;
        try {
            input = new Scanner(System.in).nextLine();
            if (input.isBlank() || input.contains(" ")) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            error("\n输入有误，请重新输入");
            input = scanner();
        }
        return input;
    }

    /**
     * 统一处理范围输入
     *
     * @param optionCount [int]
     * @return int
     * @author MoNo
     * @since 2022/10/12 21:51
     */
    public static int scannerOption(int optionCount) {
        int option;
        try {
            option = Integer.parseInt(scanner());
            if (option < 0 || option > optionCount) {
                throw new RuntimeException();
            }else {
                log("");//换行
            }
        } catch (Exception e) {
            error("\n输入错误，请重新输入1-" + optionCount);
            option = scannerOption(optionCount);
        }
        return option;
    }

    /**
     * 校验ip
     *
     * @param ip [java.lang.String]
     * @return boolean
     * @author MoNo
     * @since 2022/10/12 21:51
     */
    public static boolean checkIp(String ip) {
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ip);
        return m.matches();
    }

    /**
     * 通过类名获取字节码文件
     *
     * @param className [java.lang.String]
     * @return java.lang.Class<?>
     * @author MoNo
     * @since 2022/10/12 21:51
     */
    public static Class<?> getClassForName(String className) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            ManageException.throwException(e);
        }
        return aClass;
    }
}
