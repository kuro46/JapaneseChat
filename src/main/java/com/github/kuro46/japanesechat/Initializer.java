package com.github.kuro46.japanesechat;

import org.bukkit.plugin.java.JavaPlugin;

public final class Initializer extends JavaPlugin {

    @Override
    public void onEnable() {
        JapaneseChat.init(this);
    }
}
