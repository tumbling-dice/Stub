class ListViewTestActivity extends Activity with OnItemClickListener {
	
	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.list_view_test)
		
		val listView = findViewOptional[ListView](R.id.listView).get
		val items = List("0", "1", "2")
		
		listView.setOnItemClickListener(this)
		listView.addFooterView(getFooter())
		listView.setAdapter(new ArrayAdapter[String](getApplicationContext(), R.layout.adapter_test, items))
		
	}
	
	def getFooter() = {
		val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
		inflater.inflate(R.layout.footer_test)
	}
	
	override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) = {
		val listView = findViewOptional[ListView](R.id.listView).get
		listView.getAdapterOptional[ArrayAdapter[String]] match {
			case Some(adapter): => adapter.toSeqItems[String].foreach(x -> getApplicationContext().showToastShort(x))
			case _ => getApplicationContext().showToastShort("this adapter is not ArrayAdapter.")
		}
	}
	
}