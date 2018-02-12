package com.geariot.platform.fishery.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.geariot.platform.fishery.model.ExcelData;


public class DataExportExcel {

	static Logger log = Logger.getLogger(DataExportExcel.class);

	// 第一步，创建一个webbook，对应一个Excel文件
	public HSSFWorkbook generateExcel() {
		return new HSSFWorkbook();
	}

	public HSSFWorkbook generateSheet(HSSFWorkbook wb, String sheetName, String[] fields, List<ExcelData> list) {

		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(sheetName);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// 设置表头字段名
		HSSFCell cell;
		int m = 0;
		for (String fieldName : fields) {
			sheet.setDefaultColumnStyle(m, style);
			cell = row.createCell(m);
			cell.setCellValue(fieldName);
			m++;
		}

		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			ExcelData data = list.get(i);

			row.createCell(0).setCellValue(i + 1);

			row.createCell(1).setCellValue(data.getDevice_sn());
			row.createCell(2).setCellValue(data.getOxygen());
			row.createCell(3).setCellValue(data.getpH_value());

			row.createCell(4).setCellValue(data.getReceiveTime());

			row.createCell(5).setCellValue(data.getWater_temperature());
		}
		return wb;
	}

	public void export(HSSFWorkbook wb, HttpServletResponse response) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		try {
			response.setHeader("content-disposition",
					"attachment;filename=" + URLEncoder.encode("设备数据表" + sdf.format(now), "utf-8") + ".xls");
			OutputStream out = response.getOutputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			byte[] xlsBytes = baos.toByteArray();
			out.write(xlsBytes);
			out.close();
		} catch (IOException e) {
			log.error("IOException:" + e.getMessage());
		}

	}
}
