package me.nick.proxyspoof.common.mojang;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.nick.proxyspoof.common.GsonInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangApi
{

    private static final int TIMEOUT = 5000;
    private static final Gson GSON = GsonInstance.gson();
    private static final String API_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String API_SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    // Caches -- no time limit as data is mostly non-changing
    // Must be manually reset through command
    private static Map<String, UUIDResponse> uuidCache = new HashMap<>();
    private static Map<String, LoginResponse> skinCache = new HashMap<>();

    public static HttpResponse request(String urlString) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setConnectTimeout(TIMEOUT);
        urlConn.setReadTimeout(TIMEOUT);

        StringBuilder builder = new StringBuilder();
        int responseCode = urlConn.getResponseCode();

        if (responseCode == 200)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));

            // Read response
            String input;
            while ((input = reader.readLine()) != null)
            {
                builder.append(input);
            }
            reader.close();
        }

        return new HttpResponse(responseCode, builder.toString());
    }

    /**
     * Clears both UUID and skin caches
     */
    public static void clearCache()
    {
        skinCache.clear();
        uuidCache.clear();
    }

    /**
     * Returns a UUID object (dashed) of a player given
     * their name.
     *
     * @param name name of player
     * @return UUIDResponse containing UUID of specified player, or OfflineUUID on error
     */
    public static UUIDResponse getUUID(String name)
    {
        if (uuidCache.containsKey(name)) return uuidCache.get(name);

        try
        {
            HttpResponse response = request(String.format(API_UUID_URL, name));

            if (response.getResponseCode() == 200)
            {
                String content = response.getContent();
                StringBuilder uuidBuilder = new StringBuilder();

                JsonObject responseObject = GSON.fromJson(content, JsonObject.class);

                String uuid = responseObject.get("id").getAsString();
                uuidBuilder.append(uuid, 0, 8).append("-")
                        .append(uuid, 8, 12).append("-")
                        .append(uuid, 12, 16).append("-")
                        .append(uuid, 16, 20).append("-")
                        .append(uuid, 20, uuid.length());

                String responseName = responseObject.get("name").getAsString();
                UUID responseId = UUID.fromString(uuidBuilder.toString());
                UUIDResponse onlineResponse = new UUIDResponse(responseName, responseId, true);

                uuidCache.put(responseName, onlineResponse);
                return onlineResponse;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new UUIDResponse(name, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)), false);
    }

    /**
     * Returns a LoginResult object including the uuid, name, and
     * skin properties of a certain player given their uuid.
     *
     * @param uuid player UUID object
     * @return LoginResponse with skin stored in properties, or null on error
     */
    public static LoginResponse getSkin(UUID uuid)
    {
        return getSkin(uuid.toString().replace("-", ""));
    }

    /**
     * Returns a LoginResult object including the uuid, name, and
     * skin properties of a certain player given their uuid.
     *
     * @param uuid player UUID string without dashes
     * @return LoginResponse with skin stored in properties, or null on error
     */
    public static LoginResponse getSkin(String uuid)
    {
        if (skinCache.containsKey(uuid)) return skinCache.get(uuid);

        LoginResponse loginResponse = null;
        try
        {
            HttpResponse response = request(String.format(API_SKIN_URL, uuid));

            if (response.getResponseCode() == 200)
            {
                loginResponse = GSON.fromJson(response.getContent(), LoginResponse.class);
                skinCache.put(uuid, loginResponse);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return loginResponse;
    }
}
