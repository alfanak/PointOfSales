package setup;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.configs;
import database.database;

public class setup
{
	static final int BTN_USE = 0;
	static final int BTN_REPLACE = 1;
	static final int BTN_CANCEL = 2;
	
	private static boolean print_out = true;
	
	database db = database.get_instance();
	
	private File sql_file = new File(getClass().getResource("/resources/tables.sql").getFile());
	
	data _data = new data();
	
	public static boolean is_installed()
	{
		if(configs.get_boolean("INSTALLED", false))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean connect_server(String host, String port, String user, String pass)
	{
		int connection_status = db.connect(host, port, user, pass, "", false);
		
		if(connection_status == database.STATUS_USER_APPROVED)
		{
			db.server_info().set(host, port, user, pass, "");
			
			return true;
		}
		return false;
	}
	
	public boolean connect_db(String db_name)
	{
		db.server_info().set_db(db_name);
		
		int status = db.connect(false);
		
		if(status == database.STATUS_CONNECTED)
		{
			
			return true;
		}
		return false;
	}
	
	public boolean create_tables()
	{
		try
		{
			String sql_tables;
			
			sql_tables = Files.readString(sql_file.toPath());
			
			String[] stmts_tables = sql_tables.split(";");
			
			for(String stmt:stmts_tables)
			{
				if( ! stmt.isBlank())
				{
					db.query_nr(stmt.trim());
				}
			}
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
			
		
		/*
		// إضافة صورة تلقائية للمنتجات التي ليس لديها صورة
		
		File img_file = new File(getClass().getResource("/icons/noproduct64x64.png").getFile());
		
		if(img_file.exists())
		{
			BufferedImage bi = ImageIO.read(img_file);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageIO.write(bi, "JPG", baos);
			
			PreparedStatement pstmt = pstmt("INSERT INTO product_images(data) SELECT ? WHERE NOT EXISTS (SELECT * FROM product_images)");
			
			pstmt.setBytes(1, baos.toByteArray());09:23
			
			pstmt.executeUpdate();
		}
		*/
	}
	
	public void set_primary_data()
	{
		String[] str_roles = new String[]
		{
			"مدير",
			"محاسب",
			"عامل مبيعات",
			"أمين مخزن"
		};
		
		String[] str_role_permissions = new String[]
		{
			"ALL", 
			"USERS_VIEW, USERS_ADD, USERS_EDIT, USERS_DELETE, USERS_VIEW_PERMISSIONS, PRODUCTS, SALES, BUYS, INVENTORY, EXPENSES, SHOPPING_LISTS, SETTINGS_GENERAL",
			"SALES",
			"PRODUCTS, INVENTORY"
		};
		
		for(int i = 0; i < str_roles.length; i++)
		{
			PreparedStatement pstmt = db.pstmt("INSERT INTO user_roles(name, permissions) VALUES(?, ?)");
			
			try
			{
				pstmt.setString(1, str_roles[i]);
				pstmt.setString(2, str_role_permissions[i]);
				pstmt.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean create_account(String name, String password, int role_id)
	{
		PreparedStatement pstmt = db.pstmt("INSERT INTO users(name, pass, role_id) VALUES(?, md5(?), ?)");
			
		try
		{
			pstmt.setString(1, name);
			pstmt.setString(2, new String(password));
			pstmt.setInt(3, role_id);
			pstmt.executeUpdate();
			
			return true;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			
			return false;
		}
	}
	
	public boolean db_exists(String db_name)
	{
		return db.check_db(db_name);
	}
	
	public boolean valid_charset(String charset)
	{
		try
		{
			PreparedStatement pstmt = db.pstmt("SELECT * FROM information_schema.CHARACTER_SETS WHERE CHARACTER_SET_NAME = ?");
			
			pstmt.setString(1, charset);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public boolean valid_collation(String collation)
	{
		try
		{
			PreparedStatement pstmt = db.pstmt("SELECT * FROM information_schema.COLLATIONS WHERE COLLATION_NAME = ?");
			
			pstmt.setString(1, collation);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public boolean valid_charset_collation(String charset, String collation)
	{
		try
		{
			PreparedStatement pstmt = db.pstmt("SELECT * FROM information_schema.COLLATIONS WHERE CHARACTER_SET_NAME = ? AND COLLATION_NAME = ?");
			
			pstmt.setString(1, charset);
			pstmt.setString(2, collation);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean check_tables() throws SQLException
	{
		try
		{
			String sql_tables;
			
			sql_tables = Files.readString(sql_file.toPath());
			
			String[] stmts_tables = sql_tables.split(";");
			
			String prefix = "CREATE TABLE IF NOT EXISTS ";
			
			boolean missing_tables = false;
			
			print_out("فحص الجداول:");
			
			for(String stmt:stmts_tables)
			{
				if( ! stmt.isBlank())
				{
					stmt = stmt.trim();
					
					if( ! stmt.startsWith(prefix))
					{
						print_err("خطأ في قراءة ملف بنية قاعدة البيانات.");
						
						return false;
					}
					
					String table_name = stmt.substring(prefix.length(), stmt.indexOf('(', prefix.length()+1)).trim();
					
					ResultSet res_table = db.query("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='"+_data.db_server_data.db()+"' AND table_name='"+table_name+"'");
					
					if(res_table.next() && res_table.getInt(1) > 0)
					{
						print_out(table_name+": موجود");
					}
					else
					{
						missing_tables = true;
						
						print_err(table_name+": غير موجود");
					}
				}
			}
			
			if(missing_tables)
			{
				print_err("خطأ أثناء فحص الجداول، قاعدة البيانات لا تحتوي على جميع الجداول المطلوبة.");
				
				return false;
			}
			
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		return false;
		
	}
	
	void print_out(String msg)
	{
		if(print_out)
		{
			System.out.println(msg);
			System.out.flush();
		}
	}
	
	void print_err(String msg)
	{
		if(print_out)
		{
			System.err.println(msg);
			System.err.flush();
		}
	}
	
	class data
	{
		database.server_data db_server_data = new database.server_data();
		
		String user_name;
		String user_pass;
	}
}
