/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicasinfii2023;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import static com.itextpdf.kernel.pdf.PdfName.Color;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import model.Categorias;
import model.DAO.CategoriasDAO;
import model.DAO.EmpresasDAO;
import model.DAO.NominaDAO;
import model.DAO.TrabajadorDAO;
import model.Empresas;
import model.Nomina;
import model.Trabajador;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import practicasinfii2023.Practica1;

/**
 *
 * @author Usuario
 */
public class ExcelManager {

    private String ruta;
    private double trienio;
    private double impTrienio;
    private double impDescAccTrabEmp;
    private double impDescContComEmp;
    private double impDescFogEmp;
    private double impDescDesEmp;
    private double impDescFormEmp;
    private double impDescCuotaOGTrab;
    private double impDescDesTrab;
    private double impDescFormTrab;
    private double impDescMeiTrab;
    private double impDescMeiEmp;
    private LinkedList<Trabajador> listaTrabajadores;
    private LinkedList<Categorias> listaCategorias;
    private LinkedList<Empresas> listaEmpresas;
    private LinkedList<Nomina> listaNominas;
    private LinkedList<Trabajador> listaErroresDniNie;
    private LinkedList<Trabajador> listaErroresCCC;
    private LinkedList<String> listaCCCErroneos;
    private HashMap<String, Double> mapeadorComp;
    private HashMap<String, Double> mapeadorSal;
    private HashMap<Integer, Double> mapeadorImp;
    private HashMap<Double, Double> mapeadorBruto;
    private HashMap<String, Double> mapeadorDesc;
    private Calendar date;
    private DecimalFormat df;
    private int dias;
    //CREAR "Calendar Date"

