package cn.rabig.gui.main;

import javafx.application.Application;

/**
 * @author MoNo
 * @since 2022/9/4 18:09
 **/
public class Launch {
    /**
     * javafx应用程序需要添加额外参数指定javafx-sdk路径才能运行
     * 于是我们通过非继承Application类的的方法来调用launch()方法
     * 当然也可以通过javafx:run指令调用maven插件来运行，但请添加对应的插件
     * 由于这种方式启动会创建一个匿名内部类，因此会报一个警报，但不影响程序运行
     *
     * @param args [java.lang.String]
     * @return void
     * @author MoNo
     * @since 2022/9/6 18:49
     */
    public static void main(String[] args) {
        Application.launch(GuiApp.class);
    }
}
