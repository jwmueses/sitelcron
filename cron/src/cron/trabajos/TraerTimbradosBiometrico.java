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
        
            SQLServer objSQL = new SQLServer( Parametro.getMsSqlIp(), Parametro.getMsSqlPuerto(), Parametro.getMsSqlBaseDatos(), Parametro.getMsSqlUsuario(), Parametro.getMsSqlClave() );
            Asistencias objAsistencias = new Asistencias( Parametro.getIp(), Parametro.getPuerto(), Parametro.getBaseDatos(), Parametro.getUsuario(), Parametro.getClave() );
            
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formateador = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            SimpleDateFormat originalFormatISO = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat originalFormatSQL = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy/MM/dd");

            ResultSet getMysql = null;
            String fi = Fecha.getFecha("ISO");
            String ff = Fecha.getFecha("ISO");
            Date date;
            String fif = "";
            String fff = "";
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
                getMysql = objSQL.consulta("SELECT * FROM RALog where date between '" + fif + " 00:00:00' and '" + fff + " 23:59:59' ORDER BY RN DESC");
                try {
                    if (getMysql != null) {
                        while (getMysql.next()) {
                            String id = (getMysql.getString("RN") != null) ? getMysql.getString("RN") : "";
                            String id_bio = (getMysql.getString("UID") != null) ? getMysql.getString("UID") : "";
                            String us = (getMysql.getString("Name") != null) ? getMysql.getString("Name") : "";
                            String fecha = (getMysql.getString("Date") != null) ? getMysql.getString("Date") : "0";
                            String hora = (getMysql.getString("Time") != null) ? getMysql.getString("Time") : "0";
                            //estado = (grupo.getString("Time")!=null) ? grupo.getBoolean("Time") : true;
                            try {
                                objAsistencias.getMysqlSetPostgres(id, id_bio, us, fecha, hora);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        getMysql.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    objSQL.cerrar();
                }

                //encrustado
                Biometricos objbio = new Biometricos( Parametro.getMsSqlIp(), Parametro.getMsSqlPuerto(), Parametro.getMsSqlBaseDatos(), Parametro.getMsSqlUsuario(), Parametro.getMsSqlClave() );
                ResultSet rs = objbio.getBiometricosActivos();
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
                                ResultSet getMysqlbio = BiometricoActivos.consulta("SELECT * FROM RALog where date between '" + fif + " 00:00:00' and '" + fff + " 23:59:59' ORDER BY RN DESC");
                                if (getMysqlbio != null) {
                                    while (getMysqlbio.next()) {
                                        String id = (getMysqlbio.getString("RN") != null) ? getMysqlbio.getString("RN") : "";
                                        String id_bio = (getMysqlbio.getString("UID") != null) ? getMysqlbio.getString("UID") : "";
                                        String us = (getMysqlbio.getString("Name") != null) ? getMysqlbio.getString("Name") : "";
                                        String fecha = (getMysqlbio.getString("Date") != null) ? getMysqlbio.getString("Date") : "0";
                                        String hora = (getMysqlbio.getString("Time") != null) ? getMysqlbio.getString("Time") : "0";
                                        objAsistencias.getMysqlSetPostgres(id, id_bio, us, fecha, hora, id_sucursal + "-" + id_biometrico);
                                    }
                                    getMysqlbio.close();
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
                } finally {
                    objbio.cerrar();
                }
                //fin encrustado
                //---------------------------Atrasos-----------------------------
                String usSalida = "";
                String fechaSalida = "";
                String id_bioSalida = "";

                //5-2
                ResultSet getUsuarioPick = objAsistencias.usuarioPickAll(1, "desc", fi, ff);

                try {
                    while (getUsuarioPick.next()) {
                        usSalida = (getUsuarioPick.getString("usuario") != null) ? getUsuarioPick.getString("usuario") : "0";
                        fechaSalida = (getUsuarioPick.getString("fecha") != null) ? getUsuarioPick.getString("fecha") : "0";
                        id_bioSalida = (getUsuarioPick.getString("ac_no") != null) ? getUsuarioPick.getString("ac_no") : "0";
                        String diaConsulta = Fecha.getDiaSemana(fechaSalida);
                        fechaSalida = fechaSalida.replace("-", "/");

                        ResultSet horarioConsulta = objAsistencias.horarioConsulta(usSalida, fechaSalida);
                        if (horarioConsulta.next()) {
                            int contPick = 0;
                            String id_horario = (horarioConsulta.getString("id_horario") != null ? horarioConsulta.getString("id_horario") : "0");
                            String estado = "puntual";

                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);

                            String getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);

                            String horario = this.ordenarHorario(getRecuperacionDias);
                            String picks = this.noRepetidos(getPick);

                            if (horario.compareTo("") == 0) {
                                horario = horaDiferente(getPick);
                            }

                            String horarioPicks[] = picks.split(",");
                            String matrizHorario[] = horario.split(",");

                            for (int i = 0; i < matrizHorario.length; i++) {
                                if (contPick == horarioPicks.length) {
                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                    estado = "no timbro";
                                } else {
                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
                                    String pick = horarioPicks[contPick];
                                    contPick++;
                                    for (int j = 0;; j++) {
                                        if (i == matrizHorario.length) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        calendar.setTime(hora2);
                                        calendar.add(calendar.MINUTE, 240);
                                        hora2 = calendar.getTime();
                                        if (i != matrizHorario.length - 1) {
                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
                                        }
                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            if ((i + 1) % 2 != 0) {
                                                estado = "atrasado";
                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
                                            }
                                            break;
                                        } else {
                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                            estado = "no timbro";
                                            i++;
                                        }
                                    }
                                }
                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
                            }
                        } else {
                            String id_horario = objAsistencias.asistencia(id_bioSalida, usSalida, fechaSalida);
                            int contPick = 0;
                            String estado = "puntual";

                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);

                            String getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);

                            String horario = this.ordenarHorario(getRecuperacionDias);
                            String picks = this.noRepetidos(getPick);

                            if (horario.compareTo("") == 0) {
                                horario = horaDiferente(getPick);
                            }

                            String horarioPicks[] = picks.split(",");
                            String matrizHorario[] = horario.split(",");

                            for (int i = 0; i < matrizHorario.length; i++) {
                                if (contPick == horarioPicks.length) {
                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                    estado = "no timbro";
                                } else {
                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
                                    String pick = horarioPicks[contPick];
                                    contPick++;
                                    for (int j = 0;; j++) {
                                        if (i == matrizHorario.length) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        calendar.setTime(hora2);
                                        calendar.add(calendar.MINUTE, 240);
                                        hora2 = calendar.getTime();
                                        if (i != matrizHorario.length - 1) {
                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
                                        }
                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            if ((i + 1) % 2 != 0) {
                                                estado = "atrasado";
                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
                                            }
                                            break;
                                        } else {
                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                            estado = "no timbro";
                                            i++;
                                        }
                                    }
                                }
                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //14-7
                getUsuarioPick = objAsistencias.usuarioPickAll(2, "asc", fi, ff);
                try {
                    while (getUsuarioPick.next()) {
                        usSalida = (getUsuarioPick.getString("usuario") != null) ? getUsuarioPick.getString("usuario") : "0";
                        fechaSalida = (getUsuarioPick.getString("fecha") != null) ? getUsuarioPick.getString("fecha") : "0";
                        id_bioSalida = (getUsuarioPick.getString("ac_no") != null) ? getUsuarioPick.getString("ac_no") : "0";
                        String diaConsulta = Fecha.getDiaSemana(fechaSalida);
                        fechaSalida = fechaSalida.replace("-", "/");

                        ResultSet horarioConsulta = objAsistencias.horarioConsulta(usSalida, fechaSalida);
                        if (horarioConsulta.next()) {
                            int contPick = 0;
                            String id_horario = (horarioConsulta.getString("id_horario") != null ? horarioConsulta.getString("id_horario") : "0");
                            String estado = "puntual";

                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);

                            String getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);

                            String horario = this.ordenarHorario(getRecuperacionDias);
                            String picks = this.noRepetidos(getPick);

                            if (horario.compareTo("") == 0) {
                                horario = horaDiferente(getPick);
                            }

                            String horarioPicks[] = picks.split(",");
                            String matrizHorario[] = horario.split(",");

                            for (int i = 0; i < matrizHorario.length; i++) {
                                if (contPick == horarioPicks.length) {
                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                    estado = "no timbro";
                                } else {
                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
                                    String pick = horarioPicks[contPick];
                                    contPick++;
                                    for (int j = 0;; j++) {
                                        if (i == matrizHorario.length) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        calendar.setTime(hora2);
                                        calendar.add(calendar.MINUTE, 240);
                                        hora2 = calendar.getTime();
                                        if (i != matrizHorario.length - 1) {
                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
                                        }
                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            if ((i + 1) % 2 != 0) {
                                                estado = "atrasado";
                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
                                            }
                                            break;
                                        } else {
                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                            estado = "no timbro";
                                            i++;
                                        }
                                    }
                                }
                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
                            }
                        } else {
                            //String getHorario=objAsistencias.getHorario(usSalida,diaConsulta,id_horario);
                            String getHorario = "";
                            String horario = "";
                            String id_horario = "";
                            id_horario = objAsistencias.asistencia147(id_bioSalida, usSalida, fechaSalida);
                            int contPick = 0;
                            String estado = "puntual";

                            String getPick = objAsistencias.getPick(usSalida, fechaSalida);
                            getHorario = objAsistencias.getHorario(usSalida, diaConsulta, id_horario);
                            String getPermisos = objAsistencias.getPermisos(usSalida, fechaSalida, getHorario);
                            String getRecuperacionDias = objAsistencias.getRecuperacionDias(usSalida, fechaSalida, getPermisos);

                            if (getRecuperacionDias.compareTo("") == 0) {
                                getRecuperacionDias = horaDiferente(getPick);
                            }
                            horario = this.ordenarHorario(getRecuperacionDias);
                            String picks = this.noRepetidos(getPick);

                            String horarioPicks[] = picks.split(",");
                            String matrizHorario[] = horario.split(",");

                            for (int i = 0; i < matrizHorario.length; i++) {
                                if (contPick == horarioPicks.length) {
                                    objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                    estado = "no timbro";
                                } else {
                                    Date horaPick = formateador.parse(fechaSalida + " " + horarioPicks[contPick]);
                                    String pick = horarioPicks[contPick];
                                    contPick++;
                                    for (int j = 0;; j++) {
                                        if (i == matrizHorario.length) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        Date hora1 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        Date hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i]);
                                        calendar.setTime(hora2);
                                        calendar.add(calendar.MINUTE, 240);
                                        hora2 = calendar.getTime();
                                        if (i != matrizHorario.length - 1) {
                                            hora2 = formateador.parse(fechaSalida + " " + matrizHorario[i + 1]);
                                        }
                                        if (horaPick.before(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            break;
                                        }
                                        if (horaPick.after(hora1) && horaPick.before(hora2)) {
                                            objAsistencias.asistencia("" + (i + 1), pick, usSalida, fechaSalida);
                                            if ((i + 1) % 2 != 0) {
                                                estado = "atrasado";
                                                objAsistencias.asistenciaGuardar(usSalida, pick, fechaSalida);
                                            }
                                            break;
                                        } else {
                                            objAsistencias.asistencia("" + (i + 1), "00:00:00", usSalida, fechaSalida);
                                            estado = "no timbro";
                                            i++;
                                        }
                                    }
                                }
                                objAsistencias.asistenciaEstado(usSalida, estado, fechaSalida);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            
            }finally {
                objAsistencias.cerrar();
                objSQL.cerrar();
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
 
