package gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import common.common;
import common.lang;
import common.date;
import database.database;


public class custom_models
{
	static Font lcd_font;
	
	static void init()
	{
		try
		{
			File lcd_font_file = new File(custom_models.class.getResource("/resources/lcd.ttf").getFile());
			
			lcd_font = Font.createFont(Font.TRUETYPE_FONT, lcd_font_file);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(lcd_font);
			
		}
		catch (FontFormatException | IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void focus(Component comp)
	{
		SwingUtilities.invokeLater(()->
		{
			comp.requestFocusInWindow() ;
		});
	}
	
	public static class lcd_screen extends JLabel
	{
		private static final long serialVersionUID = 1L;

		lcd_screen(String label)
		{
			super(label);
			
			setFont(lcd_font.deriveFont(50f));
			setOpaque(true);
			setBackground(new Color(150, 235, 43));
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(0), BorderFactory.createLineBorder(Color.white)));
		}
	}
	
	public static class Item
	{
		int value = 0;
		String text_leading = "";
		String text_trailing = "";
		
		Item() {}
		
		Item(int value, String text_leading, String text_trailing)
		{
			this.value = value;
			this.text_leading = text_leading;
			this.text_trailing = text_trailing;
		}
		
		void set_value(int v, String t_l, String t_t)
		{
			value = v;
			text_leading = t_l;
			text_trailing = t_t;
		}
		
		void set_value(int v, String t_l)
		{
			set_value(v, t_l, "");
		}
		
		public String toString()
		{
			return text_leading;
		}

		public static custom_models.Item[] create_items_list(String[] labels)
		{
			return create_items_list(labels, 1);
		}

		public static custom_models.Item[] create_items_list(String[] labels, int start_value)
		{
			custom_models.Item[] items = new custom_models.Item[labels.length];
			
			for(int i = start_value; i < labels.length + start_value; i++)
			{
				items[i - start_value] = new custom_models.Item(i, labels[i - start_value], "");
			}
			return items;
		}
	}
	
	public static class form extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		int columns = 1;
		
		int current_column = 0;
		int current_row = 0;
		
		GBConstraints c;
		
		public form(int form_columns)
		{
			super(new GridBagLayout());
			columns = form_columns;
			c = new GBConstraints();
		}
		
		public form()
		{
			super(new GridBagLayout());
			c = new GBConstraints();
		}
		
		void align_contents_right()
		{
			setLayout(new FlowLayout(FlowLayout.LEADING));
		}
		
		@Override
		public Component add(Component comp)
		{
			add_next_cell(comp, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_START);
			return this;
		}
		
		public void add(Component[] comps)
		{
			for(int i = 0; i < comps.length; i++)
			{
				add(comps[i]);
			}
		}
		
		public Component add(Component comp, int grid_width)
		{
			add_next_cell(comp, grid_width, GridBagConstraints.NONE, GridBagConstraints.LINE_START);
			
			return this;
		}
		
		public void add(Component comp, int grid_width, int fill)
		{
			add_next_cell(comp, grid_width, fill, GridBagConstraints.LINE_START);
		}
		
		public void add(Component comp, int grid_width, int fill, int align)
		{
			add_next_cell(comp, grid_width, fill, align);
		}
		
		public void add_next_cell(Component comp, int grid_width, int fill, int align)
		{
			add(comp, current_column, current_row, grid_width, fill, align);
			
			current_column += grid_width - 1; 
			
			if(current_column < columns - 1)
			{
				current_column ++;
			}
			else
			{
				current_row ++;
				current_column = 0;
			}
		}
		
