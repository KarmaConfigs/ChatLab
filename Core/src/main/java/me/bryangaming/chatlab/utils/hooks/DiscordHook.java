package me.bryangaming.chatlab.utils.hooks;

import github.scarsz.discordsrv.DiscordSRV;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.listeners.DiscordSrvListener;
import org.bukkit.Bukkit;

public class DiscordHook {

    private final PluginService pluginService;

    public DiscordHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            pluginService.getPlugin().getLogger().info("Addons - DiscordSRV not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - DiscordSRV not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        DiscordSRV.api.subscribe(new DiscordSrvListener(pluginService));
        pluginService.getPlugin().getLogger().info("Addons - Loaded DiscordSRV..");

    }
}
