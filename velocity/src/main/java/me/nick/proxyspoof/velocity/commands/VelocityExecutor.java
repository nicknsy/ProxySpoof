package me.nick.proxyspoof.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.nick.proxyspoof.common.commands.AbstractExecutor;
import me.nick.proxyspoof.common.utils.ChatUtil;
import net.kyori.adventure.text.Component;

public class VelocityExecutor implements AbstractExecutor
{

    private final CommandSource source;

    public VelocityExecutor(CommandSource source)
    {
        this.source = source;
    }

    @Override
    public void sendFormattedMessage(String message)
    {
        source.sendMessage(Component.text(ChatUtil.formatColor(message)));
    }

    @Override
    public String getName()
    {
        return isPlayer() ? ((Player) source).getUsername() : "UNKNOWN";
    }

    @Override
    public boolean isPlayer()
    {
        return source instanceof Player;
    }
}
