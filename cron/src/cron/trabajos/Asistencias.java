/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Jorge
 */
public class Asistencias extends DataBase {

    SimpleDateFormat formateador = new SimpleDateFormat("yyyy/MM/dd");

    public Asistencias(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getPermisoTmp(String usuario, String contraseña) {
        return this.consulta("select * from tbl_empleado_asistencia_tmp where usuario='" + usuario + "' and fecha='" + contraseña + "' order by hora;");
    }

    /**
     * Informacion general de las asistencias
     *
     * @param id Clave primaria de la vista (id_empleado_asistencia)
     * @return ResultSet
     */
    public ResultSet getInfo(String id) {
        return this.consulta("select * from vta_empleado_asistencia where id_empleado_asistencia=" + id + ";");
    }

    /**
     * Obtiene informacion de los registros del biometrico de un determinado
     * dia.
     *
     * @param fecha Fecha que desea ver los registros del biometrico.
     * @param usuario Usuario en el biometrico.
     * @return ResultSet
     */
    public ResultSet getAsistenciaDetalles(String fecha, String usuario) {
        return this.consulta("select * from tbl_empleado_asistencia_tmp where estado!='puntual' and fecha='" + fecha + "' and usuario='" + usuario + "';");
    }

    public ResultSet getPermisosDetalles(String id) {
        return this.consulta("SELECT * FROM tab_diasrecuperados where id_permiso=" + id + ";");
    }

    public ResultSet getVacaciones(String id) {
        return this.consulta("SELECT * FROM vta_empleado_vacaciones where id_empleado_vacaciones=" + id + " order by fecha_pedido;");
    }

    public ResultSet getVacacionesDetalles(String id) {
        return this.consulta("SELECT * FROM vta_empleado_vacaciones where id_empleado=" + id + " order by fecha_pedido;");
    }

    public ResultSet getTipoPermiso(String estado) {
        return this.consulta("SELECT numdias, nombre, descripcion FROM tab_tipopermiso where eliminado=" + estado + ";");
    }

    public ResultSet getTiposPermisos(String id) {
        return this.consulta("SELECT * FROM tab_tipopermiso where id_tipo=" + id + ";");
    }

    public ResultSet getDetallesPermisos(String id) {
        return this.consulta("select e.id_empleado, e.nombre, e.fecha_ingreso, e.diasvacaciones,\n"
                + " e.fecha_ingreso, date_part('year', now()) - date_part('year', e.fecha_ingreso) as num_anios,\n"
                + "(DATE_PART('month', now()) - DATE_PART('month', e.fecha_ingreso))+(date_part('year', now()) - date_part('year', e.fecha_ingreso))*12::int as meses,\n"
                + "DATE_PART('day', now() - e.fecha_ingreso) as dias,\n"
                + "(select sum(numdias)from vta_empleado_permiso where id_empleado=e.id_empleado and permisoestado='a' and forma_rec='1') as diaspedidos,\n"
                + "(select sum(numhoras) from vta_empleado_permiso where id_empleado=e.id_empleado and permisoestado='a' and forma_rec='1') as horaspedidos,\n"
                + "(select sum(num_minutos) from vta_empleado_permiso where id_empleado=e.id_empleado and permisoestado='a' and forma_rec='1') as minutospedidos,\n"
                + "(select sum(numdias) from tbl_empleado_vacaciones where estado='a' and id_empleado=e.id_empleado) as vacaciones\n"
                + "from tbl_empleado e where e.id_empleado=" + id + ";");
    }

    public String getMesesVacaciones(String id) {
        String num_meses = "";
        try {
            ResultSet r = this.consulta("select c.sucursal, e.nombre || ' ' || e.apellido as empleado, e.fecha_ingreso, date_part('year', now()) - date_part('year', e.fecha_ingreso) as num_anios,\n"
                    + "(DATE_PART('month', now()) - DATE_PART('month', e.fecha_ingreso))+(date_part('year', now()) - date_part('year', e.fecha_ingreso))*12::int as meses,\n"
                    + "DATE_PART('day', now() - e.fecha_ingreso) as di\n"
                    + "from vta_empleado e\n"
                    + "JOIN vta_sucursal c ON c.id_sucursal = e.id_sucursal \n"
                    + "where id_empleado=" + id + "");
            if (r.next()) {
                num_meses = (r.getString("meses") != null) ? r.getString("meses") : "";
                r.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num_meses;
    }

    /*public String insertar(String id_tipo, String nDias, String nHoras, String nMinutos, String fecha_permiso, String recuperac, String hInicial, String descripcion, String usuario)
    {
        id_tipo = id_tipo.compareTo("-0")!=0 ? id_tipo : "NULL";
        nDias = id_tipo.compareTo("NULL")==0 ? nDias : id_tipo;
        String id_empleado=getId(usuario);
        return this.insert("INSERT INTO tbl_permiso(id_tipo, id_empleado, numdias, numhoras, fechafin, fechainicio, aprobado, forma_rec, hora_inicio, hora_fin, "
            + "descripcion, estado, num_minutos, fecha_pedido) " +
            "VALUES("+id_tipo+","+id_empleado+", "+nDias+", "+nHoras+", (CAST('"+fecha_permiso+"' AS DATE) + CAST('"+nDias+" days' AS INTERVAL)), '"+fecha_permiso+"', false, "+recuperac+", '"+hInicial+"',"
                + "(cast( '"+hInicial+"' as time)+cast(' "+nHoras+" hour' as interval)+cast(' "+(Integer.parseInt(nMinutos)+1)+" minutes' as interval)), '"+descripcion+"', 's', '"+nMinutos+"', date 'now()');");
    }*/
    public boolean actualizarTipoPermiso(String id, String nombre, String descripcion, String numdias, String eliminado) {
        return this.ejecutar("Update tab_tipopermiso set nombre='" + nombre + "', descripcion='" + descripcion + "', numdias=" + numdias + ", eliminado=" + eliminado + " where id_tipo=" + id + ";");
    }

    public boolean insertarTipo(String id_permiso, String[][] matriz) {
        List ejec = new ArrayList();
        for (int i = 0; i < matriz.length; i++) {
            ejec.add("INSERT INTO tab_diasrecuperados(id_permiso, dia, hora_entrada, hora_salida) values (" + id_permiso + ",'" + matriz[i][0] + "','" + matriz[i][1] + "'+ CAST('1 minutes' AS INTERVAL),'" + matriz[i][2] + "');");
        }
        return this.transacciones(ejec);
    }

    /**
     * Guardar el registro de asistencia en la Base de Datos Postgresql.
     *
     * @param id
     * @param id_bio
     * @param us
     * @param fecha
     * @param hora
     * @return
     */
    public boolean getMysqlSetPostgres(String id, String id_bio, String us, String fecha, String hora) {
        return this.ejecutar("insert into tbl_empleado_asistencia_tmp values (" + id + ",'" + id_bio + "','" + us + "','" + fecha + "','" + hora + "','puntual',(select modalidad from tab_horarios h join tbl_empleado e on h.id_horario=e.id_horario where e.alias='" + us + "'))");
    }

    public boolean getMysqlSetPostgres(String id, String id_bio, String us, String fecha, String hora, String id_sucursal) {
        return this.ejecutar("insert into tbl_empleado_asistencia_tmp values (" + id + ",'" + id_bio + "','" + us + "','" + fecha + "','" + hora + "','puntual',(select modalidad from tab_horarios h join tbl_empleado e on h.id_horario=e.id_horario where e.alias='" + us + "'),'" + id_sucursal + "')");
    }

    /**
     * Guarda la asistencia del empleado.
     *
     * @param contador Identificador para la posicion del registro.
     * @param hora Hora del registro.
     * @param usuario usuario.
     * @param fecha dia del registro.
     * @return (true) Si se actualiza correctamente. (false) Si tuvo algun
     * error.
     */
    public boolean asistencia(String contador, String hora, String usuario, String fecha) {
        return this.ejecutar("update tbl_empleado_asistencia set hora_" + contador + "='" + hora + "' where nombre_biometrico='" + usuario + "' and dia='" + fecha + "'");
    }

    public String asistencia(String id_bioSalida, String usSalida, String fechaSalida) {
        String id_horario = "";
        this.ejecutar("insert into tbl_empleado_asistencia (ac_no, nombre_biometrico, dia, id_horario) values ('" + id_bioSalida + "','" + usSalida + "','" + fechaSalida + "', (select id_horario from tbl_empleado where alias='" + usSalida + "'));");
        try {
            ResultSet r = this.consulta("select id_horario from tbl_empleado_asistencia where nombre_biometrico='" + usSalida + "' and dia='" + fechaSalida + "';");
            if (r.next()) {
                id_horario = (r.getString("id_horario") != null) ? r.getString("id_horario") : "";
            }
            r.close();
        } catch (Exception e) {
            e.getMessage();
        }
        return id_horario;
    }

    /**
     * Obtiene el id del Horario para registrar la asistencia en la BD.
     *
     * @param usuario Usuario que se autentica.
     * @param fecha Fecha del registro.
     * @return id del Horario.
     */
    public String idHorario(String usuario, String fecha) {
        String id_horario = "";
        String id_bio = "";
        String modalidad = "";
        ResultSet r = this.consulta("select e.id_horario, h.modalidad,e.id_empleado as id_bio from tbl_empleado e, tab_horarios h where e.id_horario=h.id_horario and e.alias='" + usuario + "';");
        try {
            if (r.next()) {
                modalidad = (r.getString("modalidad") != null) ? r.getString("modalidad") : "";
                id_bio = (r.getString("id_bio") != null) ? r.getString("id_bio") : "";
                if (modalidad.compareTo("1") == 0) {
                    id_horario = (r.getString("id_horario") != null) ? r.getString("id_horario") : "";
                }
                if (modalidad.compareTo("2") == 0) {
                    ResultSet horarioConsulta = this.horarioConsulta(usuario, fecha);
                    if (horarioConsulta.next()) {
                        id_horario = (horarioConsulta.getString("id_horario") != null ? horarioConsulta.getString("id_horario") : "0");
                    } else {
                        id_horario = this.asistencia147(id_bio, usuario, fecha);
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return id_horario;
    }

    public String nombreHorario(String id_horario) {
        String horario = "";
        ResultSet r = this.consulta("select * from tab_horarios where id_horario=" + id_horario + ";");
        try {
            if (r.next()) {
                horario = (r.getString("nombre") != null) ? r.getString("nombre") : "";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return horario;
    }

    public String idHorario(String nombreHorario) {
        String idhorario = "";
        ResultSet r = this.consulta("select * from tab_horarios where nombre='" + nombreHorario + "';");
        try {
            if (r.next()) {
                idhorario = (r.getString("id_horario") != null) ? r.getString("id_horario") : "";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return idhorario;
    }

    /**
     * Retorna el id del Empleado segun el nombre de usuario.
     *
     * @param usuario Nombre de usuario que se autentica.
     * @return Id del Empleado.
     */
    public String idEmpleado(String usuario) {
        String idEmpleado = "";
        ResultSet r = this.consulta("select id_empleado from tbl_empleado where alias='" + usuario + "';");
        try {
            if (r.next()) {
                idEmpleado = (r.getString("id_empleado") != null) ? r.getString("id_empleado") : "";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return idEmpleado;
    }

    /**
     * Consulta del id del horario para los empleados con asistencia 14/7.
     *
     * @param id_bioSalida Clave primaria (id_empleado) de la tabla
     * empleado(tbl_empleado).
     * @param usSalida Usuario de autenticacion.
     * @param fechaSalida fecha para el registro de la asistencia.
     * @return id del Horario que se va a utilizar para registrar la asistencia.
     */
    public String asistencia147(String id_bioSalida, String usSalida, String fechaSalida) {
        String idHorarioTmp = "";
        String id_horario = "";
        String fecha_final = "";
        String horario = "";
        //formateador.parse(fechaSalida+" "+horarioPicks[contPick]);
        try {
//            for (int i = 0; i < 1;) {
                ResultSet r = this.consulta("select id_horario from tbl_empleado_asistencia_147 where usuario='" + usSalida + "' and '" + fechaSalida + "' between fecha_inicial and fecha_final;");
                if (r.next()) {
                    id_horario = (r.getString("id_horario") != null) ? r.getString("id_horario") : "";
                    this.ejecutar("insert into tbl_empleado_asistencia (ac_no, nombre_biometrico, dia, id_horario) values ('" + id_bioSalida + "','" + usSalida + "','" + fechaSalida + "', '" + id_horario + "');");
//                    i++;
                } else {
                    ResultSet h = this.consulta("select id_horario, fecha_final from tbl_empleado_asistencia_147 where usuario='" + usSalida + "' order by fecha_final desc limit 1");
                    if (h.next()) {
                        id_horario = (h.getString("id_horario") != null) ? h.getString("id_horario") : "";
                        fecha_final = (h.getString("fecha_final") != null) ? h.getString("fecha_final") : "";
                        horario = this.nombreHorario(id_horario);
                    }
                    fechaSalida = fechaSalida.replace("-", "/");
                    fecha_final = fecha_final.replace("-", "/");
                    if (formateador.parse(fechaSalida).before(formateador.parse(fecha_final))) {
                        id_horario = "0";
                        this.ejecutar("insert into tbl_empleado_asistencia (ac_no, nombre_biometrico, dia, id_horario) values ('" + id_bioSalida + "','" + usSalida + "','" + fechaSalida + "', '" + id_horario + "');");
//                        i++;
                    }
                    String j = horario.substring(horario.length() - 2, horario.length());
                    if (j.compareTo("-T") == 0) {
                        idHorarioTmp = this.idHorario(horario.substring(0, horario.length() - 2));
                        if (idHorarioTmp.compareTo("") != 0) {
                            this.ejecutar("insert into tbl_empleado_asistencia_147 (usuario, fecha_inicial, fecha_final, id_horario) values ('" + usSalida + "',CAST('" + fecha_final + "' AS DATE) + CAST('8 days' AS INTERVAL), CAST('" + fecha_final + "' AS DATE) + CAST('14 days' AS INTERVAL), " + idHorarioTmp + ");");
                        }
                    } else {
                        idHorarioTmp = this.idHorario(horario + "-T");
                        if (idHorarioTmp.compareTo("") != 0) {
                            this.ejecutar("insert into tbl_empleado_asistencia_147 (usuario, fecha_inicial, fecha_final, id_horario) values ('" + usSalida + "',CAST('" + fecha_final + "' AS DATE) + CAST('1 days' AS INTERVAL),CAST('" + fecha_final + "' AS DATE) + CAST('7 days' AS INTERVAL), " + idHorarioTmp + ");");
                        }
                    }
//                }
                r.close();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return id_horario;
    }

    /////nuevo
    public String asistencia147fijo(String id_bioSalida, String usSalida, String fechaSalida) {
        String idHorarioTmp = "";
        String id_horario = "";
        String fecha_final = "";
        String horario = "";
        //formateador.parse(fechaSalida+" "+horarioPicks[contPick]);
        try {
//            for (int i = 0; i < 1;) {
                ResultSet r = this.consulta("select id_horario from tbl_empleado_asistencia_147 where usuario='" + usSalida + "' and '" + fechaSalida + "' between fecha_inicial and fecha_final;");
                if (r.next()) {
                    id_horario = (r.getString("id_horario") != null) ? r.getString("id_horario") : "";
                    this.ejecutar("insert into tbl_empleado_asistencia (ac_no, nombre_biometrico, dia, id_horario) values ('" + id_bioSalida + "','" + usSalida + "','" + fechaSalida + "', '" + id_horario + "');");
//                    i++;
                } else {
                    ResultSet h = this.consulta("select id_horario, fecha_final from tbl_empleado_asistencia_147 where usuario='" + usSalida + "' order by fecha_final desc limit 1");
                    if (h.next()) {
                        id_horario = (h.getString("id_horario") != null) ? h.getString("id_horario") : "";
                        fecha_final = (h.getString("fecha_final") != null) ? h.getString("fecha_final") : "";
                        horario = this.nombreHorario(id_horario);
                    }
                    fechaSalida = fechaSalida.replace("-", "/");
                    fecha_final = fecha_final.replace("-", "/");
                    if (formateador.parse(fechaSalida).before(formateador.parse(fecha_final))) {
                        id_horario = "0";
                        this.ejecutar("insert into tbl_empleado_asistencia (ac_no, nombre_biometrico, dia, id_horario) values ('" + id_bioSalida + "','" + usSalida + "','" + fechaSalida + "', '" + id_horario + "');");
//                        i++;
                    }
                    int j = Integer.parseInt(this.numero_semana(usSalida));
                    if (j == 0 || j == 1) {
                        j = 2;
                        idHorarioTmp = this.idHorario(horario);
                        if (idHorarioTmp.compareTo("") != 0) {
                            this.ejecutar("insert into tbl_empleado_asistencia_147 (usuario, fecha_inicial, fecha_final, id_horario) values ('" + usSalida + "',CAST('" + fecha_final + "' AS DATE) + CAST('1 days' AS INTERVAL),CAST('" + fecha_final + "' AS DATE) + CAST('7 days' AS INTERVAL), " + idHorarioTmp + ");");
                            this.actualizarsemana(usSalida, j);
                        }
                    } else if (j == 2) {
                        j = 1;
                        idHorarioTmp = this.idHorario(horario);
                        if (idHorarioTmp.compareTo("") != 0) {
                            this.ejecutar("insert into tbl_empleado_asistencia_147 (usuario, fecha_inicial, fecha_final, id_horario) values ('" + usSalida + "',CAST('" + fecha_final + "' AS DATE) + CAST('8 days' AS INTERVAL), CAST('" + fecha_final + "' AS DATE) + CAST('14 days' AS INTERVAL), " + idHorarioTmp + ");");
                            this.actualizarsemana(usSalida, j);
                        }
                    }
                }
                r.close();
//            }
        } catch (Exception e) {
            e.getMessage();
        }
        return id_horario;
    }

    public String numero_semana(String usuario) {
        String idhorario = "";
        ResultSet r = this.consulta("select numero_semana from tbl_empleado where alias='" + usuario + "';");
        try {
            if (r.next()) {
                idhorario = (r.getString("numero_semana") != null) ? r.getString("numero_semana") : "";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return idhorario;
    }

    public boolean actualizarsemana(String usuario, int numero) {
        return this.ejecutar("update tbl_empleado set numero_semana='" + numero + "' where alias='" + usuario + "'");
    }

    ///
    /**
     * Registra el atraso del asistencia en la hora indicada.
     *
     * @param usuario usario del registro.
     * @param hora hora atrasada.
     * @param fecha fecha del registro.
     * @return (true) Si se actualizo correctamente. (false) Si tuvo algun
     * error.
     */
    public boolean asistenciaGuardar(String usuario, String hora, String fecha) {
        return this.ejecutar("update tbl_empleado_asistencia_tmp set estado='atrasado' where usuario='" + usuario + "' and hora='" + hora + "' and fecha='" + fecha + "'");
    }

    /**
     * Actualiza es estado del registro para el horario.
     *
     * @param usuario Usuario del registro.
     * @param estado estado para el registro del horario.
     * @param fecha Fecha a modificar.
     * @return (true) Si se actualizo correctamente. (false) Si tuvo algun
     * error.
     */
    public boolean asistenciaEstado(String usuario, String estado, String fecha) {
        return this.ejecutar("update tbl_empleado_asistencia set estado='" + estado + "' where nombre_biometrico='" + usuario + "' and dia='" + fecha + "'");
    }

    /**
     * Obtiene toda la informacion de los usuarios que se registraron en el
     * biometrico.
     *
     * @param modalidad (1) Si es 5/7 o 2 si es 14/7.
     * @param orden Modo de orden Asc o Desc.
     * @param fi Fecha inicial para la actualizacion.
     * @param ff Fecha Final para la actualizacion.
     * @return ResultSet.
     */
    public ResultSet usuarioPickAll(int modalidad, String orden, String fi, String ff) {
        return this.consulta("select distinct ac_no,usuario, fecha from tbl_empleado_asistencia_tmp where modalidad=" + modalidad + " and fecha between '" + fi + "' and '" + ff + "' order by fecha " + orden + "");
    }

    /**
     * Obtiene toda la informacion del usuarios que se registra en el
     * biometrico.
     *
     * @param modalidad (1) Si es 5/7 o 2 si es 14/7.
     * @param orden Modo de orden Asc o Desc.
     * @param usuario usuario a consultar.
     * @return ResultSet.
     */
    public ResultSet usuarioPick(int modalidad, String orden, String usuario) {
        return this.consulta("select distinct ac_no,usuario, fecha from tbl_empleado_asistencia_tmp where modalidad=" + modalidad + " and usuario='" + usuario + "' and fecha=date 'now()' order by fecha " + orden + "");
    }

    /**
     * Consulta los registros de asistencia del dia solicitado.
     *
     * @param usuario usuario que se autentica.
     * @param fecha Fecha del registro de la asistencia.
     * @return ResultSet.
     */
    public ResultSet horarioConsulta(String usuario, String fecha) {
        return this.consulta("select * from tbl_empleado_asistencia where nombre_biometrico='" + usuario + "' and dia='" + fecha + "'");
    }

    /**
     * Obtiene todos los registros del biometrico
     *
     * @param usSalida usuario de autenticacion.
     * @param fechaSalida fecha de registro.
     * @return Todos los registros del Biometrico separados por comas.
     */
    public String getPick(String usSalida, String fechaSalida) {
        String picks = "";
        String filtro = "";
        try {
            ResultSet r = this.consulta("select hora from tbl_empleado_asistencia_tmp where usuario='" + usSalida + "' and fecha='" + fechaSalida + "' order by usuario, fecha, hora;");
            while (r.next()) {
                filtro = (r.getString("hora") != null) ? r.getString("hora") : "";
                if (filtro.compareTo("") != 0) {
                    picks += filtro + ",";
                }
            }
            if (picks.compareTo("") != 0) {
                picks = picks.substring(0, picks.length() - 1);
            }
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

    /**
     * Obtiene la hora inicial y final del permiso si los tiene.
     *
     * @param usSalida Usuario que se autentico.
     * @param fechaSalida fecha de autenticacion.
     * @param horario Cadena del horario el cual se va a unir con los permisos.
     * @return Horario mas Permisos.
     */
    public String getPermisos(String usSalida, String fechaSalida, String horario) {
        String picks = horario;
        String filtroInicio = "";
        String filtroFin = "";
        try {
            ResultSet r = this.consulta("select e.empleado, e.alias, p.fechainicio, p.fechafin, p.hora_inicio::time without time zone, p.hora_fin::time without time zone, p.numdias, p.numhoras, p.num_minutos "
                    + "from tbl_permiso p "
                    + "join vta_empleado e on p.id_empleado=e.id_empleado "
                    + "where p.estado='a' and forma_rec in (1,3) and (numhoras>0 or num_minutos>0) and hora_inicio is not null and hora_fin is not null "
                    + "and e.alias='" + usSalida + "' and '" + fechaSalida + "' between p.fechainicio and p.fechafin "
                    + "order by empleado;");
            while (r.next()) {
                filtroInicio = (r.getString("hora_inicio") != null) ? r.getString("hora_inicio") : "";
                filtroFin = (r.getString("hora_fin") != null) ? r.getString("hora_fin") : "";
                if (filtroInicio.compareTo("") != 0) {
                    picks += filtroInicio + ",";
                }
                if (filtroFin.compareTo("") != 0) {
                    picks += filtroFin + ",";
                }
            }
            /*if(picks.compareTo("")!=0){
                picks = picks.substring(0, picks.length()-1); 
            }*/
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

    /**
     * Obtiene hora inicial y final de recuperacion de dias si los tiene.
     *
     * @param usSalida Usuario de autenticacion
     * @param fechaSalida Fecha de registro de asistencia.
     * @param permiso Cadena de horarios mas permisos.
     * @return Permisos mas recuperacion.
     */
    public String getRecuperacionDias(String usSalida, String fechaSalida, String permiso) {
        String picks = permiso;
        String filtroInicio = "";
        String filtroFin = "";
        try {
            ResultSet r = this.consulta("select e.id_empleado, e.empleado, e.alias, dr.dia, dr.hora_entrada, dr.hora_salida  "
                    + "from tbl_permiso p "
                    + "join tab_diasrecuperados dr on p.id_permiso=dr.id_permiso "
                    + "join vta_empleado e on p.id_empleado=e.id_empleado "
                    + "where p.estado='a' and forma_rec in (2) and e.alias='" + usSalida + "' and '" + fechaSalida + "'=dr.dia;");
            while (r.next()) {
                filtroInicio = (r.getString("hora_entrada") != null) ? r.getString("hora_entrada") : "";
                filtroFin = (r.getString("hora_salida") != null) ? r.getString("hora_salida") : "";
                if (filtroInicio.compareTo("") != 0) {
                    picks += filtroInicio + ",";
                }
                if (filtroFin.compareTo("") != 0) {
                    picks += filtroFin + ",";
                }
            }
            if (picks.compareTo("") != 0) {
                picks = picks.substring(0, picks.length() - 1);
            }
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

    /**
     * Obtiene informacion del Horario.
     *
     * @param usSalida Usuario del empleado.
     * @param dia Dia de la semana.
     * @return Horario.
     */
    public String getHorario(String usSalida, String dia) {
        String picks = "";
        String filtro1 = "";
        String estado1 = "";
        String filtro2 = "";
        String estado2 = "";
        String filtro3 = "";
        String estado3 = "";
        String filtro4 = "";
        String estado4 = "";
        String filtro5 = "";
        String estado5 = "";
        String filtro6 = "";
        String estado6 = "";
        try {
            ResultSet r = this.consulta("select CAST(horaentrada1 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada1,horaentrada2, "
                    + "CAST(horaentrada3 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada3,horaentrada4, "
                    + "CAST(horaentrada5 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada5,horaentrada6, "
                    + "estado1,estado2,estado3,estado4,estado5,estado6 "
                    + "from vta_horariodetalle "
                    + "where id_horario=(select id_horario from tbl_empleado where alias='" + usSalida + "') "
                    + "and lower(dia_semana)='" + dia + "' order by id_horario,dia;");
            if (r.next()) {
                filtro1 = (r.getString("horaentrada1") != null) ? r.getString("horaentrada1") : "";
                filtro2 = (r.getString("horaentrada2") != null) ? r.getString("horaentrada2") : "";
                filtro3 = (r.getString("horaentrada3") != null) ? r.getString("horaentrada3") : "";
                filtro4 = (r.getString("horaentrada4") != null) ? r.getString("horaentrada4") : "";
                filtro5 = (r.getString("horaentrada5") != null) ? r.getString("horaentrada5") : "";
                filtro6 = (r.getString("horaentrada6") != null) ? r.getString("horaentrada6") : "";

                estado1 = (r.getString("estado1") != null) ? r.getString("estado1") : "";
                estado2 = (r.getString("estado2") != null) ? r.getString("estado2") : "";
                estado3 = (r.getString("estado3") != null) ? r.getString("estado3") : "";
                estado4 = (r.getString("estado4") != null) ? r.getString("estado4") : "";
                estado5 = (r.getString("estado5") != null) ? r.getString("estado5") : "";
                estado6 = (r.getString("estado6") != null) ? r.getString("estado6") : "";
                if (filtro1.compareTo("") != 0 && estado1.compareTo("t") == 0) {
                    picks += filtro1 + ",";
                }
                if (filtro2.compareTo("") != 0 && estado2.compareTo("t") == 0) {
                    picks += filtro2 + ",";
                }
                if (filtro3.compareTo("") != 0 && estado3.compareTo("t") == 0) {
                    picks += filtro3 + ",";
                }
                if (filtro4.compareTo("") != 0 && estado4.compareTo("t") == 0) {
                    picks += filtro4 + ",";
                }
                if (filtro5.compareTo("") != 0 && estado5.compareTo("t") == 0) {
                    picks += filtro5 + ",";
                }
                if (filtro6.compareTo("") != 0 && estado6.compareTo("t") == 0) {
                    picks += filtro6 + ",";
                }
            }
            /*if(picks.compareTo(null)!=0){
                picks = picks.substring(0, picks.length()-1); 
            }*/
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

    /**
     * Obtiene el horario del empleado.
     *
     * @param usSalida Usuario que se autentica.
     * @param dia Dia de autenticacion.
     * @param id_horario id del horario del empleado.
     * @return El horario del empleado separados por (,) comas.
     */
    public String getHorario(String usSalida, String dia, String id_horario) {
        String picks = "";
        String filtro1 = "";
        String estado1 = "";
        String filtro2 = "";
        String estado2 = "";
        String filtro3 = "";
        String estado3 = "";
        String filtro4 = "";
        String estado4 = "";
        String filtro5 = "";
        String estado5 = "";
        String filtro6 = "";
        String estado6 = "";
        try {
            ResultSet r = this.consulta("select CAST(horaentrada1 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada1,horaentrada2, "
                    + "CAST(horaentrada3 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada3,horaentrada4, "
                    + "CAST(horaentrada5 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada5,horaentrada6, "
                    + "estado1,estado2,estado3,estado4,estado5,estado6 "
                    + "from vta_horariodetalle "
                    + "where id_horario='" + id_horario + "' "
                    + "and lower(dia_semana)='" + dia + "' order by id_horario,dia;");
            if (r.next()) {
                filtro1 = (r.getString("horaentrada1") != null) ? r.getString("horaentrada1") : "";
                filtro2 = (r.getString("horaentrada2") != null) ? r.getString("horaentrada2") : "";
                filtro3 = (r.getString("horaentrada3") != null) ? r.getString("horaentrada3") : "";
                filtro4 = (r.getString("horaentrada4") != null) ? r.getString("horaentrada4") : "";
                filtro5 = (r.getString("horaentrada5") != null) ? r.getString("horaentrada5") : "";
                filtro6 = (r.getString("horaentrada6") != null) ? r.getString("horaentrada6") : "";

                estado1 = (r.getString("estado1") != null) ? r.getString("estado1") : "";
                estado2 = (r.getString("estado2") != null) ? r.getString("estado2") : "";
                estado3 = (r.getString("estado3") != null) ? r.getString("estado3") : "";
                estado4 = (r.getString("estado4") != null) ? r.getString("estado4") : "";
                estado5 = (r.getString("estado5") != null) ? r.getString("estado5") : "";
                estado6 = (r.getString("estado6") != null) ? r.getString("estado6") : "";
                if (filtro1.compareTo("") != 0 && estado1.compareTo("t") == 0) {
                    picks += filtro1 + ",";
                }
                if (filtro2.compareTo("") != 0 && estado2.compareTo("t") == 0) {
                    picks += filtro2 + ",";
                }
                if (filtro3.compareTo("") != 0 && estado3.compareTo("t") == 0) {
                    picks += filtro3 + ",";
                }
                if (filtro4.compareTo("") != 0 && estado4.compareTo("t") == 0) {
                    picks += filtro4 + ",";
                }
                if (filtro5.compareTo("") != 0 && estado5.compareTo("t") == 0) {
                    picks += filtro5 + ",";
                }
                if (filtro6.compareTo("") != 0 && estado6.compareTo("t") == 0) {
                    picks += filtro6 + ",";
                }
            }
            /*if(picks.compareTo(null)!=0){
                picks = picks.substring(0, picks.length()-1); 
            }*/
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

    /**
     * Obtener informacion del horario.
     *
     * @param usSalida Usuario
     * @param dia Dia a consultar.
     * @param nombre Nombre del horario.
     * @return Horario.
     */
    public String getHorarioNombre(String usSalida, String dia, String nombre, String fecha_HE) {
        String picks = "";
        String filtro1 = "";
        String estado1 = "";
        String filtro2 = "";
        String estado2 = "";
        String filtro3 = "";
        String estado3 = "";
        String filtro4 = "";
        String estado4 = "";
        String filtro5 = "";
        String estado5 = "";
        String filtro6 = "";
        String estado6 = "";
        try {
            ResultSet r = this.consulta("select CAST(horaentrada1 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada1,horaentrada2, "
                    + "CAST(horaentrada3 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada3,horaentrada4, "
                    + "CAST(horaentrada5 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada5,horaentrada6, "
                    + "estado1,estado2,estado3,estado4,estado5,estado6 "
                    + "from vta_horariodetalle as D inner join tbl_empleado_asistencia_147 as A on A.id_horario=D.id_horario "
                    + "where D.id_horario=(select id_horario from vta_horario where nombre='" + nombre + "') "
                    + "and lower(dia_semana)='" + dia + "' and A.usuario='" + usSalida + "' and '" + fecha_HE + "' between A.fecha_inicial and A.fecha_final "
                    + "order by D.id_horario, dia;");
            if (r.next()) {
                filtro1 = (r.getString("horaentrada1") != null) ? r.getString("horaentrada1") : "";
                filtro2 = (r.getString("horaentrada2") != null) ? r.getString("horaentrada2") : "";
                filtro3 = (r.getString("horaentrada3") != null) ? r.getString("horaentrada3") : "";
                filtro4 = (r.getString("horaentrada4") != null) ? r.getString("horaentrada4") : "";
                filtro5 = (r.getString("horaentrada5") != null) ? r.getString("horaentrada5") : "";
                filtro6 = (r.getString("horaentrada6") != null) ? r.getString("horaentrada6") : "";

                estado1 = (r.getString("estado1") != null) ? r.getString("estado1") : "";
                estado2 = (r.getString("estado2") != null) ? r.getString("estado2") : "";
                estado3 = (r.getString("estado3") != null) ? r.getString("estado3") : "";
                estado4 = (r.getString("estado4") != null) ? r.getString("estado4") : "";
                estado5 = (r.getString("estado5") != null) ? r.getString("estado5") : "";
                estado6 = (r.getString("estado6") != null) ? r.getString("estado6") : "";
                if (filtro1.compareTo("") != 0 && estado1.compareTo("t") == 0) {
                    picks += filtro1 + ",";
                }
                if (filtro2.compareTo("") != 0 && estado2.compareTo("t") == 0) {
                    picks += filtro2 + ",";
                }
                if (filtro3.compareTo("") != 0 && estado3.compareTo("t") == 0) {
                    picks += filtro3 + ",";
                }
                if (filtro4.compareTo("") != 0 && estado4.compareTo("t") == 0) {
                    picks += filtro4 + ",";
                }
                if (filtro5.compareTo("") != 0 && estado5.compareTo("t") == 0) {
                    picks += filtro5 + ",";
                }
                if (filtro6.compareTo("") != 0 && estado6.compareTo("t") == 0) {
                    picks += filtro6 + ",";
                }
            }
            /*if(picks.compareTo(null)!=0){
                picks = picks.substring(0, picks.length()-1); 
            }*/
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

    public String getHorarioNombre(String usSalida, String fecha) {
        String id_horario = "";
        try {
            ResultSet r = this.consulta("select id_horario from tbl_empleado_asistencia_147 where '" + fecha + "' between fecha_inicial and  fecha_final and  usuario='" + usSalida + "';");
            if (r.next()) {
                id_horario = (r.getString("id_horario") != null) ? r.getString("id_horario") : "";
            }
            r.close();
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return id_horario;
    }

    public String getHorarioNombre(String usSalida, String dia, String nombre, String fecha_HE, String parm1) {
        String picks = "";
        String filtro1 = "";
        String estado1 = "";
        String filtro2 = "";
        String estado2 = "";
        String filtro3 = "";
        String estado3 = "";
        String filtro4 = "";
        String estado4 = "";
        String filtro5 = "";
        String estado5 = "";
        String filtro6 = "";
        String estado6 = "";
        try {
            ResultSet r = this.consulta("select CAST(horaentrada1 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada1,horaentrada2, "
                    + "CAST(horaentrada3 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada3,horaentrada4, "
                    + "CAST(horaentrada5 AS time) + CAST('1 minutes' AS INTERVAL) as horaentrada5,horaentrada6, "
                    + "estado1,estado2,estado3,estado4,estado5,estado6 "
                    + "from vta_horariodetalle as D inner join tbl_empleado_asistencia_147 as A on A.id_horario=D.id_horario "
                    + "where D.id_horario=" + nombre + " "
                    + "and lower(dia_semana)='" + dia + "' and A.usuario='" + usSalida + "' and '" + fecha_HE + "' between A.fecha_inicial and A.fecha_final "
                    + "order by D.id_horario, dia;");
            if (r.next()) {
                filtro1 = (r.getString("horaentrada1") != null) ? r.getString("horaentrada1") : "";
                filtro2 = (r.getString("horaentrada2") != null) ? r.getString("horaentrada2") : "";
                filtro3 = (r.getString("horaentrada3") != null) ? r.getString("horaentrada3") : "";
                filtro4 = (r.getString("horaentrada4") != null) ? r.getString("horaentrada4") : "";
                filtro5 = (r.getString("horaentrada5") != null) ? r.getString("horaentrada5") : "";
                filtro6 = (r.getString("horaentrada6") != null) ? r.getString("horaentrada6") : "";

                estado1 = (r.getString("estado1") != null) ? r.getString("estado1") : "";
                estado2 = (r.getString("estado2") != null) ? r.getString("estado2") : "";
                estado3 = (r.getString("estado3") != null) ? r.getString("estado3") : "";
                estado4 = (r.getString("estado4") != null) ? r.getString("estado4") : "";
                estado5 = (r.getString("estado5") != null) ? r.getString("estado5") : "";
                estado6 = (r.getString("estado6") != null) ? r.getString("estado6") : "";
                if (filtro1.compareTo("") != 0 && estado1.compareTo("t") == 0) {
                    picks += filtro1 + ",";
                }
                if (filtro2.compareTo("") != 0 && estado2.compareTo("t") == 0) {
                    picks += filtro2 + ",";
                }
                if (filtro3.compareTo("") != 0 && estado3.compareTo("t") == 0) {
                    picks += filtro3 + ",";
                }
                if (filtro4.compareTo("") != 0 && estado4.compareTo("t") == 0) {
                    picks += filtro4 + ",";
                }
                if (filtro5.compareTo("") != 0 && estado5.compareTo("t") == 0) {
                    picks += filtro5 + ",";
                }
                if (filtro6.compareTo("") != 0 && estado6.compareTo("t") == 0) {
                    picks += filtro6 + ",";
                }
            }
            /*if(picks.compareTo(null)!=0){
                picks = picks.substring(0, picks.length()-1); 
            }*/
            r.close();
        } catch (Exception e) {
            picks = e.getMessage();
        }
        return picks;
    }

}
