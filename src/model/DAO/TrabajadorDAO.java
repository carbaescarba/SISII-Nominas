package model.DAO;

import java.util.LinkedList;
import java.util.Date;
import java.util.List;
import model.HibernateUtil;
import model.Trabajador;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Usuario
 */
public class TrabajadorDAO {

    SessionFactory sf = null;
    Session sesion = null;
    Transaction tx = null;

    /**
     * Abre la sesión
     */
    public TrabajadorDAO() {
        sf = HibernateUtil.getSessionFactory();
        sesion = sf.openSession();
    }

    
    public Trabajador getTrabajador(String nif, String name, Date fechaAlta) {
        Trabajador tb = null;
        try {
            String consulta = "FROM Trabajador n WHERE n.nifnie=:param";
            Query query = sesion.createQuery(consulta);
            query.setParameter("param", nif);
            List listResult = query.list();
            if (!listResult.isEmpty()) {
                for (int i = 0; i < listResult.size(); i++) {
                    if (((Trabajador) listResult.get(i)).getNombre().equals(name) && (((Trabajador) listResult.get(i)).getFechaAlta().compareTo(fechaAlta) == 0)) {
                        tb = (Trabajador) listResult.get(i);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al intentar hacer la consulta. " + e.getMessage());
        }
        return tb;
    }

  
    public void addTrabajador(Trabajador tb) {
        tx = sesion.beginTransaction();

        if (this.getTrabajador(tb.getNifnie(), tb.getNombre(), tb.getFechaAlta()) == null) {
            sesion.saveOrUpdate(tb);
        }
        tx.commit();
    }

   
    public void borraTodo() {
        tx = sesion.beginTransaction();

        String consulta = "DELETE FROM Trabajador";
        Query query = sesion.createQuery(consulta);
        query.executeUpdate();
        tx.commit();
    }

   
    public LinkedList<Trabajador> aginarIDTrabajadores(LinkedList<Trabajador> listaTrabajadores) {
        int idTrab = 0;
        //Obtengo el ID del último trabajador
        tx = sesion.beginTransaction();
        //Obtengo el último trabajador guardado
        Trabajador tbB = (Trabajador) sesion.createQuery("FROM Trabajador ORDER BY idTrabajador DESC").setMaxResults(1).uniqueResult();
        tx.commit();
        if (tbB != null) {
            idTrab = tbB.getIdTrabajador();
        }
        for (int i = 0; i < listaTrabajadores.size(); i++) {
            Trabajador tb = this.getTrabajador(listaTrabajadores.get(i).getNifnie(), listaTrabajadores.get(i).getNombre(), listaTrabajadores.get(i).getFechaAlta());
            if (tb == null) {
                listaTrabajadores.get(i).setIdTrabajador(++idTrab);
            } else {
                listaTrabajadores.get(i).setIdTrabajador(tb.getIdTrabajador());
            }
        }

        return listaTrabajadores;
    }
}
