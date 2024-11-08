package com.example.estatepricecalculate;

public class TransactionResult {
    private double buyerInitPayment;
    private double buyerDownPayment;
    private double sellerNetAmount;

    public TransactionResult(double buyerInitPayment, double buyerDownPayment, double sellerNetAmount) {
        this.buyerInitPayment = buyerInitPayment;
        this.buyerDownPayment = buyerDownPayment;
        this.sellerNetAmount = sellerNetAmount;
    }

    public double getBuyerDownPayment() {
        return buyerDownPayment;
    }

    public double getSellerNetAmount() {
        return sellerNetAmount;
    }

    public double getBuyerInitPayment() {
        return buyerInitPayment;
    }

    @Override
    public String toString() {
        return String.format("买方首付款需支付：%.2f 万元\n买方需支付总额：%.2f 万元\n卖方可得金额：%.2f 万元",buyerInitPayment, buyerDownPayment, sellerNetAmount);
    }
}
