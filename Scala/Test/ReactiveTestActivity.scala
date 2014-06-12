class ReactiveTestActivity extends Activity {

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.reactive_test)
	}
	
	def start(v: View) = {
		var prog = new ProgressDialog(this)
		prog.setTitle("test")
		prog.setMessage("Test...")
		
		val task = new ReactiveAsyncTask[String, String](x -> {
			if(!getApplicationContext().isMainThread) {
				Some(x)
			} else {
				None
			}
		}).prepare(prog)
		.success(x -> getApplicationContext().showToastShort(s))
		.failure(() -> getApplicationContext().showToastShort("None."))
		.error(e -> getApplicationContext().showToastShort("Error."))
		.cancel(() -> getApplicationContext().showToastShort("Cancel."))
		
		task.execute("Success.")
	}
	
}