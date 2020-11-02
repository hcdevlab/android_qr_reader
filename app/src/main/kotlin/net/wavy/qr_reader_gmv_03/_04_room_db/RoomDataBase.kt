
package net.wavy.qr_reader_gmv_03._04_room_db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.DataDaoRecycler
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.RowEntityRecycler
import net.wavy.qr_reader_gmv_03._04_room_db.user.DataDaoUser
import net.wavy.qr_reader_gmv_03._04_room_db.user.RowEntityUser
import net.wavy.qr_reader_gmv_03._04_room_db.web.DataDaoWeb
import net.wavy.qr_reader_gmv_03._04_room_db.web.RowEntityWeb


@Database(entities = arrayOf(RowEntityUser::class, RowEntityWeb::class, RowEntityRecycler::class), version = 1)
abstract class RoomDataBase : RoomDatabase()
{
	// Tablas:
	abstract fun getDaoUser(): DataDaoUser
	abstract fun getDaoWeb(): DataDaoWeb
	abstract fun getDaoRecycler(): DataDaoRecycler

	companion object
	{
		const val DATABASE_NAME = "roomDataBase"
		private var INSTANCE: RoomDataBase? = null

		fun getInstance(context: Context): RoomDataBase?
		{
			if (INSTANCE == null)
			{
				synchronized(RoomDataBase::class)
				{
					INSTANCE = Room.databaseBuilder<RoomDataBase>(context.applicationContext, RoomDataBase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
				}
			}
			return INSTANCE
		}

		fun destroyInstance()
		{
			INSTANCE = null
		}
	}
}
