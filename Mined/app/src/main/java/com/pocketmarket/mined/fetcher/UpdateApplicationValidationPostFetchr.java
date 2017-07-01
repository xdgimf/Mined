package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.UploadedFormDTO;
import com.pocketmarket.mined.utility.Utils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class UpdateApplicationValidationPostFetchr extends MainFetchr {
    private static final String TAG = "UpdateApplicationValidationPostFetchr";
    private String ID = "id";

    private String NAME = "name";

    private String BUNDLED_PLANS = "bundledplans";

    private String HOME_OWNDERSHIP = "homeownership";

    private String TELEPHONE_WIRING = "telephonewiring";

    private String EMPLOYMENT_STATUS = "employmentstatus";

    private String EMPLOYMENT_ADDRESS = "employmentaddress";

    private String EMPLOYMENT_POSITION = "employmentposition";

    private String EMPLOYMENT_SALARY = "employmentsalary";

    private String BIRTH_DATE = "birthdate";

    private String SEX = "sex";

    private String CIVIL_STATUS = "civilstatus";

    private String CITIZENSHIP = "citizenship";

    private String EMAIL = "email";

    private String CELL_NO = "cellno";

    private String ADDRESS = "address";

    public UploadedFormDTO fetchItems(String sUrl, UploadedFormDTO uploadedFormDTO, String accessToken) {

        String response = null;
        try{
            Log.i(TAG, "URL: " + sUrl + ", name: " + uploadedFormDTO.getName() + ", accessToken: " + accessToken);

            HashMap<String, String> params = new HashMap<>();
            params.put(ID, Integer.toString(uploadedFormDTO.getId()));
            params.put(NAME, uploadedFormDTO.getName());
            params.put(BUNDLED_PLANS, uploadedFormDTO.getBundledplans());
            params.put(HOME_OWNDERSHIP, uploadedFormDTO.getHomeownership());
            params.put(TELEPHONE_WIRING, uploadedFormDTO.getTelephonewiring());
            params.put(EMPLOYMENT_STATUS, uploadedFormDTO.getEmploymentstatus());
            params.put(EMPLOYMENT_ADDRESS, uploadedFormDTO.getEmploymentaddress());
            params.put(EMPLOYMENT_POSITION, uploadedFormDTO.getEmploymentposition());
            params.put(EMPLOYMENT_SALARY, uploadedFormDTO.getEmploymentsalary());
            params.put(BIRTH_DATE, uploadedFormDTO.getBirthdate());
            params.put(SEX, uploadedFormDTO.getSex());
            params.put(CIVIL_STATUS, uploadedFormDTO.getCivilstatus());
            params.put(CITIZENSHIP, uploadedFormDTO.getCitizenship());
            params.put(EMAIL, uploadedFormDTO.getEmail());
            params.put(CELL_NO, uploadedFormDTO.getCellno());
            params.put(ADDRESS, uploadedFormDTO.getAddress());

            // request of http post
            response = getUrl(sUrl, METHOD_TYPE_POST, params, accessToken);

            if (response == null)
                return null;

            if (!Utils.isJSONValid(response))
                return null;

            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);

            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return null;




        }catch (IOException e) {
            Log.e(TAG, "Failed to fetch items: ", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed parse items: ", e);
        }

        return uploadedFormDTO;

    }
}

