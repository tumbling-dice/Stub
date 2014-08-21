public final class ContextExtensions {
	
	public static <T extends Serializable> void saveSerializable(Context context, T obj, String path) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
 
		try {
			try {
				fos = context.openFileOutput(path, 0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
 
			try {
				if (fos != null) {
					oos = new ObjectOutputStream(fos);
					if (oos != null) oos.writeObject(obj);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} finally {
			if(fos != null) {
				try {
					if(oos != null) oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T loadSerializable(Context context, String path) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		T obj = null;
 
		try {
			try {
				fis = context.openFileInput(path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
 
			try {
				ois = new ObjectInputStream(fis);
				obj = (T) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
 
		} finally {
			if(fis != null) {
				try {
					if(ois != null) ois.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return obj;
	}
	
}