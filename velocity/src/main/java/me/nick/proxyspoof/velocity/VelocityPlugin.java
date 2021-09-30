package me.nick.proxyspoof.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import javassist.*;
import me.nick.proxyspoof.common.SettingsManager;
import me.nick.proxyspoof.common.commands.SpoofCommandBase;
import me.nick.proxyspoof.velocity.commands.SpoofCommand;
import me.nick.proxyspoof.velocity.listeners.ConnectionListener;

@Plugin(id = "proxyspoof", name = "ProxySpoof", version = "${project.version}", authors = {"Nick"})
public class VelocityPlugin
{

    private SettingsManager settingsManager = new SettingsManager();
    private ProxyServer server;

    @Inject
    public VelocityPlugin(ProxyServer server)
    {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event)
    {
        // Change return of VelocityServerConnection's method to get IP to forward
        modifyClasses();

        // Register join/leave listener
        server.getEventManager().register(this, new ConnectionListener(settingsManager));

        // Register commands
        server.getCommandManager().register("spoof", new SpoofCommand(new SpoofCommandBase(), settingsManager));
    }

    private void modifyClasses()
    {
        try
        {
            CtClass inetClass = ClassPool.getDefault().get("com.velocitypowered.proxy.connection.backend.VelocityServerConnection");
            CtMethod getAddressMethod = inetClass.getDeclaredMethod("getPlayerRemoteAddressAsString");

            getAddressMethod.setBody("return ((java.net.InetSocketAddress) proxyPlayer.getVirtualHost().get()).getHostString();");

            inetClass.toClass();
        }
        catch (NotFoundException | CannotCompileException e)
        {
            e.printStackTrace();
        }
    }
}
