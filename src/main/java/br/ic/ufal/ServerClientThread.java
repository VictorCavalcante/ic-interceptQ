package br.ic.ufal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerClientThread extends Thread {

    Socket clientSocket;
    private ServerRequestUtil serverRequest;

    ServerClientThread(Socket clientSocket, ServerRequestUtil serverRequest) {
        this.clientSocket = clientSocket;
        this.serverRequest = serverRequest;
    }

    public void run() {

        DataOutputStream outToClient = null; //Get OUT-handle
        BufferedReader inFromClient = null; //Get IN-handle
        String requestInput;
        String[] requestParams;
        String reqMethod;
        String reqUrl;
        String response = "";

        try {
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean connected = true;

        while (connected) {
            try {
                System.out.println("---------------------------------------------");
                requestInput = inFromClient.readLine();

                if (!requestInput.equals("EXIT")) {
                    requestParams = requestInput.split(" ");
                    reqMethod = requestParams[0];
                    reqUrl = requestParams[1];

                    if (reqMethod.equals("GET")) {
                        response = this.serverRequest.sendGet(reqUrl, null);
                    } else if (reqMethod.equals("POST")) {
                        response = this.serverRequest.sendPost(reqUrl, null);
                    }

                    //write back to Socket
                    outToClient.writeBytes(response + '\n');
                } else {
                    connected = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                connected = false;
            }
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
