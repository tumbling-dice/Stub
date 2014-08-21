package inujini_.sqlite.meta.factory;

import inujini_.sqlite.meta.ISqlite;
import inujini_.sqlite.meta.annotation.SqliteField;
import inujini_.sqlite.meta.annotation.SqliteTable;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import lombok.Cleanup;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import lombok.Data;
import lombok.experimental.Accessors;

@SupportedAnnotationTypes({"inujini_.sqlite.meta.annotation.SqliteTable"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@ExtensionMethod({ APTExtensions.class, WriterExtensions.class })
public class PropertyFactory extends AbstractProcessor {
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		val messager = processingEnv.getMessager();

		try {
			// 処理対象となった型をすべて取得する
			val targets = ElementFilter.typesIn(roundEnv.getRootElements());
			
			for (val target : targets) {
				// クラスに含まれているアノテーション（SqliteTable）を取得する
				val tblAttr = target.getAnnotation(SqliteTable.class);
				if(tblAttr == null) continue;

				val metaTable = new MetaTable();
				metaTable.setTableName(tblAttr.value());
				metaTable.setHasPrimaryId(tblAttr.isPrimaryId());
				metaTable.setPackageName(target.getPackageName());

				// 中のFieldの情報を取得する
				val fieldElements = ElementFilter.fieldsIn(target.getEnclosedElements());
				val fields = new ArrayList<MetaField>();

				for (val fieldElement : fieldElements) {

					val fieldAttr = fieldElement.getAnnotation(SqliteField.class);
					if(fieldAttr == null) continue;

					val metaField = new MetaField();
					metaField.setAutoincrement(fieldAttr.autoincrement());
					metaField.setColumnName(fieldAttr.name());
					metaField.setDefaultValue(fieldAttr.defaultValue());
					metaField.setIndexName(fieldAttr.indexName());
					metaField.setNotNull(fieldAttr.notNull());
					metaField.setPrimary(fieldAttr.primary());
					metaField.setType(fieldAttr.type());
					metaField.setUnique(fieldAttr.unique());

					fields.add(metaField);
				}

				metaTable.setFields(fields);
				createMetaClass(metaTable);
				
			}
		} catch(Exception e) {
			messager.printMessage(Kind.ERROR, e.getMessage());
		}

		return true;
	}

	private void createMetaClass(MetaTable table) throws IOException {
		val className = String.format("Meta%s", table.getTableName());
		val fileName = String.format("%s.%s", table.getPackageName(), className);
		
		@Cleanup val writer = processingEnv.getFiler().createSourceFile(fileName).openWriter();
		
		writer.writeLineFormat("package %s;", table.getPackageName())
			.writeLine("\n")
			.writeLine("import inujini_.sqlite.meta.ColumnProperty;")
			.writeLine("import inujini_.sqlite.meta.ISqlite;")
			.writeLine("\n")
			.writeLineFormat("public final class %s {", className)
			.writeLine("\n")
			.writeLineFormat("\tpublic static final String TBL_NAME = \"%s\";", table.getTableName());
				
		for (val field : table.getFields()) {
			val property = new StringBuilder();

			String type = null;
			switch(field.getType()) {
				case ISqlite.FIELD_BLOB:
					type = "ISqlite.FIELD_BLOB";
					break;
				case ISqlite.FIELD_INTEGER:
					type = "ISqlite.FIELD_INTEGER";
					break;
				case ISqlite.FIELD_NULL:
					type = "ISqlite.FIELD_NULL";
					break;
				case ISqlite.FIELD_REAL:
					type = "ISqlite.FIELD_REAL";
					break;
				case ISqlite.FIELD_TEXT:
					type = "ISqlite.FIELD_TEXT";
					break;
				default:
					type = "ISqlite.FIELD_TEXT";
					break;
			}

			property.appendString(field.getColumnName()).append(", ")
				.append(type).append(", ")
				.append(field.isNotNull()).append(", ")
				.append(field.isPrimary()).append(", ")
				.append(field.isAutoincrement()).append(", ")
				.append(field.isUnique()).append(", ")
				.appendString(field.getIndexName()).append(", ")
				.appendString(field.getDefaultValue());

			writer.writeLineFormat("\tpublic static final ColumnProperty %s = new ColumnProperty(", field.getColumnName())
				.writeLineFormat("\t\t\t%s);", property.toString());
		}
				
		writer.writeLine("\tpublic static boolean hasPrimaryId() {")
			.writeLineFormat("\t\treturn %s;", (table.isPrimaryId() ? "true" : "false"))
			.writeLine("\t}");
		
		writer.writeLine("}");

		writer.flush();
	}
	
	final class APTExtensions {
		
		static String getPackageName(TypeElement element) {
			String className = element.toString();
			int pos = className.lastIndexOf(".");
			return className.substring(0, pos) + ".meta";
		}
	}

	final class WriterExtensions {
		
		static Writer writeLine(Writer writer, String s) {
			writer.write(s);
			writer.write("\n");
			
			return writer;
		}
		
		static Writer writeFormat(Writer writer, String s, Object... params) {
			return writer.write(String.format(s, params));
		}
		
		static Writer writeLineFormat(Writer writer, String s, Object... params) {
			writeFormat(writer, s, params).write("\n");
			return writer;
		}
		
		static StringBuilder appendString(StringBuilder sb, String v) {
			return sb.append("\"").append(v).append("\"");
		}
		
	}

}
