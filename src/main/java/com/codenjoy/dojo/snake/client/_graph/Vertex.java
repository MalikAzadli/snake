package com.codenjoy.dojo.snake.client._graph;

import java.util.Objects;

public class Vertex {
    private String label;
    private Integer lowestCost = -1;
    private int x;
    private int y;

    public Vertex(String label) {
        this.label = label;
        String[] coordinates = label.split(":");
        this.x = Integer.parseInt(coordinates[0]);
        this.y = Integer.parseInt(coordinates[1]);
    }

    public void setLowestCost(int lowestCost) {
        this.lowestCost = lowestCost;
    }

    public Integer getLowestCost() {
        return lowestCost;
    }

    public String getLabel() {
        return label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(label, vertex.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
