package cn.rabig.cli.controller;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

/**
 * @author MoNo
 * @since 2022/9/6 18:32
 **/
@SuppressWarnings("unused")
public interface BaseSystem {
    /**
     * 受支持的路由器固件系统列表
     *
     * @author MoNo
     * @since 2022/10/12 21:31
     */
    List<String> systemList = List.of("Openwrt");

    /**
     * 检查路由器环境，返回安装信息
     *
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 21:32
     */
    SimpleEntry<Boolean, String> check();

    /**
     * 是否安装SGU-Script
     *
     * @return boolean
     * @author MoNo
     * @since 2022/10/12 21:32
     */
    boolean isInstall();

    /**
     * 执行安装命令
     *
     * @param username [java.lang.String]
     * @param password [java.lang.String]
     * @param ip       [java.lang.String]
     * @param mode     [java.lang.String]
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 22:03
     */
    SimpleEntry<Boolean, String> install(String username, String password, String ip, String mode);

    /**
     * 执行卸载命令
     *
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 21:33
     */
    SimpleEntry<Boolean, String> uninstall();

    /**
     * 输出错误日志
     *
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 22:00
     */
    SimpleEntry<Boolean, String> log();
}
