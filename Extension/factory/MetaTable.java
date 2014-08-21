package inujini_.sqlite.meta.factory;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data class MetaTable {
	private String tableName;
	@Accessors(prefix = "has") private boolean hasPrimaryId;
	private List<MetaField> fields;
	private String packageName;
}
