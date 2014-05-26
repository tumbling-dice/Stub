private WeakHashMap<String, List<Field>> _killList;

@Override
protected void onDestroy() {
	super.onDestroy();
	
	View v = findViewById(R.id.root);
	if(v == null) return;
	
	_killList = new WeakHashMap<String, List<Field>>();
	killThemAll(v);
	_killList.clear();
	_killList = null;
}

private void killThemAll(View v) {
	Class c = v.getClass();
	String killClassName = c.getName();
	
	if(!_killList.containsKey(killClassName)) {
		Field[] fields = c.getDeclaredFields();
		
		for(Field f : fields) {
			try {
				f.setAccessible(true);
				f.set(v, null);
			} catch(SecurityException e) {
				
			} catch(IllegalArgumentException e) {
				
			} catch(IllegalAccessException e) {
				
			}
		}
		
		_killList.put(killClassName, Arrays.asList(fields));
	} else {
		for(Field f : _killList.get(killClassName)) {
			try {
				f.setAccessible(true);
				f.set(v, null);
			} catch(SecurityException e) {
				
			} catch(IllegalArgumentException e) {
				
			} catch(IllegalAccessException e) {
				
			}
		}
	}
	
	if(v instanceof ViewGroup) {
		while(v.getChildCount() != 0) {
			View nextView = v.getChildAt(0);
			killThemAll(nextView);
			v.removeView(nextView);
			nextView = null;
		}
	}
	
}