		public void add(Component comp, int gridx, int gridy, int grid_width, int fill, int align)
		{
			c.gridx = gridx;
			c.gridx = gridx;
			c.gridwidth = grid_width;
			c.fill = fill;
			c.anchor = align;
			add(comp, c);
		}
		
	}
	
	public static class button extends JButton
	{
		private static final long serialVersionUID = 1L;

		button(String text, ActionListener al)
		{
			super(text);
			addActionListener(al);
		}
		
		button(ImageIcon icon, ActionListener al)
		{
			super(icon);
			addActionListener(al);
		}
		
		button(String text, ImageIcon icon, ActionListener al)
		{
			super(text, icon);
			addActionListener(al);
		}
	}
	
	public static class title_label extends JLabel
	{
		private static final long serialVersionUID = 1L;

		title_label(String label)
		{
			super(label);
			this.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
		}
		title_label(String label, ImageIcon icon)
		{
			super(label);
			this.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
			this.setIcon(icon);
		}
		
	}
	
	public static class mllabel extends JTextArea
	{
		private static final long serialVersionUID = 1L;

		mllabel(String txt)
		{
			super(txt);
			setEditable(false);
			setOpaque(false);
			getCaret().setVisible(false);
			setFocusable(false);
		}
	}
	
	public static class separator extends JSeparator
	{
		private static final long serialVersionUID = 1L;
		separator()
		{
			super(SwingConstants.VERTICAL);
			this.setPreferredSize(new Dimension(3, 50));
		}
		separator(int width, int height)
		{
			super(SwingConstants.VERTICAL);
			this.setPreferredSize(new Dimension(width, height));
		}
	}
	
	public static class GBConstraints extends GridBagConstraints
	{
		private static final long serialVersionUID = 1L;

		GBConstraints()
		{
			weightx = 0;
			weighty = 0;
			gridwidth = 1;
			anchor = GridBagConstraints.LINE_START;
			insets = new Insets(2, 2, 2, 2);
		}
	}
	
	public static class light_indicator extends JLabel
	{
		private static final long serialVersionUID = 1L;
		
		static final int GREEN = 0;
		static final int RED   = 1;
		
		ImageIcon icon_redlight = new ImageIcon(getClass().getResource("/icons/redlight16x16.png"));
		ImageIcon icon_greenlight = new ImageIcon(getClass().getResource("/icons/greenlight16x16.png"));
		
		light_indicator(String label)
		{
			super(label);
			setIcon(icon_greenlight);
		}
		
		light_indicator(String label, int color)
		{
			set(label, color);
		}
		
		public void set(String label, int color)
		{
			setText(label);
			setIcon(color == RED ? icon_redlight : icon_greenlight);
		}
	}
	
	public static class msg_panel extends JPanel
	{
		static final int INFO = 0;
		static final int QUESTION = 1;
		static final int WARNING = 2;
		static final int ERROR = 3;
		
		Icon icon_error    = UIManager.getIcon("OptionPane.errorIcon");
		Icon icon_info     = UIManager.getIcon("OptionPane.informationIcon");
		Icon icon_warning  = UIManager.getIcon("OptionPane.warningIcon");
		Icon icon_question = UIManager.getIcon("OptionPane.questionIcon");
		
		private static final long serialVersionUID = 1L;

		msg_panel(String msg, int type)
		{
			JLabel lbl_msg = new JLabel(msg);
			
			switch(type)
			{
			case INFO: lbl_msg.setIcon(icon_info); break;
			case QUESTION: lbl_msg.setIcon(icon_question); break;
			case WARNING: lbl_msg.setIcon(icon_warning); break;
			case ERROR: lbl_msg.setIcon(icon_error); break;
			}
			
			setOpaque(true);
			setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			setBackground(Color.lightGray);
			add(lbl_msg);
		}
	}
	
	public static class labeled_value extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		JLabel lbl_value = new JLabel();
		
		labeled_value(String label, double value)
		{
			add(new JLabel(label));
			add(lbl_value);
			set_value(value);
		}
		
		void set_value(String value)
		{
			lbl_value.setText(value+"");
		}
		
		void set_value(double value)
		{
			lbl_value.setText(value+"");
		}
	}
	
	public static class square_button extends JButton
	{
		private static final long serialVersionUID = 1L;
		
		int value = 0;
		
		square_button(){}
		
		square_button(int value, String label, ActionListener al)
		{
			super(label);
			this .value = value;
			setPreferredSize(new Dimension(70, 70));
			setToolTipText(label);
			addActionListener(al);
		}
		
		square_button(int value, ImageIcon icon, String tooltip, ActionListener al)
		{
			this.value = value;
			setPreferredSize(new Dimension(70, 70));
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setOpaque(false);
			setBackground(Color.white);
			setMargin(new Insets(0, 0, 0, 0));
			setIcon(icon);
			setToolTipText(tooltip);
			addActionListener(al);
		}
	}
	
	public static class menu_button extends JButton
	{
		private static final long serialVersionUID = 1L;

		ImageIcon icon_menu = new ImageIcon(getClass().getResource("/icons/menu32x32.png"));
		
		menu_button(String caption, ActionListener al)
		{
			set_properties(caption);
			addActionListener(al);
		}
		
		void set_properties(String tooltip)
		{
			setToolTipText(tooltip);
			setIcon(icon_menu);
			setOpaque(false);
			setContentAreaFilled(false);
			setPreferredSize(new Dimension(32, 32));
			setMargin(new Insets(0, 0, 0, 0));
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setFocusPainted(false);
		}
	}
	
	public static class tools_button extends JButton
	{
		private static final long serialVersionUID = 1L;
		
		static final int BTN_VIEW        = 0;
		static final int BTN_ADD         = 1;
		static final int BTN_EDIT        = 2;
		static final int BTN_DELETE      = 3;
		static final int BTN_REMOVE      = 4;
		static final int BTN_LIST        = 5;
		static final int BTN_PAYMENT     = 6;
		static final int BTN_PERMISSIONS = 7;
		
		ImageIcon icon_view = new ImageIcon(getClass().getResource("/icons/view16x16.png"));
		ImageIcon icon_add = new ImageIcon(getClass().getResource("/icons/add16x16.png"));
		ImageIcon icon_edit = new ImageIcon(getClass().getResource("/icons/edit16x16.png"));
		ImageIcon icon_remove = new ImageIcon(getClass().getResource("/icons/remove16x16.png"));
		ImageIcon icon_delete = new ImageIcon(getClass().getResource("/icons/delete16x16.png"));
		ImageIcon icon_list = new ImageIcon(getClass().getResource("/icons/list16x16.png"));
		ImageIcon icon_payment = new ImageIcon(getClass().getResource("/icons/payment16x16.png"));
		ImageIcon icon_permissions = new ImageIcon(getClass().getResource("/icons/permissions16x16.png"));
		
		int item_index = -1;
		int operation = -1; // هذه القيمة تحدد من طرف المبرمج عند إنشاء الزر، لاستعمالها في وقت لاحق عند معالجة الأحداث المتعلقة بالزر
		
		tools_button(int type, int index, int op, ActionListener al)
		{
			set_properties("");
			
			set_icon(type, index, op, al);
		}
		
		tools_button(int type, int index, int op, String tooltip, ActionListener al)
		{
			set_properties(tooltip);
			
			set_icon(type, index, op, al);
		}
		
		void set_icon(int type, int index, int op, ActionListener al)
		{
			item_index = index;
			operation = op;
			
			switch(type)
			{
			case BTN_VIEW:        setIcon(icon_view);        break;
			case BTN_ADD:         setIcon(icon_add);         break;
			case BTN_EDIT:        setIcon(icon_edit);        break;
			case BTN_REMOVE:      setIcon(icon_remove);      break;
			case BTN_DELETE:      setIcon(icon_delete);      break;
			case BTN_LIST:        setIcon(icon_list);        break;
			case BTN_PAYMENT:     setIcon(icon_payment);     break;
			case BTN_PERMISSIONS: setIcon(icon_permissions); break;
			}
			addActionListener(al);
		}
		
		void set_properties(String caption)
		{
			//setText(caption);
			setToolTipText(caption);
			setPreferredSize(new Dimension(20, 20));
			setMargin(new Insets(0, 0, 0, 0));
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setFocusPainted(false);
		}
	}
	
	public static class list_table extends JTable
	{
		private static final long serialVersionUID = 1L;
		
		private boolean SHOW_ROW_NUMBER = true; //
		
		ArrayList<Integer> align_leading_columns = new ArrayList<>();
		
		private ArrayList<Integer> row_ids; // بعض القوائم تحفظ أرقام تعريف خاصة بكل عنصر في الجدول لأجل التحكم في العنصر عند الحاجة
		
		list_table()
		{
			setRowHeight(30);
		}
		
		list_table(TableModel model)
		{
			super(model);
			setRowHeight(30);
		}
		
		
		list_table(MouseListener ml)
		{
			setRowHeight(30);
			addMouseListener(ml);
		}
		
		void set_row_ids(ArrayList<Integer> row_ids)
		{
			this.row_ids = row_ids;
		}
		
		int get_row_id(int row_index)
		{
			return row_ids.get(row_index);
		}
		
		void set_column_width(int index, int width)
		{
			if(SHOW_ROW_NUMBER)
			{
				index++;
			}
			
			if(index >= getColumnModel().getColumnCount())
			{
				index = getColumnModel().getColumnCount() - 1;
			}
			getColumnModel().getColumn(index).setMinWidth(width);
			getColumnModel().getColumn(index).setMaxWidth(width);
		}
		
		void add_leading_align_column(int column_index)
		{
			align_leading_columns.add(column_index+1);
		}
		
		void set_model(ArrayList<Object[]>data, String[] columns)
		{
			if(SHOW_ROW_NUMBER)
			{
				for(int i = 0; i < data.size(); i++)
				{
					ArrayList<Object> tmp_data_row = new ArrayList<>(Arrays.asList(data.get(i)));
					
					tmp_data_row.add(0, i+1);
					
					data.set(i, tmp_data_row.toArray(new Object[tmp_data_row.size()]));
				}
				
				ArrayList<String> columns_tmp = new ArrayList<>(Arrays.asList(columns));
				
				columns_tmp.add(0, "#");
				
				columns = columns_tmp.toArray(new String[columns_tmp.size()]);
			}
			
			Object[][] _data = data.toArray(new Object[data.size()][columns.length]);
			
			setModel(new DefaultTableModel(_data, columns));
			
			if(SHOW_ROW_NUMBER)
			{
				set_column_width(-1, 40);
			}
		}
		
		@Override
		public TableCellRenderer getCellRenderer(int row, int column)
		{
			return new jpanel_cell();
		}
		
		@Override
		public TableCellEditor getCellEditor(int row, int column)
		{
			return new jpanel_editor();
		}
		
		public class jpanel_cell implements TableCellRenderer
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean is_selected, boolean has_focus, int row, int column)
			{
				if(value != null && value.getClass() == JPanel.class)
				{
					JPanel panel_renderer = (JPanel) value;
					panel_renderer.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 2));
					panel_renderer.setOpaque(true);
					panel_renderer.setBackground(is_selected ? table.getSelectionBackground():table.getBackground());
					panel_renderer.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					
					return panel_renderer;
				}
				
				int align = align_leading_columns.contains(column) ? SwingConstants.RIGHT : SwingConstants.CENTER;
				JLabel txt_renderer = new JLabel(value == null?"":value.toString(), align);
				txt_renderer.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
				txt_renderer.setOpaque(true);
				txt_renderer.setBackground(is_selected ? table.getSelectionBackground():table.getBackground());
				txt_renderer.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				
				if(SHOW_ROW_NUMBER && column == 0)
				{
					txt_renderer.setBackground(Color.decode("#D1DFE9"));
					txt_renderer.setForeground(Color.gray);
				}
				
				return txt_renderer;
			}
		}
		
		public static class jpanel_editor extends AbstractCellEditor implements TableCellEditor
		{
			private static final long serialVersionUID = 1L;
			
			JPanel panel;
			
			@Override
			public Object getCellEditorValue()
			{
				return panel;
			}
			@Override
			public Component getTableCellEditorComponent(JTable table, Object value, boolean is_selected, int row, int column)
			{
				if(value != null && value.getClass() == JPanel.class)
				{
					return panel = (JPanel)value;
				}
				return null;
			}
		}
	}
	
	public static class file_browser extends JFileChooser
	{
		private static final long serialVersionUID = 1L;
	
		file_browser(String title)
		{
			setDialogTitle(title);
			
			if(lang.get("DIRECTION").equals("RTL"))
			{
				applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				
				// إزاحة المنظور إلى اليمين
				
				//TODO:
			}
		}
		
	}
	
	public static class image_browser extends JFileChooser
	{
		private static final long serialVersionUID = 1L;
		
		private static final int max_size = 190;
		
		JLabel lbl_image_frame = new JLabel();
		JPanel image_view = new JPanel();
		
		ArrayList<String> str_extensions = new ArrayList<>();
		
		image_browser(String title)
		{
			image_view.setPreferredSize(new Dimension(200, 200));
			
			setDialogTitle(title);
			
			setAccessory(image_view);
			
			lbl_image_frame.setHorizontalTextPosition(JLabel.CENTER);
			lbl_image_frame.setVerticalTextPosition(JLabel.CENTER);
			
			image_view.add(lbl_image_frame);
			
			
			if(lang.get("DIRECTION").equals("RTL"))
			{
				applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				
				// إزاحة المنظور إلى اليمين
				
				//TODO:
			}
			
			addPropertyChangeListener(ev ->
				{
	                String prop = ev.getPropertyName();
	                
	                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop))
	                {
	                    preview();
	                }
	            });
		}
		
		void preview()
		{
			File f = this.getSelectedFile();
			
			if(f == null)
			{
				return;
			}
			
			try
			{
				ImageIcon icon = new ImageIcon(f.getPath());
				
				int width = icon.getIconWidth();
				int height = icon.getIconHeight();
				
				if (width > max_size || height > max_size)
				{
					int new_height = height;
					int new_width = width;
					
					double width_aspect_ratio = (double)width / (double)height;
					double height_aspect_ratio = (double)height / (double)width;

					if(width > height)
					{
						new_width = max_size;
						new_height = (int)((double)max_size/width_aspect_ratio);
					}
					else if(height > width)
					{
						new_height = max_size;
						new_width = (int)((double)max_size/height_aspect_ratio);
					}
					
					lbl_image_frame.setIcon(new ImageIcon(icon.getImage().getScaledInstance(new_width, new_height, Image.SCALE_SMOOTH)));
				}
				else
				{
                    lbl_image_frame.setIcon(icon);
                }
			 }
			 catch (Exception ex)
			 {
				 lbl_image_frame.setIcon(null);
				 ex.printStackTrace();
			 }
		}
		
		boolean is_image()
		{
			File f = this.getSelectedFile();
			
			if(f == null)
			{
				return false;
			}
			
			for(String ext:str_extensions)
			{
				if(f.getName().endsWith(ext))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	public static class research_box extends JTextField implements KeyListener, MouseListener
	{
		private static final long serialVersionUID = 1L;

		static final int ROWS = 10;
		
		common.text_change_listener text_change_listener;
		
		DefaultListModel<common.item> list_model = new DefaultListModel<>();
		JList<common.item> suggestions_list = new JList<>(list_model);
		JScrollPane scroll = new JScrollPane(suggestions_list);
		JPopupMenu popup = new JPopupMenu();
		ArrayList<common.item> suggestions;
		
		common.item selected_item;
		
		research_box(ArrayList<common.item> suggestions)
		{
			super("", 20);

			this.suggestions = suggestions;
			
			addKeyListener(this);
			
			suggestions_list.addMouseListener(this);
			suggestions_list.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			
			popup.add(scroll);
		}
		
		Component parent_window;
		
		research_box(ArrayList<common.item> suggestions, Component parent_window)
		{
			super("", 20);
			
			this.parent_window = parent_window;

			this.suggestions = suggestions;
			
			addKeyListener(this);
			
			suggestions_list.addMouseListener(this);
			suggestions_list.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			
			popup.add(scroll);
		}
		
		void show_suggestions()
		{
			String str_res = getText();
			
			String[] res_words = str_res.split(" ");
			
			list_model.clear();
			
			if(str_res.isBlank())
			{
				popup.setVisible(false);
				return;
			}
			
			int count = 0;
			
			for(common.item sugg:suggestions)
			{
				if(count >= ROWS)
				{
					break;
				}
				
				boolean match = true;
				
				for(String word:res_words)
				{
					if( ! sugg.toString().contains(word))
					{
						match = false;
						break;
					}
				}
				
				if(match)
				{
					list_model.addElement(sugg);
					count++;
				}
			}
			
			if(list_model.isEmpty())
			{
				close();
				
				return;
			}
			
			JScrollBar hscrollbar = scroll.getHorizontalScrollBar();
			hscrollbar.setValue(hscrollbar.getMaximum());
			
			popup.setPreferredSize(new Dimension(this.getWidth(), popup.getPreferredSize().height));
			popup.show(this, 0, getHeight());
			
			if(parent_window != null)
			{
				parent_window.requestFocus();
			}
			
			get_focus();
		}
		
		void close()
		{
			popup.setVisible(false);
			
			get_focus();
		}
		
		void get_focus()
		{
			if( ! hasFocus())
			{
				grabFocus();
			}
			SwingUtilities.invokeLater(() ->
			{
				if( ! hasFocus())
				{
					grabFocus();
				}
			});
		}
		
		void add_text_change_listener(common.text_change_listener text_change_listener)
		{
			this.text_change_listener = text_change_listener;
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
			if(ev.getClickCount() == 2)
			{
				selected_item = (common.item) suggestions_list.getSelectedValue();
				
				setText(selected_item.toString());
				
				dispatchEvent(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));
				
				if(text_change_listener != null)
				{
					text_change_listener.text_changed(this.getText());
				}
				
				close();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}

		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent ev)
		{
			int selected_sugg = suggestions_list.getSelectedIndex();
			
			if(ev.getKeyCode() == KeyEvent.VK_DOWN)
			{
				suggestions_list.setSelectedIndex(selected_sugg + 1);
			}
			else if(ev.getKeyCode() == KeyEvent.VK_UP)
			{
				if(selected_sugg > -1)
				{
					suggestions_list.setSelectedIndex(selected_sugg - 1);
				}
			}
			else if(ev.getKeyCode() == KeyEvent.VK_ENTER)
			{
				if(selected_sugg >= 0)
				{
					selected_item = (common.item) suggestions_list.getSelectedValue();
					
					setText(selected_item.toString());
					
					close();
				}
			}
			else
			{
				show_suggestions();
			}
			
			if(text_change_listener != null)
			{
				text_change_listener.text_changed(this.getText());
			}
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {}
	}
	
	public static class list_scroll extends JScrollPane
	{
		private static final long serialVersionUID = 1L;
		
		list_scroll(Component comp)
		{
			super(comp);
		}
		
		void scroll_down()
		{
			SwingUtilities.invokeLater(() ->
			{
			    JScrollBar verticalBar = getVerticalScrollBar();
			    verticalBar.setValue(verticalBar.getMaximum());
			});
		}
	}
	
	public static class barcodes_form extends form
	{
		private static final long serialVersionUID = 1L;
		
		ImageIcon icon_barcode = new ImageIcon(getClass().getResource("/icons/barcode.png"));
		
		ArrayList<JTextField> fields = new ArrayList<>();
		ArrayList<String> barcodes = new ArrayList<>();
		
		barcodes_form()
		{
			super(2);
		}
		
		void add_barcodes(ArrayList<String> barcodes)
		{
			this.barcodes = barcodes;
			
			update_fields();
		}
		
		void add_barcode(String barcode)
		{
			if( ! barcode.isBlank())
			{
				if( ! barcodes.contains(barcode))
				{
					barcodes.add(barcode);
				}
			}
			
			update_fields();
		}
		
		void update_fields()
		{
			this.removeAll();
			
			fields.clear();
			
			for(String barcode:barcodes)
			{
				add_field(barcode); 
			}
			
			add_field("");
			
			revalidate();
		}
		
		void add_field(String barcode)
		{
			JTextField txt_barcode_field = new JTextField(barcode, 11);
			fields.add(txt_barcode_field);
			add(txt_barcode_field);
			add(new JLabel(icon_barcode));
		}
		
		JTextField last_field()
		{
			return fields.get(fields.size() - 1);
		}
		
		void clear()
		{
			fields.clear();
			barcodes.clear();
			this.removeAll();
			
			add_barcode("");
		}
	}
	
	public static class textfield extends JTextField
	{
		private static final long serialVersionUID = 1L;
		
		textfield(String txt)
		{
			super(txt);
			set_properties();
		}
		
		textfield(String txt, int columns)
		{
			super(txt, columns);
			set_properties();
		}
		
		void set_properties()
		{
			Dimension d = getPreferredSize();
			
			d.height = 23;
			
			setPreferredSize(d);
			
			addFocusListener(new FocusAdapter()
			{
	            @Override
	            public void focusGained(FocusEvent ev)
	            {
	                selectAll();
	            }
	        });
		}
	}
	
	public static class period_panel extends JPanel implements ItemListener, ActionListener
	{
		private static final long serialVersionUID = 1L;
		
		common.update_listener ul;
		
		date.period period = new date.period();
		
		JLabel lbl_period = new JLabel("الفترة: ");
		JLabel lbl_from = new JLabel("من: ");
		JLabel lbl_to= new JLabel("إلى: ");
		
		JTextField txt_from = new JTextField("", 10);
		JTextField txt_to = new JTextField("", 10);
		
		button btn_refresh = new button("تحديث", this);
		
		form form_custom_period = new form(100);
		
		JComboBox<common.item> lst_periods = new JComboBox<>();
		
		period_panel(int period_type, common.update_listener ul)
		{
			this.ul = ul;
			
			set_period_type(date.period.NONE);
			
			common.item[] period_items = new common.item[]
					{
						new common.item(date.period.NONE, "غير محدد"),
						new common.item(date.period.DAY, "اليوم"),
						new common.item(date.period.YESTERDAY, "منذ أمس"),
						new common.item(date.period.WEAK, "هذا الأسبوع"),
						new common.item(date.period.MONTH, "هذا الشهر"),
						new common.item(date.period.YEAR, "هذه السنة"),
						new common.item(date.period.CUSTOM, "اختياري"),
					};
			
			lst_periods = new JComboBox<>(period_items);
			lst_periods.setSelectedIndex(period_type);
			
			lst_periods.addItemListener(this);
			
			set_date(period.start_date, period.end_date, period_type);
			
			form_custom_period.add(lbl_from);
			form_custom_period.add(txt_from);
			form_custom_period.add(lbl_to);
			form_custom_period.add(txt_to);
			form_custom_period.add(btn_refresh);
			
			form form = new form(100);
			
			form.add(lbl_period);
			form.add(lst_periods);
			
			form.add(form_custom_period);
			
			add(form);
		}
		
		protected void set_date(LocalDate start_date, LocalDate end_date, int selected_period)
		{
			period.start_date = start_date;
			period.end_date = end_date;
			
			lst_periods.setSelectedIndex(selected_period);
			
			txt_from.setText(start_date.format(DateTimeFormatter.ofPattern(date.DATE_FORMAT)));
			txt_to.setText(end_date.format(DateTimeFormatter.ofPattern(date.DATE_FORMAT)));
		}
		
		protected date.period get_date()
		{
			return period;
		}
		
		private void show_custom_date_fields()
		{
			if(period.type == date.period.CUSTOM)
			{
				txt_from.setText(period.start_date.format(DateTimeFormatter.ofPattern(date.DATE_FORMAT)));
				txt_to.setText(period.end_date.format(DateTimeFormatter.ofPattern(date.DATE_FORMAT)));
				
				form_custom_period.setVisible(true);
			}
			else
			{
				form_custom_period.setVisible(false);
			}
		}
		
		private void start_of_the_day(Calendar c, int days)
		{
			c.add(Calendar.DAY_OF_YEAR, -days);
			
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		}
		
		private void set_period_type(int type)
		{
			period.type = type;
			
			Calendar c = Calendar.getInstance();
			
			period.start_date = LocalDate.now();
			period.end_date = LocalDate.now();
			
			switch(type)
			{
			case date.period.NONE:c.set(1, 1, 1, 0, 0, 0); break;
			case date.period.DAY: start_of_the_day(c, 0); break;
			case date.period.YESTERDAY: ; start_of_the_day(c, 1); break;
			case date.period.WEAK: c.add(Calendar.WEEK_OF_YEAR, -1); break;
			case date.period.MONTH: c.add(Calendar.MONTH, -1); break;
			case date.period.YEAR: c.add(Calendar.YEAR, -1); break;
			}
			
			period.start_date = LocalDate.now();
			
			if(type == date.period.CUSTOM)
			{
				period.start_date = date.parse_date_only(txt_from.getText());
			}
			
			show_custom_date_fields();
		}
		
		@Override
		public void itemStateChanged(ItemEvent ev)
		{
			Object source = ev.getSource();
			
			if(source == lst_periods)
			{
				common.item selected_period = (common.item) lst_periods.getSelectedItem();
				
				set_period_type(selected_period.value);
				
				ul.updated();
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			Object source = ev.getSource();
			
			if(source == btn_refresh)
			{
				period.start_date = date.parse_date_only(txt_from.getText());
				period.end_date = date.parse_date_only(txt_to.getText());
				
				ul.updated();
			}
		}
	}
	
	public static class dialog extends JDialog implements ActionListener, MouseListener
	{
		private static final long serialVersionUID = 1L;
		
		database db;
		Component parent;
		
		public void set_visible(boolean modality, Component parent)
		{
			this.parent = parent;
			
			applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			setLocationRelativeTo(parent);
			setModal(modality);
			setVisible(true);
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}

		@Override
		public void actionPerformed(ActionEvent arg0) {}
		
	}
}
