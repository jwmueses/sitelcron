/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.com.saitel.gomax.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

/**
 *
 * @author pc01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosPagomedio {

    public DatosPagomedio() {
    }
    boolean integration;
    public class Third {

        public Third() {
        }
        
        String document;
        String document_type;
        String name;
        String email;
        String phones;
        String address;
        String type;
        
        public String getDocument() {
            return document;
        }

        public String getDocument_type() {
            return document_type;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhones() {
            return phones;
        }

        public String getAddress() {
            return address;
        }

        public String getType() {
            return type;
        }
    }


    Third third;
    int generate_invoice;
    String description;
    Double amount;
    Double amount_with_tax;
    Double amount_without_tax;
    Double tax_value;
    ArrayList<String> settings;
    String notify_url;
    String custom_value;

    public boolean isIntegration() {
        return integration;
    }

    public int getGenerate_invoice() {
        return generate_invoice;
    }

    public String getDescription() {
        return description;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getAmount_with_tax() {
        return amount_with_tax;
    }

    public Double getAmount_without_tax() {
        return amount_without_tax;
    }

    public Double getTax_value() {
        return tax_value;
    }

    public ArrayList<String> getSettings() {
        return settings;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getCustom_value() {
        return custom_value;
    }
    
    public Third getThird() {
        return third;
    }

    public void setThird(Third third) {
        this.third = third;
    }
    
}
