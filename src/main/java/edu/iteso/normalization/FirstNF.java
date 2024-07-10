package edu.iteso.normalization;

import java.util.*;

public class FirstNF extends Normalizer {

    @Override
    public Database normalize(Table table) {
        if(isNormalized(table).isNormalized) return new Database(table);

        Table table1FN = new Table(table.getName());
        Set<String> fieldSet = new HashSet();

//		Añadir a la nueva tabla todos los campos con título de la tabla origen
//		y registrar el inicio y el fin de cada atributo multivaluado
//		Añadir un sufijo autonumérico a los atributos repetidos
        int first = -1;
        int end = -1;
        int duplicateIndex = 1;
        ArrayList<Pair<Integer, Integer>> multiValuedAtt = new ArrayList<Pair<Integer, Integer>>();
        for(int f = 0; f < table.columns(); f ++) {
            if(table.isFieldUntitled(f)) {
                if(first < 0) {
                    first = f - 1;
                    end = -1;
                }
            }
            else {
                if(first >= 0) {
                    end = f - 1;
                    multiValuedAtt.add(new IntPair(first, end));
                    first = -1;
                }
                String name = table.getFieldName(f);
                if(fieldSet.contains(name)) {
                    String newName = String.format("%s_%d", name, duplicateIndex);
                    while(fieldSet.contains(newName)) {
                        duplicateIndex ++;
                        newName = String.format("%s_%d", name, duplicateIndex);
                    }
                    fieldSet.add(newName);
                    table1FN.addField(newName);
                } else {
                    fieldSet.add(name);
                    table1FN.addField(name);
                }
            }
        }
        if(first >= 0 && end < 0) multiValuedAtt.add(new IntPair(first, table.columns() - 1));

        for(Row currentRow: table) {
            if(multiValuedAtt.isEmpty()) {
                table1FN.addRow(currentRow.clone());
                continue;
            }
            Row newRow = new Row();
            for(int i = 0; i < table.columns(); i ++) {
                if(!table.isFieldUntitled(i)) newRow.add(currentRow.get(i));
            }
            table1FN.addRow(newRow);
            int firstIndex = table1FN.rows() - 1;
            int targetCol = multiValuedAtt.get(0).getFirst();
            for(int mv = 0; mv < multiValuedAtt.size(); mv ++) {
                int startMV = multiValuedAtt.get(mv).getFirst();
                int endMV   = multiValuedAtt.get(mv).getSecond();
                if(mv > 0) targetCol += startMV - multiValuedAtt.get(mv - 1).getSecond();
                int lastIndex = table1FN.rows() - 1;
                for(int i = startMV + 1; i <= endMV; i ++) {
                    String value = currentRow.get(i);
                    if(value == null || value.equals("")) continue;
                    for(int j = firstIndex; j <= lastIndex; j ++) {
                        //Row veryCurrentRow = table.getRow(j);
                        Row veryCurrentRow = table1FN.getRow(j);  // ¡¡Error!!
                        Row veryNewRow = new Row();
                        for(int k = 0; k < table1FN.columns(); k ++) {
                            if(k == targetCol) veryNewRow.add(value);
                            else veryNewRow.add(veryCurrentRow.get(k));
                        }
                        table1FN.addRow(veryNewRow);
                    }
                }
            }
        }
        findFunctionalDependencies(table1FN);
        findPrimaryKey(table1FN);
        return new Database(table1FN);
    }

