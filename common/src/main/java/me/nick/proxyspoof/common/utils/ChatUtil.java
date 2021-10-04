package me.nick.proxyspoof.common.utils;

public class ChatUtil
{

    public static String formatColor(String message)
    {
        return message.replace('&', '\u00a7');
    }
}
