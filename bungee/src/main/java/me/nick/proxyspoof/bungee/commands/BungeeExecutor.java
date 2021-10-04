package me.nick.proxyspoof.bungee.commands;

import me.nick.proxyspoof.common.commands.AbstractExecutor;
import me.nick.proxyspoof.common.utils.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeExecutor implements AbstractExecutor
{

    private final CommandSender sender;

    public BungeeExecutor(CommandSender sender)
    {
        this.sender = sender;
    }

    @Override
    public void sendFormattedMessage(String message)
    {
        sender.sendMessage(TextComponent.fromLegacyText(ChatUtil.formatColor(message)));
    }

    @Override
    public String getName()
    {
        return sender.getName();
    }

    @Override
    public boolean isPlayer()
    {
        return sender instanceof ProxiedPlayer;
    }
}
