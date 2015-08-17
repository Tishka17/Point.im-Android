package org.itishka.pointim;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import org.itishka.pointim.api.ConnectionManager;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;

public class WebSocketService extends Service {
    WebSocketCall mWebSocketCall;
    WebSocketListener mWebSocketListener = new WebSocketListener() {
        WebSocket mWebSocket;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d("WebSocketService", "onOpen: " + response.code() + ", " + response.message());
            mWebSocket = webSocket;
//            try {
//                Log.d("WebSocketService", "sending: Authorization: " + ConnectionManager.getInstance().loginResult.token);
//                webSocket.sendMessage(WebSocket.PayloadType.TEXT,
//                        new Buffer()
//                                .writeUtf8("Authorization: ")
////                                .writeUtf8("XXXXX"));
//                                .writeUtf8(ConnectionManager.getInstance().loginResult.token));
//
//            } catch (IOException e) {
//                Log.e("WebSocketService", e.getClass() + ": " + e.getMessage());
//            }
        }

        @Override
        public void onFailure(IOException e, Response response) {
            Log.d("WebSocketService", "onFailure");
        }

        @Override
        public void onMessage(BufferedSource payload, WebSocket.PayloadType type) throws IOException {
            Log.d("WebSocketService", "onMessage: " + payload.readUtf8());
            payload.close();
        }

        @Override
        public void onPong(Buffer payload) {
            Log.d("WebSocketService", "onPong: " + payload.readUtf8());
        }

        @Override
        public void onClose(int code, String reason) {
            Log.d("WebSocketService", "onClose: " + code + ", " + reason);
            mWebSocket = null;
        }
    };

    public WebSocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WebSocketService", "onCreate");
        ConnectionManager manager = ConnectionManager.getInstance();
        mWebSocketCall = WebSocketCall.create(
                manager.okHttpClient,
                new Request.Builder()
                        .url(ConnectionManager.POINT_WS)
                        .addHeader("Authorization", manager.loginResult.token)
                        .addHeader("X-CSRF", manager.loginResult.csrf_token)
                        .addHeader("User-Agent", ConnectionManager.USER_AGENT)
                        .build());
        mWebSocketCall.enqueue(mWebSocketListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
