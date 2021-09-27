package me.nick.proxyspoof.common.mojang;

public class HttpResponse
{

    private int responseCode;
    private String content;

    public HttpResponse(int responseCode, String content)
    {
        this.responseCode = responseCode;
        this.content = content;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public String getContent()
    {
        return content;
    }
}
