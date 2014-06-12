import android.widget.ImageView
import java.io.FileInputStream
import android.view.ViewGroup
import android.os.Build
import java.io.FileOutputStream
import android.widget.ListAdapter
import java.io.ObjectInputStream
import android.database.sqlite.SQLiteOpenHelper
import scala.reflect.ClassTag
import java.io.ObjectOutputStream
import android.database.sqlite.SQLiteDatabase
import android.widget.Adapter
import android.widget.ListView
import android.view.View.OnKeyListener
import android.view.KeyEvent
import android.view.View
import android.content.Context
import android.support.v4.app.Fragment
import android.R
import android.database.Cursor
import android.content.res.Resources
import android.app.Activity
import android.widget.WrapperListAdapter

/**
 * Androidで使用するimplicit classes
 */
object AndroidExtentions {

	/**
	 * Context's Method Extentions
	 */
	implicit class ContextExtention(val context: Context) {
		/**
		 * 実行中のスレッドがメインスレッド（UIスレッド）かどうかを調べます
		 *
		 * @return メインスレッドならばtrue
		 */
		def isMainThread() {
			Thread.currentThread().equals(context.getMainLooper().getThread())
		}
		
		/**
		 * [[android.widget.Toast]]を表示します
		 *
		 * @param s 表示する文字列
		 * @see [[android.widget.Toast]]
		 */
		def showToastShort(s: String) = {
			Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
		}
		
		/**
		 * [[android.widget.Toast]]を表示します
		 *
		 * @param s 表示する文字列
		 * @see [[android.widget.Toast]]
		 */
		def showToastLong(s: String) = {
			Toast.makeText(context, s, Toast.LENGTH_LONG).show()
		}
	}

	/**
	 * View's Method Extentions
	 */
	implicit class ViewExtention(val view: View) {

		/**
		 * View#findViewByIdの結果をOptionで返します
		 *
		 * @param resourceId Viewのid
		 * @tparam T Viewを継承した型
		 * @return findViewByIdの結果がnullでなく、返却値の型がTだった場合のみ値を返す
		 */
		def findViewOptional[T <: View : ClassTag](resourceId: Int) = view.findViewById(resourceId) match {
			case found if implicitly[ClassTag[T]].runtimeClass.isInstance(found) => Some(found.asInstanceOf[T])
			case _ => None
		}

		/**
		 * 内部リソースを取得します
		 * 
		 * @param name リソース名
		 * @param defType 種類名 ex.) layout, id
		 * @tparam T Viewを継承した型
		 * @return 指定されたリソース
		 */
		def findInternalView[T <: View : ClassTag](name: String, defType: String) = {
			view.findViewOptional[T](Resources.getSystem().getIdentifier(name, defType, "android"))
		}
		
		/**
		 * Viewに含まれている子Viewを列挙します
		 * 
		 * @param callback 列挙されたViewに対する処理
		 */
		def findViews(callback: PartialFunction[(Int, View), Unit]) = view match {
			case root: ViewGroup => for(v <- root.iterateView()) callback((v.getId(), v))
		}
		
		/**
		 * Viewに含まれている子Viewを列挙します
		 * 
		 * @param rootViewId RootとするViewのid
		 * @param callback 列挙されたViewに対する処理
		 */
		def findViews(rootViewId: Int, callback: PartialFunction[(Int, View), Unit]) = 
		view.findViewOptional[ViewGroup](rootViewId) match {
			case Some(root) => for(v <- root.iterateView()) callback((v.getId(), v))
		}

	}

	/**
	 * ViewGroup's Method Extentions
	 */
	implicit class ViewGroupExtention(val viewGroup: ViewGroup) {
		
		/**
		 * ViewGroupが直下に所持しているViewを列挙します
		 * 
		 * @return ViewGroupが所持しているViewのSeq
		 */
		def flattenView() = for(i <- 0 until viewGroup.getChildCount()) yield viewGroup.getChildAt(i)

		/**
		 * ViewGroupが所持しているViewをすべて展開し、列挙します
		 * 
		 * @return ViewGroupが所持しているすべてのViewのSeq
		 */
		def iterateView(): List[View] = {
			val buf: scala.collection.mutable.ListBuffer[View]
					= scala.collection.mutable.ListBuffer(viewGroup)

			for (v <- viewGroup.flattenView()) {
				v match {
					case vg: ViewGroup => buf ++= vg.iterateView()
					case _ => buf += v
				}
			}

			buf.toList
		}
	}
	
	/**
	 * ListView's Method Extentions
	 */
	implicit class ListViewExtention(val listView: ListView) {
		
		/**
		 * ListViewが所持しているListAdapterを安全に取得します
		 *
		 * @tparam T ListAdapterを継承している型
		 * @return ListViewが所持しているListAdapter
		 */
		def getAdapterOptional[T <: ListAdapter : ClassTag]() = listView.getAdapter() match {
			case null => None
			case adapter: WrapperListAdapter =>
				adapter.getWrappedAdapter() match {
					case a if implicitly[ClassTag[T]].runtimeClass.isInstance(a) =>
						Some(a.asInstanceOf[T])
					case _ => None
				}
			case adapter: ListAdapter =>
				adapter match {
					case a if implicitly[ClassTag[T]].runtimeClass.isInstance(a) =>
						Some(a.asInstanceOf[T])
					case _ => None
				}
		}

	}

