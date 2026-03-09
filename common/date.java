package common;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class date
{
	public static final String DATE_FORMAT = "yyyy/MM/dd";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	
	public static String current_time()
	{
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
	}
	
	public static String current_date()
	{
		return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
	}
	
	public static String format_date(LocalDate date)
	{
		return date != null ? date.format(DateTimeFormatter.ofPattern(DATE_FORMAT)) : "";
	}
	
	public static String format_date(LocalDateTime date_time)
	{
		return date_time != null ? date_time.format(DateTimeFormatter.ofPattern(DATE_FORMAT)) : "";
	}
	
	public static String format_time(LocalDateTime date_time)
	{
		return date_time != null ? date_time.format(DateTimeFormatter.ofPattern(TIME_FORMAT)) : "";
	}
	
	public static String format_date_time(LocalDateTime date_time)
	{
		return date_time != null ? date_time.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)): "";
	}
	
	public static LocalDateTime parse_date(String str_date)
	{
		LocalDate date = parse_date_only(str_date);
		
		if(date != null)
		{
			return date.atStartOfDay();
		}
		return parse_date_time(str_date);
	}
	
	public static LocalDate parse_date_only(String str_date)
	{
		ArrayList<DateTimeFormatter> formats = new ArrayList<>();
		
		formats.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		formats.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		formats.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		formats.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		for(DateTimeFormatter format:formats)
		{
			try
			{
				return LocalDate.parse(str_date, format);
			}
			catch(DateTimeParseException ex)
			{
				
			}
		}
		
		return null;
	}
	
	public static LocalDateTime parse_date_time(String str_date, String str_time)
	{
		return parse_date_time(str_date+" "+str_time);
	}
	
	public static LocalDateTime parse_date_time(String str_date)
	{
		ArrayList<DateTimeFormatter> formats = new ArrayList<>();
		
		formats.add(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		formats.add(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
		formats.add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
		formats.add(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm a"));
		
		formats.add(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		formats.add(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a"));
		formats.add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		formats.add(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a"));
		
		formats.add(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
		formats.add(DateTimeFormatter.ofPattern("hh:mm a dd/MM/yyyy"));
		formats.add(DateTimeFormatter.ofPattern("HH:mm yyyy/MM/dd"));
		formats.add(DateTimeFormatter.ofPattern("hh:mm a yyyy/MM/dd"));
		
		formats.add(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
		formats.add(DateTimeFormatter.ofPattern("hh:mm:ss a dd/MM/yyyy"));
		formats.add(DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd"));
		formats.add(DateTimeFormatter.ofPattern("hh:mm:ss a yyyy/MM/dd"));
		
		for(DateTimeFormatter format:formats)
		{
			try
			{
				return LocalDateTime.parse(str_date, format);
			}
			catch(DateTimeParseException ex)
			{
				
			}
		}
		
		return null;
	}
	
	public static class period
	{
		public static final int NONE = 0;
		public static final int DAY = 1;
		public static final int YESTERDAY = 2;
		public static final int WEAK = 3;
		public static final int MONTH = 4;
		public static final int YEAR = 5;
		public static final int CUSTOM = 6;
		
		public LocalDate start_date;
		public LocalDate end_date;
		
		public int type = WEAK;
	}
	
}
