package me.nick.proxyspoof.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.commands.AbstractExecutor;
import me.nick.proxyspoof.common.commands.SpoofCommandBase;

import java.util.List;

public class SpoofCommand implements SimpleCommand
{

    private SpoofCommandBase commandBase;
    private SettingsManager settingsManager;

    public SpoofCommand(SpoofCommandBase commandBase, SettingsManager settingsManager)
    {
        this.commandBase = commandBase;
        this.settingsManager = settingsManager;
    }

    @Override
    public void execute(Invocation invocation)
    {
        AbstractExecutor executor = new VelocityExecutor(invocation.source());
        commandBase.execute(settingsManager, executor, invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation)
    {
        AbstractExecutor executor = new VelocityExecutor(invocation.source());
        return commandBase.tabComplete(executor, invocation.arguments());
    }
}
