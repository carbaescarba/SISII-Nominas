/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicasinfii2023;

import java.util.List;
import org.hibernate.*;
import model.HibernateUtil;
import model.Trabajador;

/**
 *
 * @author Usuario
 */

public class Practica1 {
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    
   public Practica1() throws Exception{
       this.sessionFactory = HibernateUtil.getSessionFactory();
   }
   
   public Trabajador trabajador(String dni)throws Exception{
       try{
           this.session= this.sessionFactory.openSession();
           String consulta = "FROM Trabajador trabajador WHERE trabajador.nifnie=:param";
           Query query = this.session.createQuery(consulta);
           query.setParameter("param", dni);
           List <Trabajador> list = query.list();
           return list.get(0);
       }catch(Exception e){
           e.printStackTrace();
           throw new Exception("No hay trabajador con este NIF");
       }
   }
   
   public void incrementSalary(Trabajador trabajador){
       try{
           //Hasta que no se haga el commit controla de que todo funcione bien y en caso de fallo volver para atras
           this.session= this.sessionFactory.openSession();
           this.transaction = this.session.beginTransaction();
           String consulta = "UPDATE Categorias c SET c.salarioBaseCategoria=:param1 WHERE c.idCategoria=:param2";
           Query query = this.session.createQuery(consulta);
           query.setParameter("param1", trabajador.getCategorias().getSalarioBaseCategoria() + 200);
           query.setParameter("param2", trabajador.getCategorias().getIdCategoria());
           query.executeUpdate();
           this.transaction.commit();
       }catch(Exception e){
          // throw new Exception("No hay trabajador con este NIF");
          e.printStackTrace();
       }
       
   }
   
   public void updateCompanyName(Trabajador trabajador){
       try{
           this.session= this.sessionFactory.openSession();
           this.transaction = this.session.beginTransaction();
           String consulta = "UPDATE Empresas emp SET emp.nombre=CONCAT(emp.nombre, '2023') WHERE emp.idEmpresa!=:param2";//Se pueden modificar valores estaticos en una consulta, pero para variables no
           Query query = this.session.createQuery(consulta);
           query.setParameter("param2", trabajador.getEmpresas().getIdEmpresa());
           query.executeUpdate();
           this.transaction.commit();
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public double eliminateIRPF(){
       try{
           this.session=this.sessionFactory.openSession();
           String consulta = " SELECT MAX(n2.irpf) FROM Nomina n2";
           Query queryS = this.session.createQuery(consulta);
           double value = (double) queryS.list().get(0);
           this.transaction = this.session.beginTransaction();
            consulta = "DELETE Nomina n WHERE n.irpf =:param";
           Query query = this.session.createQuery(consulta);
           query.setParameter("param", value);
           query.executeUpdate();
           this.transaction.commit();
           return value;
       }catch(Exception e){
           e.printStackTrace();
       }
       return 0;
   }
   
   
}

