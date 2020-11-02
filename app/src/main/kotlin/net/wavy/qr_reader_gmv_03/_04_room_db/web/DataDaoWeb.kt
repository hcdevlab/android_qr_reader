package net.wavy.qr_reader_gmv_03._04_room_db.web

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.room.RawQuery


@Dao
interface DataDaoWeb
{
	@Delete
	fun deleteAll(list: List<RowEntityWeb>): Int

	@RawQuery
	fun vacuumDb(supportSQLiteQuery: SupportSQLiteQuery): Int

	@Insert()
	fun insertOne(rowEntityWeb: RowEntityWeb?): Long

	@Query("select * from rowEntityWebTable")
	fun fetchAllUser(): List<RowEntityWeb>

	@Query("select * from rowEntityWebTable")
	fun fetchAllRows(): List<RowEntityWeb>

	@Query("select * from rowEntityWebTable")
	fun fetchAllRowsLiveData(): LiveData<List<RowEntityWeb>>

	@Query("select count(*) from rowEntityWebTable")
	fun countRows(): Int

	@Query("select * from rowEntityWebTable where id = :id")
	fun fetchOneRow(id: Int): RowEntityWeb
}