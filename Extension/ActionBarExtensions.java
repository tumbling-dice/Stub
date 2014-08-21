public final class ActionBarExtensions {
	
	public static boolean collapseActionView(MenuItem item) {
		return MenuItemCompat.collapseActionView(item);
	}
	
	public static boolean expandActionView(MenuItem item) {
		return MenuItemCompat.expandActionView(item);
	}
	
	public static ActionProvider getActionProvider(MenuItem item) {
		return MenuItemCompat.getActionProvider(item);
	}
	
	public static View getActionView(MenuItem item) {
		return MenuItemCompat.getActionView(item);
	}
	
	public static boolean isActionViewExpanded(MenuItem item) {
		return MenuItemCompat.isActionViewExpanded(item);
	}
	
	public static MenuItem setActionProvider(MenuItem item, ActionProvider provider) {
		return MenuItemCompat.setActionProvider(item, provider);
	}
	
	public static MenuItem setActionView(MenuItem item, int resId) {
		return MenuItemCompat.setActionView(item, resId);
	}
	
	public static MenuItem setActionView(MenuItem item, View view) {
		return MenuItemCompat.setActionView(item, view);
	}
	
	public static MenuItem setOnActionExpandListener(MenuItem item, MenuItemCompat.OnActionExpandListener listener) {
		return MenuItemCompat.setOnActionExpandListener(item, listener);
	}
	
	public static void setShowAsAction(MenuItem item, int actionEnum) {
		MenuItemCompat.setShowAsAction(item, actionEnum);
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