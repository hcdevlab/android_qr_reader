
package net.wavy.qr_reader_gmv_03._04_room_db.recycler

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface DataDaoRecycler
{
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(vararg entity: RowEntityRecycler)

	@Query("select * from rowEntityRecyclerTable")
	// fun fetchAllUser(): LiveData<List<RowEntity>>
	fun fetchAllData(): List<RowEntityRecycler>

	// https://github.com/Thumar/RoomPersistenceLibrary/blob/master/app/src/main/java/com/app/androidkt/librarymanagment/db/dao/UserDao.java
	@Query("select * from rowEntityRecyclerTable where modified_at=Date(:date)")
	fun fetchEntitiesByDate(date: String): LiveData<List<RowEntityRecycler>>

	@Query("select * from rowEntityRecyclerTable ORDER BY created_at desc")
	fun fetchAllItems(): LiveData<List<RowEntityRecycler>>

	// @Update(onConflict = REPLACE)
	// fun updateTrail(trail: Trail)

	@Query("update rowEntityRecyclerTable set state=:paramState where name = :paramName")
	fun updateState(paramName: String, paramState: Boolean)

	@Delete
	fun deleteAll(list: List<RowEntityRecycler>?): Int
}