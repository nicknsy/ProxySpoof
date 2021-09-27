package me.nick.proxyspoof.common;

import me.nick.proxyspoof.common.mojang.MojangApi;
import me.nick.proxyspoof.common.mojang.UUIDResponse;
import me.nick.proxyspoof.common.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager
{

    private Map<String, SpoofedSettings> settingsMap = new HashMap<>();
    private Map<String, String> realUserMap = new HashMap<>();

    public SpoofedSettings getPlayerSettings(String name)
    {
        settingsMap.putIfAbsent(name, new SpoofedSettings());
        SpoofedSettings settings = settingsMap.get(name);

        // Set default values if null
        // Default = logging in as normal player (with random ip though)
        if (settings.getSpoofedIp() == null) settings.setSpoofedIp(NetworkUtil.getRandomIp());
        if (settings.getSpoofedName() == null) settings.setSpoofedName(name);
        if (settings.getSpoofedId() == null) settings.setSpoofedId(MojangApi.getUUID(name));

        UUIDResponse id = settings.getSpoofedId();
        if (settings.getSpoofedSkin() == null && id.isOnlineMode())
            settings.setSpoofedSkin(MojangApi.getSkin(id.getId()));

        return settings;
    }

    public String getRealUser(String name)
    {
        return realUserMap.getOrDefault(name, name);
    }

    public void putRealUser(String spoofedName, String realName)
    {
        realUserMap.put(spoofedName, realName);
    }

    public void removeRealUser(String name)
    {
        realUserMap.remove(name);
    }
}
