package com.lvyl.batch.mapper;

import java.util.List;
import java.util.Map;

public interface LotteryTicketMapper {
    void insertData(Map map);

    String selectMaxTerm();

    Integer selectByTerm(String term);

    List queryAllTermNoNum();

    List queryNumTenPrev(String term);

    void insertData10(Map map);

    List queryAllTermNoNum100();

    List queryNumhunPrev(String term);

    void insertData100(Map map);

    List queryAllNoneRate10();

    List queryNiceNumByTerm10(String term);

    List queryYuCeNumByTerm10(String term);

    void updateRate10(Map map);

    List queryAllNoneRate100();

    List queryYuCeNumByTerm100(String term);

    void updateRate100(Map map);
}
