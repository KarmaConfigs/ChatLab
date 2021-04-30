package me.bryangaming.chatlab.utils.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.HookModel;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.Bukkit;

public class ProtocolHook implements HookModel {

    private final PluginService pluginService;

    private ProtocolManager protocolSupport;

    public ProtocolHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            pluginService.getPlugin().getLogger().info("Addons - ProtocolLib not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - ProtocolLib not enabled! Disabling support..", LoggerTypeEnum.ERROR);
            return;
        }

        if (!TextUtils.isAllowedHooked("ProtocolLib")){
            return;
        }

        pluginService.getPlugin().getLogger().info("Addons - Loading ProtocolLib..");
        protocolSupport = ProtocolLibrary.getProtocolManager();

        pluginService.getPlugin().getLogger().info("Addons - ProtocolLib enabled! Enabling support..");
        pluginService.getLogs().log("Addons - ProtocolLib enabled! Enabling support...");
    }

    public ProtocolManager getManager() {
        return protocolSupport;
    }
}
