public class LoadingListener<T, R> implements ViewTreeObserver.OnDrawListener {
	
	private LoadViewTask _task;
	private T _arg;
	private boolean _isOnDrawed;
	
	// LoadViewにイベントを持たせるふりをしつつ、実際にはAsyncTaskにイベントをそのまま渡す
    public LoadingListener<T, R> setOnBackground(Func1<T, R> event) {
        _task.setOnBackground(event);
        return this;
    }
     
    public LoadingListener<T, R> setOnPostExecute(Action1<R> callback) {
        _task.setOnPostExecute(callback);
        return this;
    }
     
    public LoadingListener<T, R> setOnError(Action1<Exception> onError) {
        _task.setOnError(onError);
        return this;
    }
     
    public LoadingListener<T, R> setArg(T arg) {
        _arg = arg;
        return this;
    }

	
	@Override
	public void onDraw() {
		if(!_isOnDrawed) {
			_isOnDrawed = true;
			new LoadViewTask().execute(_arg);
		}
	}
	
	// 実際の読み込み処理は非同期で行う
    private static class LoadViewTask extends AsyncTask<T, Void, R> {
         
        private Func1<T, R> _onBackground;
        private Action1<R> _onPostExecute;
        private Action1<Exception> _onError;
        private Exception _error;
         
        public void setOnBackground(Func1<T, R> event) {
            _onBackground = event;
        }
         
        public void setOnPostExecute(Action1<R> callback) {
            _onPostExecute = callback;
        }
         
        public void setOnError(Action1<Exception> onError) {
            _onError = onError;
        }
         
        @Override
        protected TReturn doInBackground(T... params) {
            if(_onBackground == null) return null;
             
            try {
                return _onBackground.call(params[0]);
            } catch(Exception e) {
                _error = e;
                return null;
            }
        }
         
        @Override
        protected void onPostExecute(R result) {
             
            if(_error != null) {
                if(_onError != null) {
                    _onError.call(_error);
                    return;
                } else {
                    throw new RuntimeException(_error);
                }
            }
             
            if(_onPostExecute != null) _onPostExecute.call(result);
        }
    }

}