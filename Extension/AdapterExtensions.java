public final class AdapterExtensions {
	
	public static final class AdapterToIterable<T> implements Iterable<T>, Iterator<T> {
		
		private int _position;
		private int _maxCount;
		private Adapter _adapter;
		
		public AdapterToIterable(Adapter adapter) {
			_adapter = adapter;
			_maxCount = !adapter.isEmpty() ? adapter.getCount() : 0;
		}
		
		@Override
		public Iterator<T> iterator() {
			return this;
		}
		
		@Override
		public boolean hasNext() {
			return _position < _maxCount;
		}
		
		@Override
		public T next() {
			return (T) _adapter.getItem(_position++);
		}
		
	}
	
	public static <T> AdapterToIterable<T> toIterable(Adapter adapter) {
		return new AdapterToIterable<T>(adapter);
	}
	
}