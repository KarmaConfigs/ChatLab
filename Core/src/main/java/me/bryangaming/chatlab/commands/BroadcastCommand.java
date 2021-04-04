package atogesputo.bryangaming.chatlab.commands;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.revisor.RevisorManager;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import atogesputo.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import atogesputo.bryangaming.chatlab.managers.click.ClickChatMethod;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import atogesputo.bryangaming.chatlab.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(names = {"broadcast", "bc"})
public class BroadcastCommand implements CommandClass {

    private final PluginService pluginService;

    private final PlayerMessage playerMethod;
    private final ModuleCheck moduleCheck;

    private final Configuration command;
    private final Configuration messages;

    public BroadcastCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.moduleCheck = pluginService.getPathManager();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player sender, @OptArg("") @Text String args) {

        UUID playeruuid = sender.getUniqueId();

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("broadcast", "<message>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", args);

        if (command.getBoolean("commands.broadcast.enable-revisor")) {
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null) {
                return true;
            }
        }

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            playerMethod.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.global")
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
            playerMethod.sendSound(sender, SoundEnum.RECEIVE_BROADCAST);
        }
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "broadcast");
        return true;
    }

    @Command(names = "-click")
    public boolean onClickSubCommand(@Sender Player sender) {
        ClickChatMethod clickChatMethod = pluginService.getPlayerMethods().getChatManagent();

        if (!playerMethod.hasPermission(sender, "commands.broadcast.click")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        clickChatMethod.activateChat(sender.getUniqueId(), false);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "broadcast -click");
        return true;
    }

}