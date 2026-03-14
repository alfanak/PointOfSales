package print;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.custom_models;

public class printer implements Printable
{
	JPanel pnl_header = new JPanel();
	JPanel pnl_footer = new JPanel();
	
	Paper paper = new Paper();
	
	PrinterJob printer_job = PrinterJob.getPrinterJob();
	PageFormat page_format = printer_job.defaultPage();
	
	PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
	
    //DocPrintJob job = services[0].createPrintJob();
	
	double width = mm2pixel(210);
	double height = mm2pixel(297);
	
	double margin_right = mm2pixel(20); // مم
	double margin_bottom = mm2pixel(20);
	double margin_left = mm2pixel(20);
	double margin_top = mm2pixel(20);
	
	public void prepare()
	{
		double x = margin_left;
		double y = margin_top;
		double width = this.width - (margin_left + margin_right);
		double height = this.height - (margin_top + margin_bottom);
		
		paper.setSize(width, height);
		paper.setImageableArea(x, y, width, height);
		
		page_format.setPaper(paper);
		
		printer_job.setPrintable(this, page_format);
		
		//printer_job.addPrintJobListener(this);
		
		/*
		if(services.length > 0)
		{
			 DocPrintJob job = services[0].createPrintJob();
			 
			 FileInputStream fis;
			try {
				fis = new FileInputStream("/home/rasheed/test.txt");
				// TODO Auto-generated catch block
		        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		        Doc doc = new SimpleDoc(fis, flavor, null);
	            job.print(doc, null);
	            
			} catch (FileNotFoundException | PrintException e) {
	            e.printStackTrace();
			}
		}
		*/
		
		if(printer_job.printDialog())
		{
			new Thread(() ->
			{
				try
				{
					
					printer_job.print();
				}
				catch(PrinterException ex)
				{
					ex.printStackTrace();
				}
			}).start();
		}
	}
	
	static double mm2pixel(int value)
	{
		// التحويل من الميليمتر إلى البكسل (1 إنش = 25.4 مم = 72 بكسل)
		
		return value * 72 / 25.4; 
	}
	
	static void set_fixed_size(Component comp, int width, int height)
	{
		comp.setSize(new Dimension(width, height));
		comp.setPreferredSize(new Dimension(width, height));
		comp.setMaximumSize(new Dimension(width, height));
		comp.setMinimumSize(new Dimension(width, height));
	}
	
	static BufferedImage create_thumbnail(page page, int width, int height)
	{
		BufferedImage img = get_component_image(page);
		
		BufferedImage thumbnail = img_resize(img, width, height);
		
		return thumbnail;
	}
	
	static BufferedImage get_component_image(Component comp)
	{
		int width = (int)comp.getPreferredSize().getWidth();
		int height = (int)comp.getPreferredSize().getHeight();
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = bi.createGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		comp.printAll(g2d);
		
		g2d.dispose();
		
		return bi;
	}
	
	public static BufferedImage img_resize(BufferedImage img, int width, int height)
	{
        BufferedImage new_img = new BufferedImage(width, height, img.getType());

        Graphics2D g2d = new_img.createGraphics();
        
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();

        return new_img;
    }
	
	public static class preview extends custom_models.dialog
	{
		private static final long serialVersionUID = 1L;
		
		page page;
		
