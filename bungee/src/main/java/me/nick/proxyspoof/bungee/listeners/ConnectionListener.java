package me.nick.proxyspoof.bungee.listeners;

import me.nick.proxyspoof.bungee.BungeePlugin;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.mojang.LoginResponse;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;

import static me.nick.proxyspoof.bungee.BungeeSpoofFields.*;

public class ConnectionListener implements Listener
{

    private BungeePlugin plugin;
    private SettingsManager settingsManager;

    public ConnectionListener(BungeePlugin plugin, SettingsManager settingsManager)
    {
        this.plugin = plugin;
        this.settingsManager = settingsManager;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event)
    {
        if (!(event.getConnection() instanceof InitialHandler))
            return;

        // Block login thread
        event.registerIntent(plugin);

        InitialHandler handler = (InitialHandler) event.getConnection();
        String name = handler.getName();

        // Get default settings if necessary and spoof
        settingsManager.initializeAndGet(name).whenComplete((settings, throwable) ->
        {
            // Set host and skin
            handler.getHandshake().setHost(settings.getSpoofedIp());

            LoginResult.Property[] properties = new LoginResult.Property[0];
            LoginResponse.Property[] spoofedProperties;
            if (settings.getSpoofedProfile() != null && (spoofedProperties = settings.getSpoofedProfile().getProperties()) != null)
            {
                properties = new LoginResult.Property[spoofedProperties.length];
                for (int i = 0; i < spoofedProperties.length; i++)
                {
                    LoginResponse.Property property = spoofedProperties[i];
                    LoginResult.Property bungeeProperty = new LoginResult.Property(property.getName(), property.getValue(), property.getSignature());
                    properties[i] = (bungeeProperty);
                }
            }

            LoginResult newProfile = new LoginResult(settings.getSpoofedId().getId().toString().replace("-", ""),
                    settings.getSpoofedName(), properties);

            // Update reflection-based fields
            try
            {
                Object ipHolder = IP_HOLDER_FIELD.get(handler.getAddress());
                IP_HOLDER_HOSTNAME_FIELD.set(ipHolder, settings.getSpoofedIp());

                NAME_FIELD.set(handler, settings.getSpoofedName());
                UUID_FIELD.set(handler, settings.getSpoofedId().getId());
                LOGIN_PROFILE_FIELD.set(handler, newProfile);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

            // Add to user map and finish login
            settingsManager.putRealUser(settings.getSpoofedName(), name);

            handler.setOnlineMode(false);
            event.completeIntent(plugin);
        });
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event)
    {
        settingsManager.removeRealUser(event.getPlayer().getName());
    }
}
