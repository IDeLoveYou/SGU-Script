package cn.rabig.cli.controller;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

/**
 * @author MoNo
 * @since 2022/9/6 18:32
 **/
@SuppressWarnings("unused")
public interface BaseSystem {
    List<String> systemList = List.of("Openwrt");

    SimpleEntry<Boolean, String> check();

    SimpleEntry<Boolean, String> install(String username, String password, String ip, String mode);

    SimpleEntry<Boolean, String> uninstall();

    boolean isInstall();

    SimpleEntry<Boolean, String> log();

}
