package me.nick.proxyspoof.common.commands;

import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import me.nick.proxyspoof.common.mojang.MojangApi;
import me.nick.proxyspoof.common.mojang.UUIDResponse;

import java.util.ArrayList;
import java.util.List;

public class SpoofCommandBase
{

    public void execute(SettingsManager settingsManager, AbstractExecutor executor, String[] args)
    {
        // No args
        if (args.length < 1)
        {
            messageHelp(executor);
            return;
        }

        // One arg -- clearcache
        if (args[0].equalsIgnoreCase("clearcache") || args[0].equalsIgnoreCase("resetcache"))
        {
            MojangApi.clearCache();
            executor.sendFormattedMessage("&6Reset skin cache!");
            return;
        }

        // Main spoof command
        if ((!executor.isPlayer() && args.length < 3) || (executor.isPlayer() && args.length < 2))
        {
            messageHelp(executor);
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
            targetPlayer = settingsManager.getRealUser(executor.getName());
            setting = args[0];
            data = args[1];
        }

        SpoofedSettings settings = settingsManager.getPlayerSettings(targetPlayer);
        switch (setting.toLowerCase())
        {
            case "player":
                settings.setSpoofedName(data);

                UUIDResponse uuidResponse = MojangApi.getUUID(data);
                settings.setSpoofedId(uuidResponse);
                if (uuidResponse.isOnlineMode())
                    settings.setSpoofedName(uuidResponse.getName());
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
                executor.sendFormattedMessage("&6Unknown setting &c\"" + setting + "\"");
                return;
        }

        executor.sendFormattedMessage("&6Updated spoofed &c" + setting + "&6 setting to &c" + data);
    }

    public List<String> tabComplete(AbstractExecutor executor, String[] args)
    {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("hi");

        return suggestions;
    }

    protected void messageHelp(AbstractExecutor executor)
    {
        executor.sendFormattedMessage("&c&lSpoof Usage");
        executor.sendFormattedMessage("&6/spoof [player] <player(name/id/skin)/ip>[:raw] <data>");
        executor.sendFormattedMessage("&6/spoof clearcache");
    }
}
