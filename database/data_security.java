package database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import config.configs;

public class data_security
{
	static final String charset = "UTF-8";
	
	static final String mysqldump_path = "/opt/lampp/bin/./mysqldump";
	
	database db = database.get_instance();
	
	public boolean create_backup(File output_file, boolean compress, boolean backup_routines, boolean backup_triggers, boolean backup_views)
	{
		ArrayList<String> tables = get_tables_list();
		
		PrintWriter pw;
		
		try
		{
			OutputStream os = new FileOutputStream(output_file.getPath());
			
			if(compress)
			{
				os = new GZIPOutputStream(os);
			}
			
			pw = new PrintWriter(new OutputStreamWriter(os, charset));
			
			write_header(pw);
			
			int total_rows = 0;
			
			for(String table_name:tables)
			{
				total_rows += backup_table(pw, table_name);
			}
			
			if(backup_routines)
			{
				backup_routines(pw);
			}
			if(backup_triggers)
			{
				backup_triggers(pw);
			}
			if(backup_views)
			{
				backup_views(pw);
			}
			
			write_footer(pw, tables.size(), total_rows);
			
			pw.flush();
			
			if(compress)
			{
				((GZIPOutputStream)os).finish();
			}
			
			return true;

		}
		catch (IOException | SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	private int backup_table(PrintWriter writer, String table_name) throws SQLException
	{
		writer.println();
        writer.println("--");
        writer.println("-- بنية الجدول: `"+table_name+"`");
        writer.println("--");
        writer.println();
        
        writer.print("DROP TABLE IF EXISTS `"+table_name+"`;");
        
        ResultSet rs = db.query("SHOW CREATE TABLE `"+table_name+"`");
        
        if (rs.next())
        {
            String str_create_table = rs.getString(2);
            writer.println(str_create_table + ";");
        }
        rs.close();
        writer.println();
        
        if( ! table_has_data(table_name))
        {
        	return 0;
        }
        
        writer.println("--");
        writer.println("-- بيانات الجدول `"+table_name+"`");
        writer.println("--");
        writer.println();
        
        PreparedStatement pstmt = db.pstmt("SELECT * FROM `"+table_name+"`", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        
        pstmt.setFetchSize(Integer.MIN_VALUE);
           
        rs = pstmt.executeQuery();
        
        ResultSetMetaData metaData = rs.getMetaData();
        int columns_count = metaData.getColumnCount();

        StringBuilder insert_query = new StringBuilder();
        
        int rows_count = 0;
        
        while (rs.next())
        {
        	if (rows_count % 1000 == 0)
        	{
        		if (rows_count > 0)
        		{
                    writer.println(insert_query.toString() + ";");
                }
                insert_query = new StringBuilder();
                insert_query.append("INSERT INTO `").append(table_name).append("` VALUES ");
            }
        	else
        	{
                insert_query.append(",");
            }
            
            insert_query.append("(");
            
            for(int i = 1; i <= columns_count; i++)
            {
                if (i > 1) insert_query.append(", ");
                Object value = rs.getObject(i);
                append_sql_value(insert_query, value, metaData.getColumnType(i));
            }
            insert_query.append(")");
            
            rows_count++;
            
            // كتابة البيانات إلى الملف عند كل 1000 سطر بيانات من بيانات الجدول
            
            if (rows_count % 1000 == 0)
            {
                writer.flush();
            }
        }
        
        if (insert_query.length() > 0)
        {
            writer.println(insert_query.toString() + ";");
        }
        
        writer.println();
        writer.println("-- عدد الأسطر: "+rows_count);
        writer.println();
        
        return rows_count;
	}
	
	private boolean table_has_data(String table_name) throws SQLException
	{
		ResultSet rs = db.query("SELECT COUNT(*) FROM `"+table_name+"`");
		
		if (rs.next())
		{
			return rs.getLong(1) > 0;
		}
		return false;
	}
	
	private void append_sql_value(StringBuilder sb, Object value, int sql_type)
	{
		if(value == null)
		{
			sb.append("NULL");
		}
		else
		{
			switch (sql_type)
			{
			case Types.BIT:
			case Types.BOOLEAN:
				sb.append(((Boolean) value) ? 1 : 0);
				break;
				
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
				sb.append(value.toString());
				break;
				
			case Types.DATE:
				java.sql.Date date = (java.sql.Date) value;
				sb.append("'").append(date.toString()).append("'");
				break;
				
			case Types.TIME:
				java.sql.Time time = (java.sql.Time) value;
				sb.append("'").append(time.toString()).append("'");
				break;
				
			case Types.TIMESTAMP:
				java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
				sb.append("'").append(timestamp.toString()).append("'");
				break;
				
			default: // بيانات نصية
				String str = value.toString();
				// التخلص من الرموز الخاصة
				str = str.replace("\\", "\\\\")
						.replace("'", "\\'")
						.replace("\"", "\\\"")
						.replace("\n", "\\n")
						.replace("\r", "\\r")
						.replace("\t", "\\t")
						.replace("\0", "\\0")
						.replace("\b", "\\b")
						.replace("\u001A", "\\Z"); // Ctrl+Z
                    sb.append("'").append(str).append("'");
                    break;
            }
        }
	}
	
	private void backup_routines(PrintWriter writer) throws SQLException
	{
		writer.println();
        writer.println("--");
        writer.println("-- الإجراءات والدوال المحفوظة");
        writer.println("--");
        writer.println();
        
        // حفظ الإجراءات
        
        ResultSet rs = db.query("SHOW PROCEDURE STATUS WHERE DB='"+db.server_info().db()+"'");
        
        while (rs.next())
        {
            String procedure_name = rs.getString("Name");
            writer.println("-- الإجراء: " + procedure_name);
            writer.println("DROP PROCEDURE IF EXISTS `"+procedure_name+"`;");
            writer.println("DELIMITER $$");
            
            ResultSet create_rs = db.query("SHOW CREATE PROCEDURE `" + procedure_name + "`");
            
            if(create_rs.next())
            {
                writer.println(create_rs.getString("Create Procedure") + "$$");
            }
            create_rs.close();
            
            writer.println("DELIMITER ;");
            writer.println();
        }
        
        // حفظ الدوال
        
        rs = db.query("SHOW FUNCTION STATUS WHERE Db = '"+db.server_info().db()+"'");
        
        while (rs.next())
        {
            String function_name = rs.getString("Name");
            writer.println("-- الدالة: " + function_name);
            writer.println("DROP FUNCTION IF EXISTS `"+function_name+"`;");
            writer.println("DELIMITER $$");
            
            ResultSet create_rs = db.query("SHOW CREATE FUNCTION `"+function_name+"`");
            
            if (create_rs.next())
            {
                writer.println(create_rs.getString("Create Function") + "$$");
            }
            
            create_rs.close();
            
            writer.println("DELIMITER ;");
            writer.println();
        }
        rs.close();
	}
	
	private void backup_triggers(PrintWriter writer) throws SQLException
	{
		writer.println();
		writer.println("--");
		writer.println("-- محفزات التحديث التلقائي");
		writer.println("--");
		writer.println();
		
		ResultSet rs = db.query("SELECT TRIGGER_NAME, EVENT_OBJECT_TABLE FROM INFORMATION_SCHEMA.TRIGGERS " + "WHERE TRIGGER_SCHEMA='"+db.server_info().db()+"'");
		
		while (rs.next())
		{
            String trigger_name = rs.getString("TRIGGER_NAME");
            String table_name = rs.getString("EVENT_OBJECT_TABLE");
            
            writer.println("-- المحفز: "+trigger_name +" (on table "+table_name+")");
            writer.println("DROP TRIGGER IF EXISTS `"+trigger_name+"`;");
            writer.println("DELIMITER $$");
            
            ResultSet create_rs = db.query("SHOW CREATE TRIGGER `" + trigger_name + "`");
            
            if (create_rs.next())
            {
                String create_trigger = create_rs.getString("SQL Original Statement");
                writer.println(create_trigger + "$$");
            }
            
            create_rs.close();
            
            writer.println("DELIMITER ;");
            writer.println();
        }
        
        rs.close();
	}
	
	private void backup_views(PrintWriter writer) throws SQLException
	{
		writer.println();
        writer.println("--");
        writer.println("-- المناظير");
        writer.println("--");
        writer.println();
        
        ResultSet rs = db.meta_data().getTables(db.server_info().db(), null, "%", new String[]{"VIEW"});
        
        while (rs.next())
        {
            String view_name = rs.getString("TABLE_NAME");
            writer.println("-- المنظور: " + view_name);
            writer.println("DROP VIEW IF EXISTS `" + view_name + "`;\n");
            
            ResultSet create_rs = db.query("SHOW CREATE VIEW `" + view_name + "`");
            
            if (create_rs.next())
            {
                writer.println(create_rs.getString("Create View") + ";");
            }
            create_rs.close();
            
            writer.println();
        }
        rs.close();
	}
	
	private void write_header(PrintWriter writer) throws SQLException
	{
		writer.println("-- تم إنشاء النسخة الاحتياطية عن طريق برنامج الفنك لتسيير المحلات التجارية.");
		writer.println("-- إصدار الخادم: "+get_server_version());
		writer.println("-- قاعدة البيانات: "+db.server_info().db());
		writer.println("-- تاريخ الإنشاء: " + new Date());
		writer.println("--");
		writer.println("");
		writer.println("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;");
        writer.println("/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;");
        writer.println("/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;");
        writer.println("/*!50503 SET NAMES utf8mb4 */;");
        writer.println("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;");
        writer.println("/*!40103 SET TIME_ZONE='+00:00' */;");
        writer.println("/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;");
        writer.println("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;");
        writer.println("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;");
        writer.println("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;");
        writer.println();
	}
	
	private void write_footer(PrintWriter writer, int table_count, int total_rows)
	{
		writer.println();
        writer.println("/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;");
        writer.println("/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;");
        writer.println("/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;");
        writer.println("/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;");
        writer.println("/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;");
        writer.println("/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;");
        writer.println("/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;");
        writer.println("/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;");
        writer.println();
        writer.println("-- تم إنشاء النسخة الاحتياطية بنجاح");
        writer.println("-- عدد الجداول: " + table_count);
        writer.println("-- عدد العناصر: " + total_rows);
        writer.println("-- تاريخ الإنشاء: " + new Date());
	}
	
	private String get_server_version() throws SQLException
	{
		return db.meta_data().getDatabaseProductVersion();
	}
	
	ArrayList<String> get_tables_list()
	{
		ArrayList<String> tables = new ArrayList<>();
		
		try
		{
			ResultSet rs_tables = db.meta_data().getTables(db.server_info().db(), null, "%", new String[] {"TABLE"});
			
			while(rs_tables.next())
			{
				tables.add(rs_tables.getString("TABLE_NAME"));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return tables;
	}
	
	public boolean mysqldump_create_backup(String db_name, File output_file)
	{
		ProcessBuilder pb = new ProcessBuilder(mysqldump_path, "-u", "root", "-p", db.server_info().pass(), db_name);
		
		if(db.server_info().pass().isEmpty())
		{
			pb = new ProcessBuilder(mysqldump_path, "-u", "root", db_name);
		}
		
		try
		{
			pb.redirectOutput(output_file);
			pb.redirectErrorStream();
			
			Process p = pb.start();
			
			thread th = new thread(p);
			
			th.start();
			
			int response = p.waitFor();
			
			if(response == 0)
			{
				System.out.println("تم إنشاء نسخة احتياطية بنجاح");
				
				return true;
			}
			else
			{
				System.err.println("حدث خطأ أثناء محاولة إنشاء نسخة احتياطية: "+response);
				
				return false;
			}
		}
		catch (IOException | InterruptedException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public boolean restore_compressed(File compressed_file, boolean clean_db)
	{
		try
		{
			FileInputStream fis = new FileInputStream(compressed_file);
			GZIPInputStream gzip_is = new GZIPInputStream(fis);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(gzip_is, charset));
			
			File tmp_file = new File(compressed_file+".tmp");
			
			PrintWriter writer = new PrintWriter(new FileWriter(tmp_file));

			String line;
			
			while((line = reader.readLine()) != null)
			{
				writer.println(line);
			}
			
			restore(tmp_file, clean_db);
			
			tmp_file.delete();
			reader.close();
			writer.close();
			
			return true;
			
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public boolean restore(File sql_file_path, boolean clean_db)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(sql_file_path));
			
			StringBuilder sql_query = new StringBuilder();
			StringBuilder multiline_conditional_comment = new StringBuilder();
			
			String line;
			
			boolean in_multiline_comment = false;
			boolean in_multiline_conditionalcomment = false;
			
			enter_restore_mode();
			
			if(clean_db)
			{
				clean_db();
			}
			
			while((line = reader.readLine()) != null)
			{
				line = line.trim();
				
				if(line.isEmpty())
				{
					continue;
				}
				
				if(line.startsWith("--"))
				{
					continue;
				}
				
				if(line.startsWith("/*!"))
				{
					if(line.endsWith("*/;"))
					{
						if(in_multiline_conditionalcomment)
						{
							multiline_conditional_comment.append(strip_conditional_comment(line));
							
							db.query_nr(multiline_conditional_comment.toString());
						}
						else
						{
							db.query_nr(strip_conditional_comment(line));
						}
						
						continue;
					}
					else if(line.endsWith("*/"))
					{
						in_multiline_conditionalcomment = true;
						
						multiline_conditional_comment.append(strip_conditional_comment(line)).append(" ");
						
						continue;
					}
				}
				
				if(line.startsWith("/*"))
				{
					in_multiline_comment = true;
				}
				if(line.endsWith("*/"))
				{
					in_multiline_comment = false;
					
					continue;
				}
				
				if(in_multiline_comment)
				{
					continue;
				}
				
				sql_query.append(line).append(" ");
				
				if(line.endsWith(";"))
				{
					String sql = (sql_query.toString()).substring(0, sql_query.length()-1);
					
					db.query_nr(sql);
					
					sql_query = new StringBuilder();
					
					continue;
				}
				
			}
			
			if(sql_query.length() > 0)
			{
				db.query_nr(sql_query.toString());
			}
			
			exit_restore_mode();
			
			reader.close();
			
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		
		return false;
	}
	
	void enter_restore_mode()
	{
		db.query_nr("SET @OLD_TIME_ZONE=@@TIME_ZONE");
		db.query_nr("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'");
		db.query_nr("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0");
		db.query_nr("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0");
		db.query_nr("SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT");
		db.query_nr("SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS");
		db.query_nr("SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION");
		db.query_nr("SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0");
		db.query_nr("SET NAMES utf8mb4");
		db.query_nr("SET TIME_ZONE='+00:00'");

	}
	
	void exit_restore_mode()
	{
		db.query_nr("SET TIME_ZONE=@OLD_TIME_ZONE");
		db.query_nr("SET SQL_MODE=@OLD_SQL_MODE");
		db.query_nr("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS");
		db.query_nr("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS");
		db.query_nr("SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS");
		db.query_nr("SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION");
		db.query_nr("SET SQL_NOTES=@OLD_SQL_NOTES");
	}
	
	String strip_conditional_comment(String comment)
	{
		comment = comment.trim();
		
		if(comment.startsWith("/*!"))
		{
			comment = comment.substring(3);
		}
		
		comment = comment.trim();
		
		if(comment.endsWith("*/;"))
		{
			comment = comment.substring(0, comment.length() - 3);
		}
		
		else if(comment.endsWith("*/"))
		{
			comment = comment.substring(0, comment.length() - 2);
		}
		
		comment = comment.trim();
		
		if(Character.isDigit(comment.charAt(0)))
		{
			int digits_count = 0;
			
			while(Character.isDigit(comment.charAt(digits_count)))
			{
				digits_count++;
			}
			
			comment = comment.substring(digits_count, comment.length());
		}
		
		return comment.trim();
	}
	
	String strip_multiline_conditional_comment(String comment)
	{
		String[] lines = comment.split("\n");
		
		StringBuilder result = new StringBuilder();
		
		for(String line:lines)
		{
			result.append(strip_conditional_comment(line)).append(" ");
		}
		return result.toString();
	}
	
	protected void clean_db()
	{
		db.query_nr("DROP DATABASE IF EXISTS "+db.server_info().db());
		
		db.create_db(db.server_info().db(), configs.DEFAULT_CHARSET, configs.DEFAULT_COLLATION);
		
		db.use_db(db.server_info().db());
	}
	
	
	class thread extends Thread
	{
		thread(Process p)
		{
			InputStreamReader isr = new InputStreamReader(p.getErrorStream()); 
			BufferedReader br = new BufferedReader(isr);
			
			try
			{
				String line;
				
				while((line = br.readLine()) != null)
				{
					System.err.println(line);
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
}
