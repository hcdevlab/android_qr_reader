package net.wavy.qr_reader_gmv_03._04_room_db.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rowEntityUserTable")
data class RowEntityUser constructor(
		@PrimaryKey(autoGenerate = true)
		@ColumnInfo(name = "id")
		var id: Int?,
		@ColumnInfo(name = "keyString")
		val keyString: String,
		@ColumnInfo(name = "userName")
		val userName: String,
		@ColumnInfo(name = "jwtCookie")
		val jwtCookie: String)
{
	override fun toString(): String = "Datos: [id: " + id + " - keyString: " + keyString + " - name: " + userName + " - cookie: " + jwtCookie + "]"
}
