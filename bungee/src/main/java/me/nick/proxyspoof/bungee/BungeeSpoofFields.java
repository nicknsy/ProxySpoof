package me.nick.proxyspoof.bungee;

import net.md_5.bungee.connection.InitialHandler;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Random;

public class BungeeSpoofFields
{

    /*
    JAVA 16: --add-opens java.base/java.net=ALL-UNNAMED
     */
    public static Field NAME_FIELD;
    public static Field UUID_FIELD;
    public static Field SKIN_FIELD;
    public static Field IP_HOLDER_FIELD;
    public static Field IP_HOLDER_HOSTNAME_FIELD;

    static
    {
        try
        {
            NAME_FIELD = InitialHandler.class.getDeclaredField("name");
            UUID_FIELD = InitialHandler.class.getDeclaredField("uniqueId");
            SKIN_FIELD = InitialHandler.class.getDeclaredField("loginProfile");
            IP_HOLDER_FIELD = InetSocketAddress.class.getDeclaredField("holder");
            IP_HOLDER_HOSTNAME_FIELD = IP_HOLDER_FIELD.getType().getDeclaredField("hostname");

            NAME_FIELD.setAccessible(true);
            UUID_FIELD.setAccessible(true);
            SKIN_FIELD.setAccessible(true);
            IP_HOLDER_FIELD.setAccessible(true);
            IP_HOLDER_HOSTNAME_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static String getRandomIp()
    {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
