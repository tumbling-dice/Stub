class ActivityTest extends Activity {
	
	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		moveToDownActionBar()
		setContentView(R.layout.activity_test)
		
		findViews(_ match {
			case (R.id.textView1, v: TextView) => v.setText("TextView1 is found.")
			case (R.id.textView2, v: TextView) => v.setText("TextView2 is found.")
			case (R.id.textView3, v: TextView) => v.setText("TextView3 is found.")
			case (R.id.button1, v: Button) =>
				v.setOnClickListener(new OnClickListener() {
					override def onClick(v: View) = {
						findViews(R.id.innerLinearLayout, _ match {
							case (R.id.textView2, v: TextView) => v.setText("TextView2 is changed.")
						})
					}
				})
		})
		
	}
	
}