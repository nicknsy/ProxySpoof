package me.nick.proxyspoof.velocity.listeners;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.SpoofedSettings;
import me.nick.proxyspoof.common.mojang.LoginResponse;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

import static me.nick.proxyspoof.velocity.VelocitySpoofFields.VIRTUAL_HOST_FIELD;

public class ConnectionListener
{

    private SettingsManager settingsManager;

    public ConnectionListener(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }


    @Subscribe(order = PostOrder.FIRST)
    public EventTask onPreLogin(PreLoginEvent event)
    {
        if (!(event.getConnection() instanceof InitialInboundConnection)) return null;

        // Get defaults for settings and update reflection fields
        return EventTask.resumeWhenComplete(settingsManager.initializeAndGet(event.getUsername()).whenComplete((settings, throwable) ->
        {
            InitialInboundConnection inboundCon = (InitialInboundConnection) event.getConnection();

            try
            {
                // Virtual host is used for both hostname and player IP
                VIRTUAL_HOST_FIELD.set(inboundCon, settings.getSpoofedIp());
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

            // Set offline-mode login
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
        }));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onGameProfileRequest(GameProfileRequestEvent event)
    {
        String name = event.getUsername();
        SpoofedSettings settings = settingsManager.getPlayerSettings(name);

        // Create GameProfile
        List<GameProfile.Property> properties = new ArrayList<>();
        if (settings.getSpoofedProfile() != null && settings.getSpoofedProfile().getProperties() != null)
        {
            for (LoginResponse.Property property : settings.getSpoofedProfile().getProperties())
            {
                GameProfile.Property velocityProperty = new GameProfile.Property(property.getName(), property.getValue(), property.getSignature());
                properties.add(velocityProperty);
            }
        }

        GameProfile newProfile = new GameProfile(settings.getSpoofedId().getId(), settings.getSpoofedName(), properties);
        event.setGameProfile(newProfile);

        // Add to user map
        settingsManager.putRealUser(settings.getSpoofedName(), name);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event)
    {
        settingsManager.removeRealUser(event.getPlayer().getUsername());
    }
}
