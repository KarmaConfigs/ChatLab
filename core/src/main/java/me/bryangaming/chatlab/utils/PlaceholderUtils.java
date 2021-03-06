package me.bryangaming.chatlab.utils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.SupportManager;
import me.bryangaming.chatlab.managers.commands.ChatManager;
import me.bryangaming.chatlab.managers.commands.TagsManager;
import me.bryangaming.chatlab.utils.text.TextUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlaceholderUtils {

    private static SenderManager senderManager;
    private static SupportManager supportManager;
    private static ChatManager chatManager;
    private static TagsManager tagsManager;

    private static Configuration config;
    private static Configuration formatsFile;
    private static Configuration filtersFile;

    public PlaceholderUtils(PluginService pluginService) {
        senderManager = pluginService.getPlayerManager().getSender();
        supportManager = pluginService.getSupportManager();
        chatManager = pluginService.getPlayerManager().getChatManager();
        tagsManager = pluginService.getPlayerManager().getTagsManager();

        config = pluginService.getFiles().getConfigFile();
        formatsFile = pluginService.getFiles().getFormatsFile();
        filtersFile = pluginService.getFiles().getFiltersFile();
    }

    public static String replaceAllVariables(Player player, String string) {

        // Coming soon.. string = replaceItemVariables(player, string);
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
        string = string.replace('??', '&');

        return string;

    }


    private static String replaceEmojis(String string) {

        for (String emojiPath : filtersFile.getStringList("message.emojis")) {
             string = string.replace(emojiPath.split(";")[0], emojiPath.split(";")[1]);
        }

        return string;
    }

    public static String replacePluginVariables(String string) {
        for (String keys : config.getConfigurationSection("options.replacer").getKeys(false)) {
            string = string.replace(config.getString("options.replacer." + keys + ".variable"),
                    config.getString("options.replacer." + keys + ".format"));
        }

        return string
                .replace("%newline%", "\n");

    }

    private static String replaceItemVariables(Player player, String string) {

        if (formatsFile.getString("chat-format.inventory.item-val") == null) {
            return string;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType() == Material.AIR) {
            return string.replace(formatsFile.getString("chat-format.inventory.item-val"), "");
        }

            List<String> playerMainItems = BukkitUtils.convertItemDataInText(itemStack);

            String itemPath = "<hover:show_text:" + String.join("%newline%", playerMainItems) + ">[" + itemStack.getType().name() + "]";

            if (senderManager.hasPermission(player, "chat-format", "color")) {
                return string.replace(formatsFile.getString("chat-format.inventory.item-val"), itemPath);
            }
            return string.replace(formatsFile.getString("chat-format.inventory.item-val"), "</pre>" + itemPath + "<pre>");
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
        return tagsManager.replaceTagsVariables(player, string);
    }

    public static String replaceDiscordVariables(Member member, String text){
        return text
                .replace("%player%", member.getNickname())
                .replace("%role%", member.getRoles().get(member.getRoles().size() - 1).getName())
                .replace("%playercolor%", String.valueOf(member.getColor().getRGB()));

    }
}
