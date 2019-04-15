package com.lvyl.batch.services;

import com.lvyl.batch.actions.GetLotteryTicketDataTask;
import com.lvyl.batch.mapper.LotteryTicketMapper;
import com.lvyl.batch.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class Service01 {
    Logger logger = LoggerFactory.getLogger(GetLotteryTicketDataTask.class);
    @Autowired
    private LotteryTicketMapper lotteryTicketMapper;
    @Autowired
    private HttpUtil httpUtil;

    /**
     * 每天八点进行查询是否开奖
     */
    public void function01(){
        this.logger.info("service01 begin!");
        /**
         * 获取最新一期的号码
         */
        int newTerm = 0;
        String result = httpUtil.sendPost("http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx", "_ltype=4&_term=");
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
            String isExits = httpUtil.sendPost("http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx", "_ltype=4&_term="+termStr);
            logger.debug(termStr+":"+isExits);
            if(isExits.length()<20){
                logger.debug(termStr+":这一期不是开奖号码！");
                continue;
            }
            /**
             * 查询数据库 是否有这一期号码 没有则入库
             */
            Integer count = lotteryTicketMapper.selectByTerm(termStr);
            if (count == 0) {
                this.putData(termStr);
            }
        }


        this.logger.info("service01 end!");
    }

    /**
     * 下期号码预测  100
     */
    public void function03(){
        this.logger.info("service03 begin!");
        /*
         *   查出没有预测号码的期号
         */
        List noNumList = lotteryTicketMapper.queryAllTermNoNum100();
        /*
         * 循环开始预测
         */
        for(int i =0;i<noNumList.size();i++) {
            if (null == noNumList.get(i)) {
                break;
            }
            String term = String.valueOf(noNumList.get(i));
            /*
             * 查出这一期往前的十期号码
             */
            List tenNumPrev = lotteryTicketMapper.queryNumhunPrev(term);
            /*
             * 统计每一位出现的号码数量
             */
            Map[] numTotalCount = new Map[7];
            for (int in = 0; in < 7; in++) {
                Map<String, Integer> ticketMap = new HashMap();
                for (int ind = 0; ind < tenNumPrev.size(); ind++) {
                    Map resultMap = (Map) tenNumPrev.get(ind);
                    if (ticketMap.containsKey(resultMap.get("ticket0" + (in + 1)))) {
                        ticketMap.put(resultMap.get("ticket0" + (in + 1)) + "", ticketMap.get(resultMap.get("ticket0" + (in + 1)) + "") + 1);
                    } else {
                        ticketMap.put(resultMap.get("ticket0" + (in + 1)) + "", 1);
                    }
                }
                numTotalCount[in]=ticketMap;
            }

            /*
             *开始计算每一位的最大数字 如果有重复  则记录方式如 01/02
             */
            String[] ticket = {"0", "0", "0", "0", "0", "0", "0"};
            int[] count = {0, 0, 0, 0, 0, 0, 0};
            for (int ind = 0; ind < numTotalCount.length; ind++) {
                Map<String, Integer> resultMap = (Map) numTotalCount[ind];
                Set<String> keys = resultMap.keySet();
                for (String key : keys) {
                    int ticketCount = resultMap.get(key);
                    if (ticketCount > count[ind]) {
                        if (count[ind] == 0) {
                            ticket[ind] = key;
                            count[ind] = ticketCount;
                        } else {
                            ticket[ind] = key;
                            count[ind] = ticketCount;
                        }
                    } else if (ticketCount == count[ind]) {
                        ticket[ind] = ticket[ind] + "/" + key;
                    }
                }
            }
            Map map = new HashMap();
            map.put("Term", term);
            map.put("Date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
            map.put("Ticket01", ticket[0]);
            map.put("Ticket02", ticket[1]);
            map.put("Ticket03", ticket[2]);
            map.put("Ticket04", ticket[3]);
            map.put("Ticket05", ticket[4]);
            map.put("Ticket06", ticket[5]);
            map.put("Ticket07", ticket[6]);
            this.lotteryTicketMapper.insertData100(map);
            logger.info(term+"期入库成功！");
        }
        this.logger.info("service03 end!");
    }

    /**
     * 下期号码预测  10
     */
    public void function02(){
        this.logger.info("service02 begin!");
        /*
         *   查出没有预测号码的期号
         */
        List noNumList = lotteryTicketMapper.queryAllTermNoNum();
        /*
         * 循环开始预测
         */
        for(int i =0;i<noNumList.size();i++) {
            if (null == noNumList.get(i)) {
                break;
            }
            String term = String.valueOf(noNumList.get(i));
            /*
             * 查出这一期往前的十期号码
             */
            List tenNumPrev = lotteryTicketMapper.queryNumTenPrev(term);
            /*
             * 统计每一位出现的号码数量
             */
            Map[] numTotalCount = new Map[7];
            for (int in = 0; in < 7; in++) {
                Map<String, Integer> ticketMap = new HashMap();
                for (int ind = 0; ind < tenNumPrev.size(); ind++) {
                    Map resultMap = (Map) tenNumPrev.get(ind);
                    if (ticketMap.containsKey(resultMap.get("ticket0" + (in + 1)))) {
                        ticketMap.put(resultMap.get("ticket0" + (in + 1)) + "", ticketMap.get(resultMap.get("ticket0" + (in + 1)) + "") + 1);
                    } else {
                        ticketMap.put(resultMap.get("ticket0" + (in + 1)) + "", 1);
                    }
                }
                numTotalCount[in]=ticketMap;
            }

            /*
             *开始计算每一位的最大数字 如果有重复  则记录方式如 01/02
             */
            String[] ticket = {"0", "0", "0", "0", "0", "0", "0"};
            int[] count = {0, 0, 0, 0, 0, 0, 0};
            for (int ind = 0; ind < numTotalCount.length; ind++) {
                Map<String, Integer> resultMap = (Map) numTotalCount[ind];
                Set<String> keys = resultMap.keySet();
                for (String key : keys) {
                    int ticketCount = resultMap.get(key);
                    if (ticketCount > count[ind]) {
                        if (count[ind] == 0) {
                            ticket[ind] = key;
                            count[ind] = ticketCount;
                        } else {
                            ticket[ind] = key;
                            count[ind] = ticketCount;
                        }
                    } else if (ticketCount == count[ind]) {
                        ticket[ind] = ticket[ind] + "/" + key;
                    }
                }
            }
            Map map = new HashMap();
            map.put("Term", term);
            map.put("Date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
            map.put("Ticket01", ticket[0]);
            map.put("Ticket02", ticket[1]);
            map.put("Ticket03", ticket[2]);
            map.put("Ticket04", ticket[3]);
            map.put("Ticket05", ticket[4]);
            map.put("Ticket06", ticket[5]);
            map.put("Ticket07", ticket[6]);
            this.lotteryTicketMapper.insertData10(map);
            logger.info(term+"期入库成功！");
        }
        this.logger.info("service02 end!");
    }

    /**
     * 计算根据10期号码预测的号码的正确率
     */
    public void function04(){
        /*
         * 取出rate为空的term
         */
        List termList = lotteryTicketMapper.queryAllNoneRate10();
        /*
         * 遍历termList计算rate
         */
        for(int i=0;i<termList.size();i++){
            String term = termList.get(i)+"";
            /*
             * 取出与term对应的期号的中奖号码
             */
            List oneList = lotteryTicketMapper.queryNiceNumByTerm10(term);
            if(oneList.size()==0){
                continue;
            }
            /*
             *把前五个放进qianwunicenum中 后两个放进houernicenum中
             */
            List qianwunicenum = new ArrayList();
            List houernicenum = new ArrayList();
            for(int in=0;in<7;in++){
                Map tempMap = (Map) oneList.get(0);
                for(int ind=1;ind<=7;ind++){
                    if(ind<6){
                        qianwunicenum.add(tempMap.get("ticket0"+ind));
                    }else{
                        houernicenum.add(tempMap.get("ticket0"+ind));
                    }
                }
            }
            /*
             * 取出预测号码  并计算所有组合方式  放入yuceZuheList中
             */
            List yuceList = lotteryTicketMapper.queryYuCeNumByTerm10(term);

            List ticket01 = new ArrayList();
            List ticket02 = new ArrayList();
            List ticket03 = new ArrayList();
            List ticket04 = new ArrayList();
            List ticket05 = new ArrayList();
            List ticket06 = new ArrayList();
            List ticket07 = new ArrayList();

            List[] ticket1_7 = {ticket01,ticket02,ticket03,ticket04,ticket05,ticket06,ticket07};

            for(int in=0;in<yuceList.size();in++){
                Map tempMap = (Map)yuceList.get(in);
                for(int ind=1;ind<=7;ind++){
                    String tempNum = tempMap.get("ticket0"+ind)+"";
                    String[] tempSZ = tempNum.split("/");
                    for(String str : tempSZ){
                        ticket1_7[ind-1].add(str);
                    }
                }
            }

            List<Map<String,String>> yuceZuheList = new ArrayList();
            //index
            allPLZH(null,0,ticket1_7,yuceZuheList);

            /*
             *遍历 yuceZuheList并与中将号码 对比得出 奖金
             */
            int allmoney=0;//奖金
            for(int in=0;in<yuceZuheList.size();in++){
                Map tempMap = yuceZuheList.get(in);
                int qianwucount=0;
                int houercount=0;
                for(int ind=1;ind<=7;ind++){
                    if(ind<6){
                        if(qianwunicenum.contains(tempMap.get("ticket0"+ind)+"")){
                            qianwucount++;
                        }
                    }else{
                        if(houernicenum.contains(tempMap.get("ticket0"+ind)+"")){
                            houercount++;
                        }
                    }
                }
                /*
                 *计算奖金
                 */
                if(qianwucount==0&&houercount==2){
                    allmoney=allmoney+5;
                    break;
                }
                if(qianwucount+houercount==3){
                    allmoney=allmoney+5;
                    break;
                }
                if((qianwucount==3&&houercount==1)||(qianwucount==2&&houercount==2)){
                    allmoney=allmoney+10;
                    break;
                }
                if((qianwucount==4&&houercount==0)||(qianwucount==3&&houercount==2)){
                    allmoney=allmoney+100;
                    break;
                }
                if(qianwucount==4&&houercount==1){
                    allmoney=allmoney+600;
                    break;
                }
                if(qianwucount==4&&houercount==2){
                    allmoney=allmoney+3000;
                    break;
                }
                if(qianwucount==4&&houercount==2){
                    allmoney=allmoney+3000;
                    break;
                }
                if(qianwucount==5){
                    allmoney=allmoney+5000000;
                }
            }
            /*
             *计算rate
             */
//            float rate = ((float)allmoney)/((float)yuceZuheList.size()*2);
//            System.out.println("allmoney:"+allmoney+"   chengben:"+yuceZuheList.size()*2+"   "+(int)(rate*100));
//            Map paramMap = new HashMap();
//            paramMap.put("allmoney",allmoney);
//            paramMap.put("chengben",yuceZuheList.size()*2);
//            paramMap.put("rate",(int)(rate*100)+"%");
//            paramMap.put("ticketterm",term);
//            lotteryTicketMapper.updateRate10(paramMap);
//            logger.info(term+"入库成功！");
        }
    }
    /*
     * 递归计算所有可能的排列组合
     */
    void allPLZH(List temp,int index,List[] data,List result){
        List tempList = temp==null?new ArrayList():temp;
        if(index==7){
            result.add(tempList);
        }
        List dataList = (List) data[index];
        for(int i=0;i<dataList.size();i++){
            tempList.add(dataList.get(i));
            allPLZH(tempList,index+1,data,result);
        }
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
        Integer termRes = this.lotteryTicketMapper.selectByTerm(termParam);
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
            this.lotteryTicketMapper.insertData(map);
            this.logger.info(term + ":这一期号码已入库！");
        }
    }
}
