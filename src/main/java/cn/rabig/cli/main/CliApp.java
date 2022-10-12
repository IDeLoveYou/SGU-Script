package cn.rabig.cli.main;

import cn.hutool.core.util.ReflectUtil;
import cn.rabig.cli.controller.BaseSystem;
import cn.rabig.tools.utils.CommonUtils;
import cn.rabig.tools.utils.ShellUtils;

import java.util.AbstractMap.SimpleEntry;


/**
 * @author MoNo
 * @since 2022/8/29 16:27
 **/
public class CliApp {
    private static String mode;

    /**
     * 初始化账号信息并开始安装
     *
     * @param system [java.lang.Object]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:34
     */
    public static void initAndInstall(Object system) {
        boolean checkCurl = (boolean) ReflectUtil.getFieldValue(system, "checkCurl");
        boolean checkWget = (boolean) ReflectUtil.getFieldValue(system, "checkWget");
        String mode = "curl";
        if (checkCurl && checkWget) {
            CommonUtils.log("""
                    请选择安装脚本模式：
                    （1）：curl
                    （2）：wget""");
            switch (CommonUtils.scannerOption(2)) {
                case 1 -> mode = "curl";
                case 2 -> mode = "wget";
            }
        } else if (checkWget) {
            mode = "wget";
        }
        //安装阶段
        CommonUtils.log("请输入宽带账号，务必正确");
        String username = CommonUtils.scanner();
        CommonUtils.log("请输入宽带密码，务必正确");
        String password = CommonUtils.scanner();
        CommonUtils.log("请输入ip");
        String ip = CommonUtils.scanner();
        SimpleEntry<Boolean, String> install = ReflectUtil.invoke(system, "install", username, password, ip, mode);
        CommonUtils.log(install.getValue());
    }

    /**
     * 通过检查提示的信息，由用户确认是否安装
     *
     * @param system [java.lang.Object]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:35
     */
    public static void confirmAndStart(Object system) {
        CommonUtils.log("""
                 是否开始安装：
                （1）是
                （2）否""");
        switch (CommonUtils.scannerOption(2)) {
            case 1 -> initAndInstall(system);
            case 2 -> CommonUtils.exitCli();
        }
    }

    /**
     * 通过反射调用各个固件系统处理操作的方法
     *
     * @param system [java.lang.Object]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:35
     */
    public static void manageMode(Object system) {
        switch (mode) {
            case "install" -> {
                CommonUtils.log("开始运行检测程序");
                SimpleEntry<Boolean, String> check = ReflectUtil.invoke(system, "check");
                if (check.getKey()) {
                    //可以安装，并打印提示信息
                    CommonUtils.log(check.getValue());
                    //进入确认阶段
                    confirmAndStart(system);
                } else {
                    //无法安装，脚本退出
                    CommonUtils.error(check.getValue());
                    CommonUtils.exitCli();
                }
            }
            case "uninstall" -> {
                SimpleEntry<Boolean, String> uninstall = ReflectUtil.invoke(system, "uninstall");
                CommonUtils.log(uninstall.getValue());
            }
            case "log" -> {
                SimpleEntry<Boolean, String> uninstall = ReflectUtil.invoke(system, "log");
                CommonUtils.log("日志文件:\n" + uninstall.getValue());
            }
        }
    }

    /**
     * 初始化ssh连接
     *
     * @return cn.rabig.tools.utils.ShellUtils
     * @author MoNo
     * @since 2022/10/12 21:35
     */
    public static ShellUtils verifySsh() {
        CommonUtils.log("请输入路由器host（默认为：192.168.1.1）");
        String host = CommonUtils.scanner();
        CommonUtils.log("请输入路由器ssh端口（默认为：22）");
        String port = CommonUtils.scanner();
        CommonUtils.log("请输入路由器账号（建议使用管理员账号:root）");
        String username = CommonUtils.scanner();
        CommonUtils.log("请输入路由器账号密码（默认为：admin或者password）");
        String password = CommonUtils.scanner();
        ShellUtils shellUtils = new ShellUtils(host, port, username, password);
        if (shellUtils.isSuccess()) {
            CommonUtils.log("SSH登录成功");
        } else {
            CommonUtils.error("SSH登录失败，请重试\n");
            shellUtils = verifySsh();
        }
        return shellUtils;
    }

    /**
     * 选择路由器固件
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:35
     */
    public static void chooseSystem() {
        StringBuilder message = new StringBuilder("请输入路由器系统：");
        for (int i = 1; i <= BaseSystem.systemList.size(); i++) {
            message.append("\n（").append(i).append(")").append(BaseSystem.systemList.get(i - 1));
        }
        CommonUtils.log(message.toString());
        int index = CommonUtils.scannerOption(BaseSystem.systemList.size());
        String systemClassName = "cn.rabig.cli.controller." + BaseSystem.systemList.get(index - 1);
        Object system = ReflectUtil.newInstance(CommonUtils.getClassForName(systemClassName), verifySsh());
        manageMode(system);
    }

    /**
     * 选择需要执行的操作
     *
     * @param args [java.lang.String]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:36
     */
    public static void main(String[] args) {
        CommonUtils.log("""
                 请输入执行程序：
                （1）安装SGU-Script
                （2）卸载SGU-Script
                （3）查看日志""");
        switch (CommonUtils.scannerOption(3)) {
            case 1 -> mode = "install";
            case 2 -> mode = "uninstall";
            case 3 -> mode = "log";
        }
        chooseSystem();
    }
}
