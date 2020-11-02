
package net.wavy.qr_reader_gmv_03._04_room_db.web

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rowEntityWebTable")
data class RowEntityWeb constructor(
		@PrimaryKey(autoGenerate = true)
		@ColumnInfo(name = "id")
		var id: Int?,
		@ColumnInfo(name = "userName")
		var userName: String,
		@ColumnInfo(name = "date")
		var date: String,
		@ColumnInfo(name = "qrMachineCode")
		var qrMachineCode: String,
		@ColumnInfo(name = "latCoordinate")
		var latCoordinate: String,
		@ColumnInfo(name = "longCoordinate")
		var longCoordinate: String)
{
	override fun toString(): String =
			"Datos: " +
			"id= " + id +
			" - name: " + userName +
			" - date: " + date +
			" - qr: " + qrMachineCode +
			" - latitude: " + latCoordinate +
			" - longitude: " + longCoordinate
}
