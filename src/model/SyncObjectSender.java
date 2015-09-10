package model;

import generic.Config;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import controller.IMServerController;

import message.Message;

public class SyncObjectSender {

    private String ip;
    private int port;
    private Socket socket = null;

    public SyncObjectSender(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Object sendAndReceive(Object o) throws IOException {
        this.buildSocket();
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(o);

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Object outObject = null;;
        try {
            outObject = in.readObject();
        } catch (ClassNotFoundException e) {
        }
        in.close();
        this.destroySocket();
        return outObject;
    }

    public void send(Object o) throws IOException {
        this.buildSocket();
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(o);
        this.destroySocket();
    }

    private void buildSocket() throws IOException {
        if (this.socket == null) {
            if (Config.USE_SSL) {
                this.socket = SSLSocketFactory.getDefault().createSocket(this.ip, this.port);
                ((SSLSocket) socket).setEnabledCipherSuites(((SSLSocket) socket).getSupportedCipherSuites());
                ((SSLSocket) socket).startHandshake();
            } else {
                this.socket = new Socket(this.ip, this.port);
            }
        }
    }

    public void destroySocket() {
        try {
            this.socket.close();
            this.socket = null;
        } catch (IOException e) {
        }
    }
}
