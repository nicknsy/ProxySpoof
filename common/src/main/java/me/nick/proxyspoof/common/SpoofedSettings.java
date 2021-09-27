package me.nick.proxyspoof.common;

import me.nick.proxyspoof.common.mojang.LoginResponse;
import me.nick.proxyspoof.common.mojang.UUIDResponse;

public class SpoofedSettings
{

    private String spoofedName;
    private UUIDResponse spoofedId;
    private LoginResponse spoofedSkin;
    private String spoofedIp;

    public void clear()
    {
        spoofedName = null;
        spoofedId = null;
        spoofedSkin = null;
        spoofedIp = null;
    }

    public String getSpoofedName()
    {
        return spoofedName;
    }

    public void setSpoofedName(String spoofedName)
    {
        this.spoofedName = spoofedName;
    }

    public UUIDResponse getSpoofedId()
    {
        return spoofedId;
    }

    public void setSpoofedId(UUIDResponse spoofedId)
    {
        this.spoofedId = spoofedId;
    }

    public LoginResponse getSpoofedSkin()
    {
        return spoofedSkin;
    }

    public void setSpoofedSkin(LoginResponse spoofedSkin)
    {
        this.spoofedSkin = spoofedSkin;
    }

    public String getSpoofedIp()
    {
        return spoofedIp;
    }

    public void setSpoofedIp(String spoofedIp)
    {
        this.spoofedIp = spoofedIp;
    }
}
