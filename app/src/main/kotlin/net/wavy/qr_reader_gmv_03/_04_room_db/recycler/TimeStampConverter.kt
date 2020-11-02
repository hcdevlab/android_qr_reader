
package net.wavy.qr_reader_gmv_03._04_room_db.recycler

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class TimeStampConverter
{
	val TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
	internal var df: DateFormat = SimpleDateFormat(TIME_STAMP_FORMAT)

	@TypeConverter
	fun fromStringToDate(value: String?): Date?
	{
		if (value != null)
		{
			try
			{
				return df.parse(value)
			}
			catch (e: ParseException)
			{
				e.printStackTrace()
			}

			return null
		}
		else
		{
			return null
		}
	}

	@TypeConverter
	fun fromDateToString(value: Date?): String?
	{
		// return if (value == null) null else df.format(value)
		if (value == null)
		{
			return null
		}
		else
		{
			return df.format(value)
		}
	}
}
