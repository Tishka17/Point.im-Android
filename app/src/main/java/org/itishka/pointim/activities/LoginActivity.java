package org.itishka.pointim.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.itishka.pointim.R;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.LoginResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Button mButton;
        private EditText mLoginEdit;
        private EditText mPasswordEdit;
        private MaterialDialog mProgressDialog;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            mProgressDialog = new MaterialDialog.Builder(getActivity())
                    .cancelable(false)
                    .customView(R.layout.dialog_progress, false)
                    .build();

            mLoginEdit = (EditText) rootView.findViewById(R.id.login);
            mPasswordEdit = (EditText) rootView.findViewById(R.id.password);
            mButton = (Button) rootView.findViewById(R.id.button);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mLoginEdit.getText().toString().isEmpty()) {
                        mLoginEdit.setError(getString(R.string.error_field_required));
                        mPasswordEdit.requestFocus();
                    } else if (mPasswordEdit.getText().toString().isEmpty()) {
                        mPasswordEdit.setError(getString(R.string.error_field_required));
                        mPasswordEdit.requestFocus();
                    } else {
                        mProgressDialog.show();
                        ConnectionManager.getInstance().pointAuthService.login(mLoginEdit.getText().toString(), mPasswordEdit.getText().toString(), new Callback<LoginResult>() {
                            @Override
                            public void success(LoginResult result, Response response) {
                                if (result.isSuccess()) {
                                    result.login = mLoginEdit.getText().toString();
                                    ConnectionManager.getInstance().updateAuthorization(getActivity(), result);
                                    getActivity().finish();
                                    mProgressDialog.hide();
                                } else {
                                    Toast.makeText(getActivity(), result.error, Toast.LENGTH_SHORT).show();
                                    mProgressDialog.hide();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getActivity(), error.getBody().toString(), Toast.LENGTH_SHORT).show();
                                mProgressDialog.hide();
                            }
                        });
                    }
                }
            });
            return rootView;
        }
    }
}
