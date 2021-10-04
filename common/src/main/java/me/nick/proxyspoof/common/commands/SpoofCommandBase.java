package me.nick.proxyspoof.common.commands;

import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import me.nick.proxyspoof.common.mojang.MojangApi;
import me.nick.proxyspoof.common.mojang.UUIDResponse;
import me.nick.proxyspoof.common.utils.NetworkUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpoofCommandBase
{

    private List<String> subCommands = List.of("player", "name", "uuid", "ip", "skin", "host");
    private Map<String, List<String>> subModifiers = Map.of(
            "player", List.of("offline"),
            "host", List.of("default"),
            "name", List.of("raw"),
            "uuid", List.of("raw", "offline"),
            "id", List.of("raw", "offline")
    );

    public void execute(SettingsManager settingsManager, AbstractExecutor executor, String[] args)
    {
        // No args
        if (args.length < 1)
        {
            messageHelp(executor);
            return;
        }

        // One arg commands
        switch (args[0].toLowerCase())
        {
            case "clearcache":
            case "resetcache":
                MojangApi.clearCache();
                executor.sendFormattedMessage("&6Reset skin cache!");
                return;
            case "cleardefaults":
            case "resetdefaults":
                SpoofedSettings.clearDefaults();
                executor.sendFormattedMessage("&6Reset spoof defaults!");
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
        boolean isTargetSelf;
        String modifierString;
        Modifier modifier;

        // Parse args
        if (subCommands.contains(args[0].toLowerCase()))    // Player spoofing themselves
        {
            isTargetSelf = true;
            targetPlayer = settingsManager.getRealUser(executor.getName());
            setting = args[0];
            data = args[1];
            modifierString = args.length > 2 ? args[2] : null;
        }
        else if (args.length >= 3)  // Player spoofing another player
        {
            isTargetSelf = false;
            targetPlayer = args[0];
            setting = args[1];
            data = args[2];
            modifierString = args.length > 3 ? args[3] : null;
        }
        else
        {
            messageHelp(executor);
            return;
        }

        // Get modifier
        if (modifierString != null)
        {
            try
            {
                modifier = Modifier.valueOf(modifierString.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                executor.sendFormattedMessage("&6Unknown modifier &c\"" + modifierString + "\"");
                return;
            }
        }
        else
        {
            modifier = Modifier.NONE;
        }

        SpoofedSettings settings = settingsManager.getPlayerSettings(targetPlayer);
        switch (setting.toLowerCase())
        {
            case "player":
                settings.setSpoofedName(data);
                settings.setSpoofedIp(NetworkUtil.getRandomIp());

                if (modifier == Modifier.OFFLINE)
                {
                    settings.setSpoofedId(MojangApi.getOfflineUUID(data));
                    messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                }
                else
                {
                    MojangApi.getUUID(data).whenComplete((uuidResponse, throwable) ->
                    {
                        settings.setSpoofedId(uuidResponse);
                        if (uuidResponse.isOnlineMode())
                        {
                            settings.setSpoofedName(uuidResponse.getName());
                            MojangApi.getSkin(uuidResponse.getId()).whenComplete((loginResponse, throwable1) ->
                            {
                                settings.setSpoofedProfile(loginResponse);
                                messageComplete(executor, targetPlayer, isTargetSelf, setting, uuidResponse.getName(), modifier);
                            });
                        }
                        else
                        {
                            messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                        }
                    });
                }
                break;
            case "name":
                if (modifier == Modifier.RAW)
                {
                    settings.setSpoofedName(data);
                }
                else
                {
                    settings.setSpoofedName(StringEscapeUtils.unescapeJava(data));
                }
                messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                break;
            case "ip":
                settings.setSpoofedIp(data);
                messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                break;
            case "id":
            case "uuid":
                if (modifier == Modifier.NONE)
                {
                    MojangApi.getUUID(data).whenComplete((uuidResponse, throwable) ->
                    {
                        settings.setSpoofedId(uuidResponse);
                        messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                    });
                }
                else
                {
                    if (modifier == Modifier.OFFLINE)
                    {
                        settings.setSpoofedId(MojangApi.getOfflineUUID(data));
                    }
                    else if (modifier == Modifier.RAW)
                    {
                        settings.setSpoofedId(new UUIDResponse(null, UUID.nameUUIDFromBytes(data.getBytes(StandardCharsets.UTF_8)), false));
                    }
                    messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                }
                break;
            case "skin":
                MojangApi.getUUID(data).whenComplete((uuidResponse, throwable) ->
                {
                    if (uuidResponse.isOnlineMode())
                    {
                        MojangApi.getSkin(uuidResponse.getId()).whenComplete((loginResponse, throwable1) ->
                        {
                            settings.setSpoofedProfile(loginResponse);
                            messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                        });
                    }
                    else
                    {
                        executor.sendFormattedMessage("&6Couldn't fetch that player's skin!");
                    }
                });
                break;
            case "host":
                if (modifier == Modifier.DEFAULT)
                {
                    SpoofedSettings.setDefaultHost(data);
                }
                else
                {
                    settings.setSpoofedHost(data);
                }
                messageComplete(executor, targetPlayer, isTargetSelf, setting, data, modifier);
                break;
            default:
                executor.sendFormattedMessage("&6Unknown setting &c\"" + setting + "\"");
        }
    }

    public List<String> tabComplete(AbstractExecutor executor, String[] args)
    {
        List<String> suggestions = new ArrayList<>();
        // Technically not accurate if target players name is the same as a sub command
        boolean isTargetSelf = executor.isPlayer() && (args.length > 0 && subCommands.contains(args[0]));
        String subCommand = args.length > 0 ? (isTargetSelf ? args[0] : (args.length > 1 ? args[1] : "")) : "";

        switch (args.length)
        {
            case 0:
            case 1:
                if (executor.isPlayer()) suggestions.addAll(subCommands);
                suggestions.add("<player>");
                break;
            case 2:
                if (isTargetSelf)
                    return getSubSuggestions(subCommand);
                else
                    return subCommands;
            case 3:
                if (isTargetSelf && subModifiers.containsKey(subCommand))
                    return subModifiers.get(subCommand);
                else
                    return getSubSuggestions(subCommand);
            case 4:
                if (!isTargetSelf) return subModifiers.get(subCommand);
                break;
        }

        return suggestions;
    }

    private List<String> getSubSuggestions(String subCommand)
    {
        List<String> suggestions = new ArrayList<>();

        if (subCommand.equalsIgnoreCase("ip"))
        {
            suggestions.add(NetworkUtil.getRandomIp());
        }

        return suggestions;
    }

    private void messageHelp(AbstractExecutor executor)
    {
        executor.sendFormattedMessage("&c&lSpoof Usage");
        executor.sendFormattedMessage("&6/spoof [player] <player(name/id/skin)/ip/host> <data> [modifier]");
        executor.sendFormattedMessage("&6/spoof clearcache");
        executor.sendFormattedMessage("&6/spoof cleardefaults");
        executor.sendFormattedMessage("&9Common modifiers: &eraw offline default");
    }

    private void messageComplete(AbstractExecutor executor, String target, boolean isTargetSelf, String setting, String data, Modifier modifier)
    {
        String name = isTargetSelf ? "your" : "&c" + target + "'s";
        executor.sendFormattedMessage(String.format("&6Updated %s &6spoofed &c%s &6to &c%s &o(modifier: %s)",
                name, setting, data, modifier.name()));
    }
}
