package cn.rabig.cli.controller;

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
    }

    /**
     * 检查路由器环境
     * 返回能否安装，
     *
     * @return java.lang.String
     * @author MoNo
     * @since 2022/9/6 12:57
     */
    @Override
    public SimpleEntry<Boolean, String> check() {
        SimpleEntry<Boolean, String> checkInfo = new SimpleEntry<>(true, "检测通过,请继续");
        if (isInstall()) {
            checkInfo = new SimpleEntry<>(true, "已安装SGU-Script，是否覆盖安装");
        }
        checkCurl = shellUtils.exec("curl -help").isSuccess();
        checkWget = shellUtils.exec("wget -help").isSuccess();
        checkUA2F = !shellUtils.exec("opkg list_installed | grep \"\\ua2f\"").getOutMsg().isBlank();
        if (!checkUA2F) {
            checkInfo = new SimpleEntry<>(true, """
                    由于路由器不支持UA2F，大概率会被检测共享上网
                    封禁五分钟，后续会延迟时间
                    但是脚本支持断线自动连接，会在封禁解除后自动重连
                    您可以决定是否继续安装
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
     * @since 2022/9/9 13:11
     */
    @Override
    public boolean isInstall() {
        return shellUtils.exec("find /etc/init.d/sgu_script").isSuccess();
    }

    /**
     * 执行安装命令
     *
     * @return java.lang.String
     * @author MoNo
     * @since 2022/9/6 12:56
     */
    @Override
    public SimpleEntry<Boolean, String> install(String username, String password, String ip, String mode) {
        //删除旧脚本
        if (isInstall()) {
            uninstall();
        }
        //配置UA2F
        if (checkUA2F) {
            shellUtils.execCommandList()
                    .add("echo '# Modify the ttl.' >>/etc/firewall.user")//配置防火墙
                    .add("echo 'iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64' >>/etc/firewall.user")
                    .add("/etc/init.d/firewall reload")
                    .add("uci set ua2f.enabled.enabled=1")//配置ua2f
                    .add("uci set ua2f.firewall.handle_fw=1")
                    .add("uci set ua2f.firewall.handle_intranet=1")
                    .add("uci set ua2f.firewall.handle_tls=1")
                    .add("uci set ua2f.firewall.handle_mmtls=1")
                    .add("uci commit ua2f")
                    .add("service ua2f enable")
                    .add("/etc/init.d/ua2f start")
                    .startWithoutCheck();
        }
        //上传脚本
        if (!shellUtils.scpPutFile(FileUtils.writeSGUScript(username, password, ip, mode), "sgu_script", "/etc/init.d/", "0755")) {
            return new SimpleEntry<>(false, "脚本上传失败");
        }
        //启动脚本
        SimpleEntry<Boolean, String> message = shellUtils.execCommandList()
                .add("/etc/init.d/sgu_script enable", "添加脚本自启动失败，请自行在【路由器管理界面】-【系统】-【启动项】中找到【sgu_script】并启动")
                .add("/etc/init.d/sgu_script restart", "脚本启动失败，请重启路由器或使用命令【/etc/init.d/sgu_script restart】手动启动脚本")
                .startWithCheck();
        return message.getKey() ? new SimpleEntry<>(true, "安装成功") : message;
    }

    /**
     * 执行卸载命令
     *
     * @return java.lang.String
     * @author MoNo
     * @since 2022/9/6 12:56
     */
    @Override
    public SimpleEntry<Boolean, String> uninstall() {
        if (!isInstall()) {
            return new SimpleEntry<>(false, "尚未安装SGU-Script");
        }
        shellUtils.execCommandList()
                .add("/etc/init.d/sgu_script stop")
                .add("/etc/init.d/sgu_script disable")//删除脚本自启动
                .add("rm -f /etc/init.d/sgu_script")//删除脚本
                .add("rm -f /var/log/sgu_script.log")//删除错误日志
                .add("service ua2f stop")//停止ua2f
                .add("service ua2f disable")//取消ua2f的自启动
                .add("cat /etc/firewall.user | sed -i '/# Modify the ttl./d' /etc/firewall.user")//删除防火墙配置
                .add("cat /etc/firewall.user | sed -i '/iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64/d' /etc/firewall.user")
                .add("/etc/init.d/firewall reload")
                .startWithoutCheck();
        return new SimpleEntry<>(true, "卸载成功");
    }

    /**
     * 输出错误日志
     *
     * @return java.lang.String
     * @author MoNo
     * @since 2022/9/9 11:56
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
