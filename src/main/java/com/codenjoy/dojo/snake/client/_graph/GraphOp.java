package com.codenjoy.dojo.snake.client._graph;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.codenjoy.dojo.services.Point;

public class GraphOp {

    public static Graph create(int height, int width) {
        Graph graph = new Graph();

        IntStream.range(1, height).forEach(i -> {
            IntStream.range(1, width).forEach(j -> {
                Vertex vertex = new Vertex(String.format("%d:%d", j, i));
                graph.addVertex(vertex);
            });
        });

        IntStream.range(1, height).forEach(i -> {
            IntStream.range(1, width).forEach(j -> {
                Vertex vertex1 = graph.getVertex(new Vertex(String.format("%d:%d", j, i)));
                if (j == width - 1 && i == height - 1) {

                } else if (i == height - 1) {
                    Vertex vertex2 = graph.getVertex(new Vertex(String.format("%d:%d", j + 1, i)));
                    graph.addEdge(vertex1, vertex2);
                } else if (j == width - 1) {
                    Vertex vertex2 = graph.getVertex(new Vertex(String.format("%d:%d", j, i + 1)));
                    graph.addEdge(vertex1, vertex2);
                } else {
                    Vertex vertex2 = graph.getVertex(new Vertex(String.format("%d:%d", j, i + 1)));
                    Vertex vertex3 = graph.getVertex(new Vertex(String.format("%d:%d", j + 1, i)));
                    graph.addEdge(vertex1, vertex2);
                    graph.addEdge(vertex1, vertex3);
                }
            });
        });

        return graph;
    }

    public static void refresh(Graph graph) {
        graph = create(14, 14);
    }

    public static void removeVertices(List<Point> points, Graph graph) {
        points.forEach(p -> {
            Vertex vertex = graph.getVertex(new Vertex(String.format("%d:%d", p.getX(), p.getY())));
            graph.removeVertex(vertex);
        });
    }

    public static void addVertex(Point point, Graph graph) {
        Vertex vertexMain = new Vertex(String.format("%d:%d", point.getX(), point.getY()));
        graph.addVertex(vertexMain);
        for (int i = 0; i < 4; i++) {
            Vertex vertex;
            Optional<Vertex> vertexOpt;
            switch (i) {
                case 0:
                    vertex = new Vertex(String.format("%d:%d", point.getX() + 1, point.getY()));
                    vertexOpt = graph.getVertexOpt(vertex);
                    if (vertexOpt.isPresent()) graph.addEdge(vertexMain, vertexOpt.get());
                    break;
                case 1:
                    vertex = new Vertex(String.format("%d:%d", point.getX(), point.getY() + 1));
                    vertexOpt = graph.getVertexOpt(vertex);
                    if (vertexOpt.isPresent()) graph.addEdge(vertexMain, vertexOpt.get());
                    break;
                case 2:
                    vertex = new Vertex(String.format("%d:%d", point.getX(), point.getY() - 1));
                    vertexOpt = graph.getVertexOpt(vertex);
                    if (vertexOpt.isPresent()) graph.addEdge(vertexMain, vertexOpt.get());
                    break;
                case 3:
                    vertex = new Vertex(String.format("%d:%d", point.getX() - 1, point.getY()));
                    vertexOpt = graph.getVertexOpt(vertex);
                    if (vertexOpt.isPresent()) graph.addEdge(vertexMain, vertexOpt.get());
                    break;
            }

        }
    }
}
