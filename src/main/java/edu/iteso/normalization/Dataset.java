package edu.iteso.normalization;

import java.util.*;

public class Dataset implements Iterable<String[]> {

    private final String name;
    private final List<String> fieldList;
    private final List<String[]> dataList = new ArrayList<>();

    public Dataset(String name) {
        this.name = name;
        this.fieldList = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public int rows() {
        return this.dataList.size();
    }

    public void add(String... data) {
        this.dataList.add(data);
    }

    public void setFields(String ... fields) {
        this.fieldList.clear();
        for(String f: fields) this.fieldList.add(f);
    }

    public int fields() {
        return this.fieldList.size();
    }

    public String getField(int index) throws IndexOutOfBoundsException {
        if(index < 0 || index >= this.fieldList.size()) throw new IndexOutOfBoundsException(index);
        return this.fieldList.get(index);
    }

    public String get(int row, int col) throws IndexOutOfBoundsException {
        if (row < 0 || row >= rows()) throw new IndexOutOfBoundsException(row);
        String[] data = this.dataList.get(row);
        if (col < 0 || col >= data.length) throw new IndexOutOfBoundsException(col);
        return data[col];
    }

    public void set(int row, int col, String value) throws IndexOutOfBoundsException {
        if (row < 0 || row >= rows()) throw new IndexOutOfBoundsException(row);
        String[] data = this.dataList.get(row);
        if (col < 0 || col >= data.length) throw new IndexOutOfBoundsException(col);
        data[col] = value;
    }

    /*
    public static Dataset loadFromFile(String filename) throws IOException {
        File f = new File(filename);
        int extensionIndex = f.getName().toLowerCase().indexOf(".csv", 0);
        String tableName = f.getName().substring(0, extensionIndex);
        BufferedReader br = new BufferedReader(new FileReader(f));

        // Encontrar el máximo número de columnas de la tabla: útil cuando el último campo es multivaluado
        int maxColumns = 0;
        String line;
        while((line = br.readLine()) != null) {
            if(line.trim().equals("")) continue;
            String[] array = line.trim().split("\\s*,\\s*");
            maxColumns = Math.max(maxColumns, array.length);
        }
        br.close();

        // Con la información de todos los campos de la tabla, ya podremos crear el objeto Dataset
        Dataset dataset = new Dataset(tableName);
        // Llenar el dataset con los datos del archivo, reiniciamos la lectura
        br = new BufferedReader(new FileReader(f));
        // La primera linea tiene los titulos de las columnas
        line = br.readLine();
        // Guardar en un arreglo los datos de la primer fila, sabiendo que estan separados por comas
        // En cada posicion del arreglo se guarda el valor de una celda, excluyendo los espacios que lo rodean
        // Asignar nombre genérico autonumérico a las columnas sin nombre
        String[] firstRow = line.trim().split("\\s*,\\s*");
        for(int i = 0, a = 0; i < maxColumns; i ++) {
            if(i >= firstRow.length || firstRow[i].equals("")) {
                dataset.setField(i, "untitled_" + (a ++));
                dataset.setUntitledField(i);
            } else {
                dataset.setField(i, firstRow[i]);
            }
        }

        // Leer el resto del archivo
        while((line = br.readLine()) != null) {
            String[] data = line.trim().split("\\s*,\\s*");
            dataset.add(data);
        }
        br.close();
        return dataset;
    }
    */

    @Override
    public Iterator<String[]> iterator() {
        return this.dataList.iterator();
    }

    @Override
    public String toString() {
        return String.format("{dataset-name=%s, row-count=%d, field-names=%s}", this.name, this.rows(), this.fieldList);
    }

}