    public NormalizerResult isNormalized(Table table) {
        List<String> anomalyList = new ArrayList<>();
        int cols = table.columns();
        Map<String, Integer> fieldMap = new HashMap<>();
        for(int i = 0; i < cols; i ++) {
            String field = table.getFieldName(i);
            if(field.isEmpty()) {
                anomalyList.add(String.format("Field %d is empty", i));
            } else {
                fieldMap.compute(field, (f, count) -> count == null? 1 : count + 1);
            }
        }
        fieldMap.forEach((field, count) -> {
            if(count > 1) anomalyList.add(String.format("Field %s appears %d times", field, count));
        });
        Key primaryKey = table.getPrimaryKey();
        if(primaryKey == null || primaryKey == Key.emptyKey()) anomalyList.add("Primary key is not defined");
        else {
            Set<String> set = new HashSet<>();
            List<Integer> keyFieldIndexList = new ArrayList<>();
            for(String field: primaryKey) {
                int index = table.getFieldIndex(field);
                if(index >= 0) keyFieldIndexList.add(index);
            }
            for(Row row : table) {
                String s = keyFieldIndexList.stream().map(c -> row.get(c)).reduce("\t", String::concat);
                if(set.contains(s)) {
                    anomalyList.add(String.format("Primary key '%s' is allowing duplicate values", primaryKey));
                    break;
                } else set.add(s);
            }
        }
        return new NormalizerResult(anomalyList.isEmpty(), anomalyList);
    }

    private void findFunctionalDependencies(Table table) {
        int[] dependencies = new int[table.columns()];
        int[] rowCounts = new int[table.columns()];
        for (int i = 0; i < table.columns(); i++) {
            for (int j = 0; j < table.columns(); j++) {
                if(i == j) continue;
                int rowCount = getDependencyCalculator().isDependent(table, i, j);
                if(rowCount > 0 && getDependencyCalculator().isDependent(table, j, i) <= 0) {
                    if(rowCounts[j] == 0 || rowCount < rowCounts[j]) {
                        rowCounts[j] = rowCount;
                        dependencies[j] = i;
                    }
                }
            }
        }
        for(int j = 0; j < table.columns(); j ++) {
            if(rowCounts[j] == 0) continue;
            int i = dependencies[j];
            table.addDependency(new Key(table.getFieldName(i)), j);
        }
    }

    private void findPrimaryKey(Table table) {
        // Encontrar el subconjunto más pequeño de atributos tales que:
        //   a) No existen 2 filas de la tabla con los mismos valores en tal subconjunto
        //   b) Ningún atributo del subconjunto define a otro del subconjunto
        Queue<List<Integer>> pendingCandidates = new ArrayDeque<>();
        for (int i = 0; i < table.columns(); i++) pendingCandidates.add(List.of(i));
        List<Integer> candidate = pendingCandidates.peek();
        while(!pendingCandidates.isEmpty()) {
            candidate = pendingCandidates.poll();
            // ¿Existen 2 filas que tengan los mismos valores en las columnas de la llave candidata?
            Set<String> valuesForKey = new HashSet<>();
            boolean found = false;
            for (Row row : table) {
                String valueForKey = "";
                for (Integer I : candidate) valueForKey += row.get(I) + "#";
                if (!valuesForKey.add(valueForKey)) {
                    found = true;
                    break;
                }
            }
            int last = candidate.get(candidate.size() - 1);
            if(found) {
                // Crear llaves candidatas añadiendo un atributo más a la llave candidata actual
                for(int f = last + 1; f < table.columns(); f ++) {
                    List<Integer> newCandidate = new ArrayList<>(candidate);
                    newCandidate.add(f);
                    pendingCandidates.offer(newCandidate);
                }
            } else {
                // Si la llave candidata tiene un sólo atributo, ya encontramos la llave principal
                if(candidate.size() == 1) break;
                // Si no, ¿existen dependencias dentro de la llave candidato?
                // Sólo se determinará si existe dependencia funcional entre el último atributo y cada uno de los anteriores
                found = false;
                Key keyLast = new Key(table.getFieldName(last));
                for(int i = 0; !found && i < candidate.size() - 1; i ++) {
                    int f = candidate.get(i);
                    Key key = new Key(table.getFieldName(f));
                    if(table.getDependencies().containsKey(key)) {
                        List<String> list = table.getDependencies().get(key);
                        if(list.contains(table.getFieldName(last))) found = true;
                    }
                    if(!found && table.getDependencies().containsKey(keyLast)) {
                        List<String> list = table.getDependencies().get(keyLast);
                        if(list.contains(table.getFieldName(f))) found = true;
                    }
                }
                if(!found) break;
            }
        }
        Key primaryKey = Key.fromFieldIndices(new HashSet<>(candidate), table);
        table.setPrimaryKey(primaryKey);
        // La clave principal definirá a todos los atributos no clave que no son definidos por nadie más
        tag: for (int i = 0; i < table.columns(); i++) {
            String field = table.getFieldName(i);
            if(primaryKey.contains(field)) continue;
            for(List<String> values: table.getDependencies().values()) {
                if(values.contains(field)) continue tag;
            }
            table.addDependency(primaryKey, field);
        }
        if (!table.existsDependency(primaryKey)) table.addDependency(table.getPrimaryKey(), null);
    }

