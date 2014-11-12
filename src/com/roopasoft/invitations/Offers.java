package com.roopasoft.invitations;

import java.util.HashMap;

import com.oDesk.ClassPreamble;
import com.oDesk.api.OAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

@ClassPreamble (
	author = "Maksym Novozhylov <mnovozhilov@odesk.com>",
	date = "6/4/2014",
	currentRevision = 1,
	lastModified = "6/4/2014",
	lastModifiedBy = "Maksym Novozhylov",
	reviewers = {"Yiota Tsakiri"}
)
public final class Offers {
	
	final static String ENTRY_POINT = "api";
	
	private OAuthClient oClient = null;

	public Offers(OAuthClient client) {
		oClient = client;
		oClient.setEntryPoint(ENTRY_POINT);
	}
	
	/**
     * Get list of applications
     *
     * @param   params Parameters
     * @throws	JSONException If error occurred
	 * @return	{@link JSONObject}
     */
    public JSONObject getList(HashMap<String, String> params) throws JSONException {
        return oClient.get("/offers/v1/contractors/offers", params);
    }

    /**
     * Get specific application
     *
     * @param   reference Offer reference
     * @throws	JSONException If error occurred
	 * @return	{@link JSONObject}
     */
    public JSONObject getSpecific(String reference) throws JSONException {
        return oClient.get("/offers/v1/contractors/offers/" + reference);
    }

}