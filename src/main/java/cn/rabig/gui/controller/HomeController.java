package cn.rabig.gui.controller;

import cn.rabig.tools.utils.CommonUtils;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author MoNo
 * @since 2022/9/3 15:33
 **/
public class HomeController implements Initializable {
    public TabPane tabPane;
    public Tab install;
    public Tab uninstall;
    public TextArea welcomeInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String welcomeInfo = "\n" +
                "SGU-Script是一款免费开源的路由器锐捷认证设置程序，本质是自动配置登录脚本\n\n" +
                "主要是为了应对2022年韶关学院新上网认证，目前只支持搭载OpenWrt的路由器配置\n\n" +
                "并且Openwrt需已安装curl或wget模块，未安装的设备请先自行安装该模块\n\n" +
                "你也可以自行克隆项目，并添加对其他系统的支持\n\n" +
                "SGU-Script会实时监控你的网络状况，在掉线的时候进行自动重连，但并不能阻止共享上网的检查\n\n" +
                "由于阻止共享上网的监测较为复杂，需你自行编译固件，并添加相应的插件支持\n\n" +
                "SGU-Script只为学习研发，若你对该程序有异议，可至项目地址提起issue\n\n" +
                "若你使用本程序则默认遵循MIT license，祝你使用愉快\n\n" +
                "Gitee链接:\t" + CommonUtils.getProps("gitee") + "\n\n" +
                "Github链接:\t" + CommonUtils.getProps("github");
        this.welcomeInfo.setText(welcomeInfo);
        install.setContent(CommonUtils.loadFXML("login"));
        uninstall.setContent(CommonUtils.loadFXML("login"));
    }

    public void click() {
        this.tabPane.getSelectionModel().select(install);
    }

}
