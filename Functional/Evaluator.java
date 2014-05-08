final class Evaluator {
	
	private Collection<?> _items;
	private LinkedList<Collection<?>, Collection<?>> _functions;
	
	public Evaluator(Collection<?> items) {
		_items = items
		_functions = new LinkedList<Collection<?>, Collection<?>>();
	}
	
	public void add(Func1<Collection<?>, Collection<?>> function) {
		_functions.add(function);
	}
	
	public Collection<?> eval() {
		
		for(Func1<Collection<?>, Collection<?>> function : _functions) {
			_items = function.call(_items);
			if(_items.isEmpty()) return new ArrayList<?>();
		}
		
		return _items;
	}
}