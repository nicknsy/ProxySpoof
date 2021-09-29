package me.nick.proxyspoof.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonInstance
{
    private static final Gson GSON = new GsonBuilder().setLenient().create();

    public static Gson gson()
    {
        return GSON;
    }
}
