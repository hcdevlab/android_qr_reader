
package net.wavy.qr_reader_gmv_03._04_room_db.recycler

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "rowEntityRecyclerTable")
@TypeConverters(TimeStampConverter::class)
data class RowEntityRecycler constructor(
		@PrimaryKey(autoGenerate = true)
		@ColumnInfo(name = "id")
		var id: Long?,

		@ColumnInfo(name = "serial")
		var serial: Int,

		@ColumnInfo(name = "name")
		var name: String,

		@ColumnInfo(name = "created_at")
		// @TypeConverters(TimeStampConverter::class)
		var createdAt: Date,

		@ColumnInfo(name = "modified_at")
		// @TypeConverters(TimeStampConverter::class)
		var modifiedAt: Date?,

		@ColumnInfo(name = "state")
		var state: Boolean)
{
}