	/**
	 * Adapter's Method Extentions
	 */
	implicit class AdapterExtention(val adapter: Adapter) {
		
		/**
		 * 所持しているItemを列挙します
		 * 
		 * @return Adapterが所持しているすべてのItem
		 */
		def toSeqItems[T]() {
			for (i <- 0 until adapter.getCount()) yield adapter.getItem(i).asInstanceOf[T]
		}

	}
	
	/**
	 * Activity's Method Extentions
	 */
	implicit class ActivityExtention(val activity: Activity) {
		
		/**
		 * Activity#findViewByIdの結果をOptionで返します
		 *
		 * @param resourceId Viewのid
		 * @tparam T Viewを継承した型
		 * @return findViewByIdの結果がnullでなく、返却値の型がTだった場合のみ値を返す
		 */
		def findViewOptional[T <: View : ClassTag](resourceId: Int) = activity.findViewById(resourceId) match {
			case found if implicitly[ClassTag[T]].runtimeClass.isInstance(found) => Some(found.asInstanceOf[T])
			case _ => None
		}
		
		/**
		 * 内部リソースを取得します
		 * 
		 * @param name リソース名
		 * @param defType 種類名 ex.) layout, id
		 * @tparam T Viewを継承した型
		 * @return 指定されたリソース
		 */
		def findInternalView[T <: View : ClassTag](name: String, defType: String) = {
			activity.findViewOptional[T](Resources.getSystem().getIdentifier(name, defType, "android"))
		}
		
		/**
		 * Activityに含まれているViewを列挙します
		 * 
		 * @param callback 列挙されたViewに対する処理
		 */
		def findViews(callback: PartialFunction[(Int, View), Unit]) = {
			val root = activity.getWindow().getDecorView().asInstanceOf[ViewGroup]
			for(v <- root.iterateView()) callback((v.getId(), v))
		}
		
		/**
		 * Activityに含まれているViewを列挙します
		 * 
		 * @param rootViewId RootとするViewのid
		 * @param callback 列挙されたViewに対する処理
		 */
		def findViews(rootViewId: Int, callback: PartialFunction[(Int, View), Unit]) = 
		activity.findViewOptional[ViewGroup](rootViewId) match {
			case Some(root) => for(v <- root.iterateView()) callback((v.getId(), v))
		}
		
	}

	/**
	 * Fragment's Method Extentions
	 */
	implicit class FragmentExtention(val fragment: Fragment) {
		
		/**
		 * Application Contextを取得します
		 * 
		 * @return Application Context
		 * @throw IllegalStateException Activityが取得できなかった場合に発生
		 */
		def getApplicationContext() = fragment.getActivity() match {
			case null => throw new IllegalStateException("Activityを取得できませんでした。")
			case activity => activity.getApplicationContext()
		}
		
		/**
		 * Fragmentがタッチされたときのイベントを設定します
		 * 
		 * @param onKeyDownListener イベントリスナー
		 * @throw IllegalStateException Fragment#onCreateViewが完了していない場合に発生
		 */
		def setOnKeyDownListener(onKeyDownListener: (Int, KeyEvent) => Boolean): Unit = {
			fragment.getView() match {
				case null => throw new IllegalStateException("Fragment#onCreateViewが完了していないか、" +
					"FragmentにViewがセットされていません。")
				case v => fragment.setOnKeyDownListener(v, onKeyDownListener)
			}
		}
		
		/**
		 * Fragmentがタッチされたときのイベントを設定します
		 * 
		 * @param view Fragmentが内部で所持しているView
		 * @param onKeyDownListener イベントリスナー
		 */
		def setOnKeyDownListener(view: View, onKeyDownListener: (Int, KeyEvent) => Boolean) = {
			view.setOnKeyListener(new OnKeyListener() {
				override def onKey(v: View, keyCode: Int, event: KeyEvent) = {
					event.getAction() match {
						case KeyEvent.ACTION_DOWN => onKeyDownListener(keyCode, event)
						case _ => false
					}
				}
			})

			view.setFocusableInTouchMode(true)
		}

	}
	
