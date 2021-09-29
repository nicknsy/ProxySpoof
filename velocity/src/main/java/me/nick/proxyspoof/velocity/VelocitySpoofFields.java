package me.nick.proxyspoof.velocity;

import com.velocitypowered.proxy.connection.client.InitialInboundConnection;

import java.lang.reflect.Field;

public class VelocitySpoofFields
{

    public static Field VIRTUAL_HOST_FIELD;

    static
    {
        try
        {
            VIRTUAL_HOST_FIELD = InitialInboundConnection.class.getDeclaredField("cleanedAddress");

            VIRTUAL_HOST_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
