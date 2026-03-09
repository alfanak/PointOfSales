package gui;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class msgbox
{
	final static int OK = JOptionPane.OK_OPTION;
	final static int CANCEL = JOptionPane.CANCEL_OPTION;
	final static int NO = JOptionPane.NO_OPTION;
	final static int YES = JOptionPane.YES_OPTION;
	
	static String[] confirm_options = new String[] {"نعم", "لا","إلغاء"};
	static String[] input_options = new String[] {"موافق", "إلغاء"};
	
	public static void error(Component parent, String msg)
	{
		error(parent, msg, "خطأ");
	}
	public static void warn(Component parent, String msg)
	{
		error(parent, msg, "تنبيه");
	}
	public static void info(Component parent, String msg)
	{
		info(parent, msg, "بيان");
	}
	public static int confirm(Component parent, String msg)
	{
		return confirm(parent, msg, "تأكيد");
	}
	
	public static int options(Component parent, String msg, String title, String[] options)
	{
		return options(parent, msg, title, options, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}
	public static void error(Component parent, String msg, String title)
	{
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
	}
	public static void warn(Component parent, String msg, String title)
	{
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE);
	}
	public static void info(Component parent, String msg, String title)
	{
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	public static int confirm(Component parent, String msg, String title)
	{
		return options(parent, msg, title, confirm_options, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}
	public static int options(Component parent, String msg, String title, String[] options, int option_type, int message_type)
	{
		return JOptionPane.showOptionDialog(parent, msg, title, option_type, message_type, null, options, options[0]);
	}
	public static String input(Component parent, String input_label, String title, String default_input)
	{
		custom_models.form form = new custom_models.form(2);
		
		JTextField txt_input = new JTextField(default_input, 15);
		
		form.add(new JLabel(input_label));
		form.add(txt_input);
		
        int response = JOptionPane.showOptionDialog(parent, form, input_label, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, input_options, input_options[0]);
        
        if(response == OK)
        {
        	return txt_input.getText();
        }
        return null;
	}
}
