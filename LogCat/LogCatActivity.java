public class LogCatActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		ProgressDialog prog = new ProgressDialog(this);
		prog.setTitle("読み込み中...");
		prog.setMessage("LogCatのデータを読み込んでいます...");
		prog.setCancelable(false);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		new ReactiveAsyncTask<String, Void, List<LogCatData>>(new R1<List<LogCat>, String>(){
				@Override
				public List<LogCat> call(String x) {
					try {
						return new LogCat(x).getLog();
					} catch(IOException e) {
						throw new RuntimeException(e);
					}
				}
			}).setOnPreExecute(new V1<Void>(){
				@Override
				public void call(Void arg0) {
					prog.show();
				}
			}).setOnPostExecute(new V1<List<LogCatData>>(){
				@Override
				public void call(List<LogCatData> x) {
					if(prog != null) prog.dismiss();
					ExpandableListView ex = (ExpandableListView) findViewById(R.id.exlLog);
					LogCatAdapter adapter = new LogCatAdapter(x, getApplicationContext());
					ex.setAdapter(adapter);
				}
			}).setOnError(new V1<Exception>(){
				@Override
				public void call(Exception e) {
					Toast.makeText(getApplicationContext(), "エラーが発生しました。", Toast.LENGTH_SHORT).show();
				}
			}).execute(LogCat.ERROR);
	}
}