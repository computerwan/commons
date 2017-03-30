package com.edu.usst.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengcheng.wan on 2017/3/2.
 * see from http://www.open-open.com/lib/view/open1429847388213.html
 * 常用组件：
 * HSSFWorkbook                      excel的文档对象
 * HSSFSheet                         excel的表单
 * HSSFRow                           excel的行
 * HSSFCell                          excel的格子单元
 */
public class ExcelUtils {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 如果endRow，endColumn数量不确定，则传入-1
     * 行，列，sheet的起始都是从0开始起始
     */
    public static List<String> importExcel(String fileAddress, String sheetName, int startRow, int endRow, int startColumn, int endColumn) {
        File file = new File(fileAddress);
        HSSFWorkbook wb = null;
        HSSFSheet sh;
        HSSFRow ro;
        List<String> res = new ArrayList<>();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(file);
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            log.error("can't find excel source... due to {}", e);
        }
        if (!StringUtils.isEmpty(sheetName)) {
            sh = wb.getSheet(sheetName);
        } else {
            sh = wb.getSheetAt(0);
        }
        if (endRow == -1 && endColumn == -1) {
            for (int i = startRow; sh.getRow(i) != null; i++) {
                ro = sh.getRow(i);
                for (int j = startColumn; ro.getCell(j) != null; j++) {
                    res.addAll(saveToList(ro.getCell(j)));
                }
            }
        } else if (endRow != -1 && endColumn == -1) {
            for (int i = startRow; i <= endRow; i++) {
                ro = sh.getRow(i);
                for (int j = startColumn; ro.getCell(j) != null; j++) {
                    res.addAll(saveToList(ro.getCell(j)));
                }
            }
        } else if (endRow == -1 && endColumn != -1) {
            for (int i = startRow; sh.getRow(i) != null; i++) {
                ro = sh.getRow(i);
                for (int j = startColumn; j <= endColumn; j++) {
                    res.addAll(saveToList(ro.getCell(j)));
                }
            }
        } else if (endRow != -1 && endColumn != -1) {
            for (int i = startRow; i <= endRow; i++) {
                ro = sh.getRow(i);
                for (int j = startColumn; j <= endColumn; j++) {
                    res.addAll(saveToList(ro.getCell(j)));
                }
            }
        }
        return res;
    }


    public static List<String> importExcel(String fileAddress, int startRow, int startColumn) {
        return importExcel(fileAddress, null, startRow, -1, startColumn, -1);
    }


    /**
     * 导出excel表格
     * 总行数没有从0开始的说法
     */
    public static void exportExcel(String fileAddress, String sheetName, int totalRow, int totalColumn, List<String> res) {
        //将数组的数据写入到excel中
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        for (int i = 0; i < totalRow; i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < totalColumn; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(res.get((i + 1) * (j + 1) - 1));
            }
        }
        //导出数据到文档中
        try {
            FileOutputStream output = new FileOutputStream(fileAddress);
            wb.write(output);
            output.flush();
        } catch (Exception e) {
            log.error("can not write in to file");
        }
    }


    private static List<String> saveToList(HSSFCell hssfRow) {
        List<String> res = new ArrayList<>();
        if (hssfRow.toString() != null) {
            res.add(hssfRow.toString());
        }
        return res;
    }

}
