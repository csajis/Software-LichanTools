/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lichantools.servicios;


import java.io.File;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lichantools.gestores.GestorSesion;
import lichantools.modelos.HerramientaPañol;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class ExcelWriter {

    private static final String HOME = System.getProperty("user.home");
    private static final String NOMBRE_ARCHIVO = "/nomina-herramientas";

    public static File crearNominaHerramientas() {
                
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        XSSFSheet sheet = workbook.createSheet("Nómina de herramientas");
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        
        Map<String, CellStyle> estilos = crearEstilos(workbook);
        
        String cabecera = "Nómina de herramientas: " + GestorSesion.get().obtenerPañol().getNombre();
        String fecha = "Fecha: " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "  Hora: " + new SimpleDateFormat("HH:mm").format(new Date());
        
        XSSFCell cell; 
        XSSFRow row;
        
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue(cabecera);        
        cell.setCellStyle(estilos.get("cabecera1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$G$1"));
        row.setHeight((short)-1);
        
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(fecha);
        cell.setCellStyle(estilos.get("cabecera2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$G$2")); 
        row.setHeight((short)-1);
        
        String[] tab_titulo = {"ID", "Herramienta", "Tipo", "Caracteristicas", "Stock crítico", "Stock total", "Stock real"};

        row = sheet.createRow(3);

        int col = 0;
        for (String titulo : tab_titulo) {    
            cell = row.createCell(col++);
            cell.setCellValue(titulo);
            cell.setCellStyle(estilos.get("titulo"));
        }
        row.setHeight((short)-1);
        
        List<HerramientaPañol> herramientas = GestorSesion.get().obtenerHerramientasSesion();
        
        int fila = 4;
        for(HerramientaPañol herrp: herramientas){
            col = 0;
            String tipo_celda = (fila%2 == 0)? "par" : "impar";
            row = sheet.createRow(fila++);
            
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getHerr().getId());
            cell.setCellStyle(estilos.get(tipo_celda));
            
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getHerr().getNombre());            
            cell.setCellStyle(estilos.get(tipo_celda));
            
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getHerr().getTipoString());            
            cell.setCellStyle(estilos.get(tipo_celda));
                        
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getHerr().getCaracteristicas());            
            cell.setCellStyle(estilos.get(tipo_celda));
            
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getHerr().getStockCritico());            
            cell.setCellStyle(estilos.get(tipo_celda));
            
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getStockTotal());            
            cell.setCellStyle(estilos.get(tipo_celda));
            
            cell = row.createCell(col++);
            cell.setCellValue(herrp.getStockReal());            
            cell.setCellStyle(estilos.get(tipo_celda));
            
            row.setHeight((short) 700);
        }
        
        for (int i = 0; i < tab_titulo.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        
        System.out.println("CREANDO EXCEL");

        String archivo = HOME + NOMBRE_ARCHIVO + new SimpleDateFormat("-dd-MM-yy-").format(new Date()) + new SimpleDateFormat("HH-mm").format(new Date()) + ".xlsx";
        
        try {
            FileOutputStream outstream = new FileOutputStream(archivo);
            workbook.write(outstream);
            workbook.close();
        } 
        catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
        }

        System.out.println(archivo);
                
        File excelFile = new File(archivo);
        return excelFile;
    }
    
    private static Map<String, CellStyle> crearEstilos(Workbook wb){
        Map<String, CellStyle> estilos = new HashMap<>();
        CellStyle estilo;
        Font fuenteTitulo = wb.createFont();
        
        fuenteTitulo.setFontHeightInPoints((short) 10);
        fuenteTitulo.setBold(true);

        //celda cabecera
        estilo = wb.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);        
        estilo.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setFont(fuenteTitulo);
        estilos.put("cabecera1", estilo);
        
        //celda cabecera
        estilo = wb.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);        
        estilo.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setFont(fuenteTitulo);
        estilos.put("cabecera2", estilo);
        
        fuenteTitulo.setFontHeightInPoints((short) 8);
        fuenteTitulo.setBold(true);
        
        //celda titulo
        estilo = wb.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);        
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setFont(fuenteTitulo);
        estilos.put("titulo", estilo);
        
        //celda impar
        estilo = wb.createCellStyle();
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBottomBorderColor(IndexedColors.BLACK.getIndex());                
        estilo.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilos.put("impar", estilo);

        //celda par
        estilo = wb.createCellStyle();
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBottomBorderColor(IndexedColors.BLACK.getIndex());                 
        estilo.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilos.put("par", estilo);      
        
        return estilos;
    }
}
