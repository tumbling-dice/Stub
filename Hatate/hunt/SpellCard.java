package inujini_.hatate.data;

import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;

import java.io.Serializable;

import lombok.Data;

@Data
@SqliteTable(value="SpellCard")
public class SpellCard implements Serializable, ISqlite {
	
	@SqliteField(name="Id", type=FIELD_INTEGER, primary=true)
	private long id;
	
	@SqliteField(name="Name", type=FIELD_TEXT, notNull=true)
	private String name;
	@SqliteField(name="Power", type=FIELD_INTEGER, notNull=true)
	private int power;
	@SqliteField(name="Rarity", type=FIELD_REAL, notNull=true)
	private float rarity;
	@SqliteField(name="Count", type=FIELD_INTEGER, notNull=true, defaultValue="0")
	private int count;
	@SqliteField(name="GetFlag", type=FIELD_INTEGER, notNull=true, defaultValue="0")
	private boolean isGot;
	@SqliteField(name="EquipmentFlag", type=FIELD_INTEGER, notNull=true, defaultValue="0")
	private boolean isEquipped;
	
	@SqliteField(name="CharacterId", type=FIELD_INTEGER, notNull=true)
	private int characterId;
	@SqliteField(name="SeriesId", type=FIELD_TEXT, notNull=true)
	private int[] seriesId;
}