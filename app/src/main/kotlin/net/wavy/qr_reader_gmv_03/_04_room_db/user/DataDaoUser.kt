package net.wavy.qr_reader_gmv_03._04_room_db.user

import androidx.room.*


@Dao
interface DataDaoUser
{
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertUser(rowEntityWeb: RowEntityUser?): Long

	@Query("select * from rowEntityUserTable where keyString=(:keyString)")
	fun fetchEntityByKey(keyString: String): RowEntityUser

	@Query("delete from rowEntityUserTable where keyString=(:keyString)")
	fun deleteEntityByKey(keyString: String?)

	/*
		Agregado el 2019-04-06, elimina todos los registros de la tabla.
		El nombre de esa tabla se asigna en 'RowEntityUser' con una 'annotation':
		@Entity(tableName = "rowEntityUserTable")
	*/
	@Query("delete from rowEntityUserTable")
	fun deleteUser()
}