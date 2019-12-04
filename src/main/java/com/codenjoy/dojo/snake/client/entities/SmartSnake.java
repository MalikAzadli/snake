package com.codenjoy.dojo.snake.client.entities;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.client._graph.GraphOp;
import com.codenjoy.dojo.snake.client._graph.Graph;
import com.codenjoy.dojo.snake.client._graph.Vertex;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

public class SmartSnake {
    private Point stone;
    private Point apple;
    private Point applePrevPos;
    private Lee lee;
    private Point snake_head;
    private Point snake_tail;
    private boolean gameOver;
    private ArrayList<Point> obstacles = new ArrayList<>();
    private Graph graph;
    private Board board;
    private List<Point> unavailablePoints;
    private List<LeePoint> possibleWaysForLee;
    private List<Vertex> possibleWays;

    public SmartSnake(Board board) {
        this.board = board;
        this.graph = GraphOp.create(14, 14);
        List<Point> walls = board.getWalls();
        gameOver = board.isGameOver();
        stone = board.getStones().get(0);
        apple = board.getApples().get(0);
        applePrevPos = PointImpl.pt(0, 0);
        List<Point> snake = board.getSnake();
        snake_head = board.getHead();

        int dimX = walls.stream().mapToInt(Point::getX).max().orElse(0) + 1;
        int dimY = walls.stream().mapToInt(Point::getY).max().orElse(0) + 1;
        obstacles.addAll(walls);
        obstacles.add(stone);
        obstacles.addAll(snake);
        lee = new Lee(dimX, dimY);
        possibleWays = new ArrayList<>();
    }

    public Direction solve() {
        if (gameOver) return Direction.UP;

        if (board.getSnake().size() > 30) {
            apple = stone;
            setUnavailableVertices(false);
        } else {
            apple = board.getApples().get(0);
            setUnavailableVertices(true);
        }

        GraphOp.removeVertices(unavailablePoints, graph);
        setPath();
        Point nextDirection = getNextDirection();
        System.out.println("NEXT DIR: " + nextDirection.toString());
        Direction current = coord_to_direction(snake_head, nextDirection);
        System.gc();
        return current;
    }

    private void setUnavailableVertices(boolean eliminateStone) {
        ArrayList<Point> points = new ArrayList<>();
        if (eliminateStone) points.add(stone);
        List<Point> snake = board.getSnake();
        snake.remove(0);
        points.addAll(snake);
        this.unavailablePoints = points;
        System.out.println("UNAVAILABLE POINTS: " + unavailablePoints);
    }

    private Point getNextDirection() {
        System.out.println("FOUND PATH:" + possibleWays);
        if (possibleWays.isEmpty()) {
//            int size = board.getSnake().size();
//            Point tail = board.getSnake().get(size - 1);
//            GraphOp.addVertex(tail, graph);
//            Vertex headVertex = graph.getVertex(new Vertex(String.format("%d:%d", snake_head.getX(), snake_head.getY())));
//            Vertex tailVertex = graph.getVertex(new Vertex(String.format("%d:%d", tail.getX(), tail.getY())));
//            this.possibleWays = path(headVertex, tailVertex);
//            possibleWays.remove(0);
//            Vertex vertex = graph.getVertex(possibleWays.remove(0));
//            return PointImpl.pt(vertex.getX(), vertex.getY());
        }
        Vertex vertex = graph.getVertex(possibleWays.remove(0));
        return PointImpl.pt(vertex.getX(), vertex.getY());
    }

    public void setPath() {

        Vertex appleVertex = graph.getVertex(new Vertex(String.format("%d:%d", apple.getX(), apple.getY())));
        Vertex headVertex = graph.getVertex(new Vertex(String.format("%d:%d", snake_head.getX(), snake_head.getY())));

        this.possibleWays = path(headVertex, appleVertex);

        if (!possibleWays.isEmpty())
            possibleWays.remove(0);
    }

