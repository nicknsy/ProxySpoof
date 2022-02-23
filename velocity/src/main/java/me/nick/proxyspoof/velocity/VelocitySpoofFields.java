package me.nick.proxyspoof.velocity;

import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import com.velocitypowered.proxy.connection.client.LoginInboundConnection;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

public class VelocitySpoofFields
{

    /*
     * JAVA 16: --add-opens java.base/java.net=ALL-UNNAMED
     */
    public static Field INBOUND_DELEGATE_FIELD;
    public static Field IP_HOLDER_FIELD;
    public static Field IP_HOLDER_HOSTNAME_FIELD;
    public static Field VIRTUAL_HOST_FIELD;

    static
    {
        try
        {
            INBOUND_DELEGATE_FIELD = LoginInboundConnection.class.getDeclaredField("delegate");
            IP_HOLDER_FIELD = InetSocketAddress.class.getDeclaredField("holder");
            IP_HOLDER_HOSTNAME_FIELD = IP_HOLDER_FIELD.getType().getDeclaredField("hostname");
            VIRTUAL_HOST_FIELD = InitialInboundConnection.class.getDeclaredField("cleanedAddress");

            INBOUND_DELEGATE_FIELD.setAccessible(true);
            IP_HOLDER_FIELD.setAccessible(true);
            IP_HOLDER_HOSTNAME_FIELD.setAccessible(true);
            VIRTUAL_HOST_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
