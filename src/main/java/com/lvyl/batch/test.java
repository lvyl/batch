package com.lvyl.batch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author yllv
 * @datetime 2019-04-15 11:17
 * @desc
 */
@SuppressWarnings("all")
public class test {
    public static void main(String[] args) {
        /**
         * 获取最新一期的号码
         */
        int newTerm = 0;
        String result = sendPost("http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx", "_ltype=4&_term=");
        JSONObject jsonObjectEventName = new JSONObject(result.substring(1, result.length() - 1));
        JSONArray eventNameList = jsonObjectEventName.getJSONArray("eventName");
        newTerm = Integer.parseInt(eventNameList.get(0)+"");
        /**
         * 从最新一期循环到 07001
         */
        for(int term=newTerm;term>7001;term--){
            String termStr = term<10001?"0"+term:""+term;
            /**
             * 判断这一期是否在开奖号码
             */
            String isExits = sendPost("http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx", "_ltype=4&_term="+termStr);
            if(isExits.length()<20){

                break;
            }
        }
    }
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();

            String line;
            for(in = new BufferedReader(new InputStreamReader(conn.getInputStream())); (line = in.readLine()) != null; result = result + line) {
            }
        } catch (Exception var16) {
            System.out.println("发送 POST 请求出现异常！" + var16);
            var16.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }

        }

        return result;
    }
}
