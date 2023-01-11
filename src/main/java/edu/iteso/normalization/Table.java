package edu.iteso.normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Table implements Iterable<Row> {
    private final String name;
    private final List<String> header;
    private final Map<String, Integer> fieldIndex;
    private final List<Boolean> untitledFields;
    private final int hashCode;
    private Set<Row> data;
    private Map<Integer, Row> rowIndex;
    private Key primaryKey;
    private Map<Key, List<String>> dependencies;

    public Table(String name) {
        this.name = name;
        this.header = new ArrayList<String>();
        this.fieldIndex = new HashMap<>();
        this.data   = new HashSet<Row>();
        this.rowIndex = new HashMap<>();
        this.untitledFields = new ArrayList<Boolean>();
        this.primaryKey = Key.emptyKey();
        this.dependencies = new HashMap<>();
        this.hashCode = this.name.hashCode();
    }

//	public String toString() {
//		String s = this.name + "\n------------------\n";
//		for(int i = 0; i < header.size(); i ++) {
//			String f = header.get(i);
//			s += f;
//			if(primaryKey.contains(f)) s += " PK";
//			s += "\n";
//		}
//		return s + ", " + this.rows();
//	}

    public int hashCode() {
        return this.hashCode;
    }

    public String getName() {
        return this.name;
    }

    public void addDependency(Key A, int i) {
        addDependency(A, getFieldName(i));
    }

    public void addDependency(Key A, String B) {
        List<String> value = this.dependencies.get(A);
        if(value == null) value = new ArrayList<>();
        if(B != null) value.add(B);
        this.dependencies.put(A, value);
    }

    public boolean existsDependency(Key K) {
        return this.dependencies.containsKey(K);
    }

    public void addField(String field) {
        addField(field, true);
    }

    public void addField(String field, boolean isUntitled) {
        this.header.add(field);
        this.untitledFields.add(isUntitled);
        this.fieldIndex.put(field, this.header.size() - 1);
    }

    public int getFieldIndex(String field) {
        return this.fieldIndex.get(field);
    }

    public void addRow(Row row) {
        if(this.data.add(row)) this.rowIndex.put(this.data.size() - 1, row);
    }

//	public void moveFieldTo(String field, int newIndex) {
//		int currentIndex = this.getFieldIndex(field);
//		if(newIndex == currentIndex) return;
//		exchangeColumns(currentIndex, newIndex);
//	}
//
//	public void exchangeColumns(int f1, int f2) {
//		String field = this.header.get(f1);
//		this.header.set(f1, this.header.get(f2));
//		this.header.set(f2, field);
//
//		for(int r = 0; r < this.getRowCount(); r ++) {
//			Row row = this.data.get(r);
//			String tmp = row.get(f1);
//			row.set(f1, row.get(f2));
//			row.set(f2, tmp);
//		}
//	}

    public Map<Key, List<String>> getDependencies() {
        return this.dependencies;
    }

    public boolean contains(Row r) {
        return this.data.contains(r);
    }

    public Row get(int rowIndex) {
        return this.rowIndex.get(rowIndex);
    }

    public int rows() {
        return this.data.size();
    }

//	public String[] rowToArray(int row) {
//		if(row < 0 || row >= this.data.size()) return new String[] {""};
//		return this.data.get(row).toArray(row);
//	}
//
//	public void addToPrimaryKey(int field) {
//		this.primaryKey.addToKey(field, header.get(field));
//	}
//
//	public boolean isKey(int field) {
//		return this.primaryKey.contains(field);
//	}

    public String getFieldName(int index) {
        return this.header.get(index);
    }

    public Key getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(Key key) {
        this.primaryKey = key;
    }

//	public String at(int row, int col) {
//		if(row < 0 || row >= this.data.size() || col < 0 || col >= this.header.size()) return "INVALID INDEX";
//		return this.data.get(row).get(col);
//	}

//	public void addMarkedField(int index) {
//		this.markedFields.add(index);
//	}
//
//	public void clearMarkedFields() {
//		this.markedFields.clear();
//	}

    public int columns() {
        return this.header.size();
    }

//	public String getField(int index) {
//		return this.header.get(index);
//	}

    //	public boolean isFieldMarked(int index) {
//		return this.markedFields.contains(index);
//	}
//
//	public boolean isFieldMarked(String field) {
//		int fieldIndex = this.header.indexOf(field);
//		return isFieldMarked(fieldIndex);
//	}
//
    public boolean isFieldUntitled(int index) {
        return this.untitledFields.get(index);
    }

//	public void addComment(String c) {
//		comments.append(c);
//	}
//
//	public String getComments() {
//		return comments.toString();
//	}

    public static Table loadFromFile(File f) throws IOException {
        int extensionIndex = f.getName().toLowerCase().indexOf(".csv", 0);
        String tableName = f.getName().substring(0, extensionIndex);
        Table table = new Table(tableName);
        BufferedReader br = new BufferedReader(new FileReader(f));

        // La primera linea tiene los titulos de las columnas
        String line = br.readLine();

        // Guardar en un arreglo los datos de la primer fila, sabiendo que estan separados por comas
        // En cada posicion del arreglo se guarda el valor de una celda, excluyendo los espacios que lo rodean
        String[] cellsArray = line.trim().split("\\s*,\\s*");

        // Asignar nombre generico autonumerico a las columnas sin nombre
        // y a�adir los nombres de columnas a la tabla
        String untitledColumns = "";
        int untitledColumnsCount = 0;
        for(int i = 0, a = 1; i < cellsArray.length; i ++) {
            if(cellsArray[i].equals("")) {
                cellsArray[i] = "SIN_NOMBRE_" + (a ++);
                table.addField(cellsArray[i], true);
                if(untitledColumnsCount >= 1) untitledColumns += ", ";
                untitledColumns += (i + 1);
                untitledColumnsCount ++;
            } else table.addField(cellsArray[i], false);
        }

        // Revisar presencia de nombres de columnas repetidas
        String repeatedTitles = "";
        for(int i = 0; i < cellsArray.length; i ++) {
            if(cellsArray[i].equals("?")) continue;
            for(int j = i + 1; j < cellsArray.length; j ++) {
                if(cellsArray[i].equals(cellsArray[j])) {
                    if(!repeatedTitles.equals("")) repeatedTitles += ", ";
                    repeatedTitles += cellsArray[i];
                    break;
                }
            }
        }
//		if(!repeatedTitles.equals("")) table.addComment("Existen columnas con nombres repetidos: " + repeatedTitles);

        // Guardar el numero de columnas que tiene la fila de titulos
        // maxColumns guardara al final el numero de columnas de la fila que tiene mas columnas
        int maxColumns = cellsArray.length;

        // La segunda linea tiene las marcas para los atributos que no definen a otro
//		line = br.readLine();
//		String[] marksArray = line.split(",");
//		for(int i = 0; i < marksArray.length; i ++) {
//			if(!marksArray[i].equals("")) table.addMarkedField(i);
//		}

        // Por cada linea siguiente, agregar a la tabla una lista enlazada con los datos de la l�nea
        // Siguiendo la misma logica que cuando se añadieron los titulos de las columnas
        line = br.readLine();
        while(line != null) {
            if(line.trim().equals("")) {
                line = br.readLine();
                continue;
            }
            cellsArray = line.trim().split("\\s*,\\s*");
            Row row = new Row(cellsArray);
            if(row.size() > maxColumns) maxColumns = row.size();
            table.addRow(row);
            line = br.readLine();
        }

        // Añadir tantas titulos vacios a la fila de titulos para igualar en longitud a la fila con mas columnas
        int colsToAdd = maxColumns - table.columns();
        for(int c = 0; c < colsToAdd; c ++) {
            table.addField("UNNAMED_" + (++ untitledColumnsCount), true);
            if(untitledColumnsCount > 1) untitledColumns += ", ";
            untitledColumns += table.columns();
        }
        br.close();

        return table;
    }

    @Override
    public Iterator<Row> iterator() {
        return data.iterator();
    }

    public String toString() {
        String pk = this.getPrimaryKey() == Key.emptyKey()? "None" : this.getPrimaryKey().toString();
        String s = "*" + this.name + "*\nPK: " + pk + "\n";
        for(Row r: data) s += r + "\n";
        return s;
    }

    public Set<Table> normalize() {
        return Normalizer.normalize(this);
    }

}