		public void show(Component parent, page page)
		{
			this.page = page;
			
			JLabel x = new JLabel();
			
			int t_width = (int) (210*1.5);
			int t_height = (int) (297*1.5);
			
			set_fixed_size(x, t_width, t_height);
			
			x.setIcon(new ImageIcon(create_thumbnail(page, t_width, t_height)));
			
			//x.setIcon(new ImageIcon(get_component_image(page)));
			
			
			
			
			custom_models.form frm_settings = new custom_models.form(2);
			
			JPanel page_container = new JPanel();
			page_container.add(x);
			
			JPanel main_container = new JPanel(new BorderLayout());
			
			
			main_container.add(frm_settings, BorderLayout.LINE_START);
			main_container.add(page_container, BorderLayout.LINE_END);
			
			setTitle("طباعة");
			setSize(550, 500);
			setContentPane(main_container);
			set_visible(true, parent);
		}
	}
	
	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException
	{
		System.out.println("printing....");
		
		if (page > 0)
		{
	        return NO_SUCH_PAGE;
	    }
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		
		String txt = "بسم الله الرحمن الرحيم";
		
		g2d.drawString(txt, 0, 0);
		
		return PAGE_EXISTS;
	}
	
	
	public static class page extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		public page_size size = new page_size(page_size.NORM_A4);
		
		public page(page_size size, JPanel header, JPanel body, JPanel footer)
		{
			
			setLayout(new BorderLayout());
			setBackground(Color.yellow);
			setBorder(BorderFactory.createLineBorder(Color.black));
			
			set_fixed_size(this, (int)mm2pixel(210), (int)mm2pixel(297));
			
			
			applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			doLayout();
			validate();
			//revalidate();
			
			
			//set_fixed_size(header, (int)mm2pixel(210), 50);
			
			//set_fixed_size(body, (int)mm2pixel(210), 300);
			
			//set_fixed_size(footer, (int)mm2pixel(210), 200);
			
			//footer.revalidate();
			//footer.repaint();
			
			
			
			add(header, BorderLayout.PAGE_START);
			header.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			header.invalidate();
			header.doLayout();
			header.validate();
			
			add(body, BorderLayout.CENTER);
			
			body.doLayout();
			body.validate();
			
			add(footer, BorderLayout.PAGE_END);
			
			footer.doLayout();
			//footer.validate();
			
			applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			doLayout();
//			invalidate();
			validate();
			
		
			//revalidate();
			//repaint();
			
		}
		
		public void add_part(JPanel p, int place)
		{
			
		}
	}
	
	/*
	class thumbnail extends JLabel
	{
		private static final long serialVersionUID = 1L;
		
		int width;
		int height;
		
		thumbnail(page page, int width, int height)
		{
			BufferedImage img = get_component_image(page);
			
			BufferedImage thumnbnail = img_resize(img);
			
			this.width = width;
			this.height = height;
			
			setSize(new Dimension(width, height));
			setPreferredSize(new Dimension(width, height));
			setMaximumSize(new Dimension(width, height));
			setMinimumSize(new Dimension(width, height));
		}
	}*/
	
	
	public static class page_size
	{
		public static final int NORM_A4 = 0;
		public static final int NORM_A5 = 1;
		
		public double width;
		public double height;
		
		public page_size(int norm)
		{
			switch(norm)
			{
			case NORM_A4:
				width = mm2pixel(210);
				height = mm2pixel(297);
			case NORM_A5:
				width = mm2pixel(148);
				height = mm2pixel(210);
			}
		}
	}
	
	/*
	public static class print_area extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		JPanel header = new JPanel();
		JPanel body = new JPanel();
		JPanel footer = new JPanel();
		
		Graphics2D g2d;
		Graphics2D g2d_header;
		Graphics2D g2d_body;
		Graphics2D g2d_footer;
		
		
		
		print_area()
		{
			setLayout(new BorderLayout());
			setPreferredSize(new Dimension(width, height));
			
			add(header, BorderLayout.PAGE_START);
			add(header, BorderLayout.CENTER);
			add(header, BorderLayout.PAGE_END);
			
			
			g2d = (Graphics2D) getGraphics();
			g2d_header = (Graphics2D) header.getGraphics();
			g2d_body = (Graphics2D) body.getGraphics();
			g2d_footer = (Graphics2D) footer.getGraphics();
		}
		
		void add_to_header(Component comp)
		{
			
		}
		
		void add_to_body(Component comp)
		{
			
		}
		void add_to_footer(Component comp)
		{
		}
	}
	*/
	
}
