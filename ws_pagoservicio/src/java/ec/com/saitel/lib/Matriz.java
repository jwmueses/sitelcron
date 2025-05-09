/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.saitel.lib;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desarrollo
 */
public class Matriz {
    public static int enMatriz(String mat[][], String clave, int col)
    {
        int p=-1;
        if(mat!=null){
            for(int i=0; i<mat.length; i++){
                if(mat[i][col].compareTo(clave)==0){
                    p = i;
                    break;
                }
            }
        }
        return p;
    }
    
    public static List enMatrizTodo(String mat[][], String clave, int col)
    {
        List claves = new ArrayList();
        if(mat!=null){
            for(int i=0; i<mat.length; i++){
                if(mat[i][col].compareTo(clave)==0){
                    claves.add(i);
                }
            }
        }
        return claves;
    }
    
    public static int enMatriz(String mat[][], String clave[], int col[])
    {
        int p=-1;
        if(mat!=null){
            for(int i=0; i<mat.length; i++){
                if(mat[i][col[0]].compareTo(clave[0])==0 && mat[i][col[1]].compareTo(clave[1])==0){
                    p = i;
                    break;
                }
            }
        }
        return p;
    }
 
    public static String[][] poner(String mat[][], String vector[])
    {
        int i = mat!=null ? mat.length+1 : 1;
        String matTemp[][] = new String[i][3];
        if(mat!=null){
            System.arraycopy(mat, 0, matTemp, 0, mat.length);
        }
        for(int j=0; j<vector.length; j++){
            matTemp[i-1][j] = vector[j];
        }
        return matTemp;
    }
    
    public static String[][] ResultSetAMatriz(ResultSet rs)
    {        
        try{
            /*filas*/
            rs.last();
            int fil = rs.getRow();
            rs.beforeFirst();
            /*columnas*/
            ResultSetMetaData mdata = rs.getMetaData();
            int col = mdata.getColumnCount();
            /*parsear*/
            String ma[][] = new String[fil][col+2];
            int i=0;
            int k=0;
            int j=1;
            while(rs.next()){
                for(j=1; j<=col; j++) {
                    k = j-1;
                    ma[i][k] = (rs.getString(j)!=null) ? rs.getString(j) : "";
                }
                ma[i][j-1]="0";
                ma[i][j]="f";
                i++;
            }
            return ma;
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
        return null;
    }
    
    public static String sumarDecimales(String numero1, String numero2) 
    {
        BigDecimal num1 = new BigDecimal(numero1);
        BigDecimal num2 = new BigDecimal(numero2);
        BigDecimal res = num1.add(num2).setScale(2, RoundingMode.HALF_UP);
        return res.toString();
    }
    
    public static String[][] suprimirDuplicados(String mat[][], int j)
    {
        String distintos[][]=null;
        int pos=-1;
        try{
            for (int i=0; i<mat.length; i++) {
                pos = Matriz.enMatriz(distintos, mat[i][j], j);  
                if (pos == -1) {
                    distintos = Matriz.poner(distintos, new String[] {mat[i][0], mat[i][1], mat[i][2]});
                }else{
                    distintos[pos][1] = Matriz.sumarDecimales( distintos[pos][1], mat[i][1] );
                    distintos[pos][2] = Matriz.sumarDecimales( distintos[pos][2], mat[i][2] );
//                    distintos[pos][1] = String.valueOf( Matriz.redondear(Double.parseDouble(distintos[pos][1]) + Double.parseDouble(mat[i][1]) ) );
//                    distintos[pos][2] = String.valueOf( Matriz.redondear( Double.parseDouble(distintos[pos][2]) + Double.parseDouble(mat[i][2]) ) );
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return distintos;
    }
    
    public static double redondear(double valor)
    {
        return (Math.round(valor * Math.pow(10, 2)) / Math.pow(10, 2));
    }
    
}
