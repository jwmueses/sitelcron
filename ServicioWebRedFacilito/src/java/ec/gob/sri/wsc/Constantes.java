/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.gob.sri.wsc;

import java.text.SimpleDateFormat;

/**
 *
 * @author jorjoluiso
 */

public class Constantes
{
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

  public static final int INTENTOS_CONEXION_WS = 3;
  public static final int INTENTOS_RESPUESTA_AUTORIZACION_WS = 5;
   
}