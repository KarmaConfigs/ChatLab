package atogesputo.bryangaming.chatlab;

import atogesputo.bryangaming.chatlab.data.JQFormat;
import atogesputo.bryangaming.chatlab.data.UserData;
import atogesputo.bryangaming.chatlab.debug.DebugLogger;
import atogesputo.bryangaming.chatlab.utils.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CacheManager {

    private final Map<UUID, List<String>> ignoreCache = new HashMap<>();
    private final Map<String, Configuration> listConfig = new HashMap<>();

    private final Map<UUID, UserData> userData = new HashMap<>();
    private final Map<String, JQFormat> getJQFormat = new HashMap<>();

    public CacheManager(PluginService pluginService) {

        DebugLogger debug = pluginService.getLogs();
        debug.log("Configuration loaded!");
        debug.log("Playeruuid loaded!");

    }

    public Map<String, JQFormat> getJQFormats() {
        return getJQFormat;
    }

    public Map<UUID, UserData> getUserDatas() {
        return userData;
    }

    public Map<UUID, List<String>> getIgnorelist() {
        return ignoreCache;
    }

    public Map<String, Configuration> getConfigFiles() {
        return listConfig;
    }

}