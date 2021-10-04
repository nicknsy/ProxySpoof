package me.nick.proxyspoof.common.utils;

import java.util.Random;

public class NetworkUtil
{
    private static final Random rand = new Random();

    public static String getRandomIp()
    {
        return rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256);
    }
}
