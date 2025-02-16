package model;
// Generated 11-may-2023 20:51:23 by Hibernate Tools 4.3.1


import javax.persistence.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Trabajador generated by hbm2java
 */
public class Trabajador  implements java.io.Serializable {


     private Integer idTrabajador;
     private Categorias categorias;
     private Empresas empresas;
     private String nombre;
     private String apellido1;
     private String apellido2;
     private String nifnie;
     private String email;
     private Date fechaAlta;
     private String codigoCuenta;
     private String iban;
     private Date bajaLaboral;
     private Date altaLaboral;
     @Transient
     private int fila;
     
     private Set nominas = new HashSet(0);
     private boolean prorrata;

    public Trabajador() {
    }

	
    public Trabajador(Categorias categorias, Empresas empresas, String nombre, String apellido1, String nifnie) {
        this.categorias = categorias;
        this.empresas = empresas;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.nifnie = nifnie;
    }
    public Trabajador(Categorias categorias, Empresas empresas, String nombre, String apellido1, String apellido2, String nifnie, String email, Date fechaAlta, String codigoCuenta, String iban, Date bajaLaboral, Date altaLaboral, Set nominas, boolean prorrata) {
       this.categorias = categorias;
       this.empresas = empresas;
       this.nombre = nombre;
       this.apellido1 = apellido1;
       this.apellido2 = apellido2;
       this.nifnie = nifnie;
       this.email = email;
       this.fechaAlta = fechaAlta;
       this.codigoCuenta = codigoCuenta;
       this.iban = iban;
       this.bajaLaboral = bajaLaboral;
       this.altaLaboral = altaLaboral;
       this.nominas = nominas;
       this.prorrata = prorrata;
    }
   
    public Integer getIdTrabajador() {
        return this.idTrabajador;
    }
    
    public void setIdTrabajador(Integer idTrabajador) {
        this.idTrabajador = idTrabajador;
    }
    public Categorias getCategorias() {
        return this.categorias;
    }
    
    public void setCategorias(Categorias categorias) {
        this.categorias = categorias;
    }
    public Empresas getEmpresas() {
        return this.empresas;
    }
    
    public void setEmpresas(Empresas empresas) {
        this.empresas = empresas;
    }
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido1() {
        return this.apellido1;
    }
    
    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }
    public String getApellido2() {
        return this.apellido2;
    }
    
    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }
    public String getNifnie() {
        return this.nifnie;
    }
    
    public void setNifnie(String nifnie) {
        this.nifnie = nifnie;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getFechaAlta() {
        return this.fechaAlta;
    }
    
    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    public String getCodigoCuenta() {
        return this.codigoCuenta;
    }
    
    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }
    public String getIban() {
        return this.iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
    public Date getBajaLaboral() {
        return this.bajaLaboral;
    }
    
    public void setBajaLaboral(Date bajaLaboral) {
        this.bajaLaboral = bajaLaboral;
    }
    public Date getAltaLaboral() {
        return this.altaLaboral;
    }
    
    public void setAltaLaboral(Date altaLaboral) {
        this.altaLaboral = altaLaboral;
    }
    public Set getNominas() {
        return this.nominas;
    }
    
    public void setNominas(Set nominas) {
        this.nominas = nominas;
    }

    public int getFila(){
        return this.fila;
    }

    public void setFila(int fila){
        this.fila = fila;
    }
    
    public boolean getProrrata(){
        return this.prorrata;
    }
    
    public void setProrrata(boolean prorrata){
        this.prorrata = prorrata;
    }
   

}


