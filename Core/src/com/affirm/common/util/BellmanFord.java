package com.affirm.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BellmanFord {
    private Graph graph;

    private HashMap<Integer, ArrayList<Integer>> mapDistances;
    private ArrayList<Integer> distance;

    private Integer INFINITY = Integer.MAX_VALUE;

    private ArrayList<Integer> banned;

    public BellmanFord() {
        this.mapDistances = new HashMap<>();
        this.banned = new ArrayList<>();
    }


    private void clear() {
        distance = new ArrayList<Integer>(this.graph.getVertices() + 1);
        for(Integer i = 0 ; i <= this.graph.getVertices() ; ++i) {
            distance.add(INFINITY);
        }
    }

    private void relax(Integer from, Integer to, int weight) {
        Integer nextDistance = distance.get(from) + weight;

        if(distance.get(to) > distance.get(from) + weight) {
            distance.set(to, nextDistance);
        }
    }

    private void bellman(Integer origin) {
        if(mapDistances.containsKey(origin)) {
            return;
        }
        this.clear();
        distance.set(origin, 0);

        for(int i = 0 ; i <= this.graph.getVertices() ; ++i) {
            for(int from =  0 ; from <= this.graph.getVertices() ; ++from ) {
                if(banned.contains(from))
                    continue;
                for(Integer to : this.graph.getAdjacency()[from]) {
                    if(banned.contains(to))
                        continue;
                    relax(from ,to, -1);
                }
            }
        }

        mapDistances.put(origin, distance);
    }

    public void configure(Graph graph) {
        this.graph = graph;
        this.graph.buildSourcesTargets();
        this.process();
    }

    public int getSize() {
        Iterator it = mapDistances.entrySet().iterator();
        int size = INFINITY;

        while (it.hasNext()) {
            Map.Entry<Integer, ArrayList<Integer>> entry = (Map.Entry<Integer, ArrayList<Integer>>)it.next();
            for(int i = 0 ; i < entry.getValue().size() ; ++i) {
                size = Math.min(size, entry.getValue().get(i));
            }
        }

        return  Math.abs(size);
    }

    public void printDistances() {
        Iterator it = mapDistances.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, ArrayList<Integer>> entry = (Map.Entry<Integer, ArrayList<Integer>>)it.next();
            System.out.println("Distances for : " + entry.getKey());
            for(int i = 0 ; i < entry.getValue().size() ; ++i) {
                System.out.println("  To : " + i + " => " + entry.getValue().get(i));
            }
        }
    }

    private void process() {

        for(Integer source : this.graph.getSources()) {
            bellman(source);
        }

        this.graph.invert();
        for(Integer target : this.graph.getTargets()) {
            bellman(target);
        }
    }

    public Integer query(Integer origin, Integer node) {
        return mapDistances.get(origin).get(node);
    }

    public ArrayList<Integer> getBanned() {
        return banned;
    }

    public void setBanned(ArrayList<Integer> banned) {
        this.banned = banned;
    }
}
