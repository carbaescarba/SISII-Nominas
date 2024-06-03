package model.DAO;

import java.util.LinkedList;
import java.util.List;
import model.Empresas;
import model.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class EmpresasDAO {

    SessionFactory sf = null;
    Session sesion = null;
    Transaction tx = null;

    public EmpresasDAO() {
        sf = HibernateUtil.getSessionFactory();
        sesion = sf.openSession();
    }

    public Empresas getEmpresa(String cif) {
        Empresas em = null;
        try {
            String consulta = "FROM Empresas e WHERE e.cif=:param";
            Query query = sesion.createQuery(consulta);
            query.setParameter("param", cif);
            List listResult = query.list();
            if (!listResult.isEmpty()) {
                em = (Empresas) listResult.get(0);
            }
        } catch (Exception e) {
            System.out.println("Error al intentar hacer la consulta. " + e.getMessage());
        }
        return em;
    }

    
    public void addEmpresa(Empresas e) {
        tx = sesion.beginTransaction();
        if (getEmpresa(e.getCif()) == null) {
            sesion.saveOrUpdate(e);
        }
        tx.commit();
    }

    
  

   
    public LinkedList<Empresas> asignarIDEmpresa(LinkedList<Empresas> empresas) {
        int idEmp = 0;
        //Obtengo el ID del último trabajador
        tx = sesion.beginTransaction();
        //Obtengo el último trabajador guardado
        Empresas eB = (Empresas) sesion.createQuery("FROM Empresas ORDER BY IdEmpresa DESC").setMaxResults(1).uniqueResult();
        tx.commit();
        if (eB != null) {
            idEmp = eB.getIdEmpresa();
        }
        for (int i = 0; i < empresas.size(); i++) {
            Empresas e = this.getEmpresa(empresas.get(i).getCif());
            if (e == null) {
                empresas.get(i).setIdEmpresa(++idEmp);
            } else {
                empresas.get(i).setIdEmpresa(e.getIdEmpresa());
            }
        }
        return empresas;
    }

}
