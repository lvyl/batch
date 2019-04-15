package com.lvyl.batch.tcpserver;

import com.lvyl.batch.services.Service01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpServer {
    Logger logger = LoggerFactory.getLogger(TcpServer.class);
    @Autowired
    private Service01 service01;
    private ServerSocket serverSocket;
    private BufferedReader bufferedReader;

    {
        Thread tcpThread = new Thread(new TcpThread());
        tcpThread.start();
    }

    public class TcpThread implements Runnable{

        @Override
        public void run() {
            Socket socket = null;
            try{
                serverSocket = new ServerSocket(9979);
                logger.info("tcp启动成功！");
                while(true){
                    socket = serverSocket.accept();
                    logger.info(socket.getInetAddress()+":连接成功！");
                    /**
                     * 打印服务信息
                     */
                    printMessage(socket);
                    /**
                     * 接受消息
                     */
                    getMessage(socket);
                }
            }catch (Exception e){
                logger.error("tcp服务启动失败："+e);
            }finally {
                if(null == socket ){
                    try {
                        socket.close();
                        logger.info("socket close success!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        public void getMessage(Socket socket){
            while(true){
                String message = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    message = bufferedReader.readLine();
                    if(null == message){
                        break;
                    }
//                    System.out.println(message);
//                    if(message.equals("01")){
//                        service01.function01();
//                    }else if(message.equals("02")){
//                        service01.function02();
//                    }else if(message.equals("03")){
//                        service01.function03();
//                    }else if(message.equals("04")){
//                        service01.function04();
//                    }
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bufferedWriter.write(message+"\r\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    logger.info("accept failed!"+e);
                }
            }
        }

        public void printMessage(Socket socket) throws Exception{
            String[] serviceMsg = {
                    /*01*/        "新开奖号码入库",
                    /*02*/        "根据前十期预测下期号码",
                    /*03*/        "根据前一百期预测下期号码",
                    /*04*/        "前十期预测的准确率"
            };
            String serviceStr = "";
            for(int i=0;i<100;i++){
                serviceStr = serviceStr +"*";
            }
            serviceStr = serviceStr + "\r\n";
            for(int i=0;i<serviceMsg.length;i++){
                serviceStr = serviceStr+"*   "+(i>8?i:"0"+(i+1))+":"+serviceMsg[i];
                for(int in=0;in<(92-serviceMsg[i].length()*2);in++){
                    serviceStr = serviceStr + " ";
                }
                serviceStr = serviceStr + "*\r\n";
            }
            for(int i=0;i<100;i++){
                serviceStr = serviceStr +"*";
            }
            serviceStr = serviceStr + "\r\n";
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
            bufferedWriter.write(serviceStr);
            bufferedWriter.flush();
        }
    }
}
