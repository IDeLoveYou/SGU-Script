package cn.rabig.gui.main;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.rabig.tools.utils.CommonUtils;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class GuiApp extends Application {
    @Override
    public void start(Stage stage) {
        stage.setOnCloseRequest(event -> System.exit(0));
        Parent home = CommonUtils.loadFXML("home");
        Scene scene = new Scene(home);
        scene.setRoot(home);
        stage.setTitle("SGU-Script");
        stage.getIcons().add(new Image(ResourceUtil.getStreamSafe("icon/icon.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
