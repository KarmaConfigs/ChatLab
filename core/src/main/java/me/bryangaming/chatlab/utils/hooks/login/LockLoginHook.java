package me.bryangaming.chatlab.utils.hooks.login;

import eu.locklogin.api.module.PluginModule;
import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.listeners.login.plugin.PlayerLockLoginEvent;
import org.bukkit.Bukkit;

public class LockLoginHook extends PluginModule {

    @Override
    public void enable() {
        PluginService service = new PluginService((ChatLab) Bukkit.getServer().getPluginManager().getPlugin("ChatLab"));

        getConsole().sendMessage("&aEnabled ChatLab support for LockLogin");
        getPlugin().registerListener(new PlayerLockLoginEvent(service));
    }

    @Override
    public void disable() {
        stopTasks();

        getConsole().sendMessage("&cDisabling ChatLab support for LockLogin");
    }
}
