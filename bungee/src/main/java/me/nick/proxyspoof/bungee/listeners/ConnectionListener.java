package me.nick.proxyspoof.bungee.listeners;

import me.nick.proxyspoof.bungee.BungeePlugin;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

import static me.nick.proxyspoof.bungee.BungeeSpoofFields.*;

public class ConnectionListener implements Listener
{

    private SettingsManager settingsManager;

    public ConnectionListener(BungeePlugin plugin)
    {
        this.settingsManager = plugin.getSettingsManager();
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event)
    {
        if (!(event.getConnection() instanceof InitialHandler))
            return;

        try
        {
            InitialHandler handler = (InitialHandler) event.getConnection();
            handler.setOnlineMode(false);

            String name = handler.getName();
            SpoofedSettings settings = settingsManager.getPlayerSettings(name);

            // Update fields
            NAME_FIELD.set(handler, settings.getSpoofedName());
            UUID_FIELD.set(handler, settings.getSpoofedId());
            SKIN_FIELD.set(handler, settings.getSpoofedSkin());

            Object ipHolder = IP_HOLDER_FIELD.get(handler.getAddress());
            IP_HOLDER_HOSTNAME_FIELD.set(ipHolder, settings.getSpoofedIp());
            handler.getHandshake().setHost(settings.getSpoofedIp());

            // Add to user map
            settingsManager.putRealUser(settings.getSpoofedName(), name);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event)
    {
        settingsManager.removeRealUser(event.getPlayer().getName());
    }
}
