package barcode;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;

public class barcode_scanner
{
	public static final int SCAN_KEYBOARD  = 0;
	public static final int SCAN_CLIPOBARD = 1;
	public static final int SCAN_SCANNER   = 2;
	
	int scan_mode = SCAN_KEYBOARD;
	
	StringBuilder str_barcode = new StringBuilder();
	
	HashMap<Integer, Character> numerals = new HashMap<>();
	
	AWTEventListener global_keyboard_listener;
	
	public barcode_scanner()
	{
		set_numeral_chars();
	}
	
	public barcode_scanner(int scan_mode, scanner_listener listener)
	{
		this.scan_mode = scan_mode;
		
		set_numeral_chars();
		
		add_global_keyboad_listener(listener);
	}
	
	void set_numeral_chars()
	{
		numerals.put(KeyEvent.VK_NUMPAD0, '0');
		numerals.put(KeyEvent.VK_NUMPAD1, '1');
		numerals.put(KeyEvent.VK_NUMPAD2, '2');
		numerals.put(KeyEvent.VK_NUMPAD3, '3');
		numerals.put(KeyEvent.VK_NUMPAD4, '4');
		numerals.put(KeyEvent.VK_NUMPAD5, '5');
		numerals.put(KeyEvent.VK_NUMPAD6, '6');
		numerals.put(KeyEvent.VK_NUMPAD7, '7');
		numerals.put(KeyEvent.VK_NUMPAD8, '8');
		numerals.put(KeyEvent.VK_NUMPAD9, '9');
		
		numerals.put(KeyEvent.VK_0, '0');
		numerals.put(KeyEvent.VK_1, '1');
		numerals.put(KeyEvent.VK_2, '2');
		numerals.put(KeyEvent.VK_3, '3');
		numerals.put(KeyEvent.VK_4, '4');
		numerals.put(KeyEvent.VK_5, '5');
		numerals.put(KeyEvent.VK_6, '6');
		numerals.put(KeyEvent.VK_7, '7');
		numerals.put(KeyEvent.VK_8, '8');
		numerals.put(KeyEvent.VK_9, '9');
	}
	
	boolean is_numeral(int key_code)
	{
		if((key_code >= KeyEvent.VK_NUMPAD0 && key_code <= KeyEvent.VK_NUMPAD9) || (key_code >= KeyEvent.VK_0 && key_code <= KeyEvent.VK_9))
		{
			return true;
		}
		
		return false;
	}
	
	boolean is_function_key(int key_code)
	{
		if(key_code >= KeyEvent.VK_F1 && key_code <= KeyEvent.VK_F12)
		{
			return true;
		}
		
		return false;
	}
	
	boolean is_modifier(int key_code)
	{
		if(key_code == KeyEvent.VK_CONTROL ||key_code == KeyEvent.VK_SHIFT || key_code == KeyEvent.VK_ALT || key_code == KeyEvent.VK_ALT_GRAPH || key_code == KeyEvent.VK_META)
		{
			return true;
		}
		
		return false;
	}
	
	String scanned_codebar()
	{
		return str_barcode.toString();
	}
	
	boolean scan = false;
	
	void add_global_keyboad_listener(scanner_listener listener)
	{
		long eventMask = AWTEvent.KEY_EVENT_MASK;
		
		global_keyboard_listener = new AWTEventListener()
		{
			
			public void eventDispatched(AWTEvent ev)
			{
				if(ev instanceof KeyEvent)
				{
					KeyEvent kev = (KeyEvent) ev;
					
					int keycode = kev.getKeyCode();
					
					if(kev.getID() == KeyEvent.KEY_PRESSED)
					{
						if(scan_mode == SCAN_CLIPOBARD)
						{
							try
							{
								String barcode = (Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor)).toString();
								
								if( ! barcode.isBlank())
								{
									listener.scanned(barcode);
								}
								
								// مسح الحافظة
								// قد يسبب هدا الأمر مشاكل لدى المستخدم الذي قد يحتاج الحافظة في هذه الأثناء
								
								Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
							}
							catch (UnsupportedFlavorException | IOException ex)
							{
								if(ex.getClass() == UnsupportedFlavorException.class)
								{
									// مسح الحافظة
									// قد يسبب هدا الأمر مشاكل لدى المستخدم الذي قد يحتاج الحافظة في هذه الأثناء
									
									Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
									return;
								}
								
								ex.printStackTrace();
							}
						}
						else if(scan_mode == SCAN_KEYBOARD)
						{
							int modifiers = InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
							
							if((kev.getModifiersEx() & modifiers) == modifiers && keycode == KeyEvent.VK_F1)
							{
								kev.consume();
								
								scan = true;
							}
							
							if(scan)
							{
								kev.consume();
								
								if(keycode == KeyEvent.VK_ENTER)
								{
									// يجب حذف الرمز السابق قبل البحث في قاعدة البيانات
									// وإلا فإنه عند حدوث خطأ في جلب المنتج الموافق للرمز فإن الرمز التالي سيجمع بين الرمز الحالي والرمز الذي تم مسحه الآن
									
									String barcode = str_barcode.toString();
									
									str_barcode = new StringBuilder();
									scan = false;
									
									listener.scanned(barcode);
								}
								else
								{
									if( ! is_modifier(keycode) && ! is_function_key(keycode))
									{
										if(is_numeral(keycode))
										{
											str_barcode.append(numerals.get(keycode));
										}
										else
										{
											str_barcode.append(kev.getKeyChar());
										}
									}
								}
							}
						}
					}
					
					if(kev.getID() == KeyEvent.KEY_TYPED)
					{
						if(scan)
						{
							kev.consume();
						}
					}
					
				}
			}
		};
		
		Toolkit.getDefaultToolkit().addAWTEventListener(global_keyboard_listener , eventMask);
	}
	
	void remove_global_keyboard_listener()
	{
		Toolkit.getDefaultToolkit().removeAWTEventListener(global_keyboard_listener);
	}
	
	public interface scanner_listener
	{
		void scanned(String barcode);
	}
	
}
