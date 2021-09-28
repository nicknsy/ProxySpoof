package me.nick.proxyspoof.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import javassist.*;
import me.nick.proxyspoof.velocity.listeners.ConnectionListener;

@Plugin(id = "proxyspoof", name = "ProxySpoof", version = "1.0.0", authors = {"Nick"})
public class VelocityPlugin
{

    private ProxyServer server;

    @Inject
    public VelocityPlugin(ProxyServer server)
    {
        this.server = server;
        System.out.println("Start");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event)
    {
        // Change return of VelocityServerConnection's method to get IP to forward
        modifyClasses();

        // Register join/leave listener
        server.getEventManager().register(this, new ConnectionListener());

        // Create Brigadier command
        // TODO
    }

    private void modifyClasses()
    {
        try
        {
            CtClass inetClass = ClassPool.getDefault().get("com.velocitypowered.proxy.connection.backend.VelocityServerConnection");
            CtMethod getAddressMethod = inetClass.getDeclaredMethod("getPlayerRemoteAddressAsString");

            inetClass.addField(CtField.make("private String spoofedHost;", inetClass));
            getAddressMethod.insertBefore("if (spoofedHost != null) return spoofedHost;");

            inetClass.toClass();
        }
        catch (NotFoundException | CannotCompileException e)
        {
            e.printStackTrace();
        }
    }
}
