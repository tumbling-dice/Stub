public final class SparseArrayToIterable<T> implements Iterable<Pair<Integer, T>>, Iterator<Pair<Integer, T>> {
	
	private final SparseArray<T> _sparseArray;
	private final int _maxCount;
	private int _position = 0;
	
	public SparseArrayToIterable(SparseArray<T> sparseArray) {
		_sparseArray = sparseArray;
		_maxCount = sparseArray.getSize();
	}
	
	@Override
    public Iterator<Pair<Integer, T>> iterator() {
        return this;
    }
	
	@Override
    public boolean hasNext() {
        return _position < _maxCount;
    }
	
	@Override
    public Pair<Integer, T> next() {
    	Integer key = _sparseArray.keyAt(_position++);
    	return Pair.create(key, _sparseArray.get(key));
    }
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static <T> SparseArrayToIterable<T> toIterable(SparseArray<T> sparseArray) {
		return new SparseArrayToIterable<T>(sparseArray);
	}
	
}