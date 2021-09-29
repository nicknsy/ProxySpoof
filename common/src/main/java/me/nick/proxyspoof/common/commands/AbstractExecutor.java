package me.nick.proxyspoof.common.commands;

public interface AbstractExecutor
{

    void sendFormattedMessage(String message);

    String getName();

    boolean isPlayer();
}
