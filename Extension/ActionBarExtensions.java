@ExtensionMethod({MenuItemCompat.class})
public final class ActionBarExtensions {
	
	public static boolean collapseActionView(MenuItem item) {
		return item.collapseActionView();
	}
	
	public static boolean expandActionView(MenuItem item) {
		return item.expandActionView();
	}
	
	public static ActionProvider getActionProvider(MenuItem item) {
		return item.getActionProvider();
	}
	
	public static View getActionView(MenuItem item) {
		return item.getActionView();
	}
	
	public static boolean isActionViewExpanded(MenuItem item) {
		return item.isActionViewExpanded();
	}
	
	public static MenuItem setActionProvider(MenuItem item, ActionProvider provider) {
		return item.setActionProvider(provider);
	}
	
	public static MenuItem setActionView(MenuItem item, int resId) {
		return item.setActionView(resId);
	}
	
	public static MenuItem setActionView(MenuItem item, View view) {
		return item.setActionView(view);
	}
	
	public static MenuItem setOnActionExpandListener(MenuItem item, MenuItemCompat.OnActionExpandListener listener) {
		return item.setOnActionExpandListener(listener);
	}
	
	public static void setShowAsAction(MenuItem item, int actionEnum) {
		item.setShowAsAction(actionEnum);
	}
	
	public static void actionbarUpsideDown(ActionBarActivity activity) {
		val root = (ViewGroup) activity.getWindow().getDecorView();
		 
		View firstChild = root.getChildAt(0);
		 
		if (!(firstChild instanceof ViewGroup)) return;
		 
		//HONEYCOMB以前ならもうひとつ下のViewを取得する
		if(Build.VERSION.SDK_INT < 11) {
			firstChild = ((ViewGroup)firstChild).getChildAt(0);
		}
		 
		val actionBarContainerList = new ArrayList<View>();
		findActionBarContainer(root, actionBarContainerList);
		 
		if (actionBarContainerList.isEmpty()) return;
		 
		for (View innerView : actionBarContainerList) {
			firstChild.removeView(innerView);
		}
		 
		for (View innerView : actionBarContainerList) {
			firstChild.addView(innerView);
		}
	}
	
	private static void findActionBarContainer(View v, List<View> viewList) {
		val viewName = v.getClass().getName();
		
		if (viewName.equals("android.support.v7.internal.widget.ActionBarContainer")
			|| viewName.equals("com.android.internal.widget.ActionBarContainer")) {
			viewList.add(v);
		}
		
		if (v instanceof ViewGroup) {
			val g = (ViewGroup) v;
			 
			for (int i = 0, count = g.getChildCount(); i < count; i++) {
				findActionBarContainer(g.getChildAt(i), viewList);
			}
		}
	}
	
	
	
}