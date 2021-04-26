package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.SocialSpyEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.registry.FileLoader;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ReplyCommand implements CommandClass {

    private final PluginService pluginService;

    public ReplyCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = {"reply", "r"})
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String message) {

        FileLoader files = pluginService.getFiles();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration lang = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (message.isEmpty()) {
            playerMethod.sendMessage(sender, lang.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("reply", "<message>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData playerCache = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!playerCache.hasRepliedPlayer()) {
            playerMethod.sendMessage(sender, lang.getString("error.no-reply"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player target = Bukkit.getPlayer(playerCache.getRepliedPlayer());

        if (message.equalsIgnoreCase("-sender")) {
            playerMethod.sendMessage(sender, command.getString("commands.msg-reply.talked")
                    .replace("%player%", target.getName()));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (command.getBoolean("commands.msg-reply.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
                return true;
            }
        }

        if (!playerMethod.hasPermission(sender, "color.commands")) {
            message = "<pre>" + message + "</pre>";
        }

        playerMethod.sendMessage(sender, command.getString("commands.msg-reply.player")
                        .replace("%player%", sender.getName())
                        .replace("%arg-1%", target.getName())
                , message);
        playerMethod.sendSound(sender, SoundEnum.RECEIVE_MSG);

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (!(ignoredlist.contains(target.getName()))) {
            playerMethod.sendMessage(target, command.getString("commands.msg-reply.player")
                            .replace("%player%", sender.getName())
                            .replace("%arg-1%", target.getName())
                    , message);

            UserData targetCache = pluginService.getCache().getUserDatas().get(target.getUniqueId());

            targetCache.setRepliedPlayer(playeruuid);
            playerMethod.sendSound(sender, SoundEnum.RECEIVE_MSG);
        }

        String socialspyFormat = command.getString("commands.socialspy.spy")
                .replace("%player%", sender.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", message);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));
        return true;
    }
}


