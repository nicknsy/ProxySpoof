package me.nick.proxyspoof.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

public class ConnectionListener
{
    @Subscribe(order = PostOrder.FIRST)
    public void onPreLogin(PreLoginEvent event)
    {

    }

    public void onPlayerLeave()
    {

    }
}