	/**
	 * ActionBarActivity's Method Extentions
	 */
	implicit class ActionbarActivityExtention(val activity: Activity) {
		
		/**
		 * ActionBarを画面下に移動します
		 */
		def moveToDownActionBar() = {
			val root = activity.getWindow().getDecorView().asInstanceOf[ViewGroup]
			val firstChild = root.getChildAt((if (Build.VERSION.SDK_INT >= 11) 0 else 1)).asInstanceOf[ViewGroup]
			val views = root.iterateView()

			def actionBarContainerFilter(v: View) = {
				val viewName = v.getClass().getName()
				viewName == "android.support.v7.internal.widget.ActionBarContainer" ||
				viewName == "com.android.internal.widget.ActionBarContainer"
			}

			views.filter(actionBarContainerFilter).foreach(v => firstChild.removeView(v))
			views.filter(actionBarContainerFilter).foreach(v => firstChild.addView(v))
		}
		
		/**
		 * ActionBarのIconを表示しているImageViewを取得します
		 * 
		 * @return ActionBarのIcon
		 */
		def getActionBarIconView() = Build.VERSION.SDK_INT match {
			case ver if ver >= 11 =>
				activity.findViewById(android.R.id.home).asInstanceOf[ImageView]
			case _ =>
				activity.findViewById(R.id.home).asInstanceOf[ImageView]
		}
		
	}

	/**
	 * SQLiteOpenHelper's Method Extentions
	 */
	implicit class SQLiteOpenHelperExtention(val helper: SQLiteOpenHelper) {
		
		/**
		 * トランザクション処理を開始します
		 * 
		 * @param context
		 * @action DB操作内容
		 */
		def transaction(context: Context, action: SQLiteDatabase => Unit) : Unit = transaction(context, action, null)

		/**
		 * トランザクション処理を開始します
		 * 
		 * @param context
		 * @action DB操作内容
		 * @onError DB操作中に例外が発生した場合の処理
		 */
		def transaction(context: Context,
			action: SQLiteDatabase => Unit, onError: (Exception, SQLiteDatabase) => Unit) : Unit = {

			val db = helper.getWritableDatabase()

			if (!context.isMainThread()) db.acquireReference()

			db.beginTransaction()

			try {
				action(db)
				if (db.isOpen()) db.setTransactionSuccessful()
			} catch {
				case e: Exception => onError match {
					case null => throw e
					case _ => onError(e, db)
				}
			} finally {
				if (db.isOpen()) {
					db.endTransaction()
					db.close()
				}
			}
		}
	
		/**
		 * DBからデータを取得します
		 * 
		 * @param context
		 * @param query SQL
		 * @param mapper
		 * @tparam T 取得するデータの型
		 * @return 取得したデータのList
		 */
		def select[T](context: Context, query: String, mapper: Cursor => T) = {
			val db = helper.getReadableDatabase()

			if(!context.isMainThread()) db.acquireReference()

			def getCursor() = {
				try {
					db.rawQuery(query, null)
				} catch {
					case e: Exception =>
						db.close()
						throw e
				}
			}

			val cursor = getCursor()

			try {
				cursor.map(mapper)
			} catch {
				case e: Exception => throw e
			} finally {
				db.close()
				cursor.close()
			}
		}
	}
	
	/**
	 * Cursor's Method Extentions
	 */
	implicit class CursorExtention(val cursor: Cursor) {
		
		def getDoubleByName(columnName: String) = cursor.getDouble(cursor.getColumnIndexOrThrow(columnName))
		def getFloatByName(columnName: String) = cursor.getFloat(cursor.getColumnIndexOrThrow(columnName))
		def getIntByName(columnName: String) = cursor.getInt(cursor.getColumnIndexOrThrow(columnName))
		def getLonByName(columnName: String) = cursor.getLong(cursor.getColumnIndexOrThrow(columnName))
		def getShortByName(columnName: String) = cursor.getShort(cursor.getColumnIndexOrThrow(columnName))
		def getStringByName(columnName: String) = cursor.getString(cursor.getColumnIndexOrThrow(columnName))
		def getTypeByName(columnName: String) = cursor.getType(cursor.getColumnIndexOrThrow(columnName))

		/**
		 * Cursorからデータを取得
		 * 
		 * @param mapper
		 * @tparam T 取得するデータの型
		 * @return 取得したデータのList
		 */
		def map[T](mapper: Cursor => T): List[T] = {

			val buf = scala.collection.mutable.ListBuffer.empty[T]

			if (!cursor.moveToFirst()) return buf.toList

			do {
				buf += mapper(cursor)
			} while(cursor.moveToNext())

			buf.toList
		}
	}
	
	/*
	implicit class SerializableExtention(val serializable: Serializable) {
		def save(path: String, context: Context, onError: PartialFunction[Exception, Unit]) = {
			var fos: FileOutputStream = null
			var oos: ObjectOutputStream = null

			try {
				fos = context.openFileOutput(path, 0)
				oos = new ObjectOutputStream(fos)
				oos.writeObject(serializable)
			} catch {
				case e: Exception => onError(e)
			} finally {
				if (fos != null) {
					if (oos != null) oos.close()
					fos.close()
				}
			}
		}

		def load(path: String, context: Context, onError: PartialFunction[Exception, Unit]) = {
			var fis: FileInputStream = null
			var ois: ObjectInputStream = null

			try {
				fis = context.openFileInput(path)
				ois = new ObjectInputStream(fis)
				ois.readObject()
			} catch {
				case e: Exception => onError(e)
			} finally {
				if (fis != null) {
					if (ois != null) ois.close()
					fis.close()
				}
			}

		}
	}
	*/
}