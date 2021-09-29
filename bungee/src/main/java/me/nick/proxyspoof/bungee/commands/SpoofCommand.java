package me.nick.proxyspoof.bungee.commands;

import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.commands.AbstractExecutor;
import me.nick.proxyspoof.common.commands.SpoofCommandBase;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class SpoofCommand extends Command implements TabExecutor
{

    private SpoofCommandBase commandBase;
    private SettingsManager settingsManager;

    public SpoofCommand(SpoofCommandBase commandBase, SettingsManager settingsManager)
    {
        super("spoof");
        this.commandBase = commandBase;
        this.settingsManager = settingsManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        AbstractExecutor executor = new BungeeExecutor(sender);
        commandBase.execute(settingsManager, executor, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        AbstractExecutor executor = new BungeeExecutor(sender);
        return commandBase.tabComplete(executor, args);
    }
}
