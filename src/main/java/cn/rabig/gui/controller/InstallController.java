package cn.rabig.gui.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.rabig.tools.utils.CommonUtils;
import cn.rabig.tools.utils.GuiUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author MoNo
 * @since 2022/9/7 18:46
 **/
public class InstallController implements Initializable {
    public RadioButton curl;
    public RadioButton wget;
    public Label ua2f;
    public TextField username;
    public TextField password;
    public Label userNameInfo;
    public Label passWordInfo;
    public Label error;
    public AnchorPane installPane;
    public Button installButton;
    private Object system;
    private ToggleGroup toggleGroup;
    private final Map<String, Boolean> infoFlag;

    {
        infoFlag = new HashMap<>();
    }

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
        //初始化检查信息
        initCheckInfo();
        //初始化页面视图
        initView();
        //初始化提示信息
        initTips();
        //检查表单
        checkForm();
    }

    /**
     * 检查表单
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:37
     */
    private void checkForm() {
        //检查宽带账号
        checkUsername();
        //检查宽带密码
        checkPassword();
    }

    /**
     * 检查宽带密码
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:37
     */
    private void checkPassword() {
        infoFlag.put("password", false);
        password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!password.getText().isBlank() && password.getText().length() < 50 && !password.getText().contains(" ")) {
                    passWordInfo.setText("");
                    infoFlag.put("password", true);
                } else {
                    passWordInfo.setText("请输入正确密码");
                }
            }
        });
    }

    /**
     * 检查宽带账号
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:37
     */
    private void checkUsername() {
        infoFlag.put("username", false);
        username.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!username.getText().isBlank() && username.getText().length() < 20 && !username.getText().contains(" ")) {
                    userNameInfo.setText("");
                    infoFlag.put("username", true);
                } else {
                    userNameInfo.setText("请输入正确用户名");
                }
            }
        });
    }

    /**
     * 初始化输入提示
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:38
     */
    public void initTips() {
        curl.setTooltip(new Tooltip("是否支持使用curl登录"));
        wget.setTooltip(new Tooltip("是否支持使用wget登录"));
        ua2f.setTooltip(new Tooltip("是否支持UA2F，更改ua标头，避免被检查共享"));
        username.setTooltip(new Tooltip("请输入宽带账号"));
        password.setTooltip(new Tooltip("请输入宽带密码"));
    }

    /**
     * 通过反射执行固件函数
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:38
     */
    private void initCheckInfo() {
        system = ReflectUtil.newInstance(CommonUtils.getClassForName("cn.rabig.cli.controller." + LoginController.systemInfo), LoginController.shellUtils);
    }

    /**
     * 初始化页面视图
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:38
     */
    private void initView() {
        toggleGroup = new ToggleGroup();
        //初始化多个单选标签
        initRadioButton(curl, (boolean) ReflectUtil.getFieldValue(system, "checkCurl"));
        initRadioButton(wget, (boolean) ReflectUtil.getFieldValue(system, "checkWget"));
        //初始化ua2f检查的label事件
        if ((boolean) ReflectUtil.getFieldValue(system, "checkUA2F")) {
            ua2f.setText("支持");
        }
    }

    /**
     * 初始化单个单选标签
     *
     * @param radioButton [javafx.scene.control.RadioButton]
     * @param enable      [boolean]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:38
     */
    private void initRadioButton(RadioButton radioButton, boolean enable) {
        if (enable) {
            radioButton.setText("支持");
            radioButton.setDisable(false);
            toggleGroup.getToggles().add(radioButton);
            toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
        }
    }

    /**
     * 初始化和安装
     *
     * @param system [java.lang.Object]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:39
     */
    private void initAndInstall(Object system) {
        Alert confirm = GuiUtils.whetherAlert("是否开始安装");
        Button yesButton = (Button) confirm.getDialogPane().lookupButton(ButtonType.NEXT);
        yesButton.setOnAction(event -> {
            confirm.close();
            Alert installing = GuiUtils.setAlert("提示", "正在安装", 200, 80);
            installing.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            installing.show();
            //开始安装
            new Thread(() -> {
                RadioButton mode = (RadioButton) toggleGroup.getSelectedToggle();
                SimpleEntry<Boolean, String> install = ReflectUtil.invoke(system, "install", username.getText(), password.getText(), mode.getId());
                Platform.runLater(() -> {
                    installing.close();
                    Alert result = GuiUtils.setAlert("提示", install.getValue(), 350, 100);
                    result.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    Button unInstallOK = (Button) result.getDialogPane().lookupButton(ButtonType.OK);
                    unInstallOK.setOnAction(events -> {
                        TabPane tabPane = (TabPane) installPane.getScene().lookup("#tabPane");
                        tabPane.getTabs().get(2).setContent(CommonUtils.loadFXML("uninstall"));
                    });
                    result.showAndWait();
                });
            }).start();
        });
        confirm.showAndWait();
    }

    /**
     * 提示安装信息
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:39
     */
    public void confirmAndStart() {
        SimpleEntry<Boolean, String> check = ReflectUtil.invoke(system, "check");
        Alert confirmInfo = GuiUtils.setAlert("提示", check.getValue(), 350, 120);
        if (check.getKey()) {
            //可以安装，并打印提示信息
            //添加上一步，下一步按钮
            confirmInfo.getDialogPane().getButtonTypes().add(ButtonType.NEXT);
            confirmInfo.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            //添加下一步按钮的点击事件
            Button next = (Button) confirmInfo.getDialogPane().lookupButton(ButtonType.NEXT);
            next.setOnAction(event -> Platform.runLater(() -> {
                confirmInfo.close();
                //进入确认阶段
                initAndInstall(system);
            }));
        } else {
            //添加取消按钮
            confirmInfo.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        }
        //显示窗口
        confirmInfo.showAndWait();
    }

    /**
     * 开始检查表单并安装
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:39
     */
    public void install() {
        //检查所有参数设置无误
        AtomicBoolean flag = new AtomicBoolean(true);
        infoFlag.forEach((k, v) -> {
            if (!v) {
                flag.set(false);
            }
        });
        //设置登录信息
        if (!flag.get()) {
            error.setText("请完善宽带信息");
        } else {
            error.setText("");
            confirmAndStart();
        }
    }

    /**
     * 添加上一步的点击事件
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:39
     */
    @SuppressWarnings("DuplicatedCode")
    public void previous() {
        Alert confirm = GuiUtils.whetherAlert("是否退出登录");
        Button yesButton = (Button) confirm.getDialogPane().lookupButton(ButtonType.NEXT);
        yesButton.setOnAction(event -> {
            confirm.close();
            TabPane tabPane = (TabPane) installButton.getScene().lookup("#tabPane");
            tabPane.getTabs().get(1).setContent(CommonUtils.loadFXML("login"));
            tabPane.getTabs().get(2).setContent(CommonUtils.loadFXML("login"));
        });
        confirm.showAndWait();
    }

    /**
     * 添加键盘确认键监听
     *
     * @param keyEvent [javafx.scene.input.KeyEvent]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:40
     */
    public void enter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            installButton.requestFocus();
            install();
        }
    }
}
