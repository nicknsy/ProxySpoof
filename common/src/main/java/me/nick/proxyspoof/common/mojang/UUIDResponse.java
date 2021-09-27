package me.nick.proxyspoof.common.mojang;

import java.util.UUID;

public class UUIDResponse
{

    private String name;
    private UUID id;
    private boolean isOnlineMode;

    public UUIDResponse(String name, UUID id, boolean isOnlineMode)
    {
        this.name = name;
        this.id = id;
        this.isOnlineMode = isOnlineMode;
    }

    public String getName()
    {
        return name;
    }

    public UUID getId()
    {
        return id;
    }

    public boolean isOnlineMode()
    {
        return isOnlineMode;
    }
}
