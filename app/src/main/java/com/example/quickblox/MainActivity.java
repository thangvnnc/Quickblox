package com.example.quickblox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBHttpConnectionConfig;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    private Context context = null;
    private EditText edtUsernameReg = null;
    private EditText edtPwdReg = null;
    private Button btnReg = null;

    private EditText edtUsername = null;
    private EditText edtPwd = null;
    private Button btnLogin = null;
    private Button btnLogout = null;

    private Button btnIsLogin = null;
    private Button btnGetSession = null;
    private Button btnChat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        initQB();
        initEvent();
    }

    private void initEvent() {
        btnReg = findViewById(R.id.btnRegister);
        edtUsernameReg = findViewById(R.id.edtUsernameReg);
        edtPwdReg = findViewById(R.id.edtPwdReg);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        edtUsername = findViewById(R.id.edtUsername);
        edtPwd = findViewById(R.id.edtPwd);

        btnChat = findViewById(R.id.btnChat);
        btnIsLogin = findViewById(R.id.btnIsLogin);
        btnGetSession = findViewById(R.id.btnGetSession);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnIsLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, isSignIn() + "", Toast.LENGTH_SHORT).show();
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                startActivity(intent);
            }
        });

        btnGetSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QBSessionParameters sessionParameters = QBSessionManager.getInstance().getSessionParameters();
                if (sessionParameters == null)
                {
                    Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
                    return;
                }
                sessionParameters.getUserId(); //stores user Id if user signed in via email
                sessionParameters.getUserEmail(); //stores user's Email if user signed in via email
                sessionParameters.getAccessToken(); //stores access token for social net if user signed in via social provider
                sessionParameters.getSocialProvider(); //stores social provider if user signed in via this provider
                Toast.makeText(context, sessionParameters.getUserLogin(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initQB() {
        QBSettings.getInstance().setAccountKey(QB.ACCOUNT_KEY);
        QBSettings.getInstance().init(getApplicationContext(), QB.APP_ID, QB.AUTH_KEY, QB.AUTH_SECRET);
        QBSettings.getInstance().setEndpoints(QB.API_DOMAIN, QB.CHAT_DOMAIN, ServiceZone.PRODUCTION);

        QBHttpConnectionConfig.setConnectTimeout(300000); //timeout value in milliseconds.
        QBHttpConnectionConfig.setReadTimeout(300000); //timeout value in milliseconds.

        QBSessionManager.getInstance().addListener(new QBSessionManager.QBSessionListener() {
            @Override
            public void onSessionCreated(QBSession session) {
                //calls when session was created firstly or after it has been expired
                Toast.makeText(context, "onSessionCreated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSessionUpdated(QBSessionParameters sessionParameters) {
                //calls when user signed in or signed up
                //QBSessionParameters stores information about signed in user.
                Toast.makeText(context, "onSessionUpdated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSessionDeleted() {
                //calls when user signed Out or session was deleted
                Toast.makeText(context, "onSessionDeleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSessionRestored(QBSession session) {
                //calls when session was restored from local storage
                Toast.makeText(context, "onSessionRestored", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSessionExpired() {
                //calls when session is expired
                Toast.makeText(context, "onSessionExpired", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderSessionExpired(String provider) {
                //calls when provider's access token is expired or invalid
                Toast.makeText(context, "onProviderSessionExpired", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void register() {
        String username = edtUsernameReg.getText().toString();
        String password = edtPwdReg.getText().toString();

        QBUser qbUser = new QBUser();
        qbUser.setLogin(username);
        qbUser.setPassword(password);
        QBUsers.signUpSignInTask(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException error) {
                error.printStackTrace();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        String username = edtUsername.getText().toString();
        String password = edtPwd.getText().toString();

        QBUser qbUser = new QBUser();
        qbUser.setLogin(username);
        qbUser.setPassword(password);
        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException error) {
                error.printStackTrace();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isSignIn() {
        return QBSessionManager.getInstance().getSessionParameters() != null;
    }
}
