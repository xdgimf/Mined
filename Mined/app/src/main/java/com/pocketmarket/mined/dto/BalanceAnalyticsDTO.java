package com.pocketmarket.mined.dto;

import java.math.BigDecimal;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class BalanceAnalyticsDTO {
    private int id;

    private BigDecimal credit;

    private BigDecimal debit;

    public BalanceAnalyticsDTO(){
        super();
    }

    public BalanceAnalyticsDTO(int id, BigDecimal credit, BigDecimal debit){
        this.id = id;
        this.credit = credit;
        this.debit = debit;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

}

