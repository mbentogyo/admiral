package com.ThePod.Admirals.board;

import lombok.Getter;

public class Coordinates {
    /**  Gets the row of the coordinates (0 - 9) */
    @Getter private final int row;

    /** Gets the column of the coordinates (A - O) */
    @Getter private final int column;

    /**
     * Creates a Coordinates object
     * @param crd a string ranging from A0 to O9
     */
    public Coordinates(String crd) {
        if (!crd.matches("^[A-O][0-9]$")) {
            throw new IllegalArgumentException("Invalid coordinate format");
        }
        this.column = crd.charAt(0) - 'A';
        this.row = crd.charAt(1) - '0';
    }

    /**
     * Creates a Coordinates object
     * @param columnChar a character ranging from A to O
     * @param row an integer ranging from 0 to 9
     */
    public Coordinates(char columnChar, int row) {
        this(String.valueOf(columnChar) + row);
    }

    /**
     * Creates a Coordinates object
     * @param column an integer ranging from 0 to 15
     * @param row an integer ranging from 0 to 9
     */
    public Coordinates(int column, int row) {
        this((char) (column + 'A'), row);
    }

    @Override
    public String toString() {
        return (char)('A' + column) + String.valueOf(row);
    }
}

