package me.nick.proxyspoof.common.mojang;

public class LoginResponse
{

    private String id;
    private String name;
    private Property[] properties;

    public static class Property
    {

        private String name;
        private String value;
        private String signature;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public String getSignature()
        {
            return signature;
        }

        public void setSignature(String signature)
        {
            this.signature = signature;
        }
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Property[] getProperties()
    {
        return properties;
    }

    public void setProperties(Property[] properties)
    {
        this.properties = properties;
    }
}
