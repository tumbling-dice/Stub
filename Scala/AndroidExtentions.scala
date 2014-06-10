object AndroidExtentions {
	implicit class ViewExtention(val v: View) {
		def findViewById[T](resourceId: Int) =  v.findViewById(resourceId) match {
			case t: T => Some(t)
			case _ => None
		}
		
		def findInternalView[T](name: String, defType: String) =
			v.findViewById[T](Resources.getSystem.getIdentifier(name, defType, "android"))
		
	}
	
	implicit class ActionbarExtention(val activity: ActionBarActivity) {
		
		def upsideDown() = {
			val root = (ViewGroup) activity.getWindow.getDecorView
			var firstChild = (ViewGroupt) root.getChildAt(0)
			
			if (Build.VERSION.SDK_INT < 11)
				firstChild = (ViewGroup) firstChild.getChildAt(0)
			
			val actionBarContainerList = findActionBarContainer(root)
			
			actionBarContainerList.foreach(v => firstChild.removeView(v))
			actionBarContainerList.foreach(v => firstChild.addView(v))
		}
		
		
		private def findActionBarContainer(v: View) = {
			
			val viewName = v.getClass.gatName
			
			if (viewName == "android.support.v7.internal.widget.ActionBarContainer"
				|| viewName == "com.android.internal.widget.ActionBarContainer") {
				yield v
			} else {
				v match {
					case vg: ViewGroup => 
						for (i <- 0 until g.getChildCount())
							yield findActionBarContainer(vg.getChildAt(i))
					case _ => yield None
				}
			}
			
		}
		
	}
}