    private void findPrimaryKey1(Table table) {
        HashSet<Integer> Ap = new HashSet<Integer>();
        HashSet<Integer> Adef = new HashSet<Integer>();
        HashSet<Integer> Andf = new HashSet<Integer>();
        for (int i = 0; i < table.columns(); i++) {
            //if(!table.isMarked(i)) Ap.add(i);
            Ap.add(i);
            Andf.add(i);
        }
        int iterations = Andf.size();
        for (int i = 1; i <= iterations; i++) {
            Set<Set<Integer>> powerSet = findSubsets(Ap, i);
            System.out.println(i + ": " + powerSet);
            tag1: for (Set<Integer> phi : powerSet) {
                if (phi.size() < i) continue;
                //System.out.println("Clave candidata: " + phi);
//				Si hay un atributo en phi ya definido, ignorar la combinación phi
                for (Integer I : phi) {
                    if (Adef.contains(I)) continue tag1;
                }
//				Por cada atributo J no definido, determinar si phi define a J
                for (Integer J : Andf) {
//					Si J ya se encuentra en phi, ignorar
                    if (phi.contains(J)) continue;
                    if (getDependencyCalculator().isDependent(table, phi, J) > 0) {
                        Adef.add(J);
                        Key key = Key.fromFieldIndices(phi, table);
                        table.addDependency(key, J);
                    }
                }
                for (Integer J : Adef) {
                    Andf.remove(J);
                    Ap.remove(J);
                }
            }
        }
        table.setPrimaryKey(Key.fromFieldIndices(Ap, table));

//		Si la clave primaria no definió a ningun atributo, se agrega como dependencia con "nadie"
        Key pk = table.getPrimaryKey();
        if (!table.existsDependency(pk)) table.addDependency(table.getPrimaryKey(), null);
    }

    private static Set<Set<Integer>> findSubsets(Set<Integer> originalSet, int size) {
        Set<Set<Integer>> powerSet = new HashSet<>();
        if (size >= originalSet.size()) {
            powerSet.add(originalSet);
        } else if (size > 0) {
            for (Integer I : originalSet) {
                Set<Integer> subset = new HashSet<Integer>();
                subset.add(I);
                buildSubset(originalSet, subset, powerSet, size, I);
            }
        }
        return powerSet;
    }

    private static void buildSubset(Set<Integer> originalSet, Set<Integer> subset, Set<Set<Integer>> powerSet, int size, Integer lastAdded) {
        if (subset.size() >= size) {
            if (!powerSet.contains(subset)) powerSet.add(subset);
            return;
        }
        for (Integer I : originalSet) {
            if (I <= lastAdded || subset.contains(I)) continue;
            Set<Integer> subsetClone = new HashSet<Integer>();
            for (Integer J : subset) subsetClone.add(J);
            subsetClone.add(I);
            buildSubset(originalSet, subsetClone, powerSet, size, I);
        }
    }
}