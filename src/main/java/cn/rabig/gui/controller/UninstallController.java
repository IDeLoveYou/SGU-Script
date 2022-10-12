package cn.rabig.gui.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.rabig.tools.utils.CommonUtils;
import cn.rabig.tools.utils.GuiUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author MoNo
 * @since 2022/9/6 22:51
 **/
public class UninstallController implements Initializable {
    public AnchorPane uninstallPane;
    public TextArea logArea;
    public Button uninstallButton;
    private Object system;

    /**
     * 页面加载完成后执行
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        system = ReflectUtil.newInstance(CommonUtils.getClassForName("cn.rabig.cli.controller." + LoginController.systemInfo), LoginController.shellUtils);
        //每10秒初始化页面视图(刷新日志文件)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                initView();
            }
        }, 0, 5000);
    }

    /**
     * 初始化界面信息
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:45
     */
    private void initView() {
        boolean isInstall = ReflectUtil.invoke(system, "isInstall");
        //设置卸载按钮
        if (isInstall) {//已经安装
            uninstallButton.setDisable(false);//设置卸载按钮可用
            //设置日志文件
            AbstractMap.SimpleEntry<Boolean, String> log = ReflectUtil.invoke(system, "log");
            if (log.getKey()) {//有日志
                logArea.setText(log.getValue());
            } else {
                logArea.setText("暂无错误日志");
            }
        } else {
            logArea.setText("尚未安装SGU-Script");
            uninstallButton.setDisable(true);//设置卸载按钮不可用
        }
    }

    /**
     * 执行卸载程序
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:45
     */
    public void uninstall() {
        Alert uninstallAlert = GuiUtils.setAlert("提示", "正在卸载", 200, 80);
        uninstallAlert.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        uninstallAlert.show();
        new Thread(() -> {
            AbstractMap.SimpleEntry<Boolean, String> uninstall = ReflectUtil.invoke(system, "uninstall");
            Platform.runLater(() -> {
                uninstallAlert.close();
                Alert infoAlert = GuiUtils.setAlert("提示", uninstall.getValue(), 200, 80);
                infoAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                Button unInstallOK = (Button) infoAlert.getDialogPane().lookupButton(ButtonType.OK);
                unInstallOK.setOnAction(event -> {
                    //重新初始化页面视图
                    initView();
                });
                infoAlert.showAndWait();
            });
        }).start();
    }

    /**
     * 添加上一步的点击事件
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:46
     */
    @SuppressWarnings("DuplicatedCode")
    public void previous() {
        Alert confirm = GuiUtils.whetherAlert("是否退出登录");
        Button yesButton = (Button) confirm.getDialogPane().lookupButton(ButtonType.NEXT);
        yesButton.setOnAction(event -> {
            confirm.close();
            TabPane tabPane = (TabPane) uninstallPane.getScene().lookup("#tabPane");
            tabPane.getTabs().get(1).setContent(CommonUtils.loadFXML("login"));
            tabPane.getTabs().get(2).setContent(CommonUtils.loadFXML("login"));
        });
        confirm.showAndWait();
    }
}
