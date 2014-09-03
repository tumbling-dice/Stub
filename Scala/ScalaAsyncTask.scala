abstract class ScalaAsyncTask[+Source, +Return] extends AsyncEvents[Source, Return] {
	
	def execute(arg: Source): Unit = {
		if(preExecute.isDefined) preExecute()
		
		val worker = new WorkerRunnable[Source, Return](arg) {
			@throws(classOf[Exception])
			override def call(): Option[Return] = {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
				background(this.arg)
			}
		}
		
		THREAD_POOL_EXECUTOR.execute(new FutureTask[Option[Return]](worker) {
			override def done(): Unit = {
				try {
					val message = get() match {
						Some(result) => sHandler.obtainMessage(MESSAGE_POST_RESULT, (this, result))
						None => sHandler.obtainMessage(MESSAGE_POST_RESULT_NONE, (this, None))
					}
					
					message.sendToTarget();
					
				} catch {
					case e: InterruptedException => android.util.Log.w(LOG_TAG, e)
					case e: ExecutionException =>
						onError match {
							None => throw new RuntimeException("An error occured while executing background", e.getCause())
							_ => sHandler.obtainMessage(MESSAGE_POST_RESULT_ERROR, (this, e.getCause())).sendToTarget()
						}
					case t: Throwable =>
						onError match {
							None => throw new RuntimeException("An error occured while executing background", t)
							_ => sHandler.obtainMessage(MESSAGE_POST_RESULT_ERROR, (this, t)).sendToTarget()
						}
				}
			}
		})
	}
	
}


object ScalaAsyncTask {
	private val LOG_TAG = "ScalaAsyncTask"
	private val MESSAGE_POST_RESULT = 1
	private val MESSAGE_POST_RESULT_NONE = 2
	private val MESSAGE_POST_RESULT_ERROR = 3
	
	private val sHandler = new InternalHandler(Looper.getMainLooper())
	
	val THREAD_POOL_EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR
	
	private class WorkerRunnable[+Source, +Return](val arg: Source) extends Callable[Option[Return]]
	
	private class InternalHandler extends Handler {
		override def handleMessage(msg: Message): Unit = msg.obj match {
			r: (task: ScalaAsyncTask, error: Throwable) if task.onError.isDefined => task.onError(error)
			r: (task: ScalaAsyncTask, data) => msg.what match {
				case MESSAGE_POST_RESULT if task.onSuccess.isDefined => task.onSuccess(data)
				case MESSAGE_POST_RESULT_NONE if task.onNone.isDefined => task.onNone()
				case _ => android.util.Log.i(LOG_TAG, "finished.")
			}
			_ => android.util.Log.i(LOG_TAG, "finished.")
		}
	}
	
}

trait AsyncEvents[+Source, +Return] {
	
	val background: Source => Option[Return]
	val preExecute: Option[Unit => Unit] = None
	val onSuccess: Option[Return => Unit] = None
	val onNone: Option[Unit => Unit] = None
	val onError: Option[Throwable => Unit] = None
	val onCancel: Option[Option[Return] => Unit]] = None
	
}