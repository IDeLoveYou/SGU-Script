package cn.rabig.cli.controller;

import java.util.AbstractMap.SimpleEntry;

/**
 * 未完善
 *
 * @author MoNo
 * @since 2022/9/6 18:20
 **/
@SuppressWarnings("unused")
public class Padavan implements BaseSystem {
    @Override
    public SimpleEntry<Boolean, String> check() {
        return null;
    }

    @Override
    public boolean isInstall() {
        return false;
    }

    @Override
    public SimpleEntry<Boolean, String> install(String username, String password, String mode) {
        return null;
    }

    @Override
    public SimpleEntry<Boolean, String> uninstall() {
        return null;
    }

    @Override
    public SimpleEntry<Boolean, String> log() {
        return null;
    }
}