    public List<Vertex> path(Vertex src, Vertex des) {
//        return getAnyPath(src, des, new LinkedHashSet<>());
//        return getShorterPath(src, des, new LinkedHashSet<>());
        src.setLowestCost(0);
        System.out.println("SRC LOWEST COST: " + src.getLowestCost());
        setPathCost(src, des, 0);
        System.out.println("CUT OUT");
        System.out.println("DES LOWEST COST: " + des.getLowestCost());
        List<Vertex> path = new ArrayList<>();
        Optional<List<LeePoint>> trace = lee.trace(snake_head, apple, obstacles);
        if (des.getLowestCost() > 12 && trace.isPresent()) {
            List<LeePoint> leePoints = trace.get();
            for (LeePoint leePoint : leePoints) {
                path.add(new Vertex(String.format("%d:%d", leePoint.x(), leePoint.y())));
            }
        } else path = getShortestPath(src, des, 0, des.getLowestCost());
        System.out.println("DES LOWEST COST: " + des.getLowestCost());
        graph.setCostsDefault();
        if (!path.isEmpty()) return path;
        else {
            List<Vertex> leePoints = getShortestPath(src, des, 0, des.getLowestCost());
            return path;
        }
    }

//    public List<String> getAnyPath(String src, String des, Set<String> visited) {
//        if (src.equals(des)) return new ArrayList<>(Arrays.asList(src));
//        List<String> path = new ArrayList<>();
//        visited.add(src);
//        for (Graph.Vertex v : graph.getAdjVertices(src)) {
//            if (!visited.contains(v.label)) {
//                List<String> returned = getAnyPath(v.label, des, visited);
//                if (!returned.isEmpty()) {
//                    path.add(src);
//                    path.addAll(returned);
//                    break;
//                }
//            }
//        }
//        return path;
//    }


    public void setPathCost(Vertex src, Vertex des, int costUntilHere) {
        if (src.equals(des)) return;
        costUntilHere++;
        Queue<Vertex> queue = new LinkedList<>();
        for (Vertex v : graph.getAdjVertices(src)) {
            if (v.getLowestCost() == -1 || v.getLowestCost() > costUntilHere) {
                v.setLowestCost(costUntilHere);
                queue.add(v);
            }
        }

        Vertex vertex = queue.poll();
        while (vertex != null) {
            setPathCost(vertex, des, costUntilHere);
            vertex = queue.poll();
        }
    }


    public List<Vertex> getShortestPath(Vertex src, Vertex des, int costUntilHere, int optimalCost) {
        if (costUntilHere > optimalCost) return new ArrayList<>();
        if (src.equals(des)) return new ArrayList<>(Arrays.asList(src));

        List<Vertex> adjacents = new ArrayList<>();
        for (Vertex v : graph.getAdjVertices(src)) {
            adjacents.add(v);
        }
        costUntilHere++;
        adjacents.sort((o1, o2) -> o2.getLowestCost() - o1.getLowestCost());
        List<List<Vertex>> possiblePaths = new ArrayList<>();
        for (Vertex vertex : adjacents) {
            List<Vertex> path = getShortestPath(vertex, des, costUntilHere, optimalCost);
            if (!path.isEmpty()) {
                path.add(0, src);
                possiblePaths.add(path);
                break;
            }
        }
        if (!possiblePaths.isEmpty()) return possiblePaths.get(0);
        return new ArrayList<>();
    }

    public List<Vertex> getShorterPath(Vertex src, Vertex des, Set<Vertex> visited) {
        if (src.equals(des)) return new ArrayList<>(Collections.singletonList(src));
        List<List<Vertex>> path = new ArrayList<>();
        visited.add(src);
        for (Vertex v : graph.getAdjVertices(src)) {
            if (!visited.contains(v)) {
                List<Vertex> returned = getShorterPath(v, des, visited);
                if (!returned.isEmpty()) {
                    returned.add(0, src);
                    path.add(returned);
                }
            }
        }
        if (path.isEmpty()) return new ArrayList<>();
        return path.stream().min(Comparator.comparingInt(List::size)).get();
    }

    private Direction coord_to_direction(Point from, Point to) {
        if (to.getX() < from.getX()) return Direction.LEFT;
        if (to.getX() > from.getX()) return Direction.RIGHT;
        if (to.getY() > from.getY()) return Direction.UP;   // vise versa because of reverted board
        if (to.getY() < from.getY()) return Direction.DOWN; // vise versa because of reverted board
        throw new RuntimeException("you shouldn't be there...");
    }

}
