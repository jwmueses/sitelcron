/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.com.saitel.gomax.utils;

import com.google.gson.Gson;
import ec.com.saitel.gomax.dao.DocumentoPagomedioDAO;
import ec.com.saitel.gomax.model.DatosPagomedio;
import ec.com.saitel.gomax.model.PagosPagomedio;
import ec.com.saitel.gomax.model.PagosPagomedio.Transactions;
import ec.com.saitel.gomax.model.RespuestaPagomedio;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 *
 * @author pc01
 */
public class Pagomedio {
    
 public String crearEnlacePago(DatosPagomedio datosPagomedio, String tokenPagomedio) {
        String apiUrl = "https://api.abitmedia.cloud/pagomedios/v2/payment-requests";
        String urlPago = "";
        Gson gson = new Gson();
        String requestBody = gson.toJson(datosPagomedio);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + tokenPagomedio);
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    RespuestaPagomedio respuestaPagomedio = gson.fromJson(response.toString(), RespuestaPagomedio.class);
                    if (respuestaPagomedio.isSuccess() && respuestaPagomedio.getStatus() == 201) {
                        RespuestaPagomedio.Data data = respuestaPagomedio.getData();
                        DocumentoPagomedioDAO documentoPagomedioDAO = new DocumentoPagomedioDAO();
                        documentoPagomedioDAO.guardar(datosPagomedio);
                        documentoPagomedioDAO.cerrar();
                        urlPago = data.getUrl();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return urlPago;
    }
    
    public boolean actualizarDatosPago(String tokenPagomedio, String idClienteSuscripcionGomax){
        
 String apiUrl = "https://api.abitmedia.cloud/pagomedios/v2/payment-requests";
        String descripcion = "PAGO%20DEL%20SERVICIO%20DE%20TV%20SAITEL.%20TV" + idClienteSuscripcionGomax;
        Gson gson = new Gson();

        try {
            URL url = new URL(apiUrl + "?integration=true&description=" + descripcion);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + tokenPagomedio);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    PagosPagomedio pagosPagomedio = gson.fromJson(response.toString(), PagosPagomedio.class);
                    if (pagosPagomedio.isSuccess() && pagosPagomedio.getStatus() == 200) {
                        Optional<Transactions> transaction = pagosPagomedio.getData().stream()
                                .filter(data -> data.getStatus() == 1)
                                .findFirst()
                                .flatMap(data -> data.getTransactions().stream()
                                        .filter(tr -> tr.getStatus() == 1)
                                        .findFirst());
                        if (transaction.isPresent()) {
                            DocumentoPagomedioDAO documentoPagomedioDAO = new DocumentoPagomedioDAO();
                            documentoPagomedioDAO.actualizarPago(transaction.get(), idClienteSuscripcionGomax);
                            documentoPagomedioDAO.cerrar();
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
