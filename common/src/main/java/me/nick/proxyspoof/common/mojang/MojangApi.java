package me.nick.proxyspoof.common.mojang;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.EventLoop;
import me.nick.proxyspoof.common.GsonInstance;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class MojangApi
{

    private static final Gson GSON = GsonInstance.gson();
    private static final String API_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String API_SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    private static AsyncHttpClient httpClient = asyncHttpClient();

    // Caches -- no time limit as data is mostly non-changing
    // Must be manually reset through command
    private static Map<String, UUIDResponse> uuidCache = new HashMap<>();
    private static Map<String, LoginResponse> skinCache = new HashMap<>();

    /**
     * Clears both UUID and skin caches
     */
    public static void clearCache()
    {
        skinCache.clear();
        uuidCache.clear();
    }

    /**
     * Returns an online or offline UUID of a player given
     * their name.
     *
     * @param name name of player
     * @return CompletableFuture that returns online UUIDResponse of specified player, or offline UUIDResponse on error
     */
    public static CompletableFuture<UUIDResponse> getUUID(String name)
    {
        CompletableFuture<UUIDResponse> uuidResponseFuture = new CompletableFuture<>();
        String cachedName = name.toLowerCase();

        // Check cache
        if (uuidCache.containsKey(cachedName))
        {
            uuidResponseFuture.complete(uuidCache.get(cachedName));
            return uuidResponseFuture;
        }

        // Request from Mojang servers
        ListenableFuture<Response> responseFuture = httpClient.prepareGet(String.format(API_UUID_URL, name)).execute();
        responseFuture.toCompletableFuture().whenComplete((response, throwable) ->
        {
            if (response.getStatusCode() == 200)
            {
                String content = response.getResponseBody();
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

                uuidCache.put(responseName.toLowerCase(), onlineResponse);
                uuidResponseFuture.complete(onlineResponse);
            }

            // Return offline UUID on error
            if (!uuidResponseFuture.isDone())
                uuidResponseFuture.complete(new UUIDResponse(name, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)), false));
        });

        return uuidResponseFuture;
    }

    /**
     * Returns a LoginResult object including the uuid, name, and
     * skin properties of a certain player given their uuid.
     *
     * @param uuid player UUID object
     * @return CompletableFuture returning LoginResponse with skin stored in properties, or null on error
     */
    public static CompletableFuture<LoginResponse> getSkin(UUID uuid)
    {
        return getSkin(uuid.toString().replace("-", ""));
    }

    /**
     * Returns a LoginResult object including the uuid, name, and
     * skin properties of a certain player given their uuid.
     *
     * @param uuid player UUID string without dashes
     * @return CompletableFuture returning LoginResponse with skin stored in properties, or null on error
     */
    public static CompletableFuture<LoginResponse> getSkin(String uuid)
    {
        CompletableFuture<LoginResponse> loginResponseFuture = new CompletableFuture<>();

        // Check cache
        if (skinCache.containsKey(uuid))
        {
            loginResponseFuture.complete(skinCache.get(uuid));
            return loginResponseFuture;
        }

        // Request from Mojang servers
        ListenableFuture<Response> responseFuture = httpClient.prepareGet(String.format(API_SKIN_URL, uuid)).execute();
        responseFuture.toCompletableFuture().whenComplete((response, throwable) ->
        {
            if (response.getStatusCode() == 200)
            {
                LoginResponse loginResponse = GSON.fromJson(response.getResponseBody(), LoginResponse.class);

                skinCache.put(uuid, loginResponse);
                loginResponseFuture.complete(loginResponse);
            }

            // Return null on error
            if (!loginResponseFuture.isDone())
                loginResponseFuture.complete(null);
        });

        return loginResponseFuture;
    }
}
