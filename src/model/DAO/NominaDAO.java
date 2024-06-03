/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DAO;

import java.util.LinkedList;
import java.util.List;
import model.HibernateUtil;
import model.Nomina;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Usuario
 */
public class NominaDAO {

    SessionFactory sf = null;
    Session sesion = null;
    Transaction tx = null;


    public NominaDAO() {
        sf = HibernateUtil.getSessionFactory();
        sesion = sf.openSession();
    }

    
    public Nomina getNomina(double liquido, int mes, int anio, int idTrabajador, double brutomensual) {
        Nomina n = null;
        try {
            String consulta = "FROM Nomina n WHERE n.liquidoNomina=:param";
            Query query = sesion.createQuery(consulta);
            query.setParameter("param", liquido);
            List listResult = query.list();
            if (!listResult.isEmpty()) {
                for (int i = 0; i < listResult.size(); i++) {
                    Nomina nomina = (Nomina) listResult.get(i);
                    if (nomina.getAnio() == anio && nomina.getMes() == mes && nomina.getTrabajador().getIdTrabajador() == idTrabajador && nomina.getBrutoNomina() == brutomensual) {
                        n = nomina;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al intentar hacer la consulta. " + e.getMessage());
        }
        return n;
    }

   
    public void addNomina(Nomina nomina) {
        tx = sesion.beginTransaction();
        
        if (this.getNomina(nomina.getLiquidoNomina(), nomina.getMes(), nomina.getAnio(), nomina.getTrabajador().getIdTrabajador(), nomina.getBrutoNomina()) == null) {
            sesion.saveOrUpdate(nomina);
        }
        tx.commit();
    }

   
    
    public LinkedList<Nomina> asignarIDNominas(LinkedList<Nomina> nominas) {
        int idNomina = 0;
        //Obtengo el ID del último trabajador
        tx = sesion.beginTransaction();
        //Obtengo el último trabajador guardado
        Nomina nB = (Nomina) sesion.createQuery("FROM Nomina ORDER BY IdNomina DESC").setMaxResults(1).uniqueResult();
        tx.commit();
        if (nB != null) {
            idNomina = nB.getIdNomina();
        }
        for (int i = 0; i < nominas.size(); i++) {
            Nomina n = this.getNomina(nominas.get(i).getLiquidoNomina(), nominas.get(i).getMes(), nominas.get(i).getAnio(), nominas.get(i).getTrabajador().getIdTrabajador(), nominas.get(i).getBrutoNomina());
            if (n == null) {
                nominas.get(i).setIdNomina(++idNomina);
            } else {
                nominas.get(i).setIdNomina(n.getIdNomina());
            }
        }

        return nominas;
    }

}
