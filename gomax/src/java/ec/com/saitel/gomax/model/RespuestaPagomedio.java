/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.com.saitel.gomax.model;

/**
 *
 * @author pc01
 */
public class RespuestaPagomedio {

    public class Data{

        String url;
        String token;
        
        public String getUrl() {
            return url;
        }

        public String getToken() {
            return token;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
    
    boolean success;
    int status;
    Data data;
    
        public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }
}
