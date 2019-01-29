package android.ktodo.com.javalib;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FirstSocket {
    public static void main(String[] args) throws Exception {
        String host = "14.215.177.3";
        int port = 80;
        String CONTENT = "GET http://www.baidu.com/  HTTP/1.1\r\nHost: www.baidu.com\r\n\r\n";

        try {
//            Socket socket = new Socket(host, port);
//            Writer writer = new OutputStreamWriter(socket.getOutputStream());
//            writer.write("hello from client");
//            writer.flush();
//            writer.close();
//            socket.close();
//            OutputStream out = socket.getOutputStream() ;
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
//            out.write(CONTENT.getBytes());  //发送数据
//            int d = -1 ;
//            while((d=in.read())!=-1){       //接收
//                System.out.print((char)d);  //输出到控制台
//            }
            System.out.print("000000000");  //输出到控制台
        } catch (Exception e) {
            e.printStackTrace();
        }

        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executorService.execute(new ClientThread());
    }

    public static class ClientThread implements Runnable {

        @Override
        public void run() {
            String host = "www.baidu.com";
            int port = 80;
            try {
                Socket socket = new Socket(host, port);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
