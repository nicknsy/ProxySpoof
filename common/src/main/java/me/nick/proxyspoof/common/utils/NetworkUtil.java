package me.nick.proxyspoof.common.utils;

import java.util.Random;

public class NetworkUtil
{

    public static String getRandomIp()
    {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
