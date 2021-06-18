package me.bryangaming.chatlab.utils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SupportManager;
import me.bryangaming.chatlab.managers.commands.ChatManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    private static SupportManager supportManager;
    private static ChatManager chatManager;

    private static Configuration config;
    private static Configuration filtersFile;

    public PlaceholderUtils(PluginService pluginService) {
        supportManager = pluginService.getSupportManager();
        chatManager = pluginService.getPlayerManager().getChatManager();

        config = pluginService.getFiles().getConfigFile();
        filtersFile = pluginService.getFiles().getFiltersFile();
    }

    public static String replaceAllVariables(Player player, String string) {

        string = replaceEmojis(string);
        string = replaceTags(player, string);
        string = replacePlayerVariables(player, string);

        string = replaceVaultVariables(player, string);
        string = replacePAPIVariables(player, string);
        string = replacePluginVariables(string);

        string = setToCenter(string);
        return string;
    }
    private static String setToCenter(String string){
        if (string.startsWith("[CENTER]")){
            String stringWithOutVariable = string.substring(8);

            return StringUtils.center(stringWithOutVariable, config.getInt("options.center-space"));
        }
        return string;
    }

    private static String replacePlayerVariables(Player player, String string) {
        return string
                .replace("%world%", player.getWorld().getName())
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
    }

    private static String replacePAPIVariables(Player player, String string) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return string;
        }

        string = PlaceholderAPI.setPlaceholders(player, string);
        string = string.replace('§', '&');

        return string;

    }


    private static String replaceEmojis(String string) {

        for (String emojiPath : filtersFile.getStringList("messages.emojis")) {
             string = string.replace(emojiPath.split(";")[0], emojiPath.split(";")[1]);
        }

        return string;
    }

    private static String replacePluginVariables(String string) {
        for (String keys : config.getConfigurationSection("options.replacer").getKeys(false)) {
            string = string.replace(config.getString("options.replacer." + keys + ".variable"),
                    config.getString("options.replacer." + keys + ".format"));
        }

        return string
                .replace("%newline%", "\n");

    }

    private static String replaceVaultVariables(Player player, String string) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            return string;
        }

        if (!TextUtils.isHookEnabled("Vault")){
            return string;
        }

        Permission permission = supportManager.getVaultSupport().getPermissions();
        Chat chat = supportManager.getVaultSupport().getChat();

        if (chat == null) {
            return string;
        }

        return string.replace("%prefix%", chat.getPlayerPrefix(player)
                .replace("%suffix%", chat.getPlayerSuffix(player))
                .replace("%group%", permission.getPrimaryGroup(player)));
    }

    private static String replaceTags(Player player, String string) {
        return chatManager.replaceTagsVariables(player, string);
    }

    public static String replaceDiscordVariables(Member member, String text){
        return text
                .replace("%player%", member.getNickname())
                .replace("%role%", member.getRoles().get(member.getRoles().size() - 1).getName())
                .replace("%playercolor%", String.valueOf(member.getColor().getRGB()));

    }
}