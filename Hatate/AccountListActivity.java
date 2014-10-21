public class AccountListActivity extends ListActivity implements OnItemLongClickListener {
	
	private static final int REQ_ADD = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		val lv = super.getListView();
		val context = getApplicationContext();
		lv.setAdapter(new AccountAdapter(context, AccountDao.getAllAccount(context)));
		
		lv.setItemsCanFocus(false);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setOnItemLongClickListener(this);
	}
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
		val adapter = (AccountAdapter) getListAdapter();
		val item = (TwitterAccount) adapter.getItem(position);
		item.setUse(!item.isUse());
		AccountDao.update(item, getApplicationContext());
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		val adapter = (AccountAdapter) getListAdapter();
		val item = (TwitterAccount) adapter.getItem(position);
		
		new AlertDialog.Builder(this)
			.setTitle("確認")
			.setMessage(String.format("%sを削除します。よろしいですか？", item.getScreenName())
			.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					AccountDao.delete(item, getApplicationContext());
					adapter.remove(item);
				}
			}).setNegativeButton("キャンセル", new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}.create().show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_account_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_item_add:
			val intent = new Intent(getApplicationContext(), OauthActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra(OauthActivity.KEY_NEED_CALLBACK, true);
			startActivityForResult(intent, REQ_ADD);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK) return;
		
		if(requestCode == REQ_ADD) {
			val newAccount = (TwitterAccount) data.getSerializableExtra(OauthActivity.KEY_ACCOUNT);
			((AccountAdapter) getListView().getAdapter()).add(newAccount);
		}
	}

}