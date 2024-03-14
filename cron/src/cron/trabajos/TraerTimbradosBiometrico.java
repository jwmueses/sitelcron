/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author sistemas
 */
class TraerTimbradosBiometrico {
        
        public void obtener(){
            
            System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + "Iniciando copiado de timbrados de biometricos al sistema.");
        
//            SQLServer objSQL = new SQLServer( Parametro.getMsSqlIp(), Parametro.getMsSqlPuerto(), Parametro.getMsSqlBaseDatos(), Parametro.getMsSqlUsuario(), Parametro.getMsSqlClave() );
            Asistencias objAsistencias = new Asistencias( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
            Biometricos objbio = new Biometricos(Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
            
//            Calendar calendar = Calendar.getInstance();
//            SimpleDateFormat formateador = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            SimpleDateFormat originalFormatISO = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat originalFormatSQL = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy/MM/dd");

            String fi = Fecha.getFecha("ISO");
            String ff = Fecha.getFecha("ISO");
            Date date;

            String fif = "";
            String fff = "";
            ResultSet getMsSql = null;


            try {
                date = fi.indexOf("-") > 0 ? originalFormatISO.parse(fi) : originalFormatSQL.parse(fi);
                fif = targetFormat.format(date);
                date = ff.indexOf("-") > 0 ? originalFormatISO.parse(ff) : originalFormatSQL.parse(ff);
                fff = targetFormat.format(date);

            } catch (ParseException ex) {
                ex.printStackTrace();
                // Handle Exception.
            }

            try {
                //String id = request.getParameter("id");
                //---------------------Obtener datos --------------------------------
                SQLServer objSQL = new SQLServer( Parametro.getMsSqlIp(), Parametro.getMsSqlPuerto(), Parametro.getMsSqlBaseDatos(), Parametro.getMsSqlUsuario(), Parametro.getMsSqlClave() );
                getMsSql = objSQL.consulta("SELECT * FROM RALog where date between dateadd(day, -1, '" + fif + " 00:00:00') and '" + fff + " 23:59:59' ORDER BY RN DESC");

                try {
                    if (getMsSql != null) {
                        while (getMsSql.next()) {
                            String id = (getMsSql.getString("RN") != null) ? getMsSql.getString("RN") : "";
                            String id_bio = (getMsSql.getString("UID") != null) ? getMsSql.getString("UID") : "";
                            String us = (getMsSql.getString("Name") != null) ? getMsSql.getString("Name") : "";
                            String fecha = (getMsSql.getString("Date") != null) ? getMsSql.getString("Date") : "0";
                            String hora = (getMsSql.getString("Time") != null) ? getMsSql.getString("Time") : "0";
                            //estado = (grupo.getString("Time")!=null) ? grupo.getBoolean("Time") : true;
                            try {
                                objAsistencias.getMsSqlSetPostgres(id, id_bio, us, fecha, hora);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        getMsSql.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    objSQL.cerrar();
                }

                //encrustado

                //  BIOMETRICOS FS20
                ResultSet rs = objbio.getBiometricosActivos();
    //            if (controAsistencias && origen.compareTo("t") == 0) {
    //                rs = objbio.getBiometricosActivos();
    //            } else {    //  si origen manual, horario
    //                rs = objbio.getBiometricosActivos(id_sucursalgeneral);
    //            }
                String id_biometrico = "";
                String ipbio = "";
                String puertobio = "";
                String dbbio = "";
                String usuariobio = "";
                String clavebio = "";
                String id_sucursal = "";
                try {
                    if (rs != null) {
                        while (rs.next()) {
                            id_biometrico = (rs.getString("id_biometrico") != null ? rs.getString("id_biometrico") : "1");
                            ipbio = (rs.getString("ip_biometrico") != null ? rs.getString("ip_biometrico") : "0:0:0:0");
                            puertobio = (rs.getString("puerto_biometrico") != null ? rs.getString("puerto_biometrico") : "0");
                            dbbio = (rs.getString("db_biometrico") != null ? rs.getString("db_biometrico") : "N");
                            usuariobio = (rs.getString("usuario_biometrico") != null ? rs.getString("usuario_biometrico") : "N");
                            clavebio = (rs.getString("clave_biometrico") != null ? rs.getString("clave_biometrico") : "N");
                            id_sucursal = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "1");
                            SQLServer BiometricoActivos = new SQLServer(ipbio, Integer.parseInt(puertobio), dbbio, usuariobio, clavebio);
                            try {
                                ResultSet getMsSqlbio = BiometricoActivos.consulta("SELECT * FROM RALog where date between dateadd(day, -1, '" + fif + " 00:00:00') and '" + fff + " 23:59:59' ORDER BY RN DESC");
                                if (getMsSqlbio != null) {
                                    while (getMsSqlbio.next()) {
                                        String id = (getMsSqlbio.getString("RN") != null) ? getMsSqlbio.getString("RN") : "";
                                        String id_bio = (getMsSqlbio.getString("UID") != null) ? getMsSqlbio.getString("UID") : "";
                                        String us = (getMsSqlbio.getString("Name") != null) ? getMsSqlbio.getString("Name") : "";
                                        String fecha = (getMsSqlbio.getString("Date") != null) ? getMsSqlbio.getString("Date") : "0";
                                        String hora = (getMsSqlbio.getString("Time") != null) ? getMsSqlbio.getString("Time") : "0";
                                        objAsistencias.getMsSqlSetPostgres(id, id_bio, us, fecha, hora, id_sucursal + "-" + id_biometrico);
                                    }
                                    getMsSqlbio.close();
                                }
                            } catch (Exception e) {
                                System.out.println("error al obtener datos " + e.getMessage());
                            } finally {
                                BiometricoActivos.cerrar();
                            }
                        }
                        rs.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }




                //  BIOMETRICOS ZKTeco
                rs = objbio.getBiometricosActivosZKTeco();
    //            if (controAsistencias && manual.compareTo("t") == 0) {
    //                rs = objbio.getBiometricosActivosZKTeco();
    //            } else {
    //                rs = objbio.getBiometricosActivosZKTeco(id_sucursalgeneral);
    //            }
                try {
                    ResultSet rsEmpleados = objAsistencias.consulta("select alias, id_sucursal from tbl_empleado where estado and not eliminado and generar_rol order by alias");

                    String matEmpleados[][] = Matriz.ResultSetAMatriz( rsEmpleados );
                    if (rs != null) {
                        while (rs.next()) {
                            id_biometrico = (rs.getString("id_biometrico") != null ? rs.getString("id_biometrico") : "1");
                            ipbio = (rs.getString("ip_biometrico") != null ? rs.getString("ip_biometrico") : "0:0:0:0");
                            puertobio = (rs.getString("puerto_biometrico") != null ? rs.getString("puerto_biometrico") : "0");
                            dbbio = (rs.getString("db_biometrico") != null ? rs.getString("db_biometrico") : "N");
                            usuariobio = (rs.getString("usuario_biometrico") != null ? rs.getString("usuario_biometrico") : "N");
                            clavebio = (rs.getString("clave_biometrico") != null ? rs.getString("clave_biometrico") : "N");
    //                        id_sucursal = (rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "1");

                            DataBase BiometricoActivos = new DataBase(ipbio, Integer.parseInt(puertobio), dbbio, usuariobio, clavebio);
                            try {
                                ResultSet getMsSqlbio = BiometricoActivos.consulta("select T.id, T.emp_id, E.first_name, T.punch_time::date as Date, T.punch_time::time as Time from iclock_transaction as T inner join personnel_employee as E on T.emp_id=E.id where punch_time between ('" + fif + "'::date - '1 day'::interval)::date and '" + fff + " 23:59:59' ORDER BY emp_id DESC");
                                if (getMsSqlbio != null) {
                                    while (getMsSqlbio.next()) {
                                        String id = (getMsSqlbio.getString("id") != null) ? getMsSqlbio.getString("id") : "";
                                        String id_bio = (getMsSqlbio.getString("emp_id") != null) ? getMsSqlbio.getString("emp_id") : "";
                                        String us = (getMsSqlbio.getString("first_name") != null) ? getMsSqlbio.getString("first_name") : "";
                                        String fecha = (getMsSqlbio.getString("Date") != null) ? getMsSqlbio.getString("Date") : "0";
                                        String hora = (getMsSqlbio.getString("Time") != null) ? getMsSqlbio.getString("Time") : "0";

                                        int p = Matriz.enMatriz(matEmpleados, us, 0);
                                        id_sucursal = p>=0 ? matEmpleados[p][1] : "1";
                                        objAsistencias.getMsSqlSetPostgres(id, id_bio, us, fecha, hora, id_sucursal + "-" + id_biometrico);
                                    }
                                    getMsSqlbio.close();
                                }
                            } catch (Exception e) {
                                System.out.println("error al obtener datos " + e.getMessage());
                            } finally {
                                BiometricoActivos.cerrar();
                            }
                        }
                        rs.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + "Finalizacion de copiado de timbrados de biometricos al sistema.");
                
                
                
                
                
                
                //  Registro de semanas de turno en horarios 14/7
                ResultSet getUsuarioPick = objAsistencias.usuarioPickAll(2, "asc", fi, ff);
                try {
                    while (getUsuarioPick.next()) {
                        String usSalida = (getUsuarioPick.getString("usuario") != null) ? getUsuarioPick.getString("usuario") : "0";
                        String fechaSalida = (getUsuarioPick.getString("fecha") != null) ? getUsuarioPick.getString("fecha") : "0";
                        String id_bioSalida = (getUsuarioPick.getString("ac_no") != null) ? getUsuarioPick.getString("ac_no") : "0";
                        fechaSalida = fechaSalida.replace("-", "/");
                        objAsistencias.asistencia147(id_bioSalida, usSalida, fechaSalida);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                
                
                
                
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Inicio de validacion de timbrados contra horarios.");
                try{
                    objAsistencias.consulta("SELECT bio_registra_timbrados_saitel( (now() - '1 day'::interval)::date );");
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                System.out.println(Fecha.getFecha("SQL") + " " + Fecha.getHora() + ": Finalización de validacion de timbrados contra horarios");
        
        
        
                
                
                //---------------------------Atrasos-----------------------------
//                String usSalida = "";
//                String fechaSalida = "";
//                String id_bioSalida = "";
//
//                //5-2
//                ResultSet getUsuarioPick = objAsistencias.usuarioPickAll(1, "desc", fi, ff);
//
//                try {
//                    while (getUsuarioPick.next()) {
//                        usSalida = (getUsuarioPick.getString("usuario") != null) ? getUsuarioPick.getString("usuario") : "0";
//                        fechaSalida = (getUsuarioPick.getString("fecha") != null) ? getUsuarioPick.getString("fecha") : "0";
//                        id_bioSalida = (getUsuarioPick.getString("ac_no") != null) ? getUsuarioPick.getString("ac_no") : "0";
//                        String diaConsulta = Fecha.getDiaSemana(fechaSalida);
//                        fechaSalida = fechaSalida.replace("-", "/");
//
//                        ResultSet horarioConsulta = objAsistencias.horarioConsulta(usSalida, fechaSalida);
//                        if (horarioConsulta.next()) {
//                            int contPick = 0;
//                            String id_horario = (horarioConsulta.getString("id_horario") != null ? horarioConsulta.getString("id_horario") : "0");
//                            String estado = "puntual";
//
//                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);
//
//                            String getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
//                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
//                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);
//
//                            String horario = this.ordenarHorario(getRecuperacionDias);
//                            String picks = this.noRepetidos(getPick);
//
//                            if (horario.compareTo("") == 0) {
//                                horario = horaDiferente(getPick);
//                            }
//
//                            String horarioPicks[] = picks.split(",");
//                            String matrizHorario[] = horario.split(",");
//
//                            for (int i = 0; i < matrizHorario.length; i++) {
//                                if (contPick == horarioPicks.length) {
//                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                    estado = "no timbro";
//                                } else {
//                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
//                                    String pick = horarioPicks[contPick];
//                                    contPick++;
//                                    for (int j = 0;; j++) {
//                                        if (i == matrizHorario.length) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        calendar.setTime(hora2);
//                                        calendar.add(calendar.MINUTE, 240);
//                                        hora2 = calendar.getTime();
//                                        if (i != matrizHorario.length - 1) {
//                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
//                                        }
//                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            if ((i + 1) % 2 != 0) {
//                                                estado = "atrasado";
//                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
//                                            }
//                                            break;
//                                        } else {
//                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                            estado = "no timbro";
//                                            i++;
//                                        }
//                                    }
//                                }
//                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
//                            }
//                        } else {
//                            String id_horario = objAsistencias.asistencia(id_bioSalida, usSalida, fechaSalida);
//                            int contPick = 0;
//                            String estado = "puntual";
//
//                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);
//
//                            String getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
//                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
//                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);
//
//                            String horario = this.ordenarHorario(getRecuperacionDias);
//                            String picks = this.noRepetidos(getPick);
//
//                            if (horario.compareTo("") == 0) {
//                                horario = horaDiferente(getPick);
//                            }
//
//                            String horarioPicks[] = picks.split(",");
//                            String matrizHorario[] = horario.split(",");
//
//                            for (int i = 0; i < matrizHorario.length; i++) {
//                                if (contPick == horarioPicks.length) {
//                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                    estado = "no timbro";
//                                } else {
//                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
//                                    String pick = horarioPicks[contPick];
//                                    contPick++;
//                                    for (int j = 0;; j++) {
//                                        if (i == matrizHorario.length) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        calendar.setTime(hora2);
//                                        calendar.add(calendar.MINUTE, 240);
//                                        hora2 = calendar.getTime();
//                                        if (i != matrizHorario.length - 1) {
//                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
//                                        }
//                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            if ((i + 1) % 2 != 0) {
//                                                estado = "atrasado";
//                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
//                                            }
//                                            break;
//                                        } else {
//                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                            estado = "no timbro";
//                                            i++;
//                                        }
//                                    }
//                                }
//                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
//                            }
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                //14-7
//                getUsuarioPick = objAsistencias.usuarioPickAll(2, "asc", fi, ff);
//                try {
//                    while (getUsuarioPick.next()) {
//                        usSalida = (getUsuarioPick.getString("usuario") != null) ? getUsuarioPick.getString("usuario") : "0";
//                        fechaSalida = (getUsuarioPick.getString("fecha") != null) ? getUsuarioPick.getString("fecha") : "0";
//                        id_bioSalida = (getUsuarioPick.getString("ac_no") != null) ? getUsuarioPick.getString("ac_no") : "0";
//                        String diaConsulta = Fecha.getDiaSemana(fechaSalida);
//                        fechaSalida = fechaSalida.replace("-", "/");
//
//                        ResultSet horarioConsulta = objAsistencias.horarioConsulta(usSalida, fechaSalida);
//                        if (horarioConsulta.next()) {
//                            int contPick = 0;
//                            String id_horario = (horarioConsulta.getString("id_horario") != null ? horarioConsulta.getString("id_horario") : "0");
//                            String estado = "puntual";
//
//                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);
//
//                            String getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
//                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
//                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);
//
//                            String horario = this.ordenarHorario(getRecuperacionDias);
//                            String picks = this.noRepetidos(getPick);
//
//                            if (horario.compareTo("") == 0) {
//                                horario = horaDiferente(getPick);
//                            }
//
//                            String horarioPicks[] = picks.split(",");
//                            String matrizHorario[] = horario.split(",");
//
//                            for (int i = 0; i < matrizHorario.length; i++) {
//                                if (contPick == horarioPicks.length) {
//                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                    estado = "no timbro";
//                                } else {
//                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
//                                    String pick = horarioPicks[contPick];
//                                    contPick++;
//                                    for (int j = 0;; j++) {
//                                        if (i == matrizHorario.length) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        calendar.setTime(hora2);
//                                        calendar.add(calendar.MINUTE, 240);
//                                        hora2 = calendar.getTime();
//                                        if (i != matrizHorario.length - 1) {
//                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
//                                        }
//                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            if ((i + 1) % 2 != 0) {
//                                                estado = "atrasado";
//                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
//                                            }
//                                            break;
//                                        } else {
//                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                            estado = "no timbro";
//                                            i++;
//                                        }
//                                    }
//                                }
//                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
//                            }
//                        } else {
//                            //String getHorario=objAsistencias.getHorario(usSalida,diaConsulta,id_horario);
//                            String getHorario = "";
//                            String horario = "";
//                            String id_horario = "";
//                            id_horario = objAsistencias.asistencia147(id_bioSalida, usSalida, fechaSalida);
//                            int contPick = 0;
//                            String estado = "puntual";
//
//                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);
//                            getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
//                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
//                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);
//
//                            if (getRecuperacionDias.compareTo("") == 0) {
//                                getRecuperacionDias = horaDiferente(getPick);
//                            }
//                            horario = this.ordenarHorario(getRecuperacionDias);
//                            String picks = this.noRepetidos(getPick);
//
//                            String horarioPicks[] = picks.split(",");
//                            String matrizHorario[] = horario.split(",");
//
//                            for (int i = 0; i < matrizHorario.length; i++) {
//                                if (contPick == horarioPicks.length) {
//                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                    estado = "no timbro";
//                                } else {
//                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
//                                    String pick = horarioPicks[contPick];
//                                    contPick++;
//                                    for (int j = 0;; j++) {
//                                        if (i == matrizHorario.length) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
//                                        calendar.setTime(hora2);
//                                        calendar.add(calendar.MINUTE, 240);
//                                        hora2 = calendar.getTime();
//                                        if (i != matrizHorario.length - 1) {
//                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
//                                        }
//                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            break;
//                                        }
//                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
//                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
//                                            if ((i + 1) % 2 != 0) {
//                                                estado = "atrasado";
//                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
//                                            }
//                                            break;
//                                        } else {
//                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
//                                            estado = "no timbro";
//                                            i++;
//                                        }
//                                    }
//                                }
//                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
//                            }
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            
            }finally {
                objAsistencias.cerrar();
                objbio.cerrar();
            }
            
        }
        
        
        
        public String ordenarHorario(String getHorario) throws ParseException {
            SimpleDateFormat formateador = new SimpleDateFormat("HH:mm:ss");
            String horario = "";
            Date hora1 = null;
            Date hora2 = null;
            String matrizHorario[] = getHorario.split(",");
            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j < (matrizHorario.length) - 1; j++) {
                    hora1 = formateador.parse(matrizHorario[j]);
                    hora2 = formateador.parse(matrizHorario[j + 1]);
                    if (hora2.before(hora1)) {
                        String hora_tmp = matrizHorario[j];
                        matrizHorario[j] = matrizHorario[j + 1];
                        matrizHorario[j + 1] = hora_tmp;
                    }
                }
            }

            for (int i = 0; i < (matrizHorario.length) - 1; i++) {
                if (matrizHorario[i].compareTo(matrizHorario[i + 1]) != 0) {
                    horario += matrizHorario[i] + ",";
                } else {
                    i++;
                }
            }
            horario += matrizHorario[matrizHorario.length - 1] + ",";
            horario = horario.substring(0, horario.length() - 1);
            return horario;
        }

        
        
        /**
         * Quita los repetidos o los que esten dentro de 5 minutos al anterior.
         *
         * @param getPicks Horas a ordenar.
         * @return Horas ordenadas ascendentes.
         * @throws ParseException
         */
        public String noRepetidos(String getPicks) throws ParseException {
            SimpleDateFormat formateador = new SimpleDateFormat("HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            String horario = "";
            Date hora1 = null;
            Date hora2 = null;
            String matrizHorario[] = getPicks.split(",");
            for (int j = 0; j < matrizHorario.length; j++) {
                if (j == matrizHorario.length - 1 || matrizHorario.length == 1) {
                    horario += matrizHorario[j] + ",";
                    break;
                }
                if (matrizHorario[j].compareTo("") != 0) {
                    hora1 = formateador.parse(matrizHorario[j]);
                    calendar.setTime(hora1);
                    calendar.add(calendar.MINUTE, 5);
                    hora1 = calendar.getTime();

                    hora2 = formateador.parse(matrizHorario[j + 1]);
                    if (hora1.before(hora2)) {
                        horario += matrizHorario[j] + ",";
                    }
                }
            }
            horario = horario.substring(0, horario.length() - 1);
            return horario;
        }

        
        
        /**
         * Crea un horario temporar para dias que no son laborables, pero asiste.
         *
         * @param getPicks Registros del biometrico.
         * @return Horario creado.
         * @throws ParseException
         */
        public String horaDiferente(String getPicks) throws ParseException {
            SimpleDateFormat formateador = new SimpleDateFormat("HH:mm:ss");
            DateFormat hora = new SimpleDateFormat("HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            String horario = "";
            Date hora1 = null;
            String matrizHorario[] = getPicks.split(",");
            for (int j = 0; j < matrizHorario.length; j++) {
                hora1 = formateador.parse(matrizHorario[j]);
                calendar.setTime(hora1);
                if (j % 2 == 0) {
                    calendar.add(calendar.SECOND, -5);
                } else {
                    calendar.add(calendar.SECOND, 5);
                }
                hora1 = calendar.getTime();
                horario += hora.format(hora1) + ",";
            }
            horario = horario.substring(0, horario.length() - 1);
            return horario;
        }
        
        
        
    }
/////////////////////////////////////////////////   FIN CLASE ASISTENCIA    //////////////////////////////////////////////////////////////////////////////////    
 
