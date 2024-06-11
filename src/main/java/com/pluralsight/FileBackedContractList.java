/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import manifold.ext.delegation.rt.api.*;

import java.io.*;
import java.util.*;

/**
 * Represents a list of {@link Contract}s backed by a CSV {@link File}.
 */
public class FileBackedContractList implements SimpleList<Contract> {
    private final File file;
    @link
    List<Contract> contracts;

    /**
     * @param file The file to use as a backing
     */
    public FileBackedContractList(File file) {
        this.file = file;
        contracts = new ArrayList<>();

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            br.lines()
                .map(Contracts::fromCSV)
                .forEachOrdered(contracts::add);
        }
    }

    @Override
    public void add(Contract contract) {
        contracts.add(contract);

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw)
        ) {
            bw.newLine();
            bw.write(Contracts.makeCSV(contract));
        }
    }

    @Override
    public boolean remove(Contract item) {
        if (!contracts.remove(item))
            return false;

        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)
        ) {
            for (Contract cx : contracts) {
                bw.newLine();
                bw.write(Contracts.makeCSV(cx));
            }
        }

        return true;
    }
}