    ExcelManager(String resourcesSistemasInformacionIIxlsx, Calendar c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum EnumLetters {
        T, R, W, A, G, M, Y, F, P, D, X, B, N, J, Z, S, Q, V, H, L, C, K, E
    }

    enum PesoIban {
        A(10), B(11), C(12), D(13), E(14), F(15), G(16), H(17), I(18), J(19), K(20), L(21), M(22), N(23), O(24), P(25), Q(26), R(27), S(28), T(29), U(30), V(31), W(32), X(33), Y(34), Z(35);
        private int peso;

        private PesoIban(int peso) {
            this.peso = peso;
        }

        public int getPeso() {
            return this.peso;
        }
    }

    public ExcelManager(String ruta) {
        this.ruta = ruta;
        listaTrabajadores = new LinkedList<Trabajador>();
        listaCategorias = new LinkedList<Categorias>();
        listaEmpresas = new LinkedList<Empresas>();
        listaNominas = new LinkedList<Nomina>();
        listaErroresDniNie = new LinkedList<Trabajador>();
        listaErroresCCC = new LinkedList<Trabajador>();
        listaCCCErroneos = new LinkedList<String>();
        mapeadorComp = new HashMap<String, Double>();
        mapeadorSal = new HashMap<String, Double>();
        mapeadorImp = new HashMap<Integer, Double>();
        mapeadorBruto = new HashMap<Double, Double>();
        mapeadorDesc = new HashMap<String, Double>();
        df = new DecimalFormat("#.##");
    }

    private String checkDNI(String dni) throws Exception {
        String numero = dni.substring(0, 8);
        String letra = dni.substring(8);

        if (dni.length() == 9) {
            Integer.parseInt(numero);
            String compareLetra = EnumLetters.values()[Integer.parseInt(numero) % 23].toString();
            if (letra.equals(compareLetra)) {
            } else {
                dni = numero + compareLetra;
            }
        } else {
            throw new Exception();
        }
        return dni;
    }

    private String checkNIE(String nie) throws Exception {
        String letra = nie.substring(0, 1);

        if (letra.equals("X")) {
            String nieN = checkDNI(0 + nie.substring(1));
            return "X" + nieN.substring(1);
        } else if (letra.equals("Y")) {
            String nieN = checkDNI(1 + nie.substring(1));
            return "Y" + nieN.substring(1);
        } else if (letra.equals("Z")) {
            String nieN = checkDNI(2 + nie.substring(1));
            return "Z" + nieN.substring(1);
        } else {
            throw new Exception();
        }
    }

    public void extractInfo(String ruta, Calendar date) throws Exception {
        this.ruta = ruta;
        this.date = date;
        File file = new File(this.ruta);
        try {
            XSSFWorkbook wb = new XSSFWorkbook(file);
            this.getInfoEmpleados(wb);
            this.getInfoComplementos(wb);
            this.getInfoBruto(wb);
            this.getInfoImpTri(wb);
            for (int i = 0; i < this.listaTrabajadores.size(); i++) {
                System.out.println("Trabajador " + date.get(date.YEAR));
                if (this.listaTrabajadores.get(i).getFechaAlta().getTime() < this.date.getTimeInMillis() && !listaErroresDniNie.contains(listaTrabajadores.get(i))) {
                    generateNominas(date, this.listaTrabajadores.get(i));
                }
            }
            almacenarDatos();
            generateXMLNominas("nominas");
            for (int i = 0; i < this.listaNominas.size(); i++) {
                generatePDF(listaNominas.get(i).getTrabajador(), listaNominas.get(i));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExcelManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExcelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getInfoEmpleados(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        int fil = 2;
        int cont = 0;

        if (rowIt.hasNext()) {
            rowIt.next();
        }
        int i = 0;
        //Recorremos con el  iterador
        while (rowIt.hasNext()) {
            Row row = rowIt.next();
            if (row.getCell(0) != null) {
                System.out.println("" + i);
                String paisAcc = row.getCell(0).toString();
                String codAcc = row.getCell(1).toString();
                Date dateAlta = row.getCell(4).getDateCellValue();
                String cif = row.getCell(5).toString();
                String nombreEmp = row.getCell(6).toString();
                String categoria = row.getCell(7).toString();
                String surname1 = row.getCell(8).toString();
                String surname2 = null;
                if (row.getCell(9) != null) {
                    surname2 = row.getCell(9).toString();
                }
                String name = row.getCell(10).toString();
                String nifnie = "";
                if (row.getCell(11) != null) {
                    nifnie = row.getCell(11).toString();
                }
                String prorratExt = row.getCell(12).toString();

                Date dateBajaLab = null;
                Date dateAltaLab = null;
                if (row.getCell(13) != null) {
                    dateBajaLab = row.getCell(13).getDateCellValue();
                }
                if (row.getCell(14) != null) {
                    dateAltaLab = row.getCell(14).getDateCellValue();
                }

                Trabajador trabajador = new Trabajador();
                if (prorratExt.equals("SI")) {
                    trabajador.setProrrata(true);
                }
                Empresas empresa = new Empresas();
                if (dateBajaLab != null && dateAltaLab != null) {
                    trabajador.setAltaLaboral(dateAltaLab);
                    trabajador.setBajaLaboral(dateBajaLab);
                }
                trabajador.setNombre(name);
                trabajador.setApellido1(surname1);
                if (surname2 != null) {
                    trabajador.setApellido2(surname2);
                }
                trabajador.setCodigoCuenta(codAcc);
                trabajador.setFechaAlta(dateAlta);
                trabajador.setEmpresas(empresa);
                empresa.setCif(cif);
                empresa.setNombre(nombreEmp);
                String nifnieCorregido;
                nifnieCorregido = corrigeDniNie(nifnie);
                if (nifnieCorregido == null) {
                    trabajador.setNifnie(nifnie);
                } else {
                    trabajador.setNifnie(nifnieCorregido);
                }
                trabajador.setFila(fil);
                if (!nifnie.equals(nifnieCorregido)) {
                    listaErroresDniNie.add(trabajador);
                } else {
                    existsDniNie(nifnie, trabajador);
                }

                String cccCorregido = this.checkCCC(codAcc);
                if (!cccCorregido.equals(codAcc)) {
                    listaCCCErroneos.add(codAcc);
                    listaErroresCCC.add(trabajador);
                }
                trabajador.setCodigoCuenta(cccCorregido);

                trabajador.setIban(generateIban(trabajador, paisAcc));
                trabajador.setEmail(generateEmail(trabajador));
                Categorias c = new Categorias();
                c.setNombreCategoria(categoria);
                trabajador.setCategorias(c);
                //Setear email llamando generateEmail();
                listaTrabajadores.add(trabajador);

            }

            i++;
        }
        try {
            generateErrors("Errores", listaErroresDniNie);
            generateErrors("CCC", listaErroresCCC);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String corrigeDniNie(String nifnie) {
        try {
            if (Character.isLetter(nifnie.charAt(0))) {
                return checkNIE(nifnie);
            } else {
                return checkDNI(nifnie);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void getInfoComplementos(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheetAt(1);
        Iterator<Row> rowIt = sheet.iterator();

        if (rowIt.hasNext()) {
            rowIt.next();
        }
        while (rowIt.hasNext()) {
            Row row = rowIt.next();
            String categoria = row.getCell(2).toString();
            double complemento = row.getCell(1).getNumericCellValue();
            double salario = row.getCell(0).getNumericCellValue();
            this.mapeadorComp.put(categoria, complemento);
            this.mapeadorSal.put(categoria, salario);
        }
    }

    private void getInfoImpTri(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheetAt(2);
        Iterator<Row> rowIt = sheet.iterator();

        if (rowIt.hasNext()) {
            rowIt.next();
        }
        while (rowIt.hasNext()) {
            Row row = rowIt.next();
            double importe = row.getCell(1).getNumericCellValue();
            int trienios = (int) row.getCell(0).getNumericCellValue();
            this.mapeadorImp.put(trienios, importe);
        }
    }

    private void getInfoBruto(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheetAt(3);
        Iterator<Row> rowIt = sheet.iterator();

        if (rowIt.hasNext()) {
            Row row = rowIt.next();
            String descuentos = row.getCell(5).toString();
            double valor = row.getCell(6).getNumericCellValue();
            this.mapeadorDesc.put(descuentos, valor);
        }
        while (rowIt.hasNext()) {
            Row row = rowIt.next();
            if (row.getCell(5) != null) {
                String descuentos = row.getCell(5).toString();
                double valor = row.getCell(6).getNumericCellValue();
                this.mapeadorDesc.put(descuentos, valor);
            }
            double bruto = row.getCell(0).getNumericCellValue();
            double retencion = row.getCell(1).getNumericCellValue();
            this.mapeadorBruto.put(bruto, retencion);
        }
    }

    private void existsDniNie(String nifnie, Trabajador trab) {
        for (int i = 0; i < this.listaTrabajadores.size(); i++) {
            if (nifnie.equals(listaTrabajadores.get(i).getNifnie())) {
                listaErroresDniNie.add(trab);
            }
        }
    }

    private void generateErrors(String nameFile, LinkedList<Trabajador> listaErrores) throws Exception {

        String raiz;
        if (nameFile.equals("Errores")) {
            raiz = "Trabajadores";
        } else {
            raiz = "Cuentas";
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementacion = builder.getDOMImplementation();
        Document document = implementacion.createDocument(null, raiz, null);
        document.setXmlVersion("1.0");

        Element root = document.getDocumentElement();
        for (int i = 0; i < listaErrores.size(); i++) {
            Element worker = document.createElement("Trabajador");
            worker.setAttribute("id", listaTrabajadores.get(i).getFila() + "");
            if (nameFile.equals("Errores")) {
                Element dninie = document.createElement("NIF_NIE");
                Text textDni = document.createTextNode(listaErrores.get(i).getNifnie());
                worker.appendChild(dninie);
                dninie.appendChild(textDni);

                Element name = document.createElement("Nombre");
                Text textName = document.createTextNode(listaErrores.get(i).getNombre());
                worker.appendChild(name);
                name.appendChild(textName);

                Element ap1 = document.createElement("Primer_Apellido");
                Text textAp1 = document.createTextNode(listaErrores.get(i).getApellido1());
                worker.appendChild(ap1);
                ap1.appendChild(textAp1);
                if (listaErrores.get(i).getApellido2() != null) {
                    Element ap2 = document.createElement("Segundo_Apellido");
                    Text textAp2 = document.createTextNode(listaErrores.get(i).getApellido2());
                    worker.appendChild(ap2);
                    ap2.appendChild(textAp2);
                }
                Element emp = document.createElement("Empresa");
                Text textEmp = document.createTextNode(listaErrores.get(i).getEmpresas().getNombre());
                worker.appendChild(emp);
                emp.appendChild(textEmp);

                Element cat = document.createElement("Categoria");
                Text textCat = document.createTextNode(listaErrores.get(i).getCategorias().getNombreCategoria());
                worker.appendChild(cat);
                cat.appendChild(textCat);
            } else {
                //Crear arraylist para almacenar los CCC erroneos
                Element name = document.createElement("Nombre");
                Text textName = document.createTextNode(listaErrores.get(i).getNombre());
                worker.appendChild(name);
                name.appendChild(textName);

                Element ap1 = document.createElement("Apellidos");
                String apellidos = listaErrores.get(i).getApellido1();
                if (listaErrores.get(i).getApellido2() != null) {
                    apellidos += listaErrores.get(i).getApellido2();
                }
                Text textAp1 = document.createTextNode(apellidos);
                worker.appendChild(ap1);
                ap1.appendChild(textAp1);

                Element cccWrong = document.createElement("CCCErroneo");
                Text textCCC = document.createTextNode(listaCCCErroneos.get(i));
                worker.appendChild(cccWrong);
                cccWrong.appendChild(textCCC);

                Element iban = document.createElement("IBANCorrecto");
                Text textIban = document.createTextNode(listaErrores.get(i).getIban());
                worker.appendChild(iban);
                iban.appendChild(textIban);
            }

            root.appendChild(worker);
        }
        Source source = new DOMSource(document);
        Result result = new StreamResult(new java.io.File("./resources/" + nameFile + ".xml"));
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(source, result);
    }

    private void generateNominas(Calendar date, Trabajador trabajador) {
        //Variables que obtenemos

        String categoria = trabajador.getCategorias().getNombreCategoria();
        double anual = mapeadorSal.get(trabajador.getCategorias().getNombreCategoria());
        double complemento = mapeadorComp.get(trabajador.getCategorias().getNombreCategoria());
        int antiguedad = returnAntiguedad(trabajador.getFechaAlta(), date);

        //Variables que calculamos
        double salarioBruto = anual + complemento;

        double salarioBrutoMensual = salarioBruto / 14;
        salarioBruto += calculateTrienio(antiguedad, trabajador, date);

        double salarioExtra = calculateExtra(trabajador, antiguedad, salarioBrutoMensual);
        double salarioBrutoMensualProrrateado = calculateBrutoMensualPro(trabajador, salarioBrutoMensual, salarioExtra, antiguedad);

        //Sumamos treinio al bruto mensual
        salarioBrutoMensual += this.impTrienio;

        double irpf = calculateIRPF(date, salarioBruto);

        //Descuentos
        double accidentesTrabajoEmp = (double) this.mapeadorDesc.get("Accidentes trabajo EMPRESARIO");
        double contingenciasComunesEmp = (double) this.mapeadorDesc.get("Contingencias comunes EMPRESARIO");
        double fogasaEmp = (double) this.mapeadorDesc.get("Fogasa EMPRESARIO");
        double desempleoEmp = (double) this.mapeadorDesc.get("Desempleo EMPRESARIO");
        double formacionEmp = (double) this.mapeadorDesc.get("Formacion EMPRESARIO");
        double cuotaObreraGenTrab = (double) this.mapeadorDesc.get("Cuota obrera general TRABAJADOR");
        double cuotaDesemploTrab = (double) this.mapeadorDesc.get("Cuota desempleo TRABAJADOR");
        double cuotaFormacionTrab = (double) this.mapeadorDesc.get("Cuota formación TRABAJADOR");
        double meiTrab = (double) this.mapeadorDesc.get("MEI TRABAJADOR");
        double meiEmp = (double) this.mapeadorDesc.get("MEI EMPRESARIO");
        if (date.get(date.YEAR) < 2023) {
            meiTrab = 0;
            meiEmp = 0;
        }

        impDescAccTrabEmp = salarioBrutoMensualProrrateado * accidentesTrabajoEmp / 100;
        impDescContComEmp = salarioBrutoMensualProrrateado * contingenciasComunesEmp / 100;
        impDescFogEmp = salarioBrutoMensualProrrateado * fogasaEmp / 100;
        impDescDesEmp = salarioBrutoMensualProrrateado * desempleoEmp / 100;
        impDescFormEmp = salarioBrutoMensualProrrateado * formacionEmp / 100;
        impDescCuotaOGTrab = salarioBrutoMensualProrrateado * cuotaObreraGenTrab / 100;
        impDescDesTrab = salarioBrutoMensualProrrateado * cuotaDesemploTrab / 100;
        impDescFormTrab = salarioBrutoMensualProrrateado * cuotaFormacionTrab / 100;
        impDescMeiTrab = salarioBrutoMensualProrrateado * meiTrab / 100;
        impDescMeiEmp = salarioBrutoMensualProrrateado * meiEmp / 100;
        double costeTotalEmp;

        if (trabajador.getProrrata()) {
            costeTotalEmp = impDescAccTrabEmp + impDescContComEmp + impDescFogEmp + impDescDesEmp + impDescFormEmp + impDescMeiEmp + salarioBrutoMensualProrrateado;
        } else {
            costeTotalEmp = impDescAccTrabEmp + impDescContComEmp + impDescFogEmp + impDescDesEmp + impDescFormEmp + impDescMeiEmp + salarioBrutoMensual;
        }

        double salarioLiquid;
        double descIrpf;
        double impDescBaja = 0;
        if (trabajador.getProrrata()) {
            descIrpf = salarioBrutoMensualProrrateado * (irpf / 100);
            salarioLiquid = salarioBrutoMensualProrrateado - impDescCuotaOGTrab - impDescDesTrab - impDescFormTrab - impDescMeiTrab - descIrpf;
        } else {
            descIrpf = salarioBrutoMensual * (irpf / 100);
            salarioLiquid = salarioBrutoMensual - impDescCuotaOGTrab - impDescDesTrab - impDescFormTrab - impDescMeiTrab - descIrpf;
        }

        Nomina nomina = new Nomina();

        nomina.setAccidentesTrabajoEmpresario(accidentesTrabajoEmp);
        nomina.setAnio(date.get(date.YEAR));
        nomina.setBaseEmpresario(salarioBrutoMensualProrrateado);
        nomina.setBrutoAnual(salarioBruto);

        if (trabajador.getProrrata()) {
            nomina.setBrutoNomina(salarioBrutoMensualProrrateado);

        } else {
            nomina.setBrutoNomina(salarioBrutoMensual);

        }
        nomina.setCosteTotalEmpresario(costeTotalEmp);
        nomina.setDesempleoEmpresario(desempleoEmp);
        nomina.setDesempleoTrabajador(cuotaDesemploTrab);
        nomina.setFogasaempresario(fogasaEmp);
        nomina.setFormacionEmpresario(formacionEmp);
        nomina.setFormacionTrabajador(cuotaFormacionTrab);
        nomina.setImporteAccidentesTrabajoEmpresario(impDescAccTrabEmp);
        nomina.setImporteComplementoMes(complemento / 14);
        nomina.setImporteDescuentoBaja(impDescBaja);
        nomina.setImporteDesempleoEmpresario(impDescDesEmp);
        nomina.setImporteDesempleoTrabajador(impDescDesTrab);
        nomina.setImporteFogasaempresario(impDescFogEmp);
        nomina.setImporteFormacionEmpresario(impDescFormEmp);
        nomina.setImporteFormacionTrabajador(impDescFormTrab);
        nomina.setImporteIrpf(descIrpf);
        nomina.setImporteMeiEmpresario(impDescMeiEmp);
        nomina.setImporteMeiTrabajador(impDescMeiTrab);
        nomina.setImporteSalarioMes(anual / 14);
        nomina.setImporteSeguridadSocialEmpresario(impDescContComEmp);
        nomina.setImporteSeguridadSocialTrabajador(impDescCuotaOGTrab);
        nomina.setImporteTrienios(impTrienio);
        nomina.setIrpf(irpf);
        nomina.setLiquidoNomina(salarioLiquid);
        nomina.setMeiEmpresario(meiEmp);
        nomina.setMeiTrabajador(meiTrab);
        nomina.setMes(date.get(date.MONTH));
        nomina.setNumeroTrienios((int) trienio);
        nomina.setSeguridadSocialEmpresario(contingenciasComunesEmp);
        nomina.setSeguridadSocialTrabajador(cuotaObreraGenTrab);
        nomina.setTrabajador(trabajador);
        listaNominas.add(nomina);
        if (date.get(date.MONTH) == 6 || date.get(date.MONTH) == 12) {
            //EXTRA
            Nomina nominaExtra = new Nomina();

            nominaExtra.setAccidentesTrabajoEmpresario(accidentesTrabajoEmp);
            nominaExtra.setAnio(date.get(date.YEAR));
            nominaExtra.setBaseEmpresario(salarioBrutoMensualProrrateado);
            nominaExtra.setBrutoAnual(salarioBruto);
            nominaExtra.setDesempleoEmpresario(desempleoEmp);
            nominaExtra.setDesempleoTrabajador(cuotaDesemploTrab);
            nominaExtra.setDiasBaja(dias);
            nominaExtra.setFogasaempresario(fogasaEmp);
            nominaExtra.setFormacionEmpresario(formacionEmp);
            nominaExtra.setFormacionTrabajador(cuotaFormacionTrab);
            nominaExtra.setImporteAccidentesTrabajoEmpresario(0);
            nominaExtra.setImporteComplementoMes(0);
            nominaExtra.setImporteDescuentoBaja(0);
            nominaExtra.setImporteDesempleoEmpresario(0);
            nominaExtra.setImporteDesempleoTrabajador(0);
            nominaExtra.setImporteFogasaempresario(0);
            nominaExtra.setImporteFormacionEmpresario(0);
            nominaExtra.setImporteFormacionTrabajador(0);
            nominaExtra.setImporteIrpf(0);
            nominaExtra.setImporteMeiEmpresario(0);
            nominaExtra.setImporteMeiTrabajador(0);
            nominaExtra.setImporteSalarioMes(0);
            nominaExtra.setImporteSeguridadSocialEmpresario(0);
            nominaExtra.setImporteSeguridadSocialTrabajador(0);
            nominaExtra.setImporteTrienios(0);
            nominaExtra.setIrpf(irpf);
            nominaExtra.setMeiEmpresario(meiEmp);
            nominaExtra.setMeiTrabajador(meiTrab);
            nominaExtra.setMes(date.get(date.MONTH));
            nominaExtra.setNumeroTrienios((int) trienio);
            nominaExtra.setSeguridadSocialEmpresario(contingenciasComunesEmp);
            nominaExtra.setSeguridadSocialTrabajador(cuotaObreraGenTrab);
            nominaExtra.setTrabajador(trabajador);
            nominaExtra.setValorProrrateo(0.0);
            nominaExtra.setImporteTrienios(impTrienio);
            nomina.setImporteDescuentoBaja(impDescBaja);
             double extraComp = (double) this.mapeadorComp.get(trabajador.getCategorias().getNombreCategoria());
                //salario bruto extra
                double salarioBrutoExtra = salarioBrutoMensual;
                 extraComp = (extraComp / 14) * 100.0 / 100.0;
                if (antiguedad < 6) {
                    extraComp = (extraComp * antiguedad) / 6;
                } 
                if (antiguedad < 6) {
                    salarioBrutoExtra = (salarioBrutoExtra * antiguedad) / 6;
                } 
                double extraIrpf = salarioExtra * irpf / 100.0;
                double liquidoSalrioExtra = salarioExtra - extraIrpf;
                
            nominaExtra.setLiquidoNomina(liquidoSalrioExtra);
            nominaExtra.setCosteTotalEmpresario(salarioExtra);
            nominaExtra.setBrutoNomina(salarioBrutoMensual);
            nominaExtra.setImporteSalarioMes(salarioBrutoExtra);
            nominaExtra.setImporteComplementoMes(extraComp);
            nominaExtra.setImporteIrpf(extraIrpf);
            nominaExtra.setExtra(true);
            
            listaNominas.add(nominaExtra);
        }
    }

    private int returnAntiguedad(Date fechaAlta, Calendar date) {
        Calendar fechaAltaCal = Calendar.getInstance();
        fechaAltaCal.setTime(fechaAlta);

        int añoAlta = fechaAltaCal.get(fechaAltaCal.YEAR);
        int añoAct = date.get(date.YEAR);
        int mesAlta = fechaAltaCal.get(fechaAltaCal.MONTH);
        int mesAct = date.get(date.MONTH);

        int total = (añoAct - añoAlta) * 12 + (mesAct - mesAlta);

        return total;
    }

    private double calculateTrienio(int antiguedad, Trabajador trabajador, Calendar date) {
        int mesAct = date.get(date.MONTH);
        trienio = antiguedad + (12 - mesAct);
        impTrienio = 0;
        trienio = 0.0 + (int) trienio / 36;
        if (trienio > 0.0) {
            int t = (int) this.trienio;
            impTrienio = mapeadorImp.get(t);
        }
        double impTrienioAnual = impTrienio * 14;

        return impTrienioAnual;
    }

    private double calculateExtra(Trabajador trabajador, int antiguedad, double brutoMensual) {
        double extra;
        if (antiguedad <= 5 && !trabajador.getProrrata()) {
            extra = brutoMensual * antiguedad / 6;
        } else {
            extra = brutoMensual;
        }
        return extra + impTrienio;
    }

    private double calculateBrutoMensualPro(Trabajador trabajador, double brutoMensual, double extra, int antiguedad) {
        double brutoMensualPro;
        if (antiguedad <= 6 && !trabajador.getProrrata()) {
            brutoMensualPro = brutoMensual + (extra / antiguedad + this.impTrienio);
        } else {
            brutoMensualPro = brutoMensual + +this.impTrienio + extra / 6;
        }
        return brutoMensualPro;
    }

    private double calculateIRPF(Calendar date, double salBruto) {
        double salarioEst = 12000.0;
        while (salarioEst < salBruto) {
            salarioEst += 1000.0;
        }

        return this.mapeadorBruto.get(salarioEst);
    }

    public double impBaja(Trabajador trabajador, Calendar date, double salarioBrutoMensPro) {
        //Se ha acabado la baja o no.
        Calendar fechaBaja = Calendar.getInstance();
        fechaBaja.setTime(trabajador.getBajaLaboral());

        Calendar fechaAlta = Calendar.getInstance();
        fechaAlta.setTime(trabajador.getAltaLaboral());
        dias=0;
        if (trabajador.getBajaLaboral() != null) {
            dias = 32;
            if (date.get(date.MONTH) == 2) {
                dias = 29;
            } else if (date.get(date.MONTH) == 4 || date.get(date.MONTH) == 6 || date.get(date.MONTH) == 9 || date.get(date.MONTH) == 11) {
                dias = 31;
            }
            int diasAux = dias;
            if (date.get(date.MONTH) == fechaBaja.get(fechaBaja.MONTH)) {
                dias -= date.get(date.DAY_OF_MONTH);
            }

            if (trabajador.getAltaLaboral() != null) {

                if (date.get(date.MONTH) == fechaAlta.get(fechaAlta.MONTH)) {
                    dias -= date.get(date.DAY_OF_MONTH);
                }
            }
            int diasDesc100 = 0, diasDesc25 = 0, diasDesc40 = 0;

            if (date.get(date.MONTH) == fechaBaja.get(fechaBaja.MONTH)) {
                diasDesc100 = 3;
                if (dias < 3) {
                    diasDesc100 = dias;
                } else {
                    dias -= 3;
                    diasDesc25 = 17;
                    if (dias < 17) {
                        diasDesc25 = dias;
                    } else {
                        dias -= 17;
                        diasDesc40 = diasAux - 20;
                        if (dias < (diasAux - 20)) {
                            diasDesc40 = dias;
                        }
                    }
                }
                double salarioDia = salarioBrutoMensPro / 30;

                double impBaja = salarioDia * diasDesc100 + salarioDia * diasDesc40 * 0.4 + salarioDia * diasDesc25 * 0.25;
            } else if (date.get(date.MONTH - 1) == fechaBaja.get(fechaBaja.MONTH)) {
                int diasMesAnt = 32;
                if (date.get(date.MONTH - 1) == 2) {
                    diasMesAnt = 29;
                } else if (date.get(date.MONTH - 1) == 4 || date.get(date.MONTH - 1) == 6 || date.get(date.MONTH - 1) == 9 || date.get(date.MONTH - 1) == 11) {
                    diasMesAnt = 31;
                }
                diasMesAnt -= fechaBaja.get(fechaBaja.DAY_OF_MONTH);
                int Desc100MesAnt = 0, Desc25MesAnt = 0, Desc40MesAnt;

                if (diasMesAnt < 3) {
                    Desc100MesAnt = dias - diasMesAnt;
                } else {
                    diasMesAnt -= 3;
                    Desc25MesAnt = 17;
                    if (diasMesAnt < 17) {
                        diasDesc25 = diasMesAnt;
                    } else {
                        diasMesAnt -= 17;
                        diasDesc40 = diasAux - 20;
                        if (diasMesAnt < (diasAux - 20)) {
                            diasDesc40 = diasMesAnt;
                        }
                    }
                }
            } else {
                int t = 0;
            }
        }
        return 0;

    }

    public int diasMes(Calendar date) {
        int dias = 31;
        if (date.get(date.MONTH) == 4 || date.get(date.MONTH) == 6 || date.get(date.MONTH) == 9 || date.get(date.MONTH) == 11) {
            dias = 30;
        } else if (date.get(date.MONTH) == 2) {
            dias = 28;
        }

        return dias;
    }

    private String checkCCC(String ccc) {
        String dcont1 = "00" + ccc.substring(0, 8);
        String dcont2 = ccc.substring(10);
        String digit1 = calcDigi(dcont1);
        String digit2 = calcDigi(dcont2);

        String actuald1 = "" + ccc.charAt(8);
        String actuald2 = "" + ccc.charAt(9);

        return dcont1.substring(2) + digit1 + digit2 + dcont2;
    }

    private String calcDigi(String num) {
        int sumatorio = 0;
        for (int i = 0; i < num.length(); i++) {
            int trans = Integer.parseInt(num.charAt(i) + "");
            sumatorio += (trans * Math.pow(2, i)) % 11;
        }
        sumatorio = (11 - (sumatorio % 11)) % 11;
        if (sumatorio == 0) {
            return "" + sumatorio;
        }
        if (sumatorio == 10) {
            sumatorio = 1;
        }
        return sumatorio + "";
    }

    private String generateIban(Trabajador trabajador, String paisAcc) {
        int digit1 = PesoIban.valueOf(paisAcc.charAt(0) + "").getPeso();
        int digit2 = PesoIban.valueOf(paisAcc.charAt(1) + "").getPeso();

        String ccc = trabajador.getCodigoCuenta();
        ccc += digit1 + "" + digit2 + "00";

        BigInteger big = new BigInteger(ccc);
        BigInteger[] div = big.divideAndRemainder(new BigInteger("97"));

        int result = div[1].intValue();
        int digitsResult = 98 - result;
        String digitRes = "" + digitsResult;

        if (digitsResult < 10) {
            digitRes = "0" + digitsResult;
        }

        return paisAcc + digitRes + ccc.substring(0, ccc.length() - 6);
    }

    private String generateEmail(Trabajador trabajador) {
        String email = "" + trabajador.getNombre().charAt(0) + trabajador.getApellido1().charAt(0);
        int count = 0;

        if (trabajador.getApellido2() != null) {
            email += trabajador.getApellido2().charAt(0);
        }

        for (int i = 0; i < listaTrabajadores.size(); i++) {
            if (listaTrabajadores.get(i).getEmail().contains(email) && listaTrabajadores.get(i).getEmail().contains(trabajador.getEmpresas().getNombre())) {
                count++;
            }
        }

        if (count < 10) {
            email += "0";
        }

        email += count + "@" + trabajador.getEmpresas().getNombre() + ".com";
        return email;
    }

    private void generateXMLNominas(String nameFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementacion = builder.getDOMImplementation();
        Document document = implementacion.createDocument(null, "Nominas", null);
        document.setXmlVersion("1.0");

        Element root = document.getDocumentElement();
        root.setAttribute("fechaNomina", listaNominas.get(0).getMes() + "/" + listaNominas.get(0).getAnio());

        for (int i = 0; i < listaNominas.size(); i++) {
            Element nomina = document.createElement("Nomina");
            nomina.setAttribute("idNomina", "" + listaNominas.get(i).getIdNomina());
            Element extra = document.createElement("Extra");
            Text textExtra;
            if (listaNominas.get(i).getExtra()) {
                textExtra = document.createTextNode("S");
            } else {
                textExtra = document.createTextNode("N");
            }

            nomina.appendChild(extra);
            extra.appendChild(textExtra);

            Element fila = document.createElement("idFilaExcel");
            Text textFila = document.createTextNode("" + listaNominas.get(i).getTrabajador().getIdTrabajador());
            nomina.appendChild(fila);
            fila.appendChild(textFila);

            Element name = document.createElement("Nombre");
            Text textName = document.createTextNode(listaNominas.get(i).getTrabajador().getNombre());
            nomina.appendChild(name);
            name.appendChild(textName);

            Element dninie = document.createElement("NIF");
            Text textDni = document.createTextNode(listaNominas.get(i).getTrabajador().getNifnie());
            nomina.appendChild(dninie);
            dninie.appendChild(textDni);

            Element iban = document.createElement("IBAN");
            Text textIban = document.createTextNode(listaNominas.get(i).getTrabajador().getIban());
            nomina.appendChild(iban);
            iban.appendChild(textIban);

            Element cat = document.createElement("Categoria");
            Text textCat = document.createTextNode(listaNominas.get(i).getTrabajador().getCategorias().getNombreCategoria());
            nomina.appendChild(cat);
            cat.appendChild(textCat);

            Element brut = document.createElement("BrutoAnual");
            Text textBrut = document.createTextNode("" + listaNominas.get(i).getBrutoAnual());
            nomina.appendChild(brut);
            brut.appendChild(textBrut);

            Element irpf = document.createElement("ImporteIrpf");
            Text textIrpf = document.createTextNode("" + listaNominas.get(i).getImporteIrpf());
            nomina.appendChild(irpf);
            irpf.appendChild(textIrpf);

            Element basE = document.createElement("BaseEmpresario");
            Text textBase = document.createTextNode("" + listaNominas.get(i).getBaseEmpresario());
            nomina.appendChild(basE);
            basE.appendChild(textBase);

            Element brutN = document.createElement("BrutoNomina");
            Text textBrutn = document.createTextNode("" + listaNominas.get(i).getBrutoNomina());
            nomina.appendChild(brutN);
            brutN.appendChild(textBrutn);

            Element liq = document.createElement("LiquidoNomina");
            Text textLiq = document.createTextNode("" + listaNominas.get(i).getLiquidoNomina());
            nomina.appendChild(liq);
            liq.appendChild(textLiq);

            Element cte = document.createElement("CosteTotalEmpresario");
            Text textCte = document.createTextNode("" + listaNominas.get(i).getCosteTotalEmpresario());
            nomina.appendChild(cte);
            cte.appendChild(textCte);

            root.appendChild(nomina);
        }
        Source source = new DOMSource(document);
        Result result = new StreamResult(new java.io.File("./resources/nominas.xml"));
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(source, result);
    }

    public void generatePDF(Trabajador trabajador, Nomina nomina) throws FileNotFoundException {
        //TODO MIRAR mayusucla o minuscula
        String nombreN = trabajador.getNifnie() + trabajador.getNombre() + trabajador.getApellido1();
        if (trabajador.getApellido2() != null) {
            nombreN += trabajador.getApellido2();
        }
        nombreN += mesString(date.get(date.MONTH)) + date.get(date.YEAR);
        if (nomina.getExtra()) {
            nombreN += "Extra";
        }
        PdfWriter writer = new PdfWriter("./resources/nominas/" + nombreN + ".pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc, PageSize.LETTER);

        /**
         * *****************
         * CUADRO DATOS DE LA EMPRESA *****************
         */
        Paragraph empty = new Paragraph("");
        Table tabla1 = new Table(2);
        tabla1.setWidth(500);
        Paragraph nom = new Paragraph(trabajador.getEmpresas().getNombre());
        Paragraph cif = new Paragraph("CIF: " + trabajador.getEmpresas().getCif());

        Cell cell1 = new Cell();
        cell1.setBorder(new SolidBorder(1));
        cell1.setWidth(250);
        cell1.setTextAlignment(TextAlignment.CENTER);
        cell1.add(nom);
        cell1.add(cif);
        tabla1.addCell(cell1);
        Cell cell2 = new Cell();
        cell2.setBorder(Border.NO_BORDER);
        cell2.setPadding(10);
        cell2.setTextAlignment(TextAlignment.RIGHT);
        cell2.add(new Paragraph("IBAN: " + trabajador.getIban()));
        cell2.add(new Paragraph("Bruto anual: " + nomina.getBrutoAnual()));
        cell2.add(new Paragraph("Categoría: " + trabajador.getCategorias().getNombreCategoria()));
        //Adaptar con calendar la fecha
        Calendar alta = Calendar.getInstance();
        alta.setTime(nomina.getTrabajador().getFechaAlta());

        cell2.add(new Paragraph("Fecha de alta: " + alta.get(alta.DAY_OF_MONTH) + "/" + alta.get(alta.MONTH) + "/" + alta.get(alta.YEAR)));
        tabla1.addCell(cell2);

        Table tabla2 = new Table(2);
        tabla2.setWidth(500);
        Cell cell3 = new Cell();
        Cell celDest = new Cell();
        celDest.setBorder(new SolidBorder(1));
        Paragraph desti = new Paragraph("Destinatario: ");
        desti.setTextAlignment(TextAlignment.LEFT);
        desti.setBold();
        celDest.add(desti);
        celDest.add(new Paragraph(trabajador.getNombre() + trabajador.getApellido1() + trabajador.getApellido2()).setTextAlignment(TextAlignment.RIGHT));
        celDest.add(new Paragraph("DNI: " + trabajador.getNifnie()).setTextAlignment(TextAlignment.RIGHT));
        tabla2.addCell(celDest);

        Table tabla3 = new Table(1);
        Cell cellNo = new Cell();
        String nombre = "Nómina: " + mesString(date.get(date.MONTH)) + " de " + date.get(date.YEAR);
        if (nomina.getExtra()) {
            nombre += " EXTRA";
        }

        cellNo.add(new Paragraph(nombre));
        tabla3.addCell(cellNo);

        Table tabla4 = new Table(5);
        Cell celHeaders1 = new Cell();
        celHeaders1.setBorderTop(new SolidBorder(1));
        celHeaders1.setBorderBottom(new SolidBorder(1));
        celHeaders1.add(new Paragraph("Conceptos"));
        Cell celHeaders2 = new Cell();
        celHeaders2.setBorderTop(new SolidBorder(1));
        celHeaders2.setBorderBottom(new SolidBorder(1));
        celHeaders2.add(new Paragraph("Cantidad"));
        Cell celHeaders3 = new Cell();
        celHeaders3.setBorderTop(new SolidBorder(1));
        celHeaders3.setBorderBottom(new SolidBorder(1));
        celHeaders3.add(new Paragraph("Imp.Unitario"));
        Cell celHeaders4 = new Cell();
        celHeaders4.setBorderTop(new SolidBorder(1));
        celHeaders4.setBorderBottom(new SolidBorder(1));
        celHeaders4.add(new Paragraph("Devengo"));
        Cell celHeaders5 = new Cell();
        celHeaders5.setBorderTop(new SolidBorder(1));
        celHeaders5.setBorderBottom(new SolidBorder(1));
        celHeaders5.add(new Paragraph("Deduccion"));
        tabla4.addCell(celHeaders1);
        tabla4.addCell(celHeaders2);
        tabla4.addCell(celHeaders3);
        tabla4.addCell(celHeaders4);
        tabla4.addCell(celHeaders5);

        Cell celSalarioB1 = new Cell();
        Cell celSalarioB2 = new Cell();
        Cell celSalarioB3 = new Cell();
        Cell celSalarioB4 = new Cell();
        Cell celSalarioB5 = new Cell();

        celSalarioB1.add(new Paragraph("Salario base"));
        celSalarioB2.add(new Paragraph(diasMes(date)/* - nomina.getDiasBaja()*/ + "días"));
        celSalarioB3.add(new Paragraph("" + df.format(nomina.getImporteSalarioMes() / diasMes(date))));
        celSalarioB4.add(new Paragraph("" + df.format(nomina.getImporteSalarioMes())));
        celSalarioB5.add(new Paragraph(""));

        celSalarioB1.setBorderBottom(Border.NO_BORDER);
        celSalarioB1.setBorderTop(Border.NO_BORDER);
        celSalarioB1.setBorderRight(Border.NO_BORDER);
        celSalarioB2.setBorder(Border.NO_BORDER);
        celSalarioB3.setBorder(Border.NO_BORDER);
        celSalarioB4.setBorder(Border.NO_BORDER);
        celSalarioB5.setBorderBottom(Border.NO_BORDER);
        celSalarioB5.setBorderTop(Border.NO_BORDER);
        celSalarioB5.setBorderLeft(Border.NO_BORDER);

        Cell celPro1 = new Cell();
        Cell celPro2 = new Cell();
        Cell celPro3 = new Cell();
        Cell celPro4 = new Cell();
        Cell celPro5 = new Cell();
        Paragraph Pro4 = new Paragraph();
        int p4 = 0;
        celPro1.add(new Paragraph("Prorrateo"));
        celPro2.add(new Paragraph(diasMes(date) + "días"));
        if (trabajador.getProrrata()) {
            p4 += nomina.getImporteSalarioMes();
            Pro4.add(df.format(p4));
            celPro3.add(new Paragraph("" + df.format(nomina.getImporteSalarioMes() / diasMes(date))));
            celPro4.add(Pro4);
            celPro5.add(new Paragraph(""));
        } else {
            celPro3.add(new Paragraph("0"));
            celPro4.add(new Paragraph("0"));
            celPro5.add(new Paragraph(""));
        }

        celPro1.setBorderBottom(Border.NO_BORDER);
        celPro1.setBorderTop(Border.NO_BORDER);
        celPro1.setBorderRight(Border.NO_BORDER);
        celPro2.setBorder(Border.NO_BORDER);
        celPro3.setBorder(Border.NO_BORDER);
        celPro4.setBorder(Border.NO_BORDER);
        celPro5.setBorderBottom(Border.NO_BORDER);
        celPro5.setBorderTop(Border.NO_BORDER);
        celPro5.setBorderLeft(Border.NO_BORDER);

        Cell celComp1 = new Cell();
        Cell celComp2 = new Cell();
        Cell celComp3 = new Cell();
        Cell celComp4 = new Cell();
        Cell celComp5 = new Cell();
        celComp1.add(new Paragraph("Complemento"));
        celComp2.add(new Paragraph(diasMes(date) + "días"));
        celComp3.add(new Paragraph("" + df.format(nomina.getImporteComplementoMes() / diasMes(date))));
        celComp4.add(new Paragraph("" + df.format(nomina.getImporteComplementoMes())));
        celComp5.add(new Paragraph(""));

        celComp1.setBorderBottom(Border.NO_BORDER);
        celComp1.setBorderTop(Border.NO_BORDER);
        celComp1.setBorderRight(Border.NO_BORDER);
        celComp2.setBorder(Border.NO_BORDER);
        celComp3.setBorder(Border.NO_BORDER);
        celComp4.setBorder(Border.NO_BORDER);
        celComp5.setBorderBottom(Border.NO_BORDER);
        celComp5.setBorderTop(Border.NO_BORDER);
        celComp5.setBorderLeft(Border.NO_BORDER);

        Cell celAnt1 = new Cell();
        Cell celAnt2 = new Cell();
        Cell celAnt3 = new Cell();
        Cell celAnt4 = new Cell();
        Cell celAnt5 = new Cell();
        celAnt1.add(new Paragraph("Antigüedad"));
        celAnt2.add(new Paragraph(nomina.getNumeroTrienios() + " Trienios"));
        celAnt3.add(new Paragraph("" + df.format(nomina.getImporteTrienios() / diasMes(date))));
        celAnt4.add(new Paragraph("" + df.format(nomina.getImporteTrienios())));
        celAnt5.add(new Paragraph(""));
        celAnt1.setBorderBottom(Border.NO_BORDER);
        celAnt1.setBorderTop(Border.NO_BORDER);
        celAnt1.setBorderRight(Border.NO_BORDER);
        celAnt2.setBorder(Border.NO_BORDER);
        celAnt3.setBorder(Border.NO_BORDER);
        celAnt4.setBorder(Border.NO_BORDER);
        celAnt5.setBorderBottom(Border.NO_BORDER);
        celAnt5.setBorderTop(Border.NO_BORDER);
        celAnt5.setBorderLeft(Border.NO_BORDER);

        Cell celContGen1 = new Cell();
        Cell celContGen2 = new Cell();
        Cell celContGen3 = new Cell();
        Cell celContGen4 = new Cell();
        Cell celContGen5 = new Cell();
        celContGen1.add(new Paragraph("Contingencias generales"));
        celContGen2.add(new Paragraph(df.format(nomina.getSeguridadSocialTrabajador()) + "% de " + df.format(nomina.getBaseEmpresario())));
        celContGen3.add(new Paragraph(""));
        celContGen4.add(new Paragraph(""));
        celContGen5.add(new Paragraph("" + df.format(nomina.getImporteSeguridadSocialTrabajador())));

        celContGen1.setBorderBottom(Border.NO_BORDER);
        celContGen1.setBorderTop(Border.NO_BORDER);
        celContGen1.setBorderRight(Border.NO_BORDER);
        celContGen2.setBorder(Border.NO_BORDER);
        celContGen3.setBorder(Border.NO_BORDER);
        celContGen4.setBorder(Border.NO_BORDER);
        celContGen5.setBorderBottom(Border.NO_BORDER);
        celContGen5.setBorderTop(Border.NO_BORDER);
        celContGen5.setBorderLeft(Border.NO_BORDER);

        Cell celDes1 = new Cell();
        Cell celDes2 = new Cell();
        Cell celDes3 = new Cell();
        Cell celDes4 = new Cell();
        Cell celDes5 = new Cell();
        celDes1.add(new Paragraph("Desempleo"));
        celDes2.add(new Paragraph(df.format(nomina.getDesempleoTrabajador()) + "% de " + df.format(nomina.getBaseEmpresario())));
        celDes3.add(new Paragraph(""));
        celDes4.add(new Paragraph(""));
        celDes5.add(new Paragraph("" + df.format(nomina.getImporteDesempleoTrabajador())));

        celDes1.setBorderBottom(Border.NO_BORDER);
        celDes1.setBorderTop(Border.NO_BORDER);
        celDes1.setBorderRight(Border.NO_BORDER);
        celDes2.setBorder(Border.NO_BORDER);
        celDes3.setBorder(Border.NO_BORDER);
        celDes4.setBorder(Border.NO_BORDER);
        celDes5.setBorderBottom(Border.NO_BORDER);
        celDes5.setBorderTop(Border.NO_BORDER);
        celDes5.setBorderLeft(Border.NO_BORDER);

        Cell celForm1 = new Cell();
        Cell celForm2 = new Cell();
        Cell celForm3 = new Cell();
        Cell celForm4 = new Cell();
        Cell celForm5 = new Cell();
        celForm1.add(new Paragraph("Cuota Formación"));
        celForm2.add(new Paragraph(df.format(nomina.getFormacionTrabajador()) + "% de " + df.format(nomina.getBaseEmpresario())));
        celForm3.add(new Paragraph(""));
        celForm4.add(new Paragraph(""));
        celForm5.add(new Paragraph("" + df.format(nomina.getImporteFormacionTrabajador())));

        celForm1.setBorderBottom(Border.NO_BORDER);
        celForm1.setBorderTop(Border.NO_BORDER);
        celForm1.setBorderRight(Border.NO_BORDER);
        celForm2.setBorder(Border.NO_BORDER);
        celForm3.setBorder(Border.NO_BORDER);
        celForm4.setBorder(Border.NO_BORDER);
        celForm5.setBorderBottom(Border.NO_BORDER);
        celForm5.setBorderTop(Border.NO_BORDER);
        celForm5.setBorderLeft(Border.NO_BORDER);
        Cell celMei1 = new Cell();
        Cell celMei2 = new Cell();
        Cell celMei3 = new Cell();
        Cell celMei4 = new Cell();
        Cell celMei5 = new Cell();
        if (nomina.getMes() >= 2023) {

            celMei1.add(new Paragraph("MEI Trabajador"));
            celMei2.add(new Paragraph(df.format(nomina.getMeiTrabajador()) + "% de " + df.format(nomina.getImporteMeiTrabajador())));
            celMei3.add(new Paragraph(""));
            celMei4.add(new Paragraph(""));
            celMei5.add(new Paragraph("" + df.format(nomina.getImporteFormacionTrabajador())));

            celMei1.setBorderBottom(Border.NO_BORDER);
            celMei1.setBorderTop(Border.NO_BORDER);
            celMei1.setBorderRight(Border.NO_BORDER);
            celMei2.setBorder(Border.NO_BORDER);
            celMei3.setBorder(Border.NO_BORDER);
            celMei4.setBorder(Border.NO_BORDER);
            celMei5.setBorderBottom(Border.NO_BORDER);
            celMei5.setBorderTop(Border.NO_BORDER);
            celMei5.setBorderLeft(Border.NO_BORDER);

        }
        Cell celIRPF1 = new Cell();
        Cell celIRPF2 = new Cell();
        Cell celIRPF3 = new Cell();
        Cell celIRPF4 = new Cell();
        Cell celIRPF5 = new Cell();
        celIRPF1.add(new Paragraph("IRPF"));
        celIRPF2.add(new Paragraph(df.format(nomina.getIrpf()) + "% de " + df.format(nomina.getBrutoNomina())));
        celIRPF3.add(new Paragraph(""));
        celIRPF4.add(new Paragraph(""));
        celIRPF5.add(new Paragraph("" + df.format(nomina.getImporteIrpf())));
        celIRPF1.setBorderBottom(new SolidBorder(1));
        celIRPF2.setBorderBottom(new SolidBorder(1));
        celIRPF3.setBorderBottom(new SolidBorder(1));
        celIRPF4.setBorderBottom(new SolidBorder(1));
        celIRPF5.setBorderBottom(new SolidBorder(1));
        celIRPF1.setBorderTop(Border.NO_BORDER);
        celIRPF2.setBorderTop(Border.NO_BORDER);
        celIRPF3.setBorderTop(Border.NO_BORDER);
        celIRPF4.setBorderTop(Border.NO_BORDER);
        celIRPF5.setBorderTop(Border.NO_BORDER);
        celIRPF2.setBorderLeft(Border.NO_BORDER);
        celIRPF3.setBorderLeft(Border.NO_BORDER);
        celIRPF4.setBorderLeft(Border.NO_BORDER);
        celIRPF5.setBorderLeft(Border.NO_BORDER);
        celIRPF1.setBorderRight(Border.NO_BORDER);
        celIRPF2.setBorderRight(Border.NO_BORDER);
        celIRPF3.setBorderRight(Border.NO_BORDER);
        celIRPF4.setBorderRight(Border.NO_BORDER);

        Cell celTotalDed1 = new Cell();
        Cell celTotalDed2 = new Cell();

        Cell celTotalDed3 = new Cell();
        Cell celTotalDed4 = new Cell();
        Cell celTotalDed5 = new Cell();

        double deduc = nomina.getImporteIrpf() + nomina.getImporteFormacionTrabajador() + nomina.getImporteDesempleoTrabajador() + nomina.getImporteSeguridadSocialTrabajador() + nomina.getMeiTrabajador();
        celTotalDed1.add(new Paragraph("Total Deducciones "));
        celTotalDed2.add(new Paragraph(""));
        celTotalDed3.add(new Paragraph(""));
        celTotalDed4.add(new Paragraph(""));
        celTotalDed5.add(new Paragraph("" + df.format(deduc)));

        celTotalDed1.setBorderBottom(Border.NO_BORDER);
        celTotalDed1.setBorderTop(Border.NO_BORDER);
        celTotalDed1.setBorderRight(Border.NO_BORDER);
        celTotalDed2.setBorder(Border.NO_BORDER);
        celTotalDed3.setBorder(Border.NO_BORDER);
        celTotalDed4.setBorder(Border.NO_BORDER);
        celTotalDed5.setBorderBottom(Border.NO_BORDER);
        celTotalDed5.setBorderTop(Border.NO_BORDER);
        celTotalDed5.setBorderLeft(Border.NO_BORDER);

        Cell celTotalDev1 = new Cell();
        Cell celTotalDev2 = new Cell();
        Cell celTotalDev3 = new Cell();
        Cell celTotalDev4 = new Cell();
        Cell celTotalDev5 = new Cell();
        double devengos = (nomina.getImporteTrienios() + nomina.getImporteComplementoMes() + nomina.getValorProrrateo() + nomina.getImporteSalarioMes());
        celTotalDev1.add(new Paragraph("Total Devengos "));
        celTotalDev2.add(new Paragraph(""));
        celTotalDev3.add(new Paragraph(""));
        celTotalDev4.add(new Paragraph("" + df.format(nomina.getBrutoNomina())));
        celTotalDev5.add(new Paragraph(""));
        celTotalDev1.setBorderBottom(new SolidBorder(1));
        celTotalDev2.setBorderBottom(new SolidBorder(1));
        celTotalDev3.setBorderBottom(new SolidBorder(1));
        celTotalDev4.setBorderBottom(new SolidBorder(1));
        celTotalDev5.setBorderBottom(new SolidBorder(1));

        celTotalDev1.setBorderBottom(Border.NO_BORDER);
        celTotalDev1.setBorderTop(Border.NO_BORDER);
        celTotalDev1.setBorderRight(Border.NO_BORDER);
        celTotalDev2.setBorder(Border.NO_BORDER);
        celTotalDev3.setBorder(Border.NO_BORDER);
        celTotalDev4.setBorder(Border.NO_BORDER);
        celTotalDev5.setBorderBottom(Border.NO_BORDER);
        celTotalDev5.setBorderTop(Border.NO_BORDER);
        celTotalDev5.setBorderLeft(Border.NO_BORDER);

        Cell celLiq1 = new Cell();
        Cell celLiq2 = new Cell();
        celLiq2.setBorder(Border.NO_BORDER);
        Cell celLiq3 = new Cell();
        Cell celLiq4 = new Cell();
        Cell celLiq5 = new Cell();
        double liquido = devengos - deduc;
        celLiq1.add(new Paragraph(""));
        celLiq2.add(new Paragraph(""));
        celLiq3.add(new Paragraph("Liquido a percibir "));
        celLiq4.add(new Paragraph(""));
        celLiq5.add(new Paragraph("" + df.format(liquido)));

        celLiq1.setBorderTop(Border.NO_BORDER);
        celLiq2.setBorderTop(Border.NO_BORDER);
        celLiq3.setBorderTop(Border.NO_BORDER);
        celLiq4.setBorderTop(Border.NO_BORDER);
        celLiq5.setBorderTop(Border.NO_BORDER);
        celLiq2.setBorderLeft(Border.NO_BORDER);
        celLiq3.setBorderLeft(Border.NO_BORDER);
        celLiq4.setBorderLeft(Border.NO_BORDER);
        celLiq5.setBorderLeft(Border.NO_BORDER);
        celLiq1.setBorderRight(Border.NO_BORDER);
        celLiq2.setBorderRight(Border.NO_BORDER);
        celLiq3.setBorderRight(Border.NO_BORDER);
        celLiq4.setBorderRight(Border.NO_BORDER);

        tabla4.setWidth(500);

        tabla4.addCell(celSalarioB1);
        tabla4.addCell(celSalarioB2);
        tabla4.addCell(celSalarioB3);
        tabla4.addCell(celSalarioB4);
        tabla4.addCell(celSalarioB5);
        tabla4.addCell(celPro1);
        tabla4.addCell(celPro2);
        tabla4.addCell(celPro3);
        tabla4.addCell(celPro4);
        tabla4.addCell(celPro5);
        tabla4.addCell(celComp1);
        tabla4.addCell(celComp2);
        tabla4.addCell(celComp3);
        tabla4.addCell(celComp4);
        tabla4.addCell(celComp5);
        tabla4.addCell(celAnt1);
        tabla4.addCell(celAnt2);
        tabla4.addCell(celAnt3);
        tabla4.addCell(celAnt4);
        tabla4.addCell(celAnt5);
        tabla4.addCell(celContGen1);
        tabla4.addCell(celContGen2);
        tabla4.addCell(celContGen3);
        tabla4.addCell(celContGen4);
        tabla4.addCell(celContGen5);
        tabla4.addCell(celDes1);
        tabla4.addCell(celDes2);
        tabla4.addCell(celDes3);
        tabla4.addCell(celDes4);
        tabla4.addCell(celDes5);
        tabla4.addCell(celForm1);
        tabla4.addCell(celForm2);
        tabla4.addCell(celForm3);
        tabla4.addCell(celForm4);
        tabla4.addCell(celForm5);
        if (nomina.getMes() >= 2023) {
            tabla4.addCell(celMei1);
            tabla4.addCell(celMei2);
            tabla4.addCell(celMei3);
            tabla4.addCell(celMei4);
            tabla4.addCell(celMei4);
        }
        tabla4.addCell(celIRPF1);
        tabla4.addCell(celIRPF2);
        tabla4.addCell(celIRPF3);
        tabla4.addCell(celIRPF4);
        tabla4.addCell(celIRPF5);
        tabla4.addCell(celTotalDed1);
        tabla4.addCell(celTotalDed2);
        tabla4.addCell(celTotalDed3);
        tabla4.addCell(celTotalDed4);
        tabla4.addCell(celTotalDed5);
        tabla4.addCell(celTotalDev1);
        tabla4.addCell(celTotalDev2);
        tabla4.addCell(celTotalDev3);
        tabla4.addCell(celTotalDev4);
        tabla4.addCell(celTotalDev5);
        tabla4.addCell(celLiq1);
        tabla4.addCell(celLiq2);
        tabla4.addCell(celLiq3);
        tabla4.addCell(celLiq4);
        tabla4.addCell(celLiq5);

        Table tabla5 = new Table(2);

        Cell celContE1 = new Cell();
        Cell celContE5 = new Cell();
        celContE5.setTextAlignment(TextAlignment.RIGHT);
        celContE1.add(new Paragraph("Contingencias comunes empresario " + df.format(nomina.getSeguridadSocialEmpresario())));
        celContE1.setBorderRight(Border.NO_BORDER);
        celContE5.setBorderLeft(Border.NO_BORDER);

        celContE5.add(new Paragraph("" + df.format(nomina.getImporteSeguridadSocialEmpresario())));

        Cell celDesE1 = new Cell();
        Cell celDesE5 = new Cell();
        celDesE1.setBorderRight(Border.NO_BORDER);
        celDesE5.setBorderLeft(Border.NO_BORDER);
        celDesE5.setTextAlignment(TextAlignment.RIGHT);
        celDesE1.add(new Paragraph("Desempleo " + df.format(nomina.getDesempleoEmpresario())));
        celDesE5.add(new Paragraph("" + df.format(nomina.getImporteDesempleoEmpresario())));

        Cell celFormE1 = new Cell();
        Cell celFormE5 = new Cell();
        celFormE1.setBorderRight(Border.NO_BORDER);
        celFormE5.setBorderLeft(Border.NO_BORDER);
        celFormE5.setTextAlignment(TextAlignment.RIGHT);
        celFormE1.add(new Paragraph("Formación " + df.format(nomina.getFormacionEmpresario())));

        celFormE5.add(new Paragraph("" + df.format(nomina.getImporteFormacionEmpresario())));

        Cell celAccE1 = new Cell();
        Cell celAccE5 = new Cell();
        celAccE1.setBorderRight(Border.NO_BORDER);
        celAccE5.setBorderLeft(Border.NO_BORDER);
        celAccE5.setTextAlignment(TextAlignment.RIGHT);
        celAccE1.add(new Paragraph("Accidentes de trabajo " + df.format(nomina.getAccidentesTrabajoEmpresario())));
        celAccE5.add(new Paragraph("" + df.format(nomina.getImporteAccidentesTrabajoEmpresario())));

        Cell celFogE1 = new Cell();
        Cell celFogE5 = new Cell();

        celFogE5.setTextAlignment(TextAlignment.RIGHT);

        celFogE1.add(new Paragraph("FOGASA " + df.format(nomina.getFogasaempresario())));
        celFogE5.add(new Paragraph("" + df.format(nomina.getImporteFogasaempresario())));
        celFogE1.setBorderBottom(new SolidBorder(1));
        celFogE5.setBorderBottom(new SolidBorder(1));
        celFogE1.setBorderRight(Border.NO_BORDER);
        celFogE5.setBorderLeft(Border.NO_BORDER);
        tabla5.setWidth(500);
        tabla5.addCell(celContE1);
        tabla5.addCell(celContE5);
        tabla5.addCell(celDesE1);
        tabla5.addCell(celDesE5);
        tabla5.addCell(celFormE1);
        tabla5.addCell(celFormE5);
        tabla5.addCell(celAccE1);
        tabla5.addCell(celAccE5);
        tabla5.addCell(celFogE1);
        tabla5.addCell(celFogE5);
        if (nomina.getMes() >= 2023) {
            Cell celMeiE1 = new Cell();
            Cell celMeiE5 = new Cell();
            celMeiE1.setBorderRight(Border.NO_BORDER);
            celMeiE5.setBorderLeft(Border.NO_BORDER);
            celMeiE5.setTextAlignment(TextAlignment.RIGHT);
            celMeiE1.add(new Paragraph("MEI Empresario " + df.format(nomina.getMeiEmpresario())));
            celMeiE5.add(new Paragraph("" + df.format(nomina.getImporteMeiEmpresario())));
            tabla5.addCell(celMeiE1);
            tabla5.addCell(celMeiE5);
        }

        Table tabla6 = new Table(2);
        tabla6.setWidth(500);
        Cell totEmp1 = new Cell();

        Cell totEmp5 = new Cell();
        double totEmp = nomina.getImporteFogasaempresario() + nomina.getImporteAccidentesTrabajoEmpresario() + nomina.getImporteFormacionEmpresario() + nomina.getImporteDesempleoEmpresario() + nomina.getImporteSeguridadSocialEmpresario();
        totEmp1.add(new Paragraph("Total Empresario "));
        totEmp1.setBorderRight(Border.NO_BORDER);
        totEmp5.setBorderLeft(Border.NO_BORDER);
        totEmp5.add(new Paragraph("" + df.format(totEmp)));
        totEmp5.setTextAlignment(TextAlignment.RIGHT);

        tabla6.addCell(totEmp1);
        tabla6.addCell(totEmp5);

        Table tabla7 = new Table(2);
        tabla7.setBold();
        tabla7.setWidth(500);

        Cell totalEmp = new Cell();
        Paragraph text = new Paragraph("COSTE TOTAL TRABAJADOR:");

        totalEmp.add(text);
        Cell totalEmpV = new Cell();
        Paragraph textV = new Paragraph("" + df.format(totEmp + nomina.getBaseEmpresario()));
        totalEmp.setBorderRight(Border.NO_BORDER);
        totalEmpV.setBorderLeft(Border.NO_BORDER);
        totalEmpV.add(textV);
        totalEmpV.setTextAlignment(TextAlignment.RIGHT);
        tabla7.addCell(totalEmp);
        tabla7.addCell(totalEmpV);

        Table tabla8 = new Table(2);
        tabla8.setBold();
        tabla8.setWidth(500);

        Cell costeEmp = new Cell();
        Paragraph coste = new Paragraph("Calculo empresario: BASE ");

        costeEmp.add(coste);
        Cell costeEmpV = new Cell();
        Paragraph costeV = new Paragraph("" + df.format(nomina.getBaseEmpresario()));

        costeEmpV.add(costeV);
        costeEmpV.setTextAlignment(TextAlignment.RIGHT);
        costeEmp.setBorderRight(Border.NO_BORDER);
        costeEmpV.setBorderLeft(Border.NO_BORDER);
        tabla8.addCell(coste);
        tabla8.addCell(costeV);
        tabla3.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tabla3.setBold();
        doc.add(tabla1);
        doc.add(tabla2);
        doc.add(new Paragraph("\n"));
        doc.add(tabla3);
        doc.add(tabla4);
        doc.add(tabla8);
        doc.add(new Paragraph("\n"));
        doc.add(tabla5);
        doc.add(tabla6);
        doc.add(new Paragraph("\n"));
        doc.add(tabla7);

        doc.close();
    }

    private String mesString(int mes) {
        switch (mes) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembte";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            default:
                return "Diciembre";
        }
    }

    private void almacenarDatos() {
        //Primero actualizamos las empresas
        for (Trabajador trabajador : listaTrabajadores) {
            Empresas emp = trabajador.getEmpresas();

            for (Empresas empresa : listaEmpresas) {
                if (empresa.getCif().equals(emp.getCif())) {
                    emp = null;
                    break;
                }
            }
            if (emp != null) {
                listaEmpresas.add(emp);
            }
        }
        EmpresasDAO daoEmp = new EmpresasDAO();

        for (Trabajador trabajador : listaTrabajadores) {
            Categorias cat = trabajador.getCategorias();
            cat.setComplementoCategoria(this.mapeadorComp.get(cat.getNombreCategoria()));
            cat.setSalarioBaseCategoria(mapeadorSal.get(cat.getNombreCategoria()));
            for (Categorias categoria : listaCategorias) {
                if (categoria.getNombreCategoria().equals(cat.getNombreCategoria())) {
                    cat = null;
                    break;
                }
            }
            if (cat != null) {
                listaCategorias.add(cat);
            }
        }
        for (Empresas empresa : listaEmpresas) {
            daoEmp.addEmpresa(empresa);
        }
        listaEmpresas = daoEmp.asignarIDEmpresa(listaEmpresas);
        for (Empresas emp : listaEmpresas) {
            for (Trabajador trab : listaTrabajadores) {
                if (trab.getEmpresas().getCif().equals(emp.getCif())) {
                    trab.setEmpresas(emp);
                }
            }
        }
        CategoriasDAO daoCat = new CategoriasDAO();
        for (Categorias categoria : listaCategorias) {
            daoCat.addCategoria(categoria);
        }
        listaCategorias = daoCat.asignarIDCategorias(listaCategorias);
        for (Categorias cat : listaCategorias) {
            for (Trabajador trab : listaTrabajadores) {
                if (trab.getCategorias().getNombreCategoria().equals(cat.getNombreCategoria())) {
                    trab.setCategorias(cat);
                }
            }
        }

        TrabajadorDAO daoTrabajadores = new TrabajadorDAO();
        for (Trabajador trabajador : listaTrabajadores) {
            if (!listaErroresDniNie.contains(trabajador)) {
                daoTrabajadores.addTrabajador(trabajador);
            }
        }

        listaTrabajadores = daoTrabajadores.aginarIDTrabajadores(listaTrabajadores);

        for (Trabajador trab : listaTrabajadores) {
            for (Nomina nomina : listaNominas) {
                if (nomina.getTrabajador().getNifnie().equals(trab.getNifnie()) && nomina.getTrabajador().getNombre().equals(trab.getApellido1()) && nomina.getTrabajador().getNombre().equals(trab.getApellido1())) {
                    nomina.setTrabajador(trab);
                }
            }
        }

        NominaDAO daoNomina = new NominaDAO();
        for (Nomina nomina : listaNominas) {
            daoNomina.addNomina(nomina);
        }

        listaNominas = daoNomina.asignarIDNominas(listaNominas);

    }

}
