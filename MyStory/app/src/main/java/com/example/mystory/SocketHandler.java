package com.example.mystory;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketHandler {
    private final String SERVER_IP = "0.tcp.ngrok.io";
    private final int SERVER_PORT = 15972;
    private final int SEGMENT_SIZE = 650000;

    private String mRequest;
    private Socket mSocket;

    SocketHandler(String request) {
        mRequest = request;
        try {
            Log.d("MyStory", "initialize socket");
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            Log.d("MyStory", "ip = " + serverAddress.toString());
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int handler(char[] jsonString) {
        serverWrite(mRequest.toCharArray());
        return serverRead(jsonString);
    }

    private void serverWrite(char[] jsonString) {
        BufferedWriter out;
        int length = jsonString.length;
        int offset = 0;

         try {
             out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()),
                     SEGMENT_SIZE);
             while (offset < length) {
                 int bytesWrite = Math.min(length - offset, SEGMENT_SIZE);
                 out.write(jsonString, offset, bytesWrite);
                 offset += bytesWrite;
             }
             out.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    private int serverRead(char[] jsonString) {
        BufferedReader in;
        int offset = 0;
        int bytesRead;

        try {
            in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            bytesRead = in.read(jsonString, offset, SEGMENT_SIZE);
            while (bytesRead > 0) {
                offset += bytesRead;
                bytesRead = in.read(jsonString, offset, SEGMENT_SIZE);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return offset;
    }
}
