package cn.rabig.gui.controller;

import cn.rabig.tools.utils.CommonUtils;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        install.setContent(CommonUtils.loadFXML("login"));
        uninstall.setContent(CommonUtils.loadFXML("login"));
    }

    public void click() {
        tabPane.getSelectionModel().select(install);
    }
}
