package com.pocketmarket.mined.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.dto.UploadedFormDTO;
import com.pocketmarket.mined.fetcher.UpdateApplicationValidationPostFetchr;
import com.pocketmarket.mined.utility.AppApi;

import static com.pocketmarket.mined.R.id.civilstatus;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ApplicationFragment extends Fragment {
    private final static String TAG = "ApplicationFragment";
    public final static String EXTRA_VALIDATION_RESULT = "com.pocketmarket.mined.application.validation";

    private Spinner mProfileSelection;
    private EditText mProfileName;

    private Spinner mBundledPlansSelection;
    private EditText mBundledPlansText;

    private Spinner mHomeOwnershipSelection;
    private EditText mHomeOwnershipText;

    private Spinner mTelephoneWiringSelection;
    private EditText mTelephoneWiringText;

    private Spinner mEmploymentStatusSelection;
    private EditText mEmploymentStatusText;

    private Spinner mEmploymentAddressSelection;
    private EditText mEmploymentAddressText;

    private Spinner mEmploymentPositionSelection;
    private EditText mEmploymentPositionText;

    private Spinner mEmploymentSalarySelection;
    private EditText mEmploymentSalaryText;

    private Spinner mBirthdateSelection;
    private EditText mBirthdateText;

    private Spinner mSexSelection;
    private EditText mSexText;

    private Spinner mCivilStatusSelection;
    private EditText mCivilStatusText;

    private Spinner mCitizenshipSelection;
    private EditText mCitizenshipText;

    private Spinner mEmailSelection;
    private EditText mEmailText;

    private Spinner mCellNoSelection;
    private EditText mCellNoText;

    private Spinner mAddressSelection;
    private EditText mAddressText;

    private UploadedFormDTO mUploadedForm;

    private int mId;
    private String mName;
    private String mBundledPlans;
    private String mHomeOwnership;
    private String mTelephoneWiring;
    private String mEmploymentStatus;
    private String mEmploymentAddress;
    private String mEmploymentPosition;
    private String mEmploymentSalary;
    private String mBirthDate;
    private String mSex;
    private String mCivilstatus;
    private String mCitizenShip;
    private String mEmail;
    private String mCellNo;
    private String mAddress;

    private String mAccessToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mUploadedForm = (UploadedFormDTO) getArguments().getSerializable("uploadedForm");

        mAccessToken = getArguments().getString("accessToken");

        mId = mUploadedForm.getId();

        mName = mUploadedForm.getName();

        mBundledPlans = mUploadedForm.getBundledplans();

        mHomeOwnership = mUploadedForm.getHomeownership();

        mTelephoneWiring = mUploadedForm.getTelephonewiring();

        mEmploymentStatus = mUploadedForm.getEmploymentstatus();

        mEmploymentAddress = mUploadedForm.getEmploymentaddress();

        mEmploymentPosition = mUploadedForm.getEmploymentposition();

        mEmploymentSalary = mUploadedForm.getEmploymentsalary();

        mBirthDate = mUploadedForm.getBirthdate();

        mSex = mUploadedForm.getSex();

        mCivilstatus = mUploadedForm.getCivilstatus();

        mCitizenShip = mUploadedForm.getCitizenship();

        mEmail = mUploadedForm.getEmail();

        mCellNo = mUploadedForm.getCellno();

        mAddress = mUploadedForm.getAddress();

        Log.d(TAG, "name: " + mName + ", mBundledPlans: " + mBundledPlans + ", mHomeOwnership: " + mHomeOwnership + ", mTelephoneWiring: " + mTelephoneWiring
                + ", mEmploymentStatus: " + mEmploymentStatus + ", mEmploymentAddress: " + mEmploymentAddress + ", mEmploymentPosition: " + mEmploymentPosition
                + ", mEmploymentSalary: " + mEmploymentSalary + ", mBirthDate: " + mBirthDate + ", mSex: " + mSex + ", mCivilstatus: " + mCivilstatus
                + ",mCitizenShip: " + mCitizenShip + ", mEmail: " + mEmail + ", mCellNo: " + mCellNo + ", mAddress: " + mAddress + ", accessToken: " + mAccessToken
        );

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application, container, false);

        mProfileSelection = (Spinner) view.findViewById(R.id.profile_selection);
        mProfileName = (EditText) view.findViewById(R.id.profile_name);
        mProfileName.setText(mName);


        mBundledPlansSelection = (Spinner) view.findViewById(R.id.bundled_plans_selection);
        mBundledPlansText = (EditText) view.findViewById(R.id.bundled_plans);
        mBundledPlansText.setText(mBundledPlans);


        mHomeOwnershipSelection = (Spinner) view.findViewById(R.id.home_owndership_selection);
        mHomeOwnershipText = (EditText) view.findViewById(R.id.home_owndership);
        mHomeOwnershipText.setText(mHomeOwnership);


        mTelephoneWiringSelection = (Spinner) view.findViewById(R.id.telephonewiring_selection);
        mTelephoneWiringText = (EditText) view.findViewById(R.id.telephonewiring);
        mTelephoneWiringText.setText(mTelephoneWiring);


        mEmploymentStatusSelection = (Spinner) view.findViewById(R.id.employmentstatus_selection);
        mEmploymentStatusText = (EditText) view.findViewById(R.id.employmentstatus);
        mEmploymentStatusText.setText(mEmploymentStatus);


        mEmploymentAddressSelection = (Spinner) view.findViewById(R.id.employmentaddress_selection);
        mEmploymentAddressText = (EditText) view.findViewById(R.id.employmentaddress);
        mEmploymentAddressText.setText(mEmploymentAddress);

        mEmploymentPositionSelection = (Spinner) view.findViewById(R.id.employmentposition_selection);
        mEmploymentPositionText = (EditText) view.findViewById(R.id.employmentposition);
        mEmploymentPositionText.setText(mEmploymentPosition);


        mEmploymentSalarySelection = (Spinner) view.findViewById(R.id.employmentsalary_selection);
        mEmploymentSalaryText = (EditText) view.findViewById(R.id.employmentsalary);
        mEmploymentSalaryText.setText(mEmploymentSalary);

        mBirthdateSelection = (Spinner) view.findViewById(R.id.birthdate_selection);
        mBirthdateText = (EditText) view.findViewById(R.id.birthdate);
        mBirthdateText.setText(mBirthDate);

        mSexSelection = (Spinner) view.findViewById(R.id.sex_selection);
        mSexText = (EditText) view.findViewById(R.id.sex);
        mSexText.setText(mSex);

        mCivilStatusSelection = (Spinner) view.findViewById(R.id.civilstatus_selection);
        mCivilStatusText = (EditText) view.findViewById(civilstatus);
        mCivilStatusText.setText(mCivilstatus);

        mCitizenshipSelection = (Spinner) view.findViewById(R.id.citizenship_selection);
        mCitizenshipText = (EditText) view.findViewById(R.id.citizenship);
        mCitizenshipText.setText(mCitizenShip);

        mEmailSelection = (Spinner) view.findViewById(R.id.email_selection);
        mEmailText = (EditText) view.findViewById(R.id.email);
        mEmailText.setText(mEmail);

        mCellNoSelection = (Spinner) view.findViewById(R.id.cellno_selection);
        mCellNoText = (EditText) view.findViewById(R.id.cellno);
        mCellNoText.setText(mCellNo);

        mAddressSelection = (Spinner) view.findViewById(R.id.address_selection);
        mAddressText = (EditText) view.findViewById(R.id.address);
        mAddressText.setText(mAddress);

        initInformationSheet();
        return view;
    }

    private void initInformationSheet(){
        ArrayAdapter<String> adapterProfileSelection = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.customer_application));
        mProfileSelection.setAdapter(adapterProfileSelection);
        mProfileSelection.setSelection(0);

        //ArrayAdapter<String> adapterBundledPlansSelection = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.customer_application));
        mBundledPlansSelection.setAdapter(adapterProfileSelection);
        mBundledPlansSelection.setSelection(1);


        //ArrayAdapter<String> adapterHomeOwnershipSelection = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.customer_application));
        mHomeOwnershipSelection.setAdapter(adapterProfileSelection);
        mHomeOwnershipSelection.setSelection(2);

        //ArrayAdapter<String> adapterTelephoneWiringSelection = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.customer_application));
        mTelephoneWiringSelection.setAdapter(adapterProfileSelection);
        mTelephoneWiringSelection.setSelection(3);


        mEmploymentStatusSelection.setAdapter(adapterProfileSelection);
        mEmploymentStatusSelection.setSelection(4);


        mEmploymentAddressSelection.setAdapter(adapterProfileSelection);
        mEmploymentAddressSelection.setSelection(5);


        mEmploymentPositionSelection.setAdapter(adapterProfileSelection);
        mEmploymentPositionSelection.setSelection(6);

        mEmploymentSalarySelection.setAdapter(adapterProfileSelection);
        mEmploymentSalarySelection.setSelection(7);

        mBirthdateSelection.setAdapter(adapterProfileSelection);
        mBirthdateSelection.setSelection(8);

        mSexSelection.setAdapter(adapterProfileSelection);
        mSexSelection.setSelection(9);

        mCivilStatusSelection.setAdapter(adapterProfileSelection);
        mCivilStatusSelection.setSelection(10);

        mCitizenshipSelection.setAdapter(adapterProfileSelection);
        mCitizenshipSelection.setSelection(11);

        mEmailSelection.setAdapter(adapterProfileSelection);
        mEmailSelection.setSelection(12);

        mCellNoSelection.setAdapter(adapterProfileSelection);
        mCellNoSelection.setSelection(13);

        mAddressSelection.setAdapter(adapterProfileSelection);
        mAddressSelection.setSelection(14);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.save:
                updateUploadedForms();
                break;
        }
        return true;
    }

    /**
     * The method to update the pldt forms
     */
    private void updateUploadedForms(){
        UploadedFormDTO uploadedFormDTO = new UploadedFormDTO();


        String name = mProfileName.getText().toString().trim();
        String bundledPlans = mBundledPlansText.getText().toString().trim();
        String homeOwnership = mHomeOwnershipText.getText().toString().trim();
        String telephoneWiring = mTelephoneWiringText.getText().toString().trim();
        String employmentStatus = mEmploymentStatusText.getText().toString().trim();
        String employmentAddress = mEmploymentAddressText.getText().toString().trim();
        String employmentPosition = mEmploymentPositionText.getText().toString().trim();
        String employmentSalary = mEmploymentSalaryText.getText().toString().trim();
        String birthDate = mBirthdateText.getText().toString().trim();
        String sex = mSexText.getText().toString().trim();
        String civilStatus = mCivilStatusText.getText().toString().trim();
        String citizenShip = mCitizenshipText.getText().toString().trim();
        String email =  mEmailText.getText().toString().trim();
        String cellNo = mCellNoText.getText().toString().trim();
        String address = mAddressText.getText().toString().trim();


        uploadedFormDTO.setId(mId);
        uploadedFormDTO.setName(name);
        uploadedFormDTO.setBundledplans(bundledPlans);
        uploadedFormDTO.setHomeownership(homeOwnership);
        uploadedFormDTO.setTelephonewiring(telephoneWiring);
        uploadedFormDTO.setEmploymentstatus(employmentStatus);
        uploadedFormDTO.setEmploymentaddress(employmentAddress);
        uploadedFormDTO.setEmploymentposition(employmentPosition);
        uploadedFormDTO.setEmploymentsalary(employmentSalary);
        uploadedFormDTO.setBirthdate(birthDate);
        uploadedFormDTO.setSex(sex);
        uploadedFormDTO.setCivilstatus(civilStatus);
        uploadedFormDTO.setCitizenship(citizenShip);
        uploadedFormDTO.setEmail(email);
        uploadedFormDTO.setCellno(cellNo);
        uploadedFormDTO.setAddress(address);

        new UpdateApplicationValidationTask(uploadedFormDTO).execute(getApplicationValidation());
    }

    /**
     * The method for create url
     *
     * @return
     */
    private String getApplicationValidation() {
        return AppApi.URL_NAME + AppApi.APPLICATION_VALIDATION;
    }

    private class UpdateApplicationValidationTask extends AsyncTask<String, Void, UploadedFormDTO> {
        UploadedFormDTO uploadedFormDTO;

        public UpdateApplicationValidationTask(UploadedFormDTO uploadedFormDTO) {
            this.uploadedFormDTO = uploadedFormDTO;
        }

        @Override
        protected UploadedFormDTO doInBackground(String... url) {
            Log.i(TAG, "URL: " + url[0] + ", name: " + uploadedFormDTO.getName());
            return new UpdateApplicationValidationPostFetchr().fetchItems(url[0], uploadedFormDTO, mAccessToken);


        }

        @Override
        protected void onPostExecute(UploadedFormDTO uploadedFormDTO) {
            Log.i(TAG, "onPostExecute uploadedFormDTO: " + uploadedFormDTO);

            if (getActivity() == null)
                return;

            Log.d(TAG, "uploadedFormDTO id " + uploadedFormDTO.getId());

            int id = uploadedFormDTO.getId();

            if (id > 0){
                // set the message as complete validation
                showAssistant("validation confirmed");
            }else{
                showAssistant(null);
            }

            getActivity().finish();
        }
    }

    private void showAssistant(String result){
        Log.i(TAG, "result: " + result);
        Intent i = new Intent();
        i.putExtra(EXTRA_VALIDATION_RESULT, result);
        getActivity().setResult(Activity.RESULT_OK, i);
    }

}

