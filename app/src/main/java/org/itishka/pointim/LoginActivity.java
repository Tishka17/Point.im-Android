package org.itishka.pointim;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.LoginResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Button mButton;
        private EditText mLoginEdit;
        private EditText mPasswordEdit;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
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
                        ConnectionManager.getInstance().pointAuthService.login(mLoginEdit.getText().toString(), mPasswordEdit.getText().toString(), new Callback<LoginResult>() {
                            @Override
                            public void success(LoginResult result, Response response) {
                                if (result.isSuccess()) {
                                    result.login = mLoginEdit.getText().toString();
                                    ConnectionManager.getInstance().updateAuthorization(getActivity(), result);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), result.error, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getActivity(), error.getBody().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            return rootView;
        }
    }
}
