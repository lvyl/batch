package com.lvyl.mapper;

import java.util.Map;

public interface LotteryTicketMapper {
    void insertData(Map map);

    String selectMaxTerm();

    Integer selectByTerm(String term);
}
