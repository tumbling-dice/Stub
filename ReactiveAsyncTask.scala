class ReactiveAsyncTask[Source, Return](val exec : Source => Option[Return]) extends AsyncTask[Source, Unit, Option[Return]] {
	
	private var exception : Exception = _
	
	private var onPrepare : Unit => Unit = _
	private var onSuccess : TSource => Unit = _
	private var onFailure : Unit => Unit = _
	private var onError : Exception => Unit = _
	private var onCancel : Unit => Unit = _
	
	def prepare(event : Unit => Unit) = {
		onPrepare = event
		this
	}
	
	def success(event : TSource => Unit) = {
		onSuccess = event
		this
	}
	
	def failure(event : Unit => Unit) = {
		onFailure = event
		this
	}
	
	def error(event : Exception => Unit) = {
		onError = event
		this
	}
	
	def cancel(event : Unit => Unit) = {
		onCancel = event
		this
	}
	
	override protected def onPreExecute() = {
		if(onPrepare != null) onPrepare()
	}
	
	override protected def doInBackground(args : Source*) = {
		try {
			exec(args[0])
		} catch {
			case e : Exception => 
				exception = e
				None
		}
	}
	
	override protected def onPostExecute(r : Option[Return]) = {
		if(exception != null) {
			if(onError != null) {
				onError(exception)
			} else {
				throw exception
			}
		}
		
		r match {
			case Some(v) =>
				if(onSuccess != null) onSuccess(v)
			case None =>
				if(onFailure != null) onFailure()
		}
	}
	
	override protected onCanceled() = {
		if(onCancel != null) onCancel()
	}
	
	def execute(param : Source) = {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
			super.execute(param)
		} else {
			super.super.executeOnExecutor(THREAD_POOL_EXECUTOR, param)
		}
	}
}
