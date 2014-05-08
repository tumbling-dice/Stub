public final class Function {
	private Function() {}
	
	public interface IAction {}
	
	public interface Action implements IAction {
		void call();
	}
	
	public interface Action1<T> implements IAction {
		void call(T p);
	}
	
	public interface Action2<T1, T2> implements IAction {
		void call(T1 p1, T2 p2);
	}
	
	public interface Action3<T1, T2, T3> implements IAction {
		void call(T1 p1, T2 p2, T3 p3)
	}
	
	public interface Action4<T1, T2, T3, T4> implements IAction {
		void call(T1 p1, T2 p2, T3 p3, T4 p4);
	}
	
	public interface Action5<T1, T2, T3, T4, T5> implements IAction {
		void call(T1 p1, T2 p2, T3 p3, T4 p4, T5 p5);
	}
	
	public interface Actions implements IAction {
		void call(Object... args);
	}
	
	public interface IFunc {}
	
	public interface Func<R> implements IFunc {
		R call();
	}
	
	public interface Func1<T, R> implements IFunc {
		R call(T p);
	}
	
	public interface Func2<T1, T2, R> implements IFunc {
		R call(T1 p1, T2 p2);
	}
	
	public interface Func3<T1, T2, T3, R> implements IFunc {
		R call(T1 p1, T2 p2, T3 p3);
	}
	
	public interface Func4<T1, T2, T3, T4, R> implements IFunc {
		R call(T1 p1, T2 p2, T3 p3, T4 p4);
	}
	
	public interface Func5<T1, T2, T3, T4, T5, R> implements IFunc {
		R call(T1 p1, T2 p2, T3 p3, T4 p4, T5 p5);
	}
	
	public interface Funcs<R> implements IFunc {
		R call(Object... args);
	}
	
	public interface Predicate<T> implements Func1<T, Boolean> {
		Boolean call(T p1);
	}
}