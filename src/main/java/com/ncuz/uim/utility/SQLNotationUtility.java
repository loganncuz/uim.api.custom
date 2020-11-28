package com.ncuz.uim.utility;

import org.springframework.stereotype.Service;

@Service
public class SQLNotationUtility {
    private String where=" where";
    private String limitOne=" limit 1";
    private String fetchOne=" FETCH FIRST 1 ROWS ONLY";

    public String getFetchOne() {
        return fetchOne;
    }

    public void setFetchOne(String fetchOne) {
        this.fetchOne = fetchOne;
    }

    public String getLimitOne() {
        return limitOne;
    }

    public void setLimitOne(String limitOne) {
        this.limitOne = limitOne;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
