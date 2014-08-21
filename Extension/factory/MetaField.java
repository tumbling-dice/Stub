package inujini_.sqlite.meta.factory;

import lombok.Data;

@Data
class MetaField {
	private String columnName;
	private int type;
	private boolean isNotNull;
	private boolean isPrimary;
	private boolean isAutoincrement;
	private boolean isUnique;
	private String indexName;
	private String defaultValue;
}
