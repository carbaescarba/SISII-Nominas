/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicasinfii2023;

import java.util.Calendar;
import java.util.Scanner;
import model.Trabajador;

/**
 *
 * @author Usuario
 */
public class PracticaSINFII2023 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        try{
        Scanner scan = new Scanner(System.in);
        String DNI = scan.nextLine();
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(DNI.split("/")[1]), Integer.parseInt(DNI.split("/")[0]), 1);
        ExcelManager ex = new ExcelManager(".\\resources\\SistemasInformacionII.xlsx");
        ex.extractInfo(".\\resources\\SistemasInformacionII.xlsx", c);
        /**Practica1 p = new Practica1();
        Trabajador t = p.trabajador( DNI);
        System.out.println("Nombre:" + t.getNombre() + "Apellidos:" + t.getApellido1() + t.getApellido2() + "NIF/NIE:" + t.getNifnie() + 
                "Nombre de la empresa:" + t.getEmpresas().getNombre() + "Categoría:" + t.getCategorias().getNombreCategoria() + "CIF de la empresa:" + t.getEmpresas().getCif()
                + "Numero Trabajadores:" + t.getEmpresas().getTrabajadors().size());
        p.incrementSalary(t);
        p.updateCompanyName(t);
        System.out.println("Valor IRPF usado: "+ p.eliminateIRPF());
        */}catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage()); 
        }
    }
    
}
