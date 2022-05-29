
public interface OnValueFormatParse<T> {
	
	public T onDataParse(String... string);
	
	public void onDataCollect(String data);
	
}
