/**
 * Copyright 2014 oDesk
 *
 * Licensed under the oDesk's API Terms of Use;
 * you may not use this file except in compliance with the Terms.
 * You may obtain a copy of the Terms at
 * 
 *    http://developers.odesk.com/API-Terms-of-Use
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oDesk.api;

import com.oDesk.ClassPreamble;
import com.oDesk.api.OAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

@ClassPreamble (
	author = "Maksym Novozhylov <mnovozhilov@odesk.com>",
	date = "6/3/2014",
	currentRevision = 1,
	lastModified = "6/3/2014",
	lastModifiedBy = "Maksym Novozhylov",
	reviewers = {"Yiota Tsakiri"}
)
public final class Auth {
	
	final static String ENTRY_POINT = "api";
	
	private OAuthClient oClient = null;

	public Auth(OAuthClient client) {
		oClient = client;
		oClient.setEntryPoint(ENTRY_POINT);
	}
	
	/**
	 * Get My Info
	 * 
	 * @throws	JSONException If error occurred
	 * @return	{@link JSONObject}
	 * */
	public JSONObject getUserInfo() throws JSONException {
		return oClient.get("/auth/v1/info");
	}

}
