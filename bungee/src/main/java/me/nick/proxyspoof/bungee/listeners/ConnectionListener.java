package me.nick.proxyspoof.bungee.listeners;

import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import me.nick.proxyspoof.common.mojang.LoginResponse;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;

import static me.nick.proxyspoof.bungee.BungeeSpoofFields.*;

public class ConnectionListener implements Listener
{

    private SettingsManager settingsManager;

    public ConnectionListener(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event)
    {
        if (!(event.getConnection() instanceof InitialHandler))
            return;

        InitialHandler handler = (InitialHandler) event.getConnection();
        handler.setOnlineMode(false);

        String name = handler.getName();
        SpoofedSettings settings = settingsManager.getPlayerSettings(name);

        // Set host and skin
        handler.getHandshake().setHost(settings.getSpoofedIp());

        if (settings.getSpoofedSkin().getProperties() != null)
        {
            LoginResponse.Property property = settings.getSpoofedSkin().getProperties()[0];
            LoginResult.Property bungeeProperty = new LoginResult.Property(property.getName(), property.getValue(), property.getSignature());
            handler.getLoginProfile().setProperties(new LoginResult.Property[] {bungeeProperty});
        }

        // Update reflection-based fields
        try
        {
            Object ipHolder = IP_HOLDER_FIELD.get(handler.getAddress());
            IP_HOLDER_HOSTNAME_FIELD.set(ipHolder, settings.getSpoofedIp());

            NAME_FIELD.set(handler, settings.getSpoofedName());
            UUID_FIELD.set(handler, settings.getSpoofedId().getId());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        // Add to user map
        settingsManager.putRealUser(settings.getSpoofedName(), name);
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event)
    {
        settingsManager.removeRealUser(event.getPlayer().getName());
    }
}
