package me.nick.proxyspoof.bungee;

import me.nick.proxyspoof.bungee.commands.SpoofCommand;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.bungee.listeners.ConnectionListener;
import me.nick.proxyspoof.common.commands.SpoofCommandBase;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeePlugin extends Plugin
{

    private SettingsManager settingsManager = new SettingsManager();

    @Override
    public void onEnable()
    {
        PluginManager pluginManager = getProxy().getPluginManager();

        pluginManager.registerListener(this, new ConnectionListener(settingsManager));
        pluginManager.registerCommand(this, new SpoofCommand(new SpoofCommandBase(), settingsManager));
    }
}
