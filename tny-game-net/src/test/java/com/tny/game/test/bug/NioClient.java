package com.tny.game.test.bug;

import com.tny.game.test.bug.Bug.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NioClient {

    private Socket socket = null;

    public void bind(String host, int port) {
        try {
            this.socket = new Socket("127.0.0.1", 9000);
            // this.socket.configureBlocking(false);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.print("receive to server");
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = null;
            while (true && reader != null) {
                msg = reader.readLine();
                if (msg.length() > 0) {
                    System.out.println(msg);
                    break;
                }
            }
            // this.socket.close();
            // this.socket.finishConnect();
            // this.socket.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // new NioClient().bind("127.0.0.1", 9000);

        Client client = new Client();
        client.connect(8080);
        // client.connect(8081);
        // client.close();
    }

}
