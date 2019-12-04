package com.codenjoy.dojo.snake.client._graph;

import java.util.*;

public class Graph {
    private Map<Vertex, List<Vertex>> adjVertices;

    Graph() {
        this.adjVertices = new HashMap<>();
    }

    public void addVertex(Vertex vertex) {
        adjVertices.putIfAbsent(vertex, new ArrayList<>());
    }

    public void removeVertex(Vertex vertex) {
        if (vertex != null) {
            adjVertices.values().stream().forEach(e -> e.remove(vertex));
            adjVertices.remove(vertex);
        }
    }

    public void addEdge(Vertex v1, Vertex v2) {
        adjVertices.get(v1).add(v2);
        adjVertices.get(v2).add(v1);
    }

    public void removeEdge(Vertex v1, Vertex v2) {
        List<Vertex> eV1 = adjVertices.get(v1);
        List<Vertex> eV2 = adjVertices.get(v2);
        if (eV1 != null)
            eV1.remove(v2);
        if (eV2 != null)
            eV2.remove(v1);
    }

    public List<Vertex> getAdjVertices(Vertex vertex) {
        return adjVertices.get(vertex);
    }

    public Vertex getVertex(Vertex vertex) {
        return adjVertices.keySet().stream().filter(vertex1 -> vertex1.equals(vertex)).findFirst().get();
    }

    public Optional<Vertex> getVertexOpt(Vertex vertex) {
        return adjVertices.keySet().stream().filter(vertex1 -> vertex1.equals(vertex)).findFirst();
    }

    public void setCostsDefault() {
        adjVertices.keySet().stream().forEach(vertex -> vertex.setLowestCost(0));
    }

    public String printGraph() {
        StringBuffer sb = new StringBuffer();
        for (Vertex v : adjVertices.keySet()) {
            sb.append(v + ":");
            sb.append(adjVertices.get(v));
//            sb.append("COST : " + v.getLowestCost());
            sb.append("\n");
        }
        return sb.toString();
    }

}