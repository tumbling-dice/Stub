public class DynamicFragmentPagerAdapter extends PagerAdapter {
	
	protected FragmentManager _fm;
	protected FragmentTransaction _ft;
	protected Fragment _primaryItem;
	protected List<FragmentInfo> _fragments = new ArrayList<FragmentInfo>();
	
	protected final class FragmentInfo {
		private Fragment fragment;
		private String name;
		private boolean isNeedRemove;
		
		public FragmentInfo(String name, Fragment fragment) {
			this.name = name;
			this.fragment = fragment;
		}
		
		public String getName() {
			return this.name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Fragment getFragment() {
			return this.fragment;
		}
		public void setFragment(Fragment fragment) {
			this.fragment = fragment;
		}
		public boolean isNeedRemove() {
			return this.isNeedRemove;
		}
		public void setNeedRemove(boolean isNeedRemove) {
			this.isNeedRemove = isNeedRemove;
		}
		
	}
	
	public DynamicFragmentPagerAdapter(FragmentManager fm) {
		_fm = fm;
	}
	
	public DynamicFragmentPagerAdapter(FragmentManager fm, Map<String, Fragment> fragments) {
		_fm = fm;
		
		for(EntrySet<String, Fragment> entry : fragments) {
			_fragments.add(new FragmentInfo(entry.getKey(), entry.getValue(), false));
		}
	}
	
	@Override
	public void startUpdate(ViewGroup container) { }
	
	@Override
	public CharSequence getPageTitle(int position) {
		return _fragments.get(position).getName();
	}
	
	@Override
	public int getCount() {
		// PagerAdapter#getCountはPagerAdapter#destroyItemが呼ばれた後でも同値が返ってくる必要がある
		// @see: ViewPager#dataSetChanged
		
		int count = 0;
		
		// 削除フラグが立っているFragmentInfoはカウントしない
		for(FragmentInfo fi : _fragments) {
			if(!fi.isNeedRemove()) count++;
		}
		
		return count;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(_ft == null) _ft = _fm.beginTransaction();
		
		FragmentInfo fi = _fragments.get(position);
		String tag = container.getId() + fi.getName();
		
		// destroyItemでdetachされていたFragmentの場合はattachする
		Fragment f = _fm.findFragmentByTag(tag);
		if(f != null) {
			_fm.attach(f)
			return f;
		}
		
		f = fi.getFragment();
		
		if(!f.equals(_primaryItem)) {
			f.setMenuVisibility(false);
			f.setUserVisibleHint(false);
		}
		
		_ft.add(container.getId(), f, tag);
		
		return f;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// 本来はViewPager（container）の中からView（object）を取り除く処理っぽい
		// PagerAdapter#getItemPositionでPOSITION_NONEが返ってくるとこれが呼ばれる
		// と言うか、思っている以上にViewPagerの色んなところから呼ばれる
		
		// 厄介なのは「画面上で表示しきれなくなったobjectもここを通過する」と言う点だろう
		// その一点のためだけにFragmentPagerAdapterとFragmentStatePagerAdapterの処理が意味不明になっていると言っても過言ではない
		
		Fragment fragment = (Fragment)object;
		
		if(_ft == null) _ft = _fm.beginTransaction();
		
		// 削除フラグが立っている場合は_fragmentsから削除し、FragmentTransactionからも削除する
		if(_fragments.get(position).isNeedRemove()) {
			_fragments.remove(position);
			_ft.remove(fragment);
		} else {
			// 削除フラグが立っていない（画面から非表示になるだけetc）場合はdetachするだけにとどめる
			_ft.detach(fragment);
		}
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment)object;
		
		if(!fragment.equals(_primaryItem)) {
			
			if(_primaryItem != null) {
				_primaryItem.setMenuVisibility(false);
				_primaryItem.setUserVisibleHint(false);
			}
			
			fragment.setMenuVisibility(true);
			fragment.setUserVisibleHint(true);
			_primaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if(_ft != null) {
			_ft.commitAllowingStateLoss();
			_ft = null;
			_fm.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment)object).getView() == view;
	}

	@Override
	public Parcelable saveState() {
		Bundle state = new Bundle();
		
		for(int i = 0, size = _fragments.size(); i < size; i++) {
			FragmentInfo fi = _fragments.get(i);
			state.putString("name", fi.getName());
			state.putBoolean("isNeedRemove", fi.isNeedRemove());
			_fm.putFragment(state, "f" + i, fi.getFragment());
		}
		
		return state;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		if(state == null) return;
		
		Bundle bundle = (Bundle)state;
		bundle.setClassLoader(loader);
		
		for(String key : bundle.keySet()) {
			if(!key.startsWith("f")) continue; 
			
			int index = Integer.parseInt(key.substring(1));
			
			Fragment f = _fm.getFragment(bundle, key);
			FragmentInfo fi = new FragmentInfo(bundle.getString("name"), f);
			fi.setNeedRemove(bundle.getBoolean("isNeedRemove"));
			
			_fragments.set(index, fi);
		}
	}
	
	@Override
	public int getItemPosition(Object object) {
		Fragment f = (Fragment) object;
		
		for(FragmentInfo fi : _fragments) {
			if(fi.isNeedRemove() && fi.getFragment().equals(f)) {
				return POSITION_NONE;
			}
		}
		
		return POSITION_UNCHANGED;
	}
	
	public void add(String name, Fragment fragment) {
		_fragments.add(new FragmentInfo(name, fragment));
	}
	
	public Fragment get(int position) {
		return _fragments.get(position).getFragment();
	}
	
	public void remove(int position) {
		_fragments.get(position).setNeedRemove(true);
	}
	
	public void replace(int position, String name, Fragment fragment) {
		_fragments.get(position).setNeedRemove(true);
		_fragments.add(position + 1, new FragmentInfo(name, fragment));
	}
	
}