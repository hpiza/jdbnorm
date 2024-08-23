package edu.iteso.normalization;

import edu.iteso.database.Datatype;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Table implements Iterable<Row> {
    private final String name;
    private final List<String> header;
    private final Map<String, Integer> fieldIndex;
    private final List<Boolean> untitledFields;
    private final int hashCode;
    private final Set<Row> data;
    private final Map<Integer, Row> rowIndex;
    private Key primaryKey;
    private final Map<Key, List<String>> dependencies;

    private final Map<Key, String> foreignKeys;

    public Table(String name) {
        this.name = name;
        this.header = new ArrayList<>();
        this.fieldIndex = new HashMap<>();
        this.data   = new HashSet<>();
        this.rowIndex = new HashMap<>();
        this.untitledFields = new ArrayList<>();
        this.primaryKey = Key.emptyKey();
        this.dependencies = new HashMap<>();
        this.hashCode = this.name.hashCode();
        this.foreignKeys = new HashMap<>();
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        if(o instanceof Table t) {
            return this.name.equals(t.getName());
        }
        return false;
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

    public Map<Key, List<String>> getDependencies() {
        return this.dependencies;
    }

    public void addField(String field) {
        addField(field, false);
    }

    public void addFields(String ... fields) {
        for(String f: fields) addField(f);
    }

    public void addField(String field, boolean isUntitled) {
        this.header.add(field);
        this.untitledFields.add(isUntitled);
        this.fieldIndex.put(field, this.header.size() - 1);
    }

    public int getFieldIndex(String field) {
        if(!this.fieldIndex.containsKey(field)) return -1;
        return this.fieldIndex.get(field);
    }

    public String getFieldName(int index) {
        return this.header.get(index);
    }

    public boolean isFieldUntitled(int index) {
        return this.untitledFields.get(index);
    }

    public void addRow(Row row) {
        if(this.data.add(row)) this.rowIndex.put(this.data.size() - 1, row);
    }

    public void addRow(String... stringRow) {
        addRow(new Row(stringRow));
    }

    public Row getRow(int rowIndex) {
        return this.rowIndex.get(rowIndex);
    }
    public int rows() {
        return this.data.size();
    }

    public int columns() {
        return this.header.size();
    }

    public Key getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(Key key) {
        this.primaryKey = key;
    }

    public Iterator<Row> iterator() {
        return data.iterator();
    }

    public String toString() {
        String pk = this.getPrimaryKey() == Key.emptyKey()? "None" : this.getPrimaryKey().toString();
        StringBuilder s = Optional.ofNullable(String.format("{table-name=%s, primary-key=%s, fields=%s, dependencies=%s}\n", this.name, pk, this.header, this.dependencies)).map(StringBuilder::new).orElse(null);
        for(Row r: data) s = (s == null ? new StringBuilder("null") : s).append(r).append("\n");
        return s == null ? null : s + "---------------\n";
    }

    public void addForeignKey(Key key, String tableName) {
        this.foreignKeys.put(key, tableName);
    }

    public Set<Key> getForeignKeys() {
        return this.foreignKeys.keySet();
    }

    public String getForeignTableName(Key key) {
        return this.foreignKeys.get(key);
    }

    private Datatype[] datatypes;
    private int[] sizes;

    public Datatype getFieldDatatype(int fieldIndex) {
        if(datatypes == null) throw new RuntimeException("First call method discoverDatatypes()");
        if(fieldIndex < 0 || fieldIndex >= this.datatypes.length) throw new IllegalArgumentException("Field index is not valid: " + fieldIndex);
        return this.datatypes[fieldIndex];
    }

    public int getFieldSize(int fieldIndex) {
        if(sizes == null) throw new RuntimeException("First call method discoverDatatypes()");
        if(fieldIndex < 0 || fieldIndex >= this.datatypes.length) throw new IllegalArgumentException("Field index is not valid: " + fieldIndex);
        return this.sizes[fieldIndex];
    }

    public void discoverDatatypes() {
        this.datatypes = new Datatype[columns()];
        this.sizes = new int[columns()];
        for(int c = 0; c < columns(); c ++) {
            boolean isInt = true, isReal = true;
            int maxLength = 0;
            for(int r = 0; r < rows(); r ++) {
                String value = getRow(r).get(c);
                maxLength = Math.max(maxLength, value.length());
                if(!isReal) continue;
                try {
                    Double.parseDouble(value);
                } catch(Exception ex) {
                    isReal = false;
                }
                if(!isInt) continue;
                try {
                    Integer.parseInt(value);
                } catch(Exception ex) {
                    isInt = false;
                }
            }
            if(isInt) {
                datatypes[c] = Datatype.Integer;
                sizes[c] = 1;
            } else if(isReal) {
                datatypes[c] = Datatype.Real;
                sizes[c] = 1;
            } else {
                datatypes[c] = Datatype.Text;
                sizes[c] = maxLength % 5 == 0? maxLength: maxLength + 5 - maxLength % 5;
            }
        }
    }

    public static Table fromFile(String filename) throws IOException {
        File f = new File(filename);
        int extensionIndex = f.getName().toLowerCase().indexOf(".csv");
        String tableName = f.getName().substring(0, extensionIndex);
        Table table = new Table(tableName);
        BufferedReader br = new BufferedReader(new FileReader(f));

        // La primera linea tiene los titulos de las columnas
        String line = br.readLine();

        // Guardar en un arreglo los datos de la primer fila, sabiendo que estan separados por comas
        // En cada posicion del arreglo se guarda el valor de una celda, excluyendo los espacios que lo rodean
        String[] cellsArray = line.trim().split("\\s*,\\s*");

        // Asignar nombre generico autonumerico a las columnas sin nombre y añadir los nombres de columnas a la tabla
        int untitledColumnsCount = 0;
        for(int i = 0, a = 1; i < cellsArray.length; i ++) {
            if(cellsArray[i].equals("")) {
                cellsArray[i] = "UNTITLED_" + (a ++);
                table.addField(cellsArray[i], true);
                untitledColumnsCount ++;
            } else table.addField(cellsArray[i], false);
        }

        // Guardar el numero de columnas que tiene la fila de titulos
        // maxColumns guardara al final el numero de columnas de la fila que tiene mas columnas
        int maxColumns = cellsArray.length;

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
        // Añadir tantas titulos vacios a la fila de titulos para igualar en longitud a la fila con más columnas
        int colsToAdd = maxColumns - table.columns();
        for(int c = 0; c < colsToAdd; c ++) {
            table.addField("UNTITLED_" + (++ untitledColumnsCount), true);
        }
        br.close();
        return table;
    }

    public static void toCsvFile(Path dirPath, Table table) throws IOException {
        Path filePath = Paths.get(dirPath.toString(), table.getName() + ".csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile()));
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < table.columns(); i ++) {
            strBuilder.append(table.getFieldName(i));
            if(i < table.columns() - 1) strBuilder.append(",");
        }
        String str = strBuilder.toString();
        bw.append(str);
        bw.newLine();
        for(Row row: table) {
            StringBuilder strBuilder1 = new StringBuilder();
            for(int i = 0; i < row.size(); i ++) {
                strBuilder1.append(row.get(i));
                if(i < row.size() - 1) strBuilder1.append(",");
            }
            str = strBuilder1.toString();
            bw.append(str);
            bw.newLine();
        }
        bw.close();
    }
}
