package cn.rabig.tools.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author MoNo
 * @since 2022/9/8 20:03
 **/
public class GuiUtils {
    /**
     * 自定义弹窗
     *
     * @param title   [java.lang.String]
     * @param content [java.lang.String]
     * @param Width   [int]
     * @param Height  [int]
     * @return javafx.scene.control.Alert
     * @author MoNo
     * @since 2022/10/12 21:52
     */
    public static Alert setAlert(String title, String content, int Width, int Height) {
        Alert alert = new Alert(Alert.AlertType.NONE, content);
        //添加标题
        alert.setTitle(title);
        //添加图标
        Stage window = (Stage) alert.getDialogPane().getScene().getWindow();
        window.getIcons().add(new Image(ResourceUtil.getStreamSafe("icon/icon.png")));
        //设置宽高
        alert.getDialogPane().setPrefWidth(Width);
        alert.getDialogPane().setPrefHeight(Height);
        return alert;
    }

    /**
     * 自定义是/否弹窗
     *
     * @param message [java.lang.String]
     * @return javafx.scene.control.Alert
     * @author MoNo
     * @since 2022/10/12 21:56
     */
    public static Alert whetherAlert(String message) {
        Alert confirm = setAlert("提示", message, 350, 100);
        confirm.getDialogPane().getButtonTypes().add(ButtonType.NEXT);
        Button yesButton = (Button) confirm.getDialogPane().lookupButton(ButtonType.NEXT);
        yesButton.setText("是");
        confirm.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Button noButton = (Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL);
        noButton.setText("否");
        return confirm;
    }
}
