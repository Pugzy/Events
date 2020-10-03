package rip.bolt.ingame.api;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import rip.bolt.ingame.api.definitions.BoltMatch;
import rip.bolt.ingame.api.definitions.Participant;

import java.util.UUID;

/**
 * Manager class which uses the JAX-RS Client API to automatically create the Bolt objects (e.g. Match, Teams, etc.)
 * and populate these objects with the relevant API data.
 * Notably, this class provides the fetchMatchData and postMatchData functions.
 * 
 * @author Picajoluna
 */
public class APIManager {

    /** API's base URL (e.g. "https://bolt.rip.api/v1"). */
    private String baseURL;

    /** URI path for the resources to GET (e.g. "/match/Ranked1"). */
    private String GETPath;

    /** URI path for the resources to POST (e.g. "/match/Ranked1/finish"). */
    private String POSTPath;

    /** URI path to POST player abandons to (e.g. "users/1234-535....324-2342/invalidate"). */
    private String POSTAbandonPath;

    /** Remember to register JacksonFeature! */
    private Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class).register(AuthorisationHeaderFilter.class);;


    /** APIManager Constructor: constructed from the API base URL and resource URI paths. */
    public APIManager(String apiBaseURL, String GETPath, String POSTPath, String POSTAbandonPath) {

        // Format base URL to remove trailing forward-slash
        if (apiBaseURL.toString().endsWith("/"))
            apiBaseURL = apiBaseURL.toString().substring(0, apiBaseURL.length() - 1);

        this.baseURL = apiBaseURL;
        this.GETPath = GETPath;
        this.POSTPath = POSTPath;
        this.POSTAbandonPath = POSTAbandonPath;

        // Format URI paths to begin with a forward-slash
        if (!GETPath.toString().startsWith("/"))
            this.GETPath = "/" + GETPath;

        if (!POSTPath.toString().startsWith("/"))
            this.POSTPath = "/" + POSTPath;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getGETPath() {
        return GETPath;
    }

    public String getPOSTAbandonPath() {
        return POSTAbandonPath;
    }

    public String getPOSTPath() {
        return POSTPath;
    }

    /**
     * Fetches the current {@link BoltMatch} data from the API.
     * 
     * @return initialized {@link BoltMatch} object, whose fields are set here.
     * The JAX-RS client handles fetching the data, initializing the BoltMatch object, and setting its fields.
     * This means we can now access the fetched API data directly using the BoltMatch class getter methods.
     */
    public BoltMatch fetchMatchData() {
        try {
            return client.target(getBaseURL()).path(getGETPath()).request(MediaType.APPLICATION_JSON).get(BoltMatch.class);
        } catch (WebApplicationException e) { // Thrown if an unsuccessful status code is returned.
            if (e.getResponse().getStatus() != 404) // 404 when polling for new matches
                e.printStackTrace();
        } catch (ProcessingException e) { // General errors, e.g. connection or deserialization failures.
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Posts an abandoned player UUID to the Bolt API.
     *
     * @param uuid of player to ban
     */
    public void postMatchPlayerAbandon(UUID uuid) {
        try {
            client.target(getBaseURL()).path(getPOSTAbandonPath().replace("{uuid}", uuid.toString())).request().post(null);
        } catch (WebApplicationException e) {
            e.printStackTrace();
        } catch (ProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Posts match data to the Bolt API.
     * To single out fields to be only POSTed with JAX-RS, use the @JsonProperties(access = ACCESS.WRITE_ONLY) annotation.
     * And to single out fields to be only fetched from the API, use the @JsonProperties(access = ACCESS.READ_ONLY) annotation.
     * 
     * @param match object representing the finished match.
     */
    public void postMatchData(BoltMatch match) {
        try {
            client.target(getBaseURL()).path(getPOSTPath()).request(MediaType.APPLICATION_JSON).post(Entity.json(match), BoltMatch.class);
        } catch (WebApplicationException e) {
            e.printStackTrace();
        } catch (ProcessingException e) {
            e.printStackTrace();
        }
    }
}
