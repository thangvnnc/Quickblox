package com.example.quickblox;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class ChatActivity extends AppCompatActivity {

    private QBChatService chatService = null;
    private QBUser qbuserLogin = null;
    private Button btnLogin = null;
    private Button btnLoginChat = null;
    private Button btnLogoutChat = null;
    private Button btnIsLogin = null;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;

        initQBChat();

        btnLogin = findViewById(R.id.btnLogin);
        btnLoginChat = findViewById(R.id.btnLoginChat);
        btnLogoutChat = findViewById(R.id.btnLogoutChat);
        btnIsLogin = findViewById(R.id.btnIsLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnIsLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoggedIn = chatService.isLoggedIn();
                Toast.makeText(context, isLoggedIn+"", Toast.LENGTH_SHORT).show();
            }
        });

        btnLoginChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginChat();
            }
        });

        btnLogoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        chatService.logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                chatService.destroy();
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        QBUser user = new QBUser("tnt", "00000000");
        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                qbuserLogin = qbUser;
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginChat() {
        qbuserLogin.setPassword("00000000");
        chatService = QBChatService.getInstance();

        chatService.login(qbuserLogin, new QBEntityCallback() {

            @Override
            public void onSuccess(Object o, Bundle bundle) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException errors) {
                errors.printStackTrace();
                Toast.makeText(context, errors.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initQBChat() {
        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultPacketReplyTimeout(10000);//set reply timeout in milliseconds for connection's packet.
        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatServiceConfigurationBuilder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);
        QBChatService.getInstance().setReconnectionAllowed(true);

        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void authenticated(XMPPConnection xmppConnection, boolean b) {
                Toast.makeText(context, "authenticated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionClosed() {
                Toast.makeText(context, "connectionClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                // connection closed on error. It will be established soon
                Toast.makeText(context, "connectionClosedOnError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reconnectingIn(int seconds) {
                Toast.makeText(context, "reconnectingIn", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reconnectionSuccessful() {
                Toast.makeText(context, "reconnectionSuccessful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Toast.makeText(context, "reconnectionFailed", Toast.LENGTH_SHORT).show();
            }
        };

        QBChatService.getInstance().addConnectionListener(connectionListener);
    }
}
