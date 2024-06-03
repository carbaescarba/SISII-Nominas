/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DAO;

import java.util.LinkedList;
import java.util.List;
import model.Categorias;
import model.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Usuario
 */
public class CategoriasDAO {

    SessionFactory sf = null;
    Session sesion = null;
    Transaction tx = null;

    public CategoriasDAO() {
        sf = HibernateUtil.getSessionFactory();
        sesion = sf.openSession();
    }

    
    public Categorias getCategoria(String name) {
        Categorias c = null;
        try {
            String consulta = "FROM Categorias c WHERE c.nombreCategoria=:param";
            Query query = sesion.createQuery(consulta);
            query.setParameter("param", name);
            List listResult = query.list();
            if (!listResult.isEmpty()) {
                for (int i = 0; i < listResult.size(); i++) {
                    if (((Categorias) listResult.get(i)).getNombreCategoria().equals(name)) {
                        c = (Categorias) listResult.get(i);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al intentar hacer la consulta. " + e.getMessage());
        }
        return c;
    }

   
    public void addCategoria(Categorias c) {
        tx = sesion.beginTransaction();
        if (getCategoria(c.getNombreCategoria()) == null) {
            sesion.saveOrUpdate(c);
        }
        tx.commit();
    }

    

   
    public LinkedList<Categorias> asignarIDCategorias(LinkedList<Categorias> categorias) {
        int idCat = 0;
        //Obtengo el ID del último trabajador
        tx = sesion.beginTransaction();
        //Obtengo el último trabajador guardado
        Categorias cB = (Categorias) sesion.createQuery("FROM Categorias ORDER BY IdCategoria DESC").setMaxResults(1).uniqueResult();
        tx.commit();
        if (cB != null) {
            idCat = cB.getIdCategoria();
        }
        for (int i = 0; i < categorias.size(); i++) {
            Categorias c = this.getCategoria(categorias.get(i).getNombreCategoria());
            if (c == null) {
                categorias.get(i).setIdCategoria(++idCat);
            } else {
                categorias.get(i).setIdCategoria(c.getIdCategoria());
            }
        }
        return categorias;
    }
}
