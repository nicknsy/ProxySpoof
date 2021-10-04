package me.nick.proxyspoof.common;

import me.nick.proxyspoof.common.mojang.LoginResponse;
import me.nick.proxyspoof.common.mojang.UUIDResponse;

public class SpoofedSettings
{

    private static String defaultHost;

    private String spoofedName;
    private UUIDResponse spoofedId;
    private LoginResponse spoofedProfile;
    private String spoofedIp;
    private String spoofedHost;

    public static void clearDefaults()
    {
        defaultHost = null;
    }

    public void clear()
    {
        spoofedName = null;
        spoofedId = null;
        spoofedProfile = null;
        spoofedIp = null;
        spoofedHost = null;
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

    public LoginResponse getSpoofedProfile()
    {
        return spoofedProfile;
    }

    public void setSpoofedProfile(LoginResponse spoofedProfile)
    {
        this.spoofedProfile = spoofedProfile;
    }

    public String getSpoofedIp()
    {
        return spoofedIp;
    }

    public void setSpoofedIp(String spoofedIp)
    {
        this.spoofedIp = spoofedIp;
    }

    public void setSpoofedHost(String spoofedHost)
    {
        this.spoofedHost = spoofedHost;
    }

    public static void setDefaultHost(String defaultHost)
    {
        SpoofedSettings.defaultHost = defaultHost;
    }

    public String getSpoofedHostOrDefault()
    {
        return spoofedHost != null ? spoofedHost : (defaultHost != null ? defaultHost : spoofedIp);
    }
}
