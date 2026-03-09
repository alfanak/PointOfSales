package common;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class common
{
	public static String monetary_ns(double value)
	{
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setMinusSign('-');
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("##0.00", dfs);
		
		return df.format(value);
	}
	
	public static String monetary(double value)
	{
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setGroupingSeparator(' ');
		dfs.setMinusSign('-');
		dfs.setDecimalSeparator('.');
		
		DecimalFormat df = new DecimalFormat("#,##0.00", dfs);
		
		return df.format(value);
	}
	
	public static long txt2num(String str)
	{
		try
		{
		    return Long.valueOf(str);
		}
		catch(NumberFormatException ex)
		{
			return 0;
		}
	}

	public static Double txt2decimal(String str)
	{
		try
		{
		    return Double.valueOf(str);
		}
		catch(NumberFormatException ex)
		{
			return 0.0;
		}
	}
	
	public static String md5(String str)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(str.getBytes());
			
			byte[] digest = md.digest();
			
			StringBuilder sb = new StringBuilder();
			
			for(byte b:digest)
			{
				sb.append(String.format("%02x", b));
			}
			
			return sb.toString();
		}
		catch (NoSuchAlgorithmException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String to_string(ArrayList<?> al, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		
		for(Object obj:al)
		{
			sb.append(obj+delimiter);
		}
		
		sb.setLength(sb.length() - delimiter.length());
		
		return sb.toString();
	}
	
	public static BufferedImage scale_image(BufferedImage img, int width, int height)
	{
		if(img.getWidth() <= width && img.getHeight() <= height)
		{
			return img;
		}
		
		BufferedImage scaled_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d = scaled_image.createGraphics();
		
		g2d.drawImage(img, 0, 0, width, height, null);
		g2d.dispose();
		
		return scaled_image;
	}
	
	public interface update_listener
	{
	    void updated();
	}

	public interface text_change_listener
	{
	    void text_changed(String txt);
	}
	
	public static class item
	{
		public int value = 0;
		public String text = "";
		
		public item() {}
		
		public item(int value, String text)
		{
			this.value = value;
			this.text = text;
		}
		
		void set(int value, String text)
		{
			this.value = value;
			this.text = text;
		}
		
		public static item[] create_items_list(String[] labels)
		{
			return create_items_list(labels, 1);
		}

		public static item[] create_items_list(String[] labels, int start_value)
		{
			item[] items = new item[labels.length];
			
			for(int i = start_value; i < labels.length + start_value; i++)
			{
				items[i - start_value] = new item(i, labels[i - start_value]);
			}
			return items;
		}
		
		public static item[] create_items_list(String[] labels, int[] values)
		{
			item[] items = new item[labels.length];
			
			for(int i = 0; i < labels.length; i++)
			{
				items[i] = new item(values[i], labels[i]);
			}
			return items;
		}
		
		@Override
		public String toString()
		{
			return text;
		}
	}
		
	public static class invoice_item
	{
		public String barcode;
		public String name;
		public int id;
		public int product_id;
		public int quantity;
		public double unit_price;
		public double pack_price;
		
		public boolean is_pack()
		{
			return unit_price <= 0 && pack_price > 0;
		}
		
		public double cost()
		{
			return is_pack() ? quantity * pack_price : quantity * unit_price;
		}
		
		public invoice_item() {}
		
		public invoice_item(int product_id, String name, int quantity, double unit_price, double pack_price)
		{
			set("", 0, product_id, name, quantity, unit_price, pack_price);
		}
		
		public invoice_item(int id, int product_id, String name, int quantity, double unit_price, double pack_price)
		{
			set("", id, product_id, name, quantity, unit_price, pack_price);
		}
		
		public invoice_item(int id, int product_id, String name, int quantity, double unit_price)
		{
			set("", id, product_id, name, quantity, unit_price, 0);
		}
		
		public invoice_item(String barcode, int product_id, String name, int quantity, double unit_price)
		{
			set(barcode, 0, product_id, name, quantity, unit_price, 0);
		}
		
		public invoice_item(String barcode, int id, int product_id, String name, int quantity, double unit_price)
		{
			set(barcode, id, product_id, name, quantity, unit_price, 0);
		}
		
		private void set(String barcode, int id, int product_id, String name, int quantity, double unit_price, double pack_price)
		{
			this.barcode = barcode;
			this.id = id;
			this.product_id = product_id;
			this.name = name;
			this.quantity = quantity;
			this.unit_price = unit_price;
			this.pack_price = pack_price;
		}
	}
	
	
}
