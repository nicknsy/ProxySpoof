package me.nick.proxyspoof.common.commands;

import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import me.nick.proxyspoof.common.mojang.MojangApi;
import me.nick.proxyspoof.common.utils.NetworkUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class SpoofCommandBase
{

    private static List<String> spoofSuggestions = List.of("player", "name", "uuid", "ip", "skin");

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
                settings.setSpoofedIp(NetworkUtil.getRandomIp());

                MojangApi.getUUID(data).whenComplete((uuidResponse, throwable) ->
                {
                    settings.setSpoofedId(uuidResponse);
                    if (uuidResponse.isOnlineMode())
                    {
                        settings.setSpoofedName(uuidResponse.getName());
                        MojangApi.getSkin(uuidResponse.getId()).whenComplete((loginResponse, throwable1) ->
                        {
                            settings.setSpoofedProfile(loginResponse);
                            messageComplete(executor, setting, uuidResponse.getName());
                        });
                    }
                    else
                    {
                        messageComplete(executor, setting, uuidResponse.getName());
                    }
                });
                break;
            case "name":
                settings.setSpoofedName(StringEscapeUtils.unescapeJava(data));
                messageComplete(executor, setting, data);
                break;
            case "ip":
                settings.setSpoofedIp(data);
                messageComplete(executor, setting, data);
                break;
            case "id":
            case "uuid":
                MojangApi.getUUID(data).whenComplete((uuidResponse, throwable) ->
                {
                    settings.setSpoofedId(uuidResponse);
                    messageComplete(executor, setting, data);
                });
                break;
            case "skin":
                MojangApi.getUUID(data).whenComplete((uuidResponse, throwable) ->
                {
                    if (uuidResponse.isOnlineMode())
                    {
                        MojangApi.getSkin(uuidResponse.getId()).whenComplete((loginResponse, throwable1) ->
                        {
                            settings.setSpoofedProfile(loginResponse);
                            messageComplete(executor, setting, data);
                        });
                    }
                    else
                    {
                        executor.sendFormattedMessage("&6Couldn't fetch that player's skin!");
                    }
                });
                break;
            default:
                executor.sendFormattedMessage("&6Unknown setting &c\"" + setting + "\"");
                return;
        }
    }

    public List<String> tabComplete(AbstractExecutor executor, String[] args)
    {
        List<String> suggestions = new ArrayList<>();
        boolean isPlayer = executor.isPlayer();

        switch (args.length)
        {
            case 0:
            case 1:
                if (isPlayer)
                    return spoofSuggestions;
                else
                    suggestions.add("<player>");
                break;
            case 2:
                if (isPlayer)
                    return getSubSuggestions(args[0]);
                else
                    return spoofSuggestions;
            case 3:
                if (!isPlayer) return getSubSuggestions(args[1]);
                break;
        }

        return suggestions;
    }

    private List<String> getSubSuggestions(String subCommand)
    {
        List<String> suggestions = new ArrayList<>();

        switch (subCommand)
        {
            case "ip":
                suggestions.add(NetworkUtil.getRandomIp());
                break;
        }

        return suggestions;
    }

    private void messageHelp(AbstractExecutor executor)
    {
        executor.sendFormattedMessage("&c&lSpoof Usage");
        executor.sendFormattedMessage("&6/spoof [player] <player(name/id/skin)/ip>[:raw] <data>");
        executor.sendFormattedMessage("&6/spoof clearcache");
    }

    private void messageComplete(AbstractExecutor executor, String setting, String data)
    {
        executor.sendFormattedMessage("&6Updated spoofed &c" + setting + "&6 setting to &c" + data);
    }
}
