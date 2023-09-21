/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.web;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wilso
 */
public class Matriz {

    public static int enMatriz(String mat[][], String clave, int col) {
        int p = -1;
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                if (mat[i][col].compareTo(clave) == 0) {
                    p = i;
                    break;
                }
            }
        }
        return p;
    }

    public static List enMatrizTodo(String mat[][], String clave, int col) {
        List claves = new ArrayList();
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                if (mat[i][col].compareTo(clave) == 0) {
                    claves.add(i);
                }
            }
        }
        return claves;
    }

    public static int enMatriz(String mat[][], String clave[], int col[]) {
        int p = -1;
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                int cont = 0;
                for (int j = 0; i < clave.length; j++) {
                    if (mat[i][col[j]].compareTo(clave[j]) == 0) {
                        cont++;
                    }
                }
                if (cont == clave.length) {
                    p = i;
                    break;
                }
            }
        }
        return p;
    }

    public static int enMatrizBuscar(String mat[][], String clave[], int col[]) {
        int p = -1;
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                if (mat[i][col[0]].compareTo(clave[0]) == 0 && mat[i][col[1]].compareTo(clave[1]) == 0) {
                    p = i;
                    break;
                }
            }
        }
        return p;
    }

    public static String[][] poner(String mat[][], String vector[]) {
        int i = mat != null ? mat.length + 1 : 1;
        String matTemp[][] = new String[i][3];
        if (mat != null) {
            System.arraycopy(mat, 0, matTemp, 0, mat.length);
        }
        for (int j = 0; j < vector.length; j++) {
            matTemp[i - 1][j] = vector[j];
        }
        return matTemp;
    }

    public static String[][] ResultSetAMatriz(ResultSet rs) {
        try {
            /*filas*/
            rs.last();
            int fil = rs.getRow();
            rs.beforeFirst();
            /*columnas*/
            ResultSetMetaData mdata = rs.getMetaData();
            int col = mdata.getColumnCount();
            /*parsear*/
            String ma[][] = new String[fil][col + 2];
            int i = 0;
            int k = 0;
            int j = 1;
            while (rs.next()) {
                for (j = 1; j <= col; j++) {
                    k = j - 1;
                    ma[i][k] = (rs.getString(j) != null) ? rs.getString(j) : "";
                }
                ma[i][j - 1] = "0";
                ma[i][j] = "f";
                i++;
            }
            return ma;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }

    public static String[][] suprimirDuplicados(String mat[][], int j) {
        String distintos[][] = null;
        int pos = -1;
        try {
            for (int i = 0; i < mat.length; i++) {
                pos = Matriz.enMatriz(distintos, mat[i][j], j);
                if (pos == -1) {
                    distintos = Matriz.poner(distintos, new String[]{mat[i][0], mat[i][1], mat[i][2]});
                } else {
                    distintos[pos][1] = String.valueOf(Addons.redondear(Double.parseDouble(distintos[pos][1]) + Double.parseDouble(mat[i][1])));
                    distintos[pos][2] = String.valueOf(Addons.redondear(Double.parseDouble(distintos[pos][2]) + Double.parseDouble(mat[i][2])));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distintos;
    }

    public static String enMatriz(String mat[][], String clave, int col, String clave1, int col1, int retorno) {
        String p = "0";
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                if (mat[i][col].compareTo(clave) == 0 && mat[i][col1].compareTo(clave1) == 0) {
                    p = mat[i][retorno];
                    break;
                }
            }
        }
        return p;
    }

    public static String[][] enMatrizNueva(String mat[][], String clave, int col) {
        String indices = "";
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                if (mat[i][col].compareTo(clave) == 0) {
                    indices += i + ",";
                }
            }
            if (indices.trim().compareTo("") != 0) {
                indices = indices.substring(0, indices.length() - 1);
                String v_indice[] = indices.split(",");
                String retorno[][] = new String[v_indice.length][mat[0].length];
                for (int i = 0; i < v_indice.length; i++) {
                    for (int j = 0; j < mat[0].length; j++) {
                        retorno[i][j] = mat[Integer.parseInt(v_indice[i])][j];
                    }
                }
                return retorno;
            }

        }
        return null;
    }

    public static String[][] enMatrizadd(String mat[][], String[] vector) {
        int i = mat != null ? mat.length + 1 : 1;
        int y = mat == null ? vector.length : (vector.length > mat[0].length ? vector.length : mat[0].length);
        String matTemp[][] = new String[i][y];
        if (mat != null) {
            for (int j = 0; j < mat.length; j++) {
                for (int k = 0; k < mat[0].length; k++) {
                    matTemp[j][k] = mat[j][k];
                }
            }
        }
        for (int j = 0; j < vector.length; j++) {
            matTemp[i - 1][j] = vector[j];
        }
        return matTemp;

    }

    public static String[][] enMatrizadd(String mat[][], String mat1[][]) {
        int i = mat != null ? mat.length + mat1.length : 1;
        int y = mat == null ? mat1[0].length : (mat1[0].length > mat[0].length ? mat1[0].length : mat[0].length);
        String matTemp[][] = new String[i][y];
        if (mat != null && mat1 != null) {
            int x = 0;
            for (int j = 0; j < mat.length; j++) {
                for (int k = 0; k < mat[0].length; k++) {
                    matTemp[j][k] = mat[j][k];
                }
                x++;
            }
            for (int j = 0; j < mat1.length; j++) {
                for (int k = 0; k < mat1[0].length; k++) {
                    matTemp[x][k] = mat1[j][k];
                }
                x++;
            }
        }
        return matTemp;
    }

    public static String[] quitalDuplicados(String[][] matriz1, int x) {
        if (matriz1 != null) {
            String matriz[][] = new String[matriz1.length][matriz1[0].length];
            try {
                for (int i = 0; i < matriz1.length; i++) {
                    for (int j = 0; j < matriz1[0].length; j++) {
                        matriz[i][j] = matriz1[i][j];
                    }
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            String cadena = "";
            try {
                for (int i = 0; i < matriz.length; i++) {
                    for (int j = 0; j < matriz.length - 1; j++) {
                        if (i != j) {
                            if (matriz[i][x].compareTo(matriz[j][x]) == 0) {
                                matriz[j][x] = "";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            try {
                for (int i = 0; i < matriz.length; i++) {
                    if (matriz[i][x].compareTo("") != 0) {
                        cadena += matriz[i][x] + ",";
                    }
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage());
            }
            if (cadena.compareTo("") != 0) {
                cadena = cadena.substring(0, cadena.length() - 1);
            }
            return cadena.split(",");
        } else {
            return null;
        }
    }

    public static String[] QuitarRepetidos(String datos[]) {
        String cadena = "";
        try {
            for (int i = 0; i < datos.length; i++) {
                for (int j = 0; j < datos.length - 1; j++) {
                    if (i != j) {
                        if (datos[i].compareTo(datos[j]) == 0) {
                            datos[i] = "";
                        }
                    }
                }
            }
            int n = datos.length;
            for (int k = 0; k <= n - 1; k++) {
                if (datos[k].trim().compareTo("") != 0) {
                    cadena += datos[k] + ",";

                }

            }
            if (cadena.trim().compareTo("") != 0) {
                cadena = cadena.substring(0, cadena.length() - 1);
                return cadena.split(",");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("error al procesar el vector");
            return null;
        }
    }

    public static String QuitarRepetidos(String valores, String separador) {
        String cadena = "";
        try {
            String datos[] = valores.split(separador);
            for (int i = 0; i < datos.length; i++) {
                for (int j = 0; j < datos.length - 1; j++) {
                    if (i != j) {
                        if (datos[i].compareTo(datos[j]) == 0) {
                            datos[i] = "";
                        }
                    }
                }
            }
            int n = datos.length;
            for (int k = 0; k <= n - 1; k++) {
                if (datos[k].trim().compareTo("") != 0) {
                    cadena += datos[k] + separador;

                }

            }
            if (cadena.trim().compareTo("") != 0) {
                cadena = cadena.substring(0, cadena.length() - 1);
                return cadena;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("error al procesar el vector");
            return null;
        }
    }
}
