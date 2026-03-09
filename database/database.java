package database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import common.encryptor;
import config.configs;

public class database
{
	private static database instance;
	
	public static final int STATUS_NOT_CONNECTED    = 0;
	public static final int STATUS_MYSQL_SERVER_ON  = 1;
	public static final int STATUS_USER_APPROVED    = 2;
	public static final int STATUS_DATABASE_FOUND   = 3;
	public static final int STATUS_CONNECTED        = 4;
	
	private static server_data server_info = new server_data();
	
	private Connection CONNECTION = null;
	private Statement STATEMENT = null;
	private ResultSet RESULT = null;
	
	public int status = STATUS_NOT_CONNECTED;
	public int error_code = 0;
	public String sql_state = "";
	
	public DatabaseMetaData meta_data()
	{
		if(CONNECTION != null)
		{
			try
			{
				return CONNECTION.getMetaData();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		
		return null;
	}
	
	public server_data server_info()
	{
		return server_info;
	}
	
	public static void set_server_info(String host, String port, String user, String pass, String db)
	{
		server_info.set(host, port, user, pass, db);
	}
	
	public static synchronized database get_instance()
	{
        if (instance == null)
        {
            instance = new database();
        }
        return instance;
    }
	
	public static void load_parameters()
	{
		String host = configs.get("SERVER_HOST", configs.DEFAULT_SERVER_HOST);
		String port = configs.get("SERVER_PORT", configs.DEFAULT_SERVER_PORT);
		String user = configs.get("SERVER_USER", configs.DEFAULT_SERVER_USER);
		String pass = configs.get_pass();
		String db_name = configs.get("DATABASE", configs.DEFAULT_DB_NAME);
		
		server_info.set(host, port, user, pass, db_name);
	}
	
	public void save_parameters()
	{
		configs.put("SERVER_HOST", server_info.host());
		configs.put("SERVER_PORT", server_info.port());
		configs.put("SERVER_USER", server_info.user());
		configs.put("SERVER_PASS", encryptor.encrypt(server_info.pass(), server_info.pass()));
		configs.put("DATABASE", server_info.db());
	}
	
	//
	
	public int connect(boolean show_error_msg)
	{
		return connect(server_info.host(), server_info.port(), server_info.user(), server_info.pass(), server_info.db(), show_error_msg);
	}
	
	public int connect(String host, String port, String user, String pass, String db_name, boolean show_error_msg)
	{
		try
		{
			//  الاتصال بالخادم
			String db_url = "jdbc:mysql://"+host+":"+port;
			
			CONNECTION = DriverManager.getConnection(db_url+"?useUnicode=yes&characterEncoding=UTF-8", user, pass);
			
			// فحص اتصال المستخدم
			if(user.isBlank())
			{
				server_info.set(host, port, user, pass, db_name);
				
				return status = STATUS_MYSQL_SERVER_ON;
			}
			
			CONNECTION = DriverManager.getConnection(db_url+"?useUnicode=yes&characterEncoding=UTF-8", user, pass);
			
			// الاتصال بقاعدة البيانات
			
			if(db_name.isBlank())
			{
				server_info.set(host, port, user, pass, db_name);
				
				STATEMENT = CONNECTION.createStatement();
				
				return status = STATUS_USER_APPROVED;
			}
			
			db_url += "/"+db_name;
				
			CONNECTION = DriverManager.getConnection(db_url+"?useUnicode=yes&characterEncoding=UTF-8", user, pass);
			
			status = STATUS_DATABASE_FOUND;
			
			server_info.set(host, port, user, pass, db_name);
			
			STATEMENT = CONNECTION.createStatement();
			
			query_nr("SET GLOBAL sql_mode=''");
			query_nr("SET SESSION sql_mode=''");
			
			use_db(db_name);
			
			return status = STATUS_CONNECTED;
		}
		catch(SQLException ex)
		{
			if(show_error_msg)
			{
				show_error_msg(ex);
			}
			
			return status = STATUS_NOT_CONNECTED;
		}
	}
	
	void check_connection()
	{
		if(status == STATUS_NOT_CONNECTED)
		{
			show_error_msg("لم يتم الاتصال بخادم قاعدة البيانات، تأكد من تشغيل الخادم، ثم تأكد من بيانات المستخدم.");
		}
		else if(status == STATUS_NOT_CONNECTED)
		{
			show_error_msg("لم يتم الاتصال بخادم قاعدة البيانات، تأكد من تشغيل الخادم، ثم تأكد من بيانات المستخدم.");
		}
	}
	
	public PreparedStatement pstmt(String str)
	{
		try
		{
			return CONNECTION.prepareStatement(str, PreparedStatement.RETURN_GENERATED_KEYS);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public PreparedStatement pstmt(String str, int resultset_type, int resultset_concurrency)
	{
		try
		{
			return CONNECTION.prepareStatement(str, resultset_type, resultset_concurrency);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public ResultSet query(String query)
	{
		if(this.STATEMENT == null)
		{
			show_error_msg("لم يتم الاتصال بمزود خدمة قاعدة البيانات");
			
			return null;
		}
		
		try
		{
			RESULT = STATEMENT.executeQuery(query);
			return RESULT;
		}
		catch (SQLException ex)
		{
			show_error_msg(ex);
			
		}
		return null;
	}
	
	public void query_nr(String query)
	{
		try
		{
			STATEMENT.execute(query);
		}
		catch (SQLException ex)
		{
			show_error_msg(ex);
			
		}
	}
	
	//
	
	public String[] get_list_from_column(String table, String column, String conditions)
	{
		ResultSet result = query("SELECT "+column+" From "+table+" "+conditions);
		
		ArrayList<Object> list = new ArrayList<>();
		
		try 
		{
			while(result.next())
			{
				list.add(result.getString(column));
			}
			return list.toArray(new String[list.size()]);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	//
	
	public int count(String tablename)
	{
		return count(tablename, "");
	}
	
	//
	
	public int count(String tablename, String conditions)
	{
		try
		{
			if( ! conditions.isBlank())
			{
				conditions = " WHERE "+conditions;
			}
			
			RESULT = query("SELECT COUNT(*) FROM "+tablename+conditions);
		
			if(RESULT.next())
			{
				return RESULT.getInt(1);
			}
		}
		catch (SQLException ex)
		{
			show_error_msg(ex);
		}
		return 0;
	}
	
	//
	
	public boolean exist(String tablename, String conditions)
	{
		int result_count = count(tablename, conditions);
		
		if(result_count > 0)
		{
			return true;
		}
		return false;
	}
	
	//
	
	public void use_db(String db_name)
	{
		query_nr("USE "+db_name);
	}
	
	//
	
	public boolean create_db(String db_name, String charset, String collation)
	{
		try
		{
			STATEMENT.execute("CREATE DATABASE "+db_name+" CHARACTER SET "+charset+" COLLATE "+collation);
			
			return true;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			
			return false;
		}
	}
	
	public boolean check_db(String db_name)
	{
		PreparedStatement pstmt = pstmt("SHOW DATABASES LIKE ?");
		
		try
		{
			pstmt.setString(1, db_name);
			
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
	
	public boolean check_table(String table_name)
	{
		ResultSet rs = query("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '"+server_info.db()+"' AND table_name='"+table_name+"'");
		
		try
		{
			if(rs.next() && rs.getInt(1) > 0)
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
	
	public int tables_count()
	{
		try
		{
			ResultSet result = query("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '"+server_info.db()+"'");
			
			if(result.next())
			{
				return result.getInt(1);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			
		}
		return 0;
	}
	
	//
	
	public void show_error_msg(SQLException ex)
	{
		error_code = ex.getErrorCode();
		sql_state = ex.getSQLState();
		
		System.err.println(ex.getMessage());
		
		JOptionPane.showMessageDialog(null, "خطأ أثناء محاولة الاتصال بقاعدة البيانات.", "خطأ", JOptionPane.ERROR_MESSAGE);
	}
	
	public void show_error_msg(String msg)
	{
		show_error_msg(null, msg);
	}
	
	public void show_error_msg(Component parent, String msg)
	{
		
		JOptionPane.showMessageDialog(parent, msg, "خطأ", JOptionPane.ERROR_MESSAGE);
	}
	
	public void show_info_msg(Component parent, String msg){
		
		JOptionPane.showMessageDialog(parent, msg, "قاعدة البيانات", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void show_warn_msg(String msg){
		
		JOptionPane.showMessageDialog(null, msg, "قاعدة البيانات", JOptionPane.WARNING_MESSAGE);
	}
	
	public static class server_data
	{
		private String server_host;
		private String server_port;
		private String server_user;
		private String server_pass;
		private String db;
		
		public void set(String host, String port, String user, String password, String db_name)
		{
			server_host = host;
			server_port = port;
			server_user = user;
			server_pass = password;
			db = db_name;
		}
		
		public void set_host(String host)
		{
			server_host = host;
		}
		public void set_port(String port)
		{
			server_port = port;
		}
		public void set_user(String user)
		{
			server_user = user;
		}
		
		public void set_pass(String password)
		{
			server_pass = password;
		}
		
		public void set_db(String db_name)
		{
			db = db_name;
		}
		
		public String host()
		{
			return server_host;
		}
		
		public String port()
		{
			return server_port;
		}
		
		public String user()
		{
			return server_user;
		}
		
		public String pass()
		{
			return server_pass;
		}
		
		public String db()
		{
			return db;
		}
	}
	
}
