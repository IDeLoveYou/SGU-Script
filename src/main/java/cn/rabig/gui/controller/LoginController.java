package cn.rabig.gui.controller;

import cn.rabig.cli.controller.BaseSystem;
import cn.rabig.tools.utils.CommonUtils;
import cn.rabig.tools.utils.GuiUtils;
import cn.rabig.tools.utils.ShellUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author MoNo
 * @since 2022/9/3 21:18
 **/
public class LoginController implements Initializable {
    public AnchorPane loginPane;
    public TextField host;
    public TextField port;
    public TextField username;
    public PasswordField password;
    public Label hostInfo;
    public Label portInfo;
    public Label userNameInfo;
    public Label passWordInfo;
    public Label error;
    public static ShellUtils shellUtils;
    public static String systemInfo;
    private final Map<String, Boolean> infoFlag;
    public Button loginButton;

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
        //初始化提示信息
        initTips();
        //检查表单
        checkForm();
    }

    /**
     * 检查表单数据
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:40
     */
    private void checkForm() {
        //检查主机号
        checkHost();
        //检查端口号
        checkPort();
        //检查用户名
        checkUserName();
        //检查密码
        checkPassWord();
    }

    /**
     * 初始化框体提示信息
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:41
     */
    public void initTips() {
        host.setTooltip(new Tooltip("请输入路由器的主机地址"));
        port.setTooltip(new Tooltip("请输入SSH端口"));
        username.setTooltip(new Tooltip("请输入用户名"));
        password.setTooltip(new Tooltip("请输入密码"));
    }

    /**
     * 检查主机号
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:41
     */
    public void checkHost() {
        infoFlag.put("host", false);
        host.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!host.getText().isBlank() && !host.getText().contains(" ")) {
                    hostInfo.setText("");
                    infoFlag.put("host", true);
                } else {
                    hostInfo.setText("请输入正确主机号");
                }
            }
        });
    }

    /**
     * 检查端口号
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:41
     */
    public void checkPort() {
        port.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 5 || !newValue.matches("\\d*")) {
                port.setText(oldValue);
            }
        });
        infoFlag.put("port", false);
        port.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!port.getText().isBlank()) {
                    portInfo.setText("");
                    infoFlag.put("port", true);
                } else {
                    portInfo.setText("请输入正确端口号");
                }
            }
        });
    }

    /**
     * 检查用户名
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:41
     */
    public void checkUserName() {
        infoFlag.put("username", false);
        username.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!username.getText().isBlank() && username.getText().length() < 50 && !username.getText().contains(" ")) {
                    userNameInfo.setText("");
                    infoFlag.put("username", true);
                } else {
                    userNameInfo.setText("请输入正确用户名");
                }
            }
        });
    }

    /**
     * 检查密码
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:41
     */
    public void checkPassWord() {
        infoFlag.put("password", false);
        password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!password.getText().isBlank() && password.getText().length() < 100 && !password.getText().contains(" ")) {
                    passWordInfo.setText("");
                    infoFlag.put("password", true);
                } else {
                    passWordInfo.setText("请输入正确密码");
                }
            }
        });
    }

    /**
     * 设置检测路由器环境中弹窗
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:42
     */
    public void waitCheck() {
        Alert check = GuiUtils.setAlert("提示", "检测路由器环境中", 200, 80);
        check.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        check.show();
        new Thread(() -> {
            TabPane tabPane = (TabPane) loginPane.getScene().lookup("#tabPane");
            Parent install = CommonUtils.loadFXML("install");
            Parent uninstall = CommonUtils.loadFXML("uninstall");
            if (check.isShowing()) {
                Platform.runLater(() -> {
                    check.close();
                    tabPane.getTabs().get(1).setContent(install);
                    tabPane.getTabs().get(2).setContent(uninstall);
                });
            }
        }).start();

    }

    /**
     * 设置登录成功弹窗（选择路由器固件类型->并检查路由器环境）
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:42
     */
    public void chooseSystem() {
        Alert success = GuiUtils.setAlert("登录成功", "请选择路由器固件，并点击下一步继续", 350, 80);
        //禁用默认窗口关闭事件
        success.setOnCloseRequest(event -> {
        });
        //创建一个下拉框
        ComboBox<String> systemComboBox = new ComboBox<>();
        BaseSystem.systemList.forEach(system -> {
            systemComboBox.getItems().add(system);//添加可选项
        });
        systemComboBox.getSelectionModel().select(0);//默认选择第一个
        success.setGraphic(systemComboBox);//将idComb添加到success
        //添加上一步，下一步按钮
        success.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        success.getDialogPane().getButtonTypes().add(ButtonType.NEXT);
        //添加下一步按钮的点击事件
        Button next = (Button) success.getDialogPane().lookupButton(ButtonType.NEXT);
        next.setOnAction(event -> {
            //更新lib状态
            systemInfo = systemComboBox.getSelectionModel().getSelectedItem();
            waitCheck();
        });
        //等待操作
        success.showAndWait();
    }

    /**
     * 设置登录中弹窗
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:42
     */
    public void showAlert() {
        Alert login = GuiUtils.setAlert("提示", "登录中", 200, 80);
        //添加取消按钮
        login.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        //显示弹窗
        login.show();
        //弹窗是否显示
        if (login.isShowing()) {
            //在新线程登录ssh
            new Thread(() -> {
                //当ssh成功时
                shellUtils = new ShellUtils(host.getText(), port.getText(), username.getText(), password.getText());
                if (shellUtils.isSuccess()) {
                    //设置登录成功弹窗（选择固件类型->并检查路由器环境）
                    Platform.runLater(() -> {
                        login.close();
                        chooseSystem();
                    });
                    //当ssh失败时
                } else {
                    //设置登录失败弹窗
                    Platform.runLater(() -> {
                        login.setContentText("登录失败，请重新登录");
                        login.getDialogPane().getButtonTypes().set(0, ButtonType.PREVIOUS);
                    });
                }
            }).start();
        }
    }

    /**
     * 登录按钮点击事件
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:42
     */
    public void login() {
        //检查所有参数设置无误
        AtomicBoolean flag = new AtomicBoolean(true);
        infoFlag.forEach((k, v) -> {
            if (!v) {
                flag.set(false);
            }
        });
        //设置登录信息
        if (!flag.get()) {
            error.setText("请完善登录信息");
        } else {
            error.setText("");
            showAlert();
        }
    }

    /**
     * 添加键盘确认键监听
     *
     * @param keyEvent [javafx.scene.input.KeyEvent]
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:43
     */
    public void enter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            loginButton.requestFocus();
            login();
        }
    }
}
