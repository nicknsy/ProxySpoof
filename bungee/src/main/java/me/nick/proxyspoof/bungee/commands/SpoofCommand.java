package me.nick.proxyspoof.bungee.commands;

import me.nick.proxyspoof.bungee.BungeePlugin;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import me.nick.proxyspoof.common.mojang.MojangApi;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

public class SpoofCommand extends Command
{

    private SettingsManager settingsManager;

    public SpoofCommand(BungeePlugin plugin)
    {
        super("spoof");
        this.settingsManager = plugin.getSettingsManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        /*
        // No args
        if (args.length < 1)
        {
            messageHelp(sender);
            return;
        }

        // One arg -- clearcache
        if (args[0].equalsIgnoreCase("clearcache") || args[0].equalsIgnoreCase("resetcache"))
        {
            MojangApi.clearCache();
            sender.sendMessage(ChatUtil.formatColor("&6Reset skin cache!"));
            return;
        }

        // Main spoof command
        if ((sender instanceof ConsoleCommandSender && args.length < 3) || (sender instanceof ProxiedPlayer && args.length < 2))
        {
            messageHelp(sender);
            return;
        }

        String targetPlayer;
        String setting;
        String data;

        if (args.length >= 3)
        {
            targetPlayer = args[0];
            setting = args[1];
            data = args[2];
        }
        else
        {
            targetPlayer = settingsManager.getRealUser(sender.getName());
            setting = args[0];
            data = args[1];
        }

        SpoofedSettings settings = settingsManager.getPlayerSettings(targetPlayer);

        // TODO: Common
        switch (setting.toLowerCase())
        {
            case "player":
                settings.setSpoofedName(data);
                settings.setSpoofedId(MojangApi.getUUID(data));
                // TODO: Check online before skin set
                settings.setSpoofedSkin(MojangApi.getSkin(settings.getSpoofedId().getId()));
                break;
            case "name":
                settings.setSpoofedName(data);
                break;
            case "ip":
                settings.setSpoofedIp(data);
                break;
            case "id":
            case "uuid":
                settings.setSpoofedId(MojangApi.getUUID(data));
                break;
            case "skin":
                settings.setSpoofedSkin(MojangApi.getSkin(MojangApi.getUUID(data).getId()));
                break;
            default:
                sender.sendMessage(ChatUtil.formatColor("&6Unknown setting &c\"" + setting + "\""));
                return;
        }

        sender.sendMessage(ChatUtil.formatColor("&6Updated spoofed &c" + setting + "&6 setting to &c" + data));
        */
    }

    private void messageHelp(CommandSender sender)
    {
        /*
        sender.sendMessage(ChatUtil.formatColor("&c&lSpoof Usage"));
        sender.sendMessage(ChatUtil.formatColor("&6/spoof [player] <player(name/id/skin)/ip> <data>"));
        sender.sendMessage(ChatUtil.formatColor("&6/spoof clearcache"));
         */
    }
}
