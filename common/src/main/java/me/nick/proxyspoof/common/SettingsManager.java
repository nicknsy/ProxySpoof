package me.nick.proxyspoof.common;

import io.netty.channel.EventLoop;
import me.nick.proxyspoof.common.mojang.LoginResponse;
import me.nick.proxyspoof.common.mojang.MojangApi;
import me.nick.proxyspoof.common.mojang.UUIDResponse;
import me.nick.proxyspoof.common.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SettingsManager
{

    private Map<String, SpoofedSettings> settingsMap = new HashMap<>();
    private Map<String, String> realUserMap = new HashMap<>();

    public SpoofedSettings getPlayerSettings(String name)
    {
        settingsMap.putIfAbsent(name, new SpoofedSettings());
        return settingsMap.get(name);
    }

    public CompletableFuture<SpoofedSettings> initializeAndGet(String name)
    {
        CompletableFuture<SpoofedSettings> settingsFuture = new CompletableFuture<>();
        SpoofedSettings settings = getPlayerSettings(name);

        // Set default values if null
        // Default = logging in as normal player (with random ip though)
        if (settings.getSpoofedIp() == null) settings.setSpoofedIp(NetworkUtil.getRandomIp());
        if (settings.getSpoofedName() == null) settings.setSpoofedName(name);

        // Mojang requests
        Runnable skinSet = () ->
        {
            UUIDResponse id = settings.getSpoofedId();
            if (settings.getSpoofedProfile() == null && id.isOnlineMode())
            {
                CompletableFuture<LoginResponse> loginResponseFuture = MojangApi.getSkin(id.getId());
                loginResponseFuture.whenComplete((loginResponse, throwable) ->
                {
                    settings.setSpoofedProfile(loginResponse);

                    // Skin is the last setting -- complete the whole initialization
                    settingsFuture.complete(settings);
                });
            }
            else
            {
                settingsFuture.complete(settings);
            }
        };

        if (settings.getSpoofedId() == null)
        {
            CompletableFuture<UUIDResponse> uuidResponseFuture = MojangApi.getUUID(name);
            uuidResponseFuture.whenComplete((uuidResponse, throwable) ->
            {
                settings.setSpoofedId(uuidResponse);
                skinSet.run();
            });
        }
        else
        {
            skinSet.run();
        }

        return settingsFuture;
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
