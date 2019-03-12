package com.lvyl.actions;

import com.lvyl.mapper.LotteryTicketMapper;
import com.lvyl.utils.ApplicationContextProvider;
import com.lvyl.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;
import java.util.Map;

public class GetLotteryTicketDataTask extends QuartzJobBean {
    Logger logger = LoggerFactory.getLogger(GetLotteryTicketDataTask.class);
    private LotteryTicketMapper lotteryTicket;
    @Autowired
    private HttpUtil httpUtil;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.logger.info("action begin!");
        this.lotteryTicket = (LotteryTicketMapper)ApplicationContextProvider.getBean("lotteryTicket", LotteryTicketMapper.class);
        String result = httpUtil.sendPost("http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx", "_ltype=4&_term=");
        JSONObject jsonObjectEventName = new JSONObject(result.substring(1, result.length() - 1));
        JSONArray eventNameList = jsonObjectEventName.getJSONArray("eventName");

        for(int i = 0; i < eventNameList.length(); ++i) {
            String termTmp = eventNameList.getString(i);
            Integer count = this.lotteryTicket.selectByTerm(termTmp);
            if (count == 0) {
                this.putData(termTmp);
            }
        }

        this.logger.info("action end!");
    }

    void putData(String termParam) {
        String result = null;

        try {
            result = httpUtil.sendPost("http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx", "_ltype=4&_term=" + termParam);
        } catch (Exception var15) {
            this.logger.info("http post send fail");
            return;
        }

        JSONObject jsonObject = new JSONObject(result.substring(1, result.length() - 1));
        String term = jsonObject.getJSONObject("lottery").getString("term");
        Integer termRes = this.lotteryTicket.selectByTerm(termParam);
        if (termRes == 1) {
            this.logger.info(termParam + " term has exist in database!");
        } else {
            String date = jsonObject.getJSONObject("lottery").getString("openTime_fmt1");
            String ticket01 = jsonObject.getJSONObject("lottery").getString("number").substring(0, 2);
            String ticket02 = jsonObject.getJSONObject("lottery").getString("number").substring(3, 5);
            String ticket03 = jsonObject.getJSONObject("lottery").getString("number").substring(6, 8);
            String ticket04 = jsonObject.getJSONObject("lottery").getString("number").substring(9, 11);
            String ticket05 = jsonObject.getJSONObject("lottery").getString("number").substring(12, 14);
            String ticket06 = jsonObject.getJSONObject("lottery").getString("number").substring(15, 17);
            String ticket07 = jsonObject.getJSONObject("lottery").getString("number").substring(18, 20);
            Map map = new HashMap();
            map.put("Term", term);
            map.put("Date", date);
            map.put("Ticket01", ticket01);
            map.put("Ticket02", ticket02);
            map.put("Ticket03", ticket03);
            map.put("Ticket04", ticket04);
            map.put("Ticket05", ticket05);
            map.put("Ticket06", ticket06);
            map.put("Ticket07", ticket07);
            this.lotteryTicket.insertData(map);
            this.logger.info(term + " term has insert success!");
        }
    }
}
