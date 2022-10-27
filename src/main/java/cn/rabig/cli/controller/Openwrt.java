package cn.rabig.cli.controller;

import cn.hutool.core.util.StrUtil;
import cn.rabig.tools.model.ShellModel;
import cn.rabig.tools.utils.FileUtils;
import cn.rabig.tools.utils.ShellUtils;
import lombok.Data;

import java.util.AbstractMap.SimpleEntry;

/**
 * @author MoNo
 * @since 2022/9/6 10:20
 **/
@Data
public class Openwrt implements BaseSystem {
    private boolean checkCurl;
    private boolean checkWget;
    private boolean checkUA2F;
    private ShellUtils shellUtils;

    public Openwrt(ShellUtils ShellUtils) {
        shellUtils = ShellUtils;
        checkCurl = shellUtils.exec("curl -help").isSuccess();
        checkWget = shellUtils.exec("wget -help").isSuccess();
        checkUA2F = StrUtil.isNotBlank(shellUtils.exec("opkg list_installed | grep \"\\ua2f\"").getOutMsg());
    }

    /**
     * 检查路由器环境，返回安装信息
     *
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 21:30
     */
    @Override
    public SimpleEntry<Boolean, String> check() {
        SimpleEntry<Boolean, String> checkInfo = new SimpleEntry<>(true, "检测通过,请继续");
        if (isInstall()) {
            checkInfo = new SimpleEntry<>(true, "已安装SGU-Script，是否覆盖安装");
        }
        if (!checkUA2F) {
            checkInfo = new SimpleEntry<>(true, """
                    由于路由器不支持UA2F，大概率会被检测共享上网
                    封禁五分钟，后续会延迟时间
                    但是脚本支持断线自动连接，会在封禁解除后自动重连
                    你可以决定是否继续安装
                    """);
        }
        if (!checkCurl && !checkWget) {
            checkInfo = new SimpleEntry<>(false, "检测不通过，请自行安装curl或wget");
        }
        return checkInfo;
    }

    /**
     * 是否安装SGU-Script
     *
     * @return boolean
     * @author MoNo
     * @since 2022/10/12 21:30
     */
    @Override
    public boolean isInstall() {
        return shellUtils.exec("find /etc/init.d/sgu_script").isSuccess();
    }

    /**
     * 执行安装命令
     *
     * @param username [java.lang.String]
     * @param password [java.lang.String]
     * @param ip       [java.lang.String]
     * @param mode     [java.lang.String]
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 21:30
     */
    @Override
    public SimpleEntry<Boolean, String> install(String username, String password, String ip, String mode) {
        //删除旧脚本
        if (isInstall() && !uninstall().getKey()) {
            return new SimpleEntry<>(false, "旧脚本删除失败");
        }
        //上传脚本
        if (!shellUtils.scpPutFile(FileUtils.writeSGUScript(username, password, ip, mode), "sgu_script", "/etc/init.d/", "0755")) {
            return new SimpleEntry<>(false, "脚本上传失败");
        }
        //初始化执行命令链
        ShellUtils.execCommandList CommandList = shellUtils.execCommandList();
        //配置UA2F
        if (checkUA2F) {
            CommandList.add("uci set ua2f.enabled.enabled=1", "UA2F开机自启失败")
                    .add("uci set ua2f.firewall.handle_fw=1", "UA2F（默认开启）自动配置防火墙失败")
                    .add("uci set ua2f.firewall.handle_intranet=1", "UA2F处理内网流量（默认开启），防止在访问内网服务时被检测到失败")
                    .add("uci set ua2f.firewall.handle_tls=1", "UA2F处理 443 端口流量（默认关闭）失败")
                    .add("uci set ua2f.firewall.handle_mmtls=0", "UA2F处理微信的 mmtls（默认开启）（建议关闭）失败")
                    .add("uci commit ua2f", "保存UA2F设置");
        }
        //添加脚本自启和运行脚本
        CommandList.add("rm -f /usr/bin/sgu_script && ln -s /etc/init.d/sgu_script /usr/bin/sgu_script", "创建脚本软链接失败，请自行使用【rm -f /usr/bin/sgu_script && ln -s /etc/init.d/sgu_script /usr/bin/sgu_script】命令来创建软链接")
                .add("/etc/init.d/sgu_script enable", "添加脚本自启动失败，请自行在【路由器管理界面】-【系统】-【启动项】中找到【sgu_script】并启动")
                .add("/etc/init.d/sgu_script restart", "脚本启动失败，请重启路由器或使用命令【/etc/init.d/sgu_script restart】手动启动脚本");
        //执行命令链安装SGU—Script
        SimpleEntry<Boolean, String> message = CommandList.startWithCheck(5);
        //启动UA2F
        shellUtils.exec("/etc/init.d/ua2f start");
        return message.getKey() ? new SimpleEntry<>(true, "安装成功") : message;
    }

    /**
     * 执行卸载命令
     *
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 21:30
     */
    @Override
    public SimpleEntry<Boolean, String> uninstall() {
        if (!isInstall()) {
            return new SimpleEntry<>(false, "尚未安装SGU-Script");
        }
        shellUtils.execCommandList()
                .add("/etc/init.d/sgu_script stop")//停止脚本
                .add("/etc/init.d/sgu_script disable")//删除脚本自启动
                .add("service ua2f stop")//停止ua2f
                .add("service ua2f disable")//取消ua2f的自启动
                .startWithoutCheck();
        SimpleEntry<Boolean, String> message = shellUtils.execCommandList()
                .add("rm -f /usr/bin/sgu_script", "删除脚本软链接失败")
                .add("rm -f /etc/init.d/sgu_script", "删除脚本失败")
                .add("rm -f /var/log/sgu_script.log", "删除错误日志失败")
                .startWithCheck(5);
        return message.getKey() ? new SimpleEntry<>(true, "卸载成功") : message;
    }

    /**
     * 输出错误日志
     *
     * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
     * @author MoNo
     * @since 2022/10/12 21:30
     */
    @Override
    public SimpleEntry<Boolean, String> log() {
        if (!isInstall()) {
            return new SimpleEntry<>(false, "尚未安装SGU-Script");
        }
        ShellModel exec = shellUtils.exec("cat /var/log/sgu_script.log");
        if (exec.isSuccess()) {
            return new SimpleEntry<>(true, exec.getOutMsg());
        }
        return new SimpleEntry<>(false, "暂无错误日志");
    }
}
