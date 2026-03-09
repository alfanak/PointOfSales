package common;
import java.util.Locale;
import java.util.ResourceBundle;

public class lang
{
	public static Locale ARABIC = new Locale("ar");
	public static Locale ENGLISH = new Locale("en");
	public static Locale FRENSH = new Locale("fr");
	
	private static ResourceBundle rb;
	
	public static void set_locale(Locale l)
	{
		rb = ResourceBundle.getBundle("resources.lang.lang", l);
	}
	
	public static String get(String key)
	{
		return rb.getString(key);
	}
}