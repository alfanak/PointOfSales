package config;
import java.util.prefs.Preferences;

import common.encryptor;
import database.database;

public class configs
{
	// هنا نحتفظ بمختلف الإعدادات الخاصة بالبرنامج 
	// إضافة إلى المعلومات الخاصة بقاعدة البيانات

	static final int RIGHT = 0;
	static final int BOTTOM = 1;
	static final int LEFT = 2;
	static final int TOP = 3;
	
	static final int LEADING = 4;
	static final int TRAILING = 5;
	
	private static Preferences p;
	
	// القيم الابتدائية
	
	public static final String DEFAULT_SERVER_HOST = "localhost";
	public static final String DEFAULT_SERVER_PORT = "3306";
	public static final String DEFAULT_SERVER_USER = "root";
	public static final String DEFAULT_SERVER_PASS = "";
	public static final String DEFAULT_DB_NAME     = "_point_of_sales";
	
	public static final String DEFAULT_CHARSET     = "utf8mb4";
	public static final String DEFAULT_COLLATION   = "utf8mb4_unicode_ci";
	
	public configs()
	{
		p = Preferences.userRoot().node(getClass().getName());
	}

	public static void set_installed()
	{
		put_boolean("INSTALLED", true);
	}
	
	public static boolean is_installed()
	{
		return get_boolean("INSTALLED", false) ? true : false;
	}
	
	public static void put(String key, String value)
	{
		p.put(key, value);
	}
	
	public static String get(String key, String def)
	{
		return p.get(key, def);
	}
	
	public static void put_boolean(String key, boolean value)
	{
		p.putBoolean(key, value);
	}
	
	public static boolean get_boolean(String key, boolean def)
	{
		return p.getBoolean(key, def);
	}
	
	public static void put_int(String key, int value)
	{
		p.putInt(key, value);
	}
	public static int get_int(String key, int def)
	{
		return p.getInt(key, def);
	}
	
	public static void set_defaults()
	{
		p.putInt("INIT_COUNTER", 1);
		
		database.set_server_info(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT, DEFAULT_SERVER_USER, DEFAULT_SERVER_PASS, DEFAULT_DB_NAME);
		database.get_instance().save_parameters();
	}
	
	public static String get_pass()
	{
		String decrypted_password = encryptor.decrypt(get("PASS", ""), get("PASS", ""));
		
		return decrypted_password;
	}
	
	public static int get_init_counter()
	{
		return p.getInt("INIT_COUNTER", 0);
	}
	
	public static int inc_init_counter()
	{
		int init_counter = p.getInt("INIT_COUNTER", 0) + 1;
		p.putInt("INIT_COUNTER", init_counter);
		return init_counter;
	}
	
}
