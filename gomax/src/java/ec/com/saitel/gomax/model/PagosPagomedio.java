package ec.com.saitel.gomax.model;


import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pc01
 */
public class PagosPagomedio {
    
    public class Data {
        
        int status;
        List<Transactions> transactions;
        
        public void setStatus(int status) {
            this.status = status;
        }

        public void setTransactions(List<Transactions> transactions) {
            this.transactions = transactions;
        }

        public int getStatus() {
            return status;
        }

        public List<Transactions> getTransactions() {
            return transactions;
        }

        public Data() {
        }
    }

    public class Transactions{

        int status;
        String acquirer;
        String card_brand;
        String payment_id;
        String merchant_transaction_id;
        
        public Transactions() {
        }
        
        public void setPayment_id(String payment_id) {
            this.payment_id = payment_id;
        }

        public void setMerchant_transaction_id(String merchant_transaction_id) {
            this.merchant_transaction_id = merchant_transaction_id;
        }

        public String getPayment_id() {
            return payment_id;
        }

        public String getMerchant_transaction_id() {
            return merchant_transaction_id;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }

        public void setAcquirer(String acquirer) {
            this.acquirer = acquirer;
        }

        public void setCard_brand(String card_brand) {
            this.card_brand = card_brand;
        }

        public int getStatus() {
            return status;
        }

        public String getAcquirer() {
            return acquirer;
        }

        public String getCard_brand() {
            return card_brand;
        }


    }
    
    boolean success;
    int status;
    List<Data> data;
    
    public PagosPagomedio() {
    }
    
     public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public List<Data> getData() {
        return data;
    }
}
