/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cron.trabajos;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Sistemas
 */
public class SRI implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException 
    {
        //  solo obtener la autorizacion
        DocumentosElectronicosSri objDocumentosElectronicosSri = new DocumentosElectronicosSri();
        objDocumentosElectronicosSri.obtenerAutorizaciones();
    }
}
