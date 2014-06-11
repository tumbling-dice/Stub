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

object AndroidExtentions {

	implicit class ContextExtention(val context: Context) {
		def isMainThread() = {
			Thread.currentThread().equals(context.getMainLooper().getThread())
		}

	}

	implicit class ViewExtention(val view: View) {

		def findViewOptional[T <: View : ClassTag](resourceId: Int) = view.findViewById(resourceId) match {
			case found if implicitly[ClassTag[T]].runtimeClass.isInstance(found) => Some(found.asInstanceOf[T])
			case _ => None
		}

		def findInternalView[T <: View : ClassTag](name: String, defType: String) = {
			view.findViewOptional[T](Resources.getSystem().getIdentifier(name, defType, "android"))
		}

	}

	implicit class ViewGroupExtention(val viewGroup: ViewGroup) {
		def flattenView() = for(i <- 0 until viewGroup.getChildCount()) yield viewGroup.getChildAt(i)

		def iterateView(): List[View] = {

			val buf: scala.collection.mutable.ListBuffer[View]
					= scala.collection.mutable.ListBuffer(viewGroup)

			for (v <- viewGroup.flattenView()) {
				v match {
					case vv: ViewGroup => buf ++= vv.iterateView()
					case _ => buf += v
				}
			}

			buf.toList
		}

	}

	implicit class ListViewExtention(val listView: ListView) {
		def getAdapterOptional[T <: ListAdapter]()(implicit c: ClassTag[T]) = listView.getAdapter() match {
			case null => None
			case adapter: WrapperListAdapter =>
				adapter.getWrappedAdapter() match {
					case a if c.runtimeClass.isInstance(a) =>
						Some(a.asInstanceOf[T])
					case _ => None
				}
			case adapter: ListAdapter =>
				adapter match {
					case a if c.runtimeClass.isInstance(a) =>
						Some(a.asInstanceOf[T])
					case _ => None
				}
		}

	}

	implicit class AdapterExtention[T](val adapter: Adapter) {
		def toSeq() = {
			for (i <- 0 until adapter.getCount()) yield adapter.getItem(i).asInstanceOf[T]
		}

	}

	implicit class FragmentExtention(val fragment: Fragment) {
		def getApplicationContext() = fragment.getActivity() match {
			case null => throw new IllegalStateException("Activityを取得できませんでした。")
			case activity => activity.getApplicationContext()
		}

		def setOnKeyDownListener(onKeyDownListener: (Int, KeyEvent) => Boolean): Unit = {
			fragment.getView() match {
				case null => throw new IllegalStateException("Fragment#onCreateViewが完了していないか、" +
					"FragmentにViewがセットされていません。")
				case v => fragment.setOnKeyDownListener(v, onKeyDownListener)
			}
		}

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

	implicit class ActionbarActivityExtention(val activity: Activity) {
		def moveToDownActionBar() {
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

		def getActionBarIconView() = {
			Build.VERSION.SDK_INT match {
				case ver if ver >= 11 =>
					activity.findViewById(android.R.id.home).asInstanceOf[ImageView]
				case _ =>
					activity.findViewById(R.id.home)
			}
		}
	}

	implicit class SQLiteOpenHelperExtention(val helper: SQLiteOpenHelper) {
		def transaction(context: Context, action: SQLiteDatabase => Unit) : Unit = {
			transaction(context, action, null)
		}

		def transaction(context: Context,
			action: SQLiteDatabase => Unit, onError: (Exception, SQLiteDatabase) => Unit): Unit = {

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

	implicit class CursorExtention(val cursor: Cursor) {
		def getDoubleByName(columnName: String) = cursor.getDouble(cursor.getColumnIndexOrThrow(columnName))
		def getFloatByName(columnName: String) = cursor.getFloat(cursor.getColumnIndexOrThrow(columnName))
		def getIntByName(columnName: String) = cursor.getInt(cursor.getColumnIndexOrThrow(columnName))
		def getLonByName(columnName: String) = cursor.getLong(cursor.getColumnIndexOrThrow(columnName))
		def getShortByName(columnName: String) = cursor.getShort(cursor.getColumnIndexOrThrow(columnName))
		def getStringByName(columnName: String) = cursor.getString(cursor.getColumnIndexOrThrow(columnName))
		def getTypeByName(columnName: String) = cursor.getType(cursor.getColumnIndexOrThrow(columnName))

		def map[T](mapper: Cursor => T): List[T] = {

			val buf = scala.collection.mutable.ListBuffer.empty[T]

			if (!cursor.moveToFirst()) return buf.toList

			do {
				buf += mapper(cursor)
			} while(cursor.moveToNext())

			buf.toList
		}
	}

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
}