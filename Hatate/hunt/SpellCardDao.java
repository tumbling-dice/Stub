package inujini_.hatate.sqlite.dao;

@ExtensionMethod({SqliteUtil.class, CursorExtensions.class, Linq.class})
public class SpellCardDao {
	
	private static final Func1<Cursor, SpellCard> _converter = new Func1<Cursor, SpellCard>() {
		@Override
		public SpellCard call(Cursor x) {
			val s = new SpellCard();
			s.setId(x.getLongByMeta(MetaSpellCard.Id));
			s.setName(x.getStringByMeta(MetaSpellCard.Name));
			s.setPower(x.getIntByMeta(MetaSpellCard.Power));
			s.setRarity(x.getFloatByMeta(MetaSpellCard.Rarity));
			s.setCount(x.getIntByMeta(MetaSpellCard.Count));
			s.setGot(x.getBooleanByMeta(MetaSpellCard.GetFlag));
			s.setEquipped(x.getBooleanByMeta(MetaSpellCard.EquipmentFlag));
			s.setCharacterId(x.getIntByMeta(MetaSpellCard.CharacterId));
			s.setSeriesId(x.getIntByMeta(MetaSpellCard.SeriesId));
			return s;
		}
	}
	
	public static List<SpellCard> getAllSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME).toString();
		return new DatabaseHelper(context).getList(q, context, _converter);
	}
	
	public static List<SpellCard> getHaveSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.GetFlag, true)
					.toString();
		
		return new DatabaseHelper(context).getList(q, context, _converter);
	}
	
	public static List<SpellCard> getEquippedSpellCards(Context context) {
		val q = new QueryBuilder().selectAll().from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.EquipmentFlag, true)
					.toString();
		
		return new DatabaseHelper(context).getList(q, context, _converter);
	}
	
	public static int getEquipCount(Context context) {
		val q = new QueryBuilder().select(MetaSpellCard.Id).from(MetaSpellCard.TBL_NAME)
					.where().equal(MetaSpellCard.EquipmentFlag, true)
					.toString();
		
		return new DatabaseHelper(context).getList(q, context, _converter).size();
	}
	
	public static void update(Context context, SpellCard spellCard) {
		
		val cv = new ContentValues();
		val id = spellCard.getId();
		
		cv.put(MetaSpellCard.Count.getColumnName(), spellCard.getCount() + 1);
		cv.put(MetaSpellCard.GetFlag.getColumnName(), 1);
		
		new DatabaseHelper(context).transaction(new Action1<SQLiteDatabase>() {
			@Override
			public void call(SqliteDatabase x) {
				x.update(MetaSpellCard.TBL_NAME, cv, "Id = ?", id);
			}
		});
	}
	
	public static void insert(Context context, SpellCard spellCard) {
		val q = createInsertQuery(spellCard);
		
		new DatabaseHelper(context).transaction(new Action1<SQLiteDatabase>() {
			@Override
			public void call(SqliteDatabase x) {
				x.execSQL(q);
			}
		});
	}
	
	public static void bulkInsert(Context context, List<SpellCard> spellCards) {
		val querys = spellCards.linq().select(new Func1<SpellCard, String>(){
			@Override
			public String call(SpellCard s) {
				return createInsertQuery(s);
			}
		}).toList();
		
		new DatabaseHelper(context).transaction(new Action1<SQLiteDatabase>() {
			@Override
			public void call(SqliteDatabase x) {
				for(val q : querys)
					x.execSQL(q);
			}
		});
	}
	
	private static String createInsertQuery(SpellCard s) {
		return new QueryBuilder()
				.insert(MetaSpellCard.TBL_NAME
					, new ColumnValuePair(MetaSpellCard.Name, s.getName())
					, new ColumnValuePair(MetaSpellCard.Power, s.getPower())
					, new ColumnValuePair(MetaSpellCard.Rarity, s.getRarity())
					, new ColumnValuePair(MetaSpellCard.Count, s.getCount())
					, new ColumnValuePair(MetaSpellCard.GetFlag, s.isGot())
					, new ColumnValuePair(MetaSpellCard.EquipmentFlag, s.isEquipped())
					, new ColumnValuePair(MetaSpellCard.CharacterId, s.getCharacterId())
					, new ColumnValuePair(MetaSpellCard.SeriesId, s.getSeriesId())
				)
				.toString();
	}
	
	
	private static HashMap<String, HashMap<Long, String>> _names;
	public static String getName(Context context, long id, String tableName) {
		if(_names == null){
			_names = new HashMap<String, HashMap<Long, String>>();
		}
		
		if(!_names.containsKey(tableName)) {
			_names.put(tableName, new DatabaseHelper(context).getHashMap(new Func1<Cursor, Long>(){
				@Override
				public Long call(Cursor x) {
					return x.getLong(x.getColumnIndex("Id"));
				}
			}, new Func1<Cursor, String>() {
				@Override
				public String call(Cursor x) {
					return x.getString(x.getColumnIndex("Name"));
				}
			}));
		}
		
		return _names.get(tableName).get(id);
	}
}