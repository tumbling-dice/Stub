public final class SQLiteExtentions {
	
	/**
	 * DBから要素を取得
	 * @param helper
	 * @param query Select文
	 * @param context
	 * @param f CursorからTを抽出するFunc
	 * @return 要素が見つからない場合はNull
	 */
	public static <T> T get(SQLiteOpenHelper helper, String query, Context cont, Func1<Cursor, T> f) {

		val db = helper.getReadableDatabase();

		if(!isMainThread(cont)) db.acquireReference();

		Cursor c = null;

		try {
			c = db.rawQuery(query, null);
		} catch(RuntimeException e) {
			db.close();
			throw e;
		}

		if(!c.moveToFirst()) {
			c.close();
			helper.close();
			return null;
		}

		T obj;

		try {
			obj = f.call(c);
		} finally {
			c.close();
			helper.close();
		}

		return obj;
	}

	/**
	 * DBからすべての要素を取得
	 * @param helper 
	 * @param query Select文
	 * @param context
	 * @param f CursorからTを抽出するFunc
	 * @return 要素が見つからない場合は空のリスト
	 */
	public static <T> List<T> getList(SQLiteOpenHelper helper, String query, Context cont, Func1<Cursor, T> f) {

		val dataList = new ArrayList<T>();

		val db = helper.getReadableDatabase();
		if(!isMainThread(cont)) db.acquireReference();

		Cursor c = null;
		try {
			c = db.rawQuery(query, null);
		} catch(RuntimeException e) {
			helper.close();
			throw e;
		}

		if(!c.moveToFirst()) {
			c.close();
			helper.close();
			return dataList;
		}

		try {
			do {
				dataList.add(f.call(c));
			} while (c.moveToNext());
		} finally {
			c.close();
			helper.close();
		}

		return dataList;
	}

	/**
	 * トランザクション処理
	 * @param helper
	 * @param context
	 * @param act 実行内容
	 */
	public static void transaction(SQLiteOpenHelper helper, Context cont, Action1<SQLiteDatabase> act) {
		val db = helper.getWritableDatabase();

		if(!isMainThread(cont)) db.acquireReference();

		db.beginTransaction();
		try {
			act.call(db);
			if(db.isOpen()) db.setTransactionSuccessful();
		} finally {
			if(db.isOpen()) {
				db.endTransaction();
				helper.close();
			}
		}
	}

	/**
	 * トランザクション処理
	 * @param helper
	 * @param context
	 * @param act 実行内容
	 * @param finallyAct finally節で実行する内容(endTransactionは必ず呼ばれる)
	 */
	public void transaction(SQLiteOpenHelper helper, Context cont
			, Action1<SQLiteDatabase> act, Action1<SQLiteDatabase> finallyAct) {

		val db = helper.getWritableDatabase();
		if(!isMainThread(cont)) db.acquireReference();

		db.beginTransaction();
		try {
			act.call(db);
			if(db.isOpen()) db.setTransactionSuccessful();
		} finally {
			finallyAct.call(db);
			if(db.isOpen()) {
				db.endTransaction();
				helper.close();
			}
		}
	}
	
	private static boolean isMainThread(Context cont) {
		return Thread.currentThread().equals(cont.getMainLooper().getThread());
	}
